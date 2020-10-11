package xyz.dean.architecture.mvc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_mvc.*
import xyz.dean.architecture.R

@SuppressLint("SetTextI18n")
class MvcActivity : AppCompatActivity() {
    private val mvcModel: MvcModel = MvcModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvc)
        val typeface = Typeface.createFromAsset(assets,"fonts/font.ttf")
        phrase_tv.typeface = typeface

        getPhrase()

        next_bt.setOnClickListener { getPhrase() }
    }

    private fun getPhrase() {
        mvcModel.requestPhrase(
            onResult = {
                phrase_tv.text = it.phrase
                from_tv.text = "——${it.fromWho ?: ""}「${it.from}」"
            },
            onError = {
                Toast.makeText(this,
                    "Request phrase failed. Caused by: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        mvcModel.onDestroy()
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, MvcActivity::class.java)
    }
}
