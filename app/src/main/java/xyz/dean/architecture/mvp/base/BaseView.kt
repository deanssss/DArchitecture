package xyz.dean.architecture.mvp.base

interface BaseView<T : BasePresenter> {
    fun setPresenter(presenter: T)
}