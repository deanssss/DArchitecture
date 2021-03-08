package xyz.dean.architecture.mvi

sealed class MviIntent {
    object InitialIntent : MviIntent()
    object NextIntent : MviIntent()
}
