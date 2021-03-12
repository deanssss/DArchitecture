package xyz.dean.architecture.util.reactivex.transformer

import io.reactivex.*

interface ComposeTransformer<T>
    : ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T>,
        CompletableTransformer