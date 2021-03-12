package xyz.dean.architecture.util.reactivex

import android.annotation.SuppressLint
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleTransformer
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import xyz.dean.architecture.BuildConfig
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object RxHttp {
    private val unsafeManager: X509TrustManager = object : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) { }
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) { }
        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }

    private val unsafeSSLSocketFactory = kotlin.runCatching {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(unsafeManager), SecureRandom())
        sslContext.socketFactory
    }.onFailure { log.e("RxHttp", "Create SSLSocketFactory failed!", it) }
            .getOrDefault(null)

    private val debugOrStaging = BuildConfig.DEBUG
    private val releaseOkHttpClient: OkHttpClient = OkHttpClient.Builder().build()
    private val debugOkHttpClient: OkHttpClient = OkHttpClient.Builder().applyDebugSettings().build()
    private val defaultOkHttpClient = if (debugOrStaging) debugOkHttpClient else releaseOkHttpClient

    @JvmStatic
    fun httpRequest(
            request: Request,
            clientBuilderSupplier: () -> OkHttpClient.Builder
    ): Single<Response> {
        val builder = clientBuilderSupplier()
                .apply { if (debugOrStaging) applyDebugSettings() }
        return httpRequest(request, builder.build())
    }

    @JvmStatic
    @JvmOverloads
    fun httpRequest(
            request: Request,
            client: OkHttpClient = defaultOkHttpClient
    ): Single<Response> {
        return Single.create { emitter ->
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    emitter.onSuccess(response)
                } else {
                    throw RuntimeException("HttpRequestError: $response")
                }
            } catch (e: Exception) {
                val emitted = emitter.tryOnError(e)
                if (!emitted) {
                    log.w("RxHttp", "Error not emitted:", e)
                }
            }
        }
    }

    @JvmStatic
    fun httpRequest(
            request: Request,
            clientBuilderSupplier: () -> OkHttpClient.Builder,
            responseProcessor: (SingleEmitter<Response>, Response) -> Unit
    ): Single<Response> {
        val builder = clientBuilderSupplier()
                .apply { if (debugOrStaging) applyDebugSettings() }
        return Single.create { emitter ->
            try {
                val response = builder.build().newCall(request).execute()
                responseProcessor(emitter, response)
            } catch (e: Exception) {
                val emitted = emitter.tryOnError(e)
                if (!emitted) {
                    log.w("RxHttp","Error not emitted:", e)
                }
            }
        }
    }

    private fun OkHttpClient.Builder.applyDebugSettings(): OkHttpClient.Builder {
        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        if (unsafeSSLSocketFactory != null) {
            sslSocketFactory(unsafeSSLSocketFactory, unsafeManager)
        }
        hostnameVerifier(HostnameVerifier { _, _ -> true })
        return this
    }

    @JvmStatic
    fun responseToString(): SingleTransformer<Response, String> = SingleTransformer { upstream ->
        upstream.map { response ->
            response.body
                    .runCatching { String(this!!.bytes(), StandardCharsets.UTF_8) }
                    .getOrDefault("")
        }
    }
}