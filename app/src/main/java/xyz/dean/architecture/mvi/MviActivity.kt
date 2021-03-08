package xyz.dean.architecture.mvi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.view.RxView
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDispose
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_mvp.*
import xyz.dean.architecture.R

class MviActivity : AppCompatActivity() {
    private val viewModel = MviViewModel()

    private val nextClickSubject = PublishSubject.create<MviIntent.NextIntent>()

    private fun initialIntent(): Observable<MviIntent> = Observable.just(MviIntent.InitialIntent)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvi)
        val typeface = Typeface.createFromAsset(assets,"fonts/font.ttf")
        phrase_tv.typeface = typeface

        RxView.clicks(findViewById<Button>(R.id.next_bt))
            .map { MviIntent.NextIntent }
            .autoDispose(AndroidLifecycleScopeProvider.from(this))
            .subscribe(nextClickSubject)

        viewModel.state()
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(this))
            .subscribe(::render)

        viewModel.processIntents(intents())
    }

    fun intents(): Observable<MviIntent> = Observable.merge(
        initialIntent(),
        nextClickSubject
    )

    @SuppressLint("SetTextI18n")
    private fun render(state: MviViewState) {
        when (state) {
            MviViewState.Idle -> { }
            is MviViewState.Error -> {
                Toast.makeText(this, "${state.err}", Toast.LENGTH_SHORT).show()
            }
            is MviViewState.Success -> {
                findViewById<TextView>(R.id.phrase_tv).text = state.phraseInfo.phrase
                findViewById<TextView>(R.id.from_tv).text = "——${state.phraseInfo.fromWho ?: ""}「${state.phraseInfo.from}」"
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, MviActivity::class.java)
    }
}