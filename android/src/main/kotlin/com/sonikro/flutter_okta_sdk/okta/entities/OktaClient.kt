package com.sonikro.flutter_okta_sdk.okta.entities

import com.okta.oidc.OIDCConfig
import com.okta.oidc.clients.AuthClient
import com.okta.oidc.clients.web.WebAuthClient

object OktaClient {
    var isInitilized: Boolean = false
    private var config: OIDCConfig? = null
    private var webClient: WebAuthClient? = null
    private var authClient: AuthClient? = null

    fun init(config: OIDCConfig, webClient: WebAuthClient, authClient: AuthClient) {
        this.config = config
        this.webClient = webClient
        this.authClient = authClient
        isInitilized = true
    }

    fun getConfig(): OIDCConfig {
        if (!isInitilized) {
            throw IllegalStateException(Errors.NOT_CONFIGURED.errorMessage)
        }
        return this.config!!
    }

    fun getWebClient(): WebAuthClient {
        if (!isInitilized) {
            throw IllegalStateException(Errors.NOT_CONFIGURED.errorMessage)
        }
        return this.webClient!!
    }

    fun getAuthClient(): AuthClient {
        if (!isInitilized) {
            throw IllegalStateException(Errors.NOT_CONFIGURED.errorMessage)
        }
        return this.authClient!!
    }

}