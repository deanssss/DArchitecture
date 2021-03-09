package xyz.dean.architecture.redux

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.view.RxView
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDispose
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mvp.*
import xyz.dean.architecture.R
import xyz.dean.architecture.api.PhraseInfo
import xyz.dean.architecture.api.PhraseService
import xyz.dean.architecture.api.core.ApiClient
import xyz.dean.architecture.common.Constant
import xyz.dean.architecture.redux.core.LambdaStore

class ReduxActivity : AppCompatActivity() {
    private val apiClient = ApiClient.Builder(Constant.API_HOST).build()
    private val phraseService: PhraseService = apiClient.createService(PhraseService::class.java)
    private val store: LambdaStore<ReduxState> = LambdaStore(ReduxState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redux)

        initData()
        bindView()
        bindEvent()
    }

    private fun initData() {
        requestPhrase()
            .autoDispose(AndroidLifecycleScopeProvider.from(this))
            .subscribe({ }, { })
    }

    private fun bindView() {
        val typeface = Typeface.createFromAsset(assets,"fonts/font.ttf")
        phrase_tv.typeface = typeface

        store.bind()
            .doOnNext {
                findViewById<TextView>(R.id.phrase_tv).text = it.phraseInfo?.phrase ?: ""
                findViewById<TextView>(R.id.from_tv).text = "——${it.phraseInfo?.fromWho ?: ""}「${it.phraseInfo?.from ?: ""}」"
            }
            .autoDispose(AndroidLifecycleScopeProvider.from(this))
            .subscribe({}, {})
    }

    private fun bindEvent() {
        RxView.clicks(findViewById(R.id.next_bt))
            .flatMapCompletable { requestPhrase() }
            .autoDispose(AndroidLifecycleScopeProvider.from(this))
            .subscribe({ }, { })
    }

    private fun requestPhrase() = phraseService.getPhraseRx()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap { info ->
            store.dispatch { it.copy(phraseInfo = info) }
        }
        .doOnError { Log.e("ReduxActivity", "Request phrase failure!", it) }
        .ignoreElement()
        .onErrorComplete()

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, ReduxActivity::class.java)
    }

    data class ReduxState(
        val phraseInfo: PhraseInfo? = null
    )
}