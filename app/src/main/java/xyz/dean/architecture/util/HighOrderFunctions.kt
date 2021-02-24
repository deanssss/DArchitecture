package xyz.dean.architecture.util

inline fun <T> T.onNull(action: () -> Unit): T {
    this ?: action()
    return this
}

inline fun <T> T.noNull(action: (T) -> Unit): T {
    if (this != null) action(this)
    return this
}