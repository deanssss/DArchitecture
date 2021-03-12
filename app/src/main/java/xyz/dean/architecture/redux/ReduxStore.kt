package xyz.dean.architecture.redux

import xyz.dean.architecture.api.PhraseInfo
import xyz.dean.architecture.redux.core.ActionStore
import xyz.dean.architecture.redux.core.BaseAction
import xyz.dean.architecture.redux.core.BaseReducer

fun createReduxStore(): ActionStore<ReduxState, ReduxAction> =
        ActionStore(ReduxState(), ReduxReducer)

sealed class ReduxAction : BaseAction
object StartRequest: ReduxAction()
class RequestComplete(val phraseInfo: PhraseInfo?) : ReduxAction()
class RequestError(val err: Throwable): ReduxAction()

object ReduxReducer : BaseReducer<ReduxState, ReduxAction> {
    override fun invoke(state: ReduxState, action: ReduxAction): ReduxState {
        return when (action) {
            StartRequest -> state.copy(loading = true)
            is RequestComplete -> state.copy(
                    loading = false,
                    phraseViewModel = PhraseViewModel.parse(action.phraseInfo))
            is RequestError -> state.copy(loading = false)
        }
    }
}