package xyz.dean.architecture.mvp

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.dean.architecture.api.PhraseInfo
import xyz.dean.architecture.api.PhraseService
import xyz.dean.architecture.api.core.ApiClient
import xyz.dean.architecture.common.Constant

class MvpPresenter(private val view: MvpContract.View) : MvpContract.Presenter {
    private val apiClient = ApiClient.Builder(Constant.API_HOST).build()
    private val phraseService: PhraseService = apiClient.createService(PhraseService::class.java)
    private val callList: MutableList<Call<*>> = mutableListOf()

    init {
        view.setPresenter(this)
    }

    override fun getPhrase() {
        val call = phraseService.getPhrase()
        callList.add(call)
        call.enqueue(object : Callback<PhraseInfo> {
            @Suppress("ThrowableNotThrown")
            override fun onResponse(call: Call<PhraseInfo>, response: Response<PhraseInfo>) {
                response.body()
                    ?.let { view.showPhrase(it) }
                    ?: view.showErrorMsg("Request phrase failed.", Exception("Phrase info is empty."))
            }

            override fun onFailure(call: Call<PhraseInfo>, t: Throwable) {
                view.showErrorMsg("Request phrase failed.", t)
            }
        })
    }

    override fun start() {
        getPhrase()
    }

    override fun destroy() {
        callList.forEach { it.cancel() }
        callList.clear()
    }
}