package com.sonikro.flutter_okta_sdk.okta.entities

import io.flutter.plugin.common.MethodChannel

class PendingOperation internal constructor(val method: String, val result: MethodChannel.Result)