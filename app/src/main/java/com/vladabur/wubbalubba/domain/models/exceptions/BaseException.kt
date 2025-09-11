package com.vladabur.wubbalubba.domain.models.exceptions

import com.vladabur.wubbalubba.domain.models.errors.ApiError


sealed class BaseException(open val error: String? = "") : Exception(error)

class ApiErrorException(private val apiError: ApiError?): BaseException(apiError?.toString())
class ConnectionErrorException : BaseException()