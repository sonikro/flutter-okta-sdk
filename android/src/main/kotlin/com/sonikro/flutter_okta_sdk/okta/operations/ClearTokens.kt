package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.clients.AuthClient
import com.okta.oidc.clients.web.WebAuthClient

fun clearTokens(webClient: WebAuthClient, authClient: AuthClient) {
    webClient.sessionClient.clear()
    authClient.sessionClient.clear()
}


