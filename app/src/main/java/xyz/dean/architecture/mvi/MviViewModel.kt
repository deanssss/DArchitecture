package xyz.dean.architecture.mvi

import com.uber.autodispose.autoDispose
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import xyz.dean.architecture.util.reactivex.notOfType

class MviViewModel : LifecycleScopeProvider<MviViewModel.LifeEvent> {
    private val processor = MviProcessor()
    private val intentSubject: PublishSubject<MviIntent> = PublishSubject.create()

    private val intentFilter = ObservableTransformer<MviIntent, MviIntent> { intents ->
        intents.publish { Observable.merge(
            it.ofType(MviIntent.InitialIntent::class.java).take(1),
            it.notOfType(MviIntent.InitialIntent::class.java)
        ) }
    }
    private val reducer = BiFunction<MviViewState, MviResult, MviViewState> { prev, result ->
        when (result) {
            is MviResult.InitialResult -> MviViewState.Success(result.phraseInfo)
            is MviResult.NextClickResult -> MviViewState.Success(result.phraseInfo)
            is MviResult.ErrorResult -> MviViewState.Error(result.err)
        }
    }
    private val stateObservable: Observable<MviViewState> = compose()

    fun processIntents(intents: Observable<MviIntent>) {
        intents.autoDispose(this).subscribe(intentSubject)
    }

    fun compose(): Observable<MviViewState> =
        intentSubject.compose(intentFilter)
            .map(this::actionFromIntent)
            .compose(processor.process)
            .scan<MviViewState>(MviViewState.Idle, reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)

    fun state() = stateObservable

    private fun actionFromIntent(intent: MviIntent): MviAction =
        when (intent) {
            MviIntent.InitialIntent -> MviAction.InitialAction
            MviIntent.NextIntent -> MviAction.NextClickAction
        }

    private val lifecycleEvents = BehaviorSubject.createDefault(LifeEvent.CREATED)
    override fun lifecycle(): Observable<LifeEvent> = lifecycleEvents.hide()
    override fun peekLifecycle(): LifeEvent? = lifecycleEvents.value
    override fun correspondingEvents(): CorrespondingEventsFunction<LifeEvent> = CORRESPONDING_EVENTS

    enum class LifeEvent {
        CREATED, CLEARED
    }

    companion object {
        private val CORRESPONDING_EVENTS = CorrespondingEventsFunction<LifeEvent> { event ->
            when (event) {
                LifeEvent.CREATED -> LifeEvent.CLEARED
                else -> throw LifecycleEndedException("Cannot bind to ViewModel lifecycle after onCleared.")
            }
        }
    }
}