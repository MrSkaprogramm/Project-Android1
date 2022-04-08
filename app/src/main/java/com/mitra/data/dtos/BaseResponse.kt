package com.mitra.data.dtos

open class BaseResponse<SUCCESS, ERROR : ErrorDto>(
    val success: SUCCESS?,
    val error: ERROR?
)

open class BaseReposnseWithError<SUCCESS>(
    success: SUCCESS?,
    error: ErrorDto?
) : BaseResponse<SUCCESS, ErrorDto>(success, error)

class SuccessResponse(
    success: SuccessDto,
    error: ErrorDto?
) : BaseReposnseWithError<SuccessDto>(success, error)

class SuccessDto(val success: Boolean)

open class ErrorDto(
    val logs: String,
    val code: Int
)