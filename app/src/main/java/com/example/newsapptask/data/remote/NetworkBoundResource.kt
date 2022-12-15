package com.example.newsapptask.data.remote

import com.example.newsapptask.common.Resource
import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))
        try {
            saveFetchResult(fetch())
            query().map { Resource.Success(it) }
        } catch (th: Throwable) {
            query().map { Resource.Error(th.message.toString(), it) }
        }
    }else{
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}