package xyz.dean.architecture.mvi

import xyz.dean.architecture.api.PhraseInfo

sealed class MviViewState {
    data class Error(val err: Throwable) : MviViewState()
    data class Success(val phraseInfo: PhraseInfo) : MviViewState()
    object Idle : MviViewState()
}