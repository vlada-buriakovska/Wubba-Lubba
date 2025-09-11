package com.vladabur.wubbalubba.data.extensions

import com.google.gson.Gson
import com.vladabur.wubbalubba.data.network.models.error.ApiErrorResponse
import com.vladabur.wubbalubba.domain.models.errors.ApiError
import com.vladabur.wubbalubba.domain.models.exceptions.ApiErrorException
import com.vladabur.wubbalubba.domain.models.exceptions.ConnectionErrorException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

fun Exception.mapToApiErrors(): Throwable {
    when (this) {
        is HttpException -> {
            return if (this.code() == 500) {
                ApiErrorException(apiError = ApiError(error = "Woops! Something happened with server"))
            } else {
                val errorResponse =
                    Gson().fromJson(
                        this.response()?.errorBody()?.string(),
                        ApiErrorResponse::class.java
                    )
                ApiErrorException(ApiErrorResponse.map(errorResponse))
            }
        }

        is UnknownHostException,
        is SocketTimeoutException,
        is ConnectException,
        is TimeoutException -> return ConnectionErrorException()

        else -> return this
    }
}
