import 'package:flutter/services.dart';

import 'BaseRequest.dart';

class OktaSDK {
  static const MethodChannel _channel =
      const MethodChannel('com.sonikro.flutter_okta_sdk');

  bool isInitialized = false;

  Future<void> setup(BaseRequest request) async {
    this.isInitialized = false;
    await _channel.invokeMethod("setup", convertBaseRequestToMap(request));
    this.isInitialized = true;
  }

  Future<void> signIn() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot sign in before initializing Okta SDK");
    }
    await _channel.invokeMethod('signIn');
  }

  Future<void> signOut() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot sign out before initializing Okta SDK");
    }
    await _channel.invokeMethod('signOut');
  }
}
