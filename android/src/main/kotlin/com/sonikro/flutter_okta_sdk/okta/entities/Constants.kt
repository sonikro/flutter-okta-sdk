package com.sonikro.flutter_okta_sdk.okta.entities

internal class Constants private constructor() {
    companion object {
        /** ======== Keys ========  */
        const val RESOLVE_TYPE_KEY = "resolve_type"
        const val ACCESS_TOKEN_KEY = "access_token"
        const val ID_TOKEN_KEY = "id_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
        const val AUTHENTICATED_KEY = "authenticated"
        const val ERROR_CODE_KEY = "error_code"
        const val ERROR_MSG_KEY = "error_message"
        const val ACTIVE_KEY = "active"
        const val TOKEN_TYPE_KEY = "token_type"
        const val SCOPE_KEY = "scope"
        const val CLIENT_ID_KEY = "client_id"
        const val DEVICE_ID_KEY = "device_id"
        const val USERNAME_KEY = "username"
        const val NBF_KEY = "nbf"
        const val EXP_KEY = "exp"
        const val IAT_KEY = "iat"
        const val SUB_KEY = "sub"
        const val AUD_KEY = "aud"
        const val ISS_KEY = "iss"
        const val JTI_KEY = "jti"
        const val UID_KEY = "uid"

        /** ======== Values ========  */
        const val AUTHORIZED = "authorized"
        const val SIGNED_OUT = "signed_out"
        const val CANCELLED = "cancelled"
    }

    init {
        throw AssertionError()
    }
}