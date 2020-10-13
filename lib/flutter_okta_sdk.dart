import 'package:flutter/services.dart';

import 'BaseRequest.dart';

class OktaSDK {
  static const MethodChannel _channel =
      const MethodChannel('com.sonikro.flutter_okta_sdk');

  bool isInitialized = false;

  Future<void> createConfig(BaseRequest request) async {
    this.isInitialized = false;
    await _channel.invokeMethod(
        "createConfig", convertBaseRequestToMap(request));
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

  Future<String> getUser() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot get user before initializing Okta SDK");
    }
    return await _channel.invokeMethod('getUser');
  }

  Future<bool> isAuthenticated() async {
    if (this.isInitialized == false) {
      throw Exception(
          "Cannot check authentication before initializing Okta SDK");
    }
    var a = await _channel.invokeMethod('isAuthenticated');
    return a;
  }
}
