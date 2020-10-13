package com.sonikro.flutter_okta_sdk.okta.operations

import android.content.Context
import com.okta.oidc.OIDCConfig
import com.okta.oidc.Okta
import com.okta.oidc.storage.SharedPreferenceStorage
import com.sonikro.flutter_okta_sdk.HttpClientImpl
import com.sonikro.flutter_okta_sdk.okta.entities.OktaClient
import com.sonikro.flutter_okta_sdk.okta.entities.OktaRequestParameters
import com.sonikro.flutter_okta_sdk.okta.entities.PendingOperation

fun createConfig(arguments: Map<String, Any>, context: Context) {
        val params = processOktaRequestArguments(arguments)
        val config = OIDCConfig.Builder()
                .clientId(params.clientId)
                .redirectUri(params.redirectUri)
                .endSessionRedirectUri(params.endSessionRedirectUri)
                .scopes(*params.scopes.toTypedArray())
                .discoveryUri(params.discoveryUri)
                .create()

        val webClient = Okta.WebAuthBuilder()
                .withConfig(config)
                .withContext(context)
                .withStorage(SharedPreferenceStorage(context))
                .withOktaHttpClient(HttpClientImpl(params.userAgentTemplate))
                .setRequireHardwareBackedKeyStore(params.requireHardwareBackedKeyStore)
                .create()

        val authClient = Okta.AuthBuilder()
                .withConfig(config)
                .withContext(context)
                .withStorage(SharedPreferenceStorage(context))
                .withOktaHttpClient(HttpClientImpl(params.userAgentTemplate))
                .setRequireHardwareBackedKeyStore(params.requireHardwareBackedKeyStore)
                .create()

        OktaClient.init(config,webClient,authClient)
        PendingOperation.success(true)
}

private fun processOktaRequestArguments(arguments: Map<String, Any>): OktaRequestParameters {
        return OktaRequestParameters(
                clientId = (arguments["clientId"] as String?)!!,
                discoveryUri = (arguments["discoveryUrl"] as String?)!!,
                endSessionRedirectUri = (arguments["endSessionRedirectUri"] as String?)!!,
                redirectUri = (arguments["redirectUrl"] as String?)!!,
                requireHardwareBackedKeyStore = (arguments["requireHardwareBackedKeyStore"] as Boolean?)
                        ?: false,
                scopes = arguments["scopes"] as ArrayList<String>,
                userAgentTemplate = (arguments["userAgentTemplate"] as String?) ?: ""
        )
}
