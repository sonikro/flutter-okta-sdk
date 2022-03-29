package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.RequestCallback
import com.okta.oidc.net.params.TokenTypeHint
import com.okta.oidc.net.response.IntrospectInfo
import com.okta.oidc.util.AuthorizationException
import com.sonikro.flutter_okta_sdk.okta.entities.Constants
import com.sonikro.flutter_okta_sdk.okta.entities.Errors
import com.sonikro.flutter_okta_sdk.okta.entities.OktaClient
import com.sonikro.flutter_okta_sdk.okta.entities.PendingOperation

fun introspectAccessToken() {
    introspectToken(TokenTypeHint.ACCESS_TOKEN)
}

fun introspectIdToken() {
    introspectToken(TokenTypeHint.ID_TOKEN)
}

fun introspectRefreshToken() {
    introspectToken(TokenTypeHint.REFRESH_TOKEN)
}

fun introspectToken(tokenName: String) {
    var sessionClient = OktaClient.getWebClient().sessionClient
    val tokens = sessionClient.tokens
    val token = when (tokenName) {
        TokenTypeHint.ACCESS_TOKEN -> tokens.accessToken!!
        TokenTypeHint.ID_TOKEN -> tokens.idToken!!
        TokenTypeHint.REFRESH_TOKEN -> tokens.refreshToken!!
        else -> {
            throw Error(Errors.ERROR_TOKEN_TYPE.errorMessage)
        }
    }

    OktaClient.getWebClient().sessionClient.introspectToken(token, tokenName, object : RequestCallback<IntrospectInfo, AuthorizationException> {
        override fun onSuccess(result: IntrospectInfo) {
            val params = mutableMapOf<Any, Any?>()

            //TODO: Create a data type for this
            params[Constants.ACTIVE_KEY] = result.isActive
            params[Constants.TOKEN_TYPE_KEY] = result.tokenType
            params[Constants.SCOPE_KEY] = result.scope
            params[Constants.CLIENT_ID_KEY] = result.clientId
            params[Constants.DEVICE_ID_KEY] = result.deviceId
            params[Constants.USERNAME_KEY] = result.username
            params[Constants.NBF_KEY] = result.nbf
            params[Constants.EXP_KEY] = result.exp
            params[Constants.IAT_KEY] = result.iat
            params[Constants.SUB_KEY] = result.sub
            params[Constants.AUD_KEY] = result.aud
            params[Constants.ISS_KEY] = result.iss
            params[Constants.JTI_KEY] = result.jti
            params[Constants.UID_KEY] = result.uid

            PendingOperation.success(params.toString())
        }

        override fun onError(error: String?, exception: AuthorizationException?) {
            PendingOperation.error(Errors.OKTA_OIDC_ERROR, exception?.errorDescription)
        }
    })
}


