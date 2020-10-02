package com.sonikro.flutter_okta_sdk

enum class Errors(val errorCode: String) {
    SIGNOUT_ERROR_CODE("signout_failed"),
    SIGNIN_ERROR_CODE("signin_failed"),
    CREATECONFIG_ERROR_CODE("createconfig_failed"),
    ACTIVITY_NOT_DEFINED("activity_null"),
    WEB_CLIENT_NOT_DEFINED("webclient_null"),
    NOT_IMPLEMENTED_ERROR_CODE("signin_client_null")
}