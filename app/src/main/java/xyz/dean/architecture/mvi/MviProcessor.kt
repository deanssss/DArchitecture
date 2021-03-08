package xyz.dean.architecture.mvi

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import xyz.dean.architecture.api.PhraseService
import xyz.dean.architecture.api.core.ApiClient
import xyz.dean.architecture.common.Constant

class MviProcessor {
    private val apiClient = ApiClient.Builder(Constant.API_HOST).build()
    private val phraseService: PhraseService = apiClient.createService(PhraseService::class.java)

    private val initialActionTransformer = ObservableTransformer<MviAction.InitialAction, MviResult> { action ->
        action.flatMap {
            phraseService.getPhraseRx()
                .map { MviResult.InitialResult(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .cast(MviResult::class.java)
                .onErrorReturn { err -> MviResult.ErrorResult(err) }
        }
    }

    private val nextActionTransformer = ObservableTransformer<MviAction.NextClickAction, MviResult> { action ->
        action.flatMap {
            phraseService.getPhraseRx()
                .map { MviResult.NextClickResult(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cast(MviResult::class.java)
                .onErrorReturn { err -> MviResult.ErrorResult(err) }
                .toObservable()
        }
    }

    val process = ObservableTransformer<MviAction, MviResult> { actions ->
        actions.publish { shared ->
            Observable.merge(
                shared.ofType(MviAction.InitialAction::class.java).compose(initialActionTransformer),
                shared.ofType(MviAction.NextClickAction::class.java).compose(nextActionTransformer)
            )
        }
    }
}