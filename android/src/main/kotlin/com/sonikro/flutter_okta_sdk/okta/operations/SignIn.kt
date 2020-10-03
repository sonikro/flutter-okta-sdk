package com.sonikro.flutter_okta_sdk.okta.operations

import android.app.Activity
import com.okta.oidc.clients.web.WebAuthClient

fun signIn(webClient: WebAuthClient, activity: Activity) {
    webClient.signIn(activity, null)
}