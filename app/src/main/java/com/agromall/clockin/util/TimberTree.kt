package com.agromall.clockin.util

import timber.log.Timber

class TimberTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String? {
        return String.format("C:%s:%s",
            super.createStackElementTag(element),
            element.lineNumber)
    }

}