package com.vladabur.wubbalubba.domain.usecases.base

import com.vladabur.wubbalubba.domain.models.exceptions.ApiErrorException
import com.vladabur.wubbalubba.domain.models.exceptions.BaseException
import com.vladabur.wubbalubba.domain.models.exceptions.ConnectionErrorException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

abstract class BaseUseCase<PARAMS, RESULT> {
    operator fun invoke(
        coroutineScope: CoroutineScope,
        params: PARAMS?,
        result: ResultCallbacks<RESULT>
    ): Job {
        return coroutineScope.launch {
            withContext(Dispatchers.Main) {
                result.onLoading?.invoke(true)
                try {
                    val resultOfWork = remoteWork(params)
                    result.onSuccess?.invoke(resultOfWork)
                } catch (e: Exception) {
                    when (e) {
                        is ConnectionErrorException -> result.onConnectionError?.invoke(e)
                        is ApiErrorException -> result.onError?.invoke(e)
                        else -> result.onUnexpectedError?.invoke(e)
                    }
                } finally {
                    result.onLoading?.invoke(false)
                }
            }
        }
    }

    abstract suspend fun remoteWork(params: PARAMS?): RESULT
}

class ResultCallbacks<T>(
    val onSuccess: ((T) -> Unit)? = null,
    val onLoading: ((Boolean) -> Unit)? = null,
    val onError: ((BaseException) -> Unit)? = null,
    val onConnectionError: ((BaseException) -> Unit)? = null,
    val onUnexpectedError: ((Throwable) -> Unit)? = null,
)

