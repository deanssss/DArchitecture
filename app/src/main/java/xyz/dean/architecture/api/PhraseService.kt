package xyz.dean.architecture.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PhraseService {
    @GET("/")
    fun getPhrase(
        @Query("c") type: String = "i"
    ): Call<PhraseInfo>
}