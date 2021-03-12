package xyz.dean.architecture.util.reactivex

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Pair
import androidx.lifecycle.Lifecycle
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

object RxGlobalActivityLifecycle {
    @JvmStatic
    fun hook(application: Application): Observable<Pair<Activity, Lifecycle.Event>> {
        return ActivityLifecycleObservable(application)
    }
}

private class ActivityLifecycleObservable(val application: Application) : Observable<Pair<Activity, Lifecycle.Event>>() {
    override fun subscribeActual(observer: Observer<in Pair<Activity, Lifecycle.Event>>) {
        val callbacks = Callbacks(observer, application)
        application.registerActivityLifecycleCallbacks(callbacks)
        observer.onSubscribe(callbacks)
    }

    private class Callbacks(
            val observer: Observer<in Pair<Activity, Lifecycle.Event>>,
            val application: Application
    ) : Application.ActivityLifecycleCallbacks, MainThreadDisposable() {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            observer.onNext(Pair.create(activity, Lifecycle.Event.ON_CREATE))
        }

        override fun onActivityStarted(activity: Activity) {
            observer.onNext(Pair.create(activity, Lifecycle.Event.ON_START))
        }

        override fun onActivityResumed(activity: Activity) {
            observer.onNext(Pair.create(activity, Lifecycle.Event.ON_RESUME))
        }

        override fun onActivityPaused(activity: Activity) {
            observer.onNext(Pair.create(activity, Lifecycle.Event.ON_PAUSE))
        }

        override fun onActivityStopped(activity: Activity) {
            observer.onNext(Pair.create(activity, Lifecycle.Event.ON_STOP))
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            observer.onNext(Pair.create(activity, Lifecycle.Event.ON_DESTROY))
        }

        override fun onDispose() {
            application.unregisterActivityLifecycleCallbacks(this)
        }
    }
}