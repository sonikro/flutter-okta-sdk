package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.clients.web.WebAuthClient
import com.sonikro.flutter_okta_sdk.okta.entities.Errors
import com.sonikro.flutter_okta_sdk.okta.entities.OktaClient
import com.sonikro.flutter_okta_sdk.okta.entities.PendingOperation

fun getAccessToken() {

    var sessionClient = OktaClient.getWebClient().sessionClient
    var tokens = sessionClient.tokens

    if (tokens.accessToken == null) {
        PendingOperation.error(Errors.NO_ACCESS_TOKEN)
    } else {
        PendingOperation.success(tokens.accessToken)
    }
}
