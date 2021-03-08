package xyz.dean.architecture.mvi

sealed class MviAction {
    object InitialAction : MviAction()
    object NextClickAction : MviAction()
}