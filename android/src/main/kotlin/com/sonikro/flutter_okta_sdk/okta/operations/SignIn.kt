package com.sonikro.flutter_okta_sdk.okta.operations

import android.app.Activity
import com.okta.oidc.clients.web.WebAuthClient
import com.sonikro.flutter_okta_sdk.okta.entities.PendingOperation

fun signIn(webClient: WebAuthClient, activity: Activity) {
    webClient.signIn(activity, null)
    PendingOperation.success()
}