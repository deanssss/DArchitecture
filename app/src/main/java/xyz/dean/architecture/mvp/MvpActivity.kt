package xyz.dean.architecture.mvp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_mvp.*
import xyz.dean.architecture.R
import xyz.dean.architecture.api.PhraseInfo

class MvpActivity : AppCompatActivity(), MvpContract.View {
    private val presenter = MvpPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvp)
        val typeface = Typeface.createFromAsset(assets,"fonts/font.ttf")
        phrase_tv.typeface = typeface

        presenter.start()

        next_bt.setOnClickListener {
            presenter.getPhrase()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showPhrase(phraseInfo: PhraseInfo) {
        phrase_tv.text = phraseInfo.phrase
        from_tv.text = "——${phraseInfo.fromWho ?: ""}「${phraseInfo.from}」"
    }

    override fun showErrorMsg(msg: String, error: Throwable) {
        Toast.makeText(this, "$msg Caused by: ${error.message}", Toast.LENGTH_SHORT).show()
    }

    override fun setPresenter(presenter: MvpContract.Presenter) { }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, MvpActivity::class.java)
    }
}