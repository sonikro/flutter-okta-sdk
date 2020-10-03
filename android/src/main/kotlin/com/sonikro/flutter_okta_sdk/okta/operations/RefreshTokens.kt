package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.RequestCallback
import com.okta.oidc.Tokens
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.util.AuthorizationException
import jdk.nashorn.internal.runtime.regexp.joni.constants.Arguments


fun refreshTokens( webClient: WebAuthClient): Boolean {
    webClient.sessionClient.refreshToken(object : RequestCallback<Tokens, AuthorizationException> {
        override fun onSuccess(result: Tokens) {
            val params: WritableMap = Arguments.createMap()
            params.putString(OktaSdkConstant.ACCESS_TOKEN_KEY, result.accessToken)
            params.putString(OktaSdkConstant.ID_TOKEN_KEY, result.idToken)
            params.putString(OktaSdkConstant.REFRESH_TOKEN_KEY, result.refreshToken)
            promise.resolve(params)
        }

        override fun onError(e: String, error: AuthorizationException) {
            promise.reject(OktaSdkError.OKTA_OIDC_ERROR.getErrorCode(), error.localizedMessage, error)
        }
    })
}


