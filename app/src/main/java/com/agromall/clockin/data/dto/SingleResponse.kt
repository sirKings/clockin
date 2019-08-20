package com.agromall.clockin.data.dto

import com.google.gson.annotations.SerializedName

data class SingleResponse<T> (
    val message: String,
    val status: Boolean,
    @SerializedName(value = "data", alternate = ["results", "items"])
    val result: T
)