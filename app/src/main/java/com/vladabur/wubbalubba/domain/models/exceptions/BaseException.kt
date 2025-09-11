package com.vladabur.wubbalubba.domain.models.exceptions

import com.vladabur.wubbalubba.domain.models.errors.ApiError


sealed class BaseException(open val error: String? = "") : Exception(error)

class ApiErrorException(val apiError: ApiError?): BaseException(apiError?.error)
class ConnectionErrorException : BaseException()