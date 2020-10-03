package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.RequestCallback
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.net.response.UserInfo
import com.okta.oidc.util.AuthorizationException


fun getUser(webClient: WebAuthClient): String {

    val sessionClient = webClient.sessionClient
    sessionClient.getUserProfile(object : RequestCallback<UserInfo, AuthorizationException> {
        override fun onSuccess(result: UserInfo) {
            promise.resolve(result.toString())
        }

        override fun onError(msg: String, error: AuthorizationException) {
            promise.reject(OktaSdkError.OKTA_OIDC_ERROR.getErrorCode(), error.localizedMessage, error)
        }
    })

}


