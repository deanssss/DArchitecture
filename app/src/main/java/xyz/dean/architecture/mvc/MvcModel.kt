package xyz.dean.architecture.mvc

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.dean.architecture.api.PhraseInfo
import xyz.dean.architecture.api.PhraseService
import xyz.dean.architecture.api.core.ApiClient
import xyz.dean.architecture.common.Constant

class MvcModel {
    private val apiClient = ApiClient.Builder(Constant.API_HOST).build()
    private val phraseService: PhraseService = apiClient.createService(PhraseService::class.java)
    private val callList: MutableList<Call<*>> = mutableListOf()

    fun requestPhrase(onResult: (PhraseInfo) -> Unit, onError: (Throwable) -> Unit) {
        val call = phraseService.getPhrase()
        callList.add(call)
        call.enqueue(object : Callback<PhraseInfo> {
            override fun onFailure(call: Call<PhraseInfo>, t: Throwable) = onError(t)

            override fun onResponse(call: Call<PhraseInfo>, response: Response<PhraseInfo>) {
                val phraseInfo = response.body()
                if (phraseInfo != null) onResult(phraseInfo)
                else onError(Exception(""))
            }
        })
    }

    fun onDestroy() {
        callList.forEach { it.cancel() }
        callList.clear()
    }
}