package xyz.dean.architecture.redux

import xyz.dean.architecture.api.PhraseInfo

data class ReduxState(
        val loading: Boolean = false,
        val phraseViewModel: PhraseViewModel = PhraseViewModel()
)

data class PhraseViewModel(
        val phrase: String = "",
        val from: String = ""
) {
    companion object {
        fun parse(phraseInfo: PhraseInfo?): PhraseViewModel {
            val phrase = phraseInfo?.phrase ?: ""
            val from = "——${phraseInfo?.fromWho ?: ""}「${phraseInfo?.from ?: ""}」"
            return PhraseViewModel(phrase, from)
        }
    }
}