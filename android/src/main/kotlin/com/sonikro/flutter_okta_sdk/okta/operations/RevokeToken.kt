package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.RequestCallback
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.net.params.TokenTypeHint
import com.okta.oidc.util.AuthorizationException
import com.sonikro.flutter_okta_sdk.okta.entities.Errors

fun revokeAccessToken(webClient: WebAuthClient){
    revokeToken(TokenTypeHint.ACCESS_TOKEN,webClient)
}
fun revokeIdToken(webClient: WebAuthClient){
    revokeToken(TokenTypeHint.ID_TOKEN,webClient)
}

fun revokeRefreshToken(webClient: WebAuthClient){
    revokeToken(TokenTypeHint.REFRESH_TOKEN, webClient)
}

private fun revokeToken( tokenName:String, webClient: WebAuthClient): Boolean {
    var sessionClient = webClient.sessionClient
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
                    promise.resolve(result)
                }

                override fun onError(msg: String, error: AuthorizationException) {
                    promise.reject(OktaSdkError.OKTA_OIDC_ERROR.getErrorCode(), error.localizedMessage, error)
                }
            })
}


