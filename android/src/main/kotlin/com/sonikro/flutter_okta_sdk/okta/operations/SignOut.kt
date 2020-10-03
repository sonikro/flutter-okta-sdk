package com.sonikro.flutter_okta_sdk.okta.operations

import android.app.Activity
import com.okta.oidc.clients.web.WebAuthClient

fun signOut(webClient: WebAuthClient, activity: Activity) {
    webClient.signOutOfOkta(activity)
}