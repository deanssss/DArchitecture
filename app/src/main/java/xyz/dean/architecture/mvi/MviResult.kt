package xyz.dean.architecture.mvi

import xyz.dean.architecture.api.PhraseInfo

sealed class MviResult {
    data class InitialResult(val phraseInfo: PhraseInfo) : MviResult()
    data class NextClickResult(val phraseInfo: PhraseInfo) : MviResult()
    data class ErrorResult(val err: Throwable) : MviResult()
}