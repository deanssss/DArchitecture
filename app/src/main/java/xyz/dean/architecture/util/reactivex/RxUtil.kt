@file:Suppress("unused")

package xyz.dean.architecture.util.reactivex

import android.app.Application
import android.content.Intent
import android.util.Pair
import androidx.fragment.app.FragmentActivity
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.schedulers.Schedulers
import xyz.dean.architecture.util.reactivex.transformer.NetworkCheckerTransformer
import xyz.dean.architecture.util.reactivex.transformer.RetryTransformer
import xyz.dean.architecture.util.reactivex.transformer.SwitchTransformer
import xyz.dean.architecture.util.Log
import xyz.dean.architecture.util.reactivex.transformer.ComposeTransformer
import xyz.dean.architecture.util.reactivex.transformer.NoNetworkConnectedException
import java.util.concurrent.TimeUnit

internal val log: Log by lazy { xyz.dean.architecture.util.log }

object RxUtil {
    const val MAIN = "io.reactivex.android:main"
    const val IO = SchedulerSupport.IO
    const val COMPUTATION = SchedulerSupport.COMPUTATION
    const val CUSTOM = SchedulerSupport.CUSTOM
    const val NONE = SchedulerSupport.NONE

    @JvmStatic
    fun startActivityForResult(activity: FragmentActivity, intent: Intent): Single<Pair<Int?, Intent?>> {
        return Single.create { emitter: SingleEmitter<Pair<Int?, Intent?>> ->
            val transaction = activity.supportFragmentManager.beginTransaction()
            val activityForResultFragment = ForResultFragment(emitter, intent)
            transaction.add(activityForResultFragment, "FragmentForResult")
            transaction.commit()
        }
    }

    @JvmStatic
    @JvmOverloads
    @SchedulerSupport(CUSTOM)
    fun <T> switchThread(
        subscribeOn: Scheduler = Schedulers.io(),
        observableOn: Scheduler = AndroidSchedulers.mainThread()
    ): ComposeTransformer<T> {
        return SwitchTransformer(subscribeOn, observableOn)
    }

    @JvmStatic
    @JvmOverloads
    @SchedulerSupport(NONE)
    fun <T> rxRetry(
        maxRetryTimes: Int = 3,
        onPreRetry: (Throwable) -> Completable = { Completable.complete() },
        tag: String = "RxRetry"
    ): ComposeTransformer<T> {
        return RetryTransformer(maxRetryTimes, onPreRetry, tag)
    }

    @JvmStatic
    @SchedulerSupport(NONE)
    @ErrorEmitted(NoNetworkConnectedException::class)
    fun <T> networkChecker(application: Application): ComposeTransformer<T> {
        return NetworkCheckerTransformer(application)
    }

    @JvmStatic
    @JvmOverloads
    @SchedulerSupport(CUSTOM)
    fun <T> antiShake(
        duration: Long = 500,
        unit: TimeUnit = TimeUnit.MILLISECONDS,
        scheduler: Scheduler = Schedulers.computation()
    ): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream -> upstream.throttleFirst(duration, unit, scheduler) }
    }
}

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any, U : Any> Observable<T>.notOfType(clazz: Class<U>): Observable<T> {
    return filter { !clazz.isInstance(it) }
}