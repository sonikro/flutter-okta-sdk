package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.RequestCallback
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.net.params.TokenTypeHint
import com.okta.oidc.util.AuthorizationException
import com.sonikro.flutter_okta_sdk.okta.entities.Errors
import com.sonikro.flutter_okta_sdk.okta.entities.OktaClient
import com.sonikro.flutter_okta_sdk.okta.entities.PendingOperation

fun revokeAccessToken() {
    revokeToken(TokenTypeHint.ACCESS_TOKEN)
}

fun revokeIdToken() {
    revokeToken(TokenTypeHint.ID_TOKEN)
}

fun revokeRefreshToken() {
    revokeToken(TokenTypeHint.REFRESH_TOKEN)
}

private fun revokeToken(tokenName: String) {
    var sessionClient = OktaClient.getWebClient().sessionClient
    val tokens = sessionClient.tokens
    val token = when (tokenName) {
        TokenTypeHint.ACCESS_TOKEN -> tokens.accessToken
        TokenTypeHint.ID_TOKEN -> tokens.idToken
        TokenTypeHint.REFRESH_TOKEN -> tokens.refreshToken
        else -> {
            throw Error(Errors.ERROR_TOKEN_TYPE.errorMessage)
        }
    }

    sessionClient.revokeToken(token,
            object : RequestCallback<Boolean, AuthorizationException> {
                override fun onSuccess(result: Boolean) {
                    PendingOperation.success(result)
                }

                override fun onError(error: String?, exception: AuthorizationException?) {
                    PendingOperation.error(Errors.OKTA_OIDC_ERROR, exception?.errorDescription)
                }
            })
}


