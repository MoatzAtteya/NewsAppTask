package com.example.newsapptask.data.remote

import com.example.newsapptask.common.Resource
import kotlinx.coroutines.flow.*

/*
    Reusable inline function which first calls the database
    and get the old saved data *query()*, and second it calls our api and
    get the data *fetch()* then it compare the 2 data,
    if the data are the same -> show cached data.
    else if the data not the same -> enter save *shouldFetch()* ,
    and *saveFetchResult()* which will delete the old data,
    and save the new data to the database.
 */

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
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}