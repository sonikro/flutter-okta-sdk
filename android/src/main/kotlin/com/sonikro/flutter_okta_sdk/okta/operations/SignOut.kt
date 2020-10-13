package com.sonikro.flutter_okta_sdk.okta.operations

import android.app.Activity
import com.okta.oidc.clients.web.WebAuthClient
import com.sonikro.flutter_okta_sdk.okta.entities.PendingOperation

fun signOut(webClient: WebAuthClient, activity: Activity) {
    webClient.signOutOfOkta(activity)
    PendingOperation.success()
}