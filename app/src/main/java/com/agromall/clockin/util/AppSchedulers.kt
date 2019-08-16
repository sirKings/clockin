package com.agromall.clockin.util

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AppSchedulers {
    fun io() = Schedulers.io()
    fun main() = AndroidSchedulers.mainThread()
    fun trampoline() = Schedulers.trampoline()
    fun computation() = Schedulers.computation()
}