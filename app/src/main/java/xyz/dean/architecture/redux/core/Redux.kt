package xyz.dean.architecture.redux.core

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet

interface BaseAction
typealias LambdaAction<S> = (S) -> S

typealias BaseReducer<S> = (S, BaseAction) -> S

fun <S> emptyReducerCreator(): BaseReducer<S> = object : BaseReducer<S> {
    override fun invoke(state: S, action: BaseAction): S = state
}

abstract class Store<S: Any>(initState: S) {
    protected val value = atomic(initState)
    protected val stateSubject: Subject<S> = BehaviorSubject.createDefault(initState).toSerialized()

    protected fun _dispatch(action: (S) -> S): Single<S> = Single.fromCallable {
        val newV = value.updateAndGet(action)
        stateSubject.onNext(newV)
        return@fromCallable newV
    }

    fun bind(): Observable<S> = stateSubject.hide()
    fun dispose() = stateSubject.onComplete()
}

class ActionStore<S: Any>(
    initState: S,
    val reducer: BaseReducer<S> = emptyReducerCreator()
) : Store<S>(initState) {
    fun dispatch(action: BaseAction): Single<S> = _dispatch { reducer(it, action) }
}

class LambdaStore<S: Any>(initState: S) : Store<S>(initState) {
    fun dispatch(action: LambdaAction<S>): Single<S> = _dispatch(action)
}