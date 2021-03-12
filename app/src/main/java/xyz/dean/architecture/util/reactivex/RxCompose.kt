package xyz.dean.architecture.util.reactivex

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

class RxCompose<T> {
    private val streams: MutableList<Observable<T>> = LinkedList()

    fun Observable<T>.push() {
        streams.add(this)
    }

    fun Single<T>.push() = this.toObservable().push()
    fun Maybe<T>.push() = this.toObservable().push()
    fun Completable.push() = this.toObservable<T>().push()

    fun mergeAll(): Observable<T> = Observable.merge(streams.toList())

    companion object {
        fun <T> compose(f: RxCompose<T>.() -> Unit): RxCompose<T> =
                RxCompose<T>().apply { f() }

        fun <T> mergeAll(f: RxCompose<T>.() -> Unit): Completable =
                Completable.defer { RxCompose<T>().apply { f() }.mergeAll().ignoreElements() }
    }
}