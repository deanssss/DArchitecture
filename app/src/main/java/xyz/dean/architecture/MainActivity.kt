package xyz.dean.architecture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import xyz.dean.architecture.mvc.MvcActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mvc_bt.setOnClickListener {
            startActivity(MvcActivity.createIntent(this))
        }
    }
}
