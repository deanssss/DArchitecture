package xyz.dean.architecture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import xyz.dean.architecture.mvc.MvcActivity
import xyz.dean.architecture.mvi.MviActivity
import xyz.dean.architecture.mvp.MvpActivity
import xyz.dean.architecture.mvvm.MvvmActivity
import xyz.dean.architecture.redux.ReduxActivity

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
        mvvm_bt.setOnClickListener {
            startActivity(MvvmActivity.createIntent(this))
        }
        mvi_bt.setOnClickListener {
            startActivity(MviActivity.createIntent(this))
        }
        redux_bt.setOnClickListener {
            startActivity(ReduxActivity.createIntent(this))
        }
    }
}
