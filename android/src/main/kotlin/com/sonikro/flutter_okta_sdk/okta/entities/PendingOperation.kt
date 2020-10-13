package com.sonikro.flutter_okta_sdk.okta.entities

import io.flutter.plugin.common.MethodChannel

object PendingOperation{
    var hasPendingOperation: Boolean = false
    var method: String? = null
    var result: MethodChannel.Result? = null

    fun init(method: String, result: MethodChannel.Result) {
        if (hasPendingOperation) {
            throw IllegalStateException(
                    "Concurrent operations detected: " + this.method + ", " + method)
        }
        this.hasPendingOperation = true
        this.method = method
        this.result = result
    }

    fun success(data: Any? = null) {
        if (!hasPendingOperation) {
            throw IllegalStateException("There is no operation pending")
        }

        this.hasPendingOperation = false
        this.method= null
        this.result!!.success(data)
    }

    fun error(error: Errors, message:String? = null, details:String? = null) {
        if (!hasPendingOperation) {
            throw IllegalStateException("There is no operation pending")
        }

        this.hasPendingOperation = false
        this.method= null
        this.result!!.error(
            error.errorCode,
            message ?: error.errorMessage,
            details)
    }
}