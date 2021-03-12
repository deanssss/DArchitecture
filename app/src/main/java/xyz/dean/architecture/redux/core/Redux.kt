package xyz.dean.architecture.redux.core

import androidx.annotation.CheckResult
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet

interface BaseAction
typealias LambdaAction<S> = (S) -> S

interface BaseReducer<S, A: BaseAction> {
    operator fun invoke(state: S, action: A): S
}

fun <S, A: BaseAction> emptyReducerCreator(): BaseReducer<S, A> = object : BaseReducer<S, A> {
    override fun invoke(state: S, action: A): S = state
}

abstract class Store<S: Any>(initState: S) {
    private val value = atomic(initState)
    private val stateSubject: Subject<S> = BehaviorSubject.createDefault(initState).toSerialized()

    protected fun _dispatch(action: (S) -> S): Single<S> = Single.fromCallable {
        val newV = value.updateAndGet(action)
        stateSubject.onNext(newV)
        return@fromCallable newV
    }

    @CheckResult
    fun bind(): Observable<S> = stateSubject.hide()

    fun dispose() = stateSubject.onComplete()

    @CheckResult
    fun <SubS> render(stateFilter: (S) -> SubS, renderer: (SubS) -> Unit): Completable {
        return this.bind()
                .map { stateFilter(it) }
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(renderer)
                .ignoreElements()
    }
}

class ActionStore<S: Any, A: BaseAction>(
    initState: S,
    val reducer: BaseReducer<S, A> = emptyReducerCreator()
) : Store<S>(initState) {
    @CheckResult
    fun dispatch(action: A): Single<S> = _dispatch { reducer(it, action) }
}

class LambdaStore<S: Any>(initState: S) : Store<S>(initState) {
    @CheckResult
    fun dispatch(action: LambdaAction<S>): Single<S> = _dispatch(action)
}