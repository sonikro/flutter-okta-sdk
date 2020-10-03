package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.clients.web.WebAuthClient

fun getIdToken(webClient: WebAuthClient): String? {

    var sessionClient = webClient.sessionClient
    var tokens = sessionClient.tokens

    return tokens.idToken
}
