package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.RequestCallback
import com.okta.oidc.Tokens
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.util.AuthorizationException
import com.sonikro.flutter_okta_sdk.okta.entities.Constants
import com.sonikro.flutter_okta_sdk.okta.entities.Errors
import com.sonikro.flutter_okta_sdk.okta.entities.OktaClient
import com.sonikro.flutter_okta_sdk.okta.entities.PendingOperation

//import jdk.nashorn.internal.runtime.regexp.joni.constants.Arguments


fun refreshTokens() {
    OktaClient.getWebClient().sessionClient.refreshToken(object : RequestCallback<Tokens, AuthorizationException> {
        override fun onSuccess(result: Tokens) {
            val params = mutableMapOf<Any, Any?>()
            params[Constants.ACCESS_TOKEN_KEY] = result.accessToken
            params[Constants.ID_TOKEN_KEY] = result.idToken
            params[Constants.REFRESH_TOKEN_KEY] = result.refreshToken
            PendingOperation.success(params.toString())
        }

        override fun onError(error: String?, exception: AuthorizationException?) {
            PendingOperation.error(Errors.OKTA_OIDC_ERROR, exception?.errorDescription)
        }
    })
}


