@file:Suppress("unused")

package xyz.dean.architecture.util.reactivex

import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface BindLife {
    private val tag: String
        get() = javaClass.simpleName

    val compositeDisposable: CompositeDisposable

    fun Disposable.bindLife() = addDisposable(this)

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun Observable<*>.bindLife() =
            subscribe({ }, { log.e(tag, "Observable has error!", it) }).bindLife()

    fun Single<*>.bindLife() =
            subscribe({ }, { log.e(tag, "Single has error!", it) }).bindLife()

    fun Maybe<*>.bindLife() =
            subscribe({ }, { log.e(tag, "Maybe has error!", it) }).bindLife()

    fun Completable.bindLife() =
            subscribe({ }, { log.e(tag, "Completable has error!", it) }).bindLife()

    fun Flowable<*>.bindLife() =
            subscribe({ }, { log.e(tag, "Flowable has error!", it) }).bindLife()

    fun removeDisposable(disposable: Disposable) {
        compositeDisposable.remove(disposable)
    }

    fun destroyDisposables() = compositeDisposable.clear()
}

fun Observable<*>.bindTo(bind: BindLife) {
    val tag = bind.javaClass.simpleName
    val disposable = subscribe({ }, { log.e(tag, "Observable has error!", it) })
    bind.addDisposable(disposable)
}

fun Single<*>.bindTo(bind: BindLife) {
    val tag = bind.javaClass.simpleName
    val disposable = subscribe({ }, { log.e(tag, "Single has error!", it) })
    bind.addDisposable(disposable)
}

fun Maybe<*>.bindTo(bind: BindLife) {
    val tag = bind.javaClass.simpleName
    val disposable = subscribe({ }, { log.e(tag, "Maybe has error!", it) })
    bind.addDisposable(disposable)
}

fun Completable.bindTo(bind: BindLife) {
    val tag = bind.javaClass.simpleName
    val disposable = subscribe({ }, { log.e(tag, "Completable has error!", it) })
    bind.addDisposable(disposable)
}

fun Flowable<*>.bindTo(bind: BindLife) {
    val tag = bind.javaClass.simpleName
    val disposable = subscribe({ }, { log.e(tag, "Flowable has error!", it) })
    bind.addDisposable(disposable)
}