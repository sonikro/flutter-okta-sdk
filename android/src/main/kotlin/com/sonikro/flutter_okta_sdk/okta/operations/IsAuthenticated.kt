package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.clients.web.WebAuthClient

fun isAuthenticated(webClient: WebAuthClient): Boolean {
    val sessionClient = webClient.sessionClient
    return sessionClient.isAuthenticated
}


