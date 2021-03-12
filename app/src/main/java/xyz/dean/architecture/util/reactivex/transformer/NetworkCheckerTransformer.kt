package xyz.dean.architecture.util.reactivex.transformer

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import io.reactivex.*
import org.reactivestreams.Publisher

internal class NetworkCheckerTransformer<T>(
        private val application: Application
) : ComposeTransformer<T> {

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return if (!application.anyNetworkConnected()) {
            Observable.error(NoNetworkConnectedException())
        } else {
            upstream
        }
    }

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return if (!application.anyNetworkConnected()) {
            Flowable.error(NoNetworkConnectedException())
        } else {
            upstream
        }
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return if (!application.anyNetworkConnected()) {
            Single.error(NoNetworkConnectedException())
        } else {
            upstream
        }
    }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return if (!application.anyNetworkConnected()) {
            Maybe.error(NoNetworkConnectedException())
        } else {
            upstream
        }
    }

    override fun apply(upstream: Completable): CompletableSource {
        return if (!application.anyNetworkConnected()) {
            Completable.error(NoNetworkConnectedException())
        } else {
            upstream
        }
    }

    private fun Context.anyNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return cm.anyNetworkConnected()
    }

    private fun ConnectivityManager?.anyNetworkConnected(): Boolean {
        return this?.allNetworks
            ?.any { network ->
                getNetworkCapabilities(network)
                    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    ?: false
            } ?: false
    }
}

class NoNetworkConnectedException : Exception("No network connected!")