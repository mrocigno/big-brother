package br.com.mrocigno.bigbrother.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val responseFlowScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
private val collectFlowScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

class MutableResponseFlow<T> : ResponseFlow<T>() {

    fun sync(flow: Flow<T>) = sync(flow) { it }

    fun <R> sync(flow: Flow<R>, map: suspend (input: R) -> T?) = sync(flow, map) { e ->
        stateFlow.value = RequestState.Error(e)
    }

    fun <R> sync(
        flow: Flow<R>,
        map: suspend (input: R) -> T?,
        onError: suspend FlowCollector<R>.(cause: Throwable) -> Unit
    ) = sync(flow, map, onError, responseFlowScope)

    fun <R> sync(
        flow: Flow<R>,
        map: suspend (input: R) -> T?,
        onError: suspend FlowCollector<R>.(cause: Throwable) -> Unit,
        scope: CoroutineScope
    ) = scope.launch {
        stateFlow.value = RequestState.Loading
        flow
            .catch(onError)
            .map(map)
            .collect {
                if (it == null || (it is List<*> && it.isEmpty())) {
                    stateFlow.value = RequestState.Empty
                } else {
                    stateFlow.value = RequestState.Success(it)
                }
            }
    }
}

open class ResponseFlow<T> {

    protected val stateFlow: MutableStateFlow<RequestState<T>> = MutableStateFlow(RequestState.Initial)

    var value: T? = null

    fun collect(
        wrapper: ResponseFlowCollectWrapper<T>.() -> Unit
    ) = collect(collectFlowScope, wrapper)

    fun collect(
        scope: CoroutineScope,
        wrapper: ResponseFlowCollectWrapper<T>.() -> Unit
    ) = scope.launch {
        stateFlow.collect {
            val actions = ResponseFlowCollectWrapper<T>().apply(wrapper)
            when (it) {
                is RequestState.Empty -> {
                    actions.onLoadingState?.invoke(false)
                    actions.onEmptyState?.invoke()
                }
                is RequestState.Loading -> {
                    actions.onLoadingState?.invoke(true)
                }
                is RequestState.Success -> {
                    actions.onLoadingState?.invoke(false)
                    actions.onDataState?.invoke(it.data)
                    value = it.data
                }
                is RequestState.Error -> {
                    actions.onLoadingState?.invoke(false)
                    actions.onErrorState?.invoke(it.t)
                }
                else -> { /* do nothing */ }
            }
        }
    }
}

class ResponseFlowCollectWrapper<T> {
    var onEmptyState: (() -> Unit)? = null
    var onLoadingState: ((isLoading: Boolean) -> Unit)? = null
    var onDataState: ((data: T) -> Unit)? = null
    var onErrorState: ((t: Throwable) -> Unit)? = null

    fun empty(onEmpty: () -> Unit) {
        onEmptyState = onEmpty
    }

    fun loading(onLoading: (isLoading: Boolean) -> Unit) {
        onLoadingState = onLoading
    }

    fun data(onData: (data: T) -> Unit) {
        onDataState = onData
    }

    fun error(onError: (t: Throwable) -> Unit) {
        onErrorState = onError
    }
}

sealed class RequestState<out T> {
    object Initial : RequestState<Nothing>()
    object Empty : RequestState<Nothing>()
    object Loading : RequestState<Nothing>()
    class Success<T>(val data: T) : RequestState<T>()
    class Error(val t: Throwable) : RequestState<Nothing>()
}
