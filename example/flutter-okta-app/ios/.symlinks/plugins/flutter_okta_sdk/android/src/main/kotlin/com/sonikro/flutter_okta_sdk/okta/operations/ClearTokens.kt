package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.clients.AuthClient
import com.okta.oidc.clients.web.WebAuthClient
import com.sonikro.flutter_okta_sdk.okta.entities.OktaClient
import com.sonikro.flutter_okta_sdk.okta.entities.PendingOperation

fun clearTokens() {
    OktaClient.getWebClient().sessionClient.clear()
    OktaClient.getAuthClient().sessionClient.clear()

    PendingOperation.success(true)
}


