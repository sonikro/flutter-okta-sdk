package com.sonikro.flutter_okta_sdk.okta.entities

import com.okta.oidc.OIDCConfig
import com.okta.oidc.clients.AuthClient
import com.okta.oidc.clients.web.WebAuthClient

data class OktaConfig(
        var config: OIDCConfig,
        var webClient: WebAuthClient,
        var authClient: AuthClient
)