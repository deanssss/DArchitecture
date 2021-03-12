package xyz.dean.architecture.redux

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_mvp.*
import xyz.dean.architecture.R
import xyz.dean.architecture.api.PhraseService
import xyz.dean.architecture.api.core.ApiClient
import xyz.dean.architecture.common.Constant
import xyz.dean.architecture.util.reactivex.BindLife
import xyz.dean.architecture.util.reactivex.RxUtil

class ReduxActivity : AppCompatActivity(), BindLife {
    override val compositeDisposable = CompositeDisposable()

    private val apiClient = ApiClient.Builder(Constant.API_HOST).build()
    private val phraseService: PhraseService = apiClient.createService(PhraseService::class.java)
    private val store = createReduxStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redux)

        initData()
        bindView()
        bindEvent()
    }

    private fun initData() {
        requestPhrase().bindLife()
    }

    private fun bindView() {
        val typeface = Typeface.createFromAsset(assets,"fonts/font.ttf")
        phrase_tv.typeface = typeface

        store.render({ it.phraseViewModel }) {
            findViewById<TextView>(R.id.phrase_tv).text = it.phrase
            findViewById<TextView>(R.id.from_tv).text = it.from
        }.bindLife()

        store.render({ it.loading }) {
            findViewById<ProgressBar>(R.id.progress).visibility =
                    if (it) View.VISIBLE else View.GONE
        }.bindLife()
    }

    private fun bindEvent() {
        RxView.clicks(findViewById(R.id.next_bt))
                .flatMapCompletable { requestPhrase() }
                .bindLife()
    }

    private fun requestPhrase() = store.dispatch(StartRequest)
            .flatMap {
                phraseService.getPhraseRx()
                        .doOnError { store.dispatch(RequestError(it)).bindLife() }
            }
            .flatMap { store.dispatch(RequestComplete(it)) }
            .compose(RxUtil.switchThread())
            .doOnError { Toast.makeText(this, "Request phrase error!", Toast.LENGTH_SHORT).show() }
            .ignoreElement()
            .onErrorComplete()

    override fun onDestroy() {
        super.onDestroy()
        store.dispose()
        compositeDisposable.clear()
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, ReduxActivity::class.java)
    }
}