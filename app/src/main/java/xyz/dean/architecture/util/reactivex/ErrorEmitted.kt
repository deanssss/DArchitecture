package xyz.dean.architecture.util.reactivex

import kotlin.reflect.KClass

/**
 * This annotation is used to indicate what type of exception may be emitted from the stream.
 */
@Suppress("unused")
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.SOURCE)
annotation class ErrorEmitted(vararg val emittedErrors: KClass<out Throwable>)