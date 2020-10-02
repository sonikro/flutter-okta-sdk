package com.sonikro.flutter_okta_sdk

import io.flutter.plugin.common.MethodChannel

class PendingOperation internal constructor(val method: String, val result: MethodChannel.Result)