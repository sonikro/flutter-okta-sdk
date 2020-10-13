package com.sonikro.flutter_okta_sdk.okta.operations

import com.okta.oidc.clients.web.WebAuthClient
import com.sonikro.flutter_okta_sdk.okta.entities.Errors
import com.sonikro.flutter_okta_sdk.okta.entities.OktaClient
import com.sonikro.flutter_okta_sdk.okta.entities.PendingOperation

fun getIdToken() {

    var sessionClient = OktaClient.getWebClient().sessionClient
    var tokens = sessionClient.tokens

    if (tokens.idToken == null) {
        PendingOperation.error(Errors.NO_ID_TOKEN)
    } else {
        PendingOperation.success(tokens.idToken)
    }
}
