package xyz.dean.architecture.util.reactivex

import android.content.Intent
import android.os.Bundle
import android.util.Pair
import androidx.fragment.app.Fragment
import io.reactivex.SingleEmitter
import java.util.*

open class ForResultFragment() : Fragment() {
    var emitter: SingleEmitter<Pair<Int?, Intent?>>? = null
    var requestCode = 0
    var intent: Intent? = null

    constructor(emitter: SingleEmitter<Pair<Int?, Intent?>>, intent: Intent) : this() {
        this.emitter = emitter
        requestCode = Random().nextInt(1000)
        this.intent = intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && intent != null) {
            startActivityForResult(intent, requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (this.requestCode == requestCode) {
            emitter?.onSuccess(Pair(resultCode, data))
        }

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.commit()
    }
}