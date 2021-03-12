package xyz.dean.architecture.util.reactivex.transformer

import io.reactivex.*
import org.reactivestreams.Publisher

internal class SwitchTransformer<T> constructor(
    private val subscribeOn: Scheduler,
    private val observableOn: Scheduler
) : ComposeTransformer<T> {

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.subscribeOn(subscribeOn)
            .observeOn(observableOn)
    }

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream.subscribeOn(subscribeOn)
            .observeOn(observableOn)
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.subscribeOn(subscribeOn)
            .observeOn(observableOn)
    }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream.subscribeOn(subscribeOn)
            .observeOn(observableOn)
    }

    override fun apply(upstream: Completable): CompletableSource {
        return upstream.subscribeOn(subscribeOn)
            .observeOn(observableOn)
    }
}