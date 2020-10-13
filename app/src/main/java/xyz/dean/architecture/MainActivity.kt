package xyz.dean.architecture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import xyz.dean.architecture.mvc.MvcActivity
import xyz.dean.architecture.mvp.MvpActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mvc_bt.setOnClickListener {
            startActivity(MvcActivity.createIntent(this))
        }
        mvp_bt.setOnClickListener {
            startActivity(MvpActivity.createIntent(this))
        }
    }
}
