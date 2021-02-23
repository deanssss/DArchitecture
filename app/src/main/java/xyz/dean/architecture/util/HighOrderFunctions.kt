package xyz.dean.architecture.util

inline fun <T> T.onNull(action: () -> Unit) {
    this ?: action()
}

inline fun <T> T.noNull(action: (T) -> Unit) {
    if (this != null) action(this)
}