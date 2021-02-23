package xyz.dean.architecture.mvvm

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import xyz.dean.architecture.R
import xyz.dean.architecture.databinding.ActivityMvvmBinding

class MvvmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMvvmBinding>(this, R.layout.activity_mvvm)
        val viewModel = ViewModelProvider(this).get(MvvmViewModel::class.java)

        viewModel.getPhrase()

        initView(viewModel, binding)
        observeLiveData(viewModel, binding)
    }

    private fun observeLiveData(
        viewModel: MvvmViewModel,
        binding: ActivityMvvmBinding
    ) {
        viewModel.phraseData.observe(this, Observer {
            binding.phraseInfo = it
        })

        viewModel.errMsgData.observe(this, Observer { msg ->
            msg?.also { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        })
    }

    private fun initView(
        viewModel: MvvmViewModel,
        binding: ActivityMvvmBinding
    ) {
        val typeface = Typeface.createFromAsset(assets, "fonts/font.ttf")
        binding.phraseTv.typeface = typeface

        binding.nextBt.setOnClickListener {
            viewModel.getPhrase()
        }
    }

    companion object {
        fun createIntent(context: Context): Intent =
                Intent(context, MvvmActivity::class.java)
    }
}