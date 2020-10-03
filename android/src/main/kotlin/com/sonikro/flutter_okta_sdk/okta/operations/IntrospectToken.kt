package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.RequestCallback
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.net.params.TokenTypeHint
import com.okta.oidc.net.response.IntrospectInfo
import com.okta.oidc.util.AuthorizationException
import com.sonikro.flutter_okta_sdk.okta.entities.Errors
import com.sonikro.flutter_okta_sdk.okta.entities.OktaSdkConstant

fun introspectAccessToken(webClient: WebAuthClient):IntrospectInfo {
    return introspectToken(TokenTypeHint.ACCESS_TOKEN, webClient)
}

fun introspectIdToken(webClient: WebAuthClient):IntrospectInfo {
    return introspectToken(TokenTypeHint.ID_TOKEN, webClient)
}

fun introspectRefreshToken(webClient: WebAuthClient):IntrospectInfo {
    return introspectToken(TokenTypeHint.REFRESH_TOKEN, webClient)
}

fun introspectToken( tokenName:String,webClient: WebAuthClient): IntrospectInfo {
    var sessionClient = webClient.sessionClient
    val tokens = sessionClient.tokens
    val token = when (tokenName) {
        TokenTypeHint.ACCESS_TOKEN -> tokens.accessToken!!
        TokenTypeHint.ID_TOKEN -> tokens.idToken!!
        TokenTypeHint.REFRESH_TOKEN -> tokens.refreshToken!!
        else -> {
           throw Error(Errors.ERROR_TOKEN_TYPE.errorMessage)
        }
    }

    webClient.sessionClient.introspectToken(token, tokenName, object : RequestCallback<IntrospectInfo, AuthorizationException> {
        override fun onSuccess(result: IntrospectInfo) {
            val params: WritableMap = Arguments.createMap()
            params.putBoolean(OktaSdkConstant.ACTIVE_KEY, result.isActive)
            params.putString(OktaSdkConstant.TOKEN_TYPE_KEY, result.tokenType)
            params.putString(OktaSdkConstant.SCOPE_KEY, result.scope)
            params.putString(OktaSdkConstant.CLIENT_ID_KEY, result.clientId)
            params.putString(OktaSdkConstant.DEVICE_ID_KEY, result.deviceId)
            params.putString(OktaSdkConstant.USERNAME_KEY, result.username)
            params.putInt(OktaSdkConstant.NBF_KEY, result.nbf)
            params.putInt(OktaSdkConstant.EXP_KEY, result.exp)
            params.putInt(OktaSdkConstant.IAT_KEY, result.iat)
            params.putString(OktaSdkConstant.SUB_KEY, result.sub)
            params.putString(OktaSdkConstant.AUD_KEY, result.aud)
            params.putString(OktaSdkConstant.ISS_KEY, result.iss)
            params.putString(OktaSdkConstant.JTI_KEY, result.jti)
            params.putString(OktaSdkConstant.UID_KEY, result.uid)
            promise.resolve(params)
        }

        override fun onError(e: String, error: AuthorizationException) {
            promise.reject(OktaSdkError.OKTA_OIDC_ERROR.getErrorCode(), error.localizedMessage, error)
        }
    }
    )
}


