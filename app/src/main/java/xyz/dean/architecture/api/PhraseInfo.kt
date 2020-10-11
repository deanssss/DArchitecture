package xyz.dean.architecture.api

import com.google.gson.annotations.SerializedName

data class PhraseInfo(
    val id: Long,
    @SerializedName("hitokoto") val phrase: String,
    val from: String,
    @SerializedName("from_who") val fromWho: String?
)