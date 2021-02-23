package xyz.dean.architecture.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.dean.architecture.api.PhraseInfo
import xyz.dean.architecture.api.PhraseService
import xyz.dean.architecture.api.core.ApiClient
import xyz.dean.architecture.common.Constant
import xyz.dean.architecture.util.noNull
import xyz.dean.architecture.util.onNull

class MvvmViewModel : ViewModel() {
    val phraseData = MutableLiveData<PhraseInfo?>()
    val errMsgData = MutableLiveData<String?>()

    private val apiClient = ApiClient.Builder(Constant.API_HOST).build()
    private val phraseService: PhraseService = apiClient.createService(PhraseService::class.java)

    fun getPhrase() {
        errMsgData.value = null
        phraseService.getPhrase().enqueue(object : Callback<PhraseInfo> {
            override fun onResponse(call: Call<PhraseInfo>, response: Response<PhraseInfo>) {
                response.body()
                    .noNull { phraseData.value = it }
                    .onNull { errMsgData.value = "Request phrase failed. Caused by: Phrase response is empty." }
            }

            override fun onFailure(call: Call<PhraseInfo>, t: Throwable) {
                errMsgData.value =  "Request phrase failed. Caused by: ${t.message}"
            }
        })
    }
}