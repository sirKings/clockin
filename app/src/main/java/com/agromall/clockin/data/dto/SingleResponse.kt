package com.agromall.clockin.data.dto

data class SingleResponse<T> (
    val message: String,
    val status: Boolean,
    val result: T
)