package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.clients.web.WebAuthClient
import com.sonikro.flutter_okta_sdk.okta.entities.Errors

fun getAccessToken(webClient: WebAuthClient): String? {

    var sessionClient = webClient.sessionClient
    var tokens = sessionClient.tokens

    return tokens.accessToken
}
