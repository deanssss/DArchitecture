package xyz.dean.architecture.util.reactivex.transformer

import io.reactivex.*
import org.reactivestreams.Publisher
import xyz.dean.architecture.util.reactivex.log
import java.util.concurrent.atomic.AtomicInteger

internal class RetryTransformer<T> constructor(
    private val maxRetryTimes: Int,
    private val onPreRetry: (Throwable) -> Completable,
    private val tag: String
) : ComposeTransformer<T> {
    private val retryCount: AtomicInteger = AtomicInteger()

    private fun handleRetry(error: Throwable): Single<Unit> {
        return if (retryCount.incrementAndGet() <= maxRetryTimes) {
            onPreRetry(error)
                .doOnComplete { log.w(tag, "Retry stream with exception: ${error.message}, Retry times: ${retryCount.get()}.") }
                .andThen(Single.just(Unit))
        } else {
            Single.error(error)
        }
    }

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.retryWhen { errFlow ->
            errFlow.flatMapSingle(this::handleRetry)
        }
    }

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream.retryWhen { errFlow ->
            errFlow.flatMapSingle(this::handleRetry)
        }
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.retryWhen { errFlow ->
            errFlow.flatMapSingle(this::handleRetry)
        }
    }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream.retryWhen { errFlow ->
            errFlow.flatMapSingle(this::handleRetry)
        }
    }

    override fun apply(upstream: Completable): CompletableSource {
        return upstream.retryWhen { errFlow ->
            errFlow.flatMapSingle(this::handleRetry)
        }
    }
}