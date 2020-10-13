package xyz.dean.architecture.mvp

import xyz.dean.architecture.api.PhraseInfo
import xyz.dean.architecture.mvp.base.BasePresenter
import xyz.dean.architecture.mvp.base.BaseView

interface MvpContract {
    interface Presenter : BasePresenter {
        fun getPhrase()
    }

    interface View : BaseView<Presenter> {
        fun showPhrase(phraseInfo: PhraseInfo)
        fun showErrorMsg(msg: String, error: Throwable)
    }
}