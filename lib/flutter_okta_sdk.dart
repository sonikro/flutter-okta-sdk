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
    return await _channel.invokeMethod('signIn');
  }

  Future<void> signOut() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot sign out before initializing Okta SDK");
    }
    await _channel.invokeMethod('signOut');
  }

  Future<String?> getUser() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot get user before initializing Okta SDK");
    }
    return await _channel.invokeMethod('getUser');
  }

  Future<bool?> isAuthenticated() async {
    if (this.isInitialized == false) {
      throw Exception(
          "Cannot check authentication before initializing Okta SDK");
    }
    return await _channel.invokeMethod('isAuthenticated');
  }

  Future<String?> getAccessToken() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot get access token before initializing Okta SDK");
    }
    return await _channel.invokeMethod('getAccessToken');
  }

  Future<String?> getIdToken() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot get id token before initializing Okta SDK");
    }
    return await _channel.invokeMethod('getIdToken');
  }

  Future<bool?> revokeAccessToken() async {
    if (this.isInitialized == false) {
      throw Exception(
          "Cannot revoke access token before initializing Okta SDK");
    }
    return await _channel.invokeMethod('revokeAccessToken');
  }

  Future<bool?> revokeIdToken() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot revoke id token before initializing Okta SDK");
    }
    return await _channel.invokeMethod('revokeIdToken');
  }

  Future<bool?> revokeRefreshToken() async {
    if (this.isInitialized == false) {
      throw Exception(
          "Cannot revoke refresh token before initializing Okta SDK");
    }
    return await _channel.invokeMethod('revokeRefreshToken');
  }

  Future<bool?> clearTokens() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot clear tokens before initializing Okta SDK");
    }
    return await _channel.invokeMethod('clearTokens');
  }

  Future<String?> introspectAccessToken() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot introspect before initializing Okta SDK");
    }
    return await _channel.invokeMethod('introspectAccessToken');
  }

  Future<String?> introspectIdToken() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot introspect before initializing Okta SDK");
    }
    return await _channel.invokeMethod('introspectIdToken');
  }

  Future<String?> introspectRefreshToken() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot introspect before initializing Okta SDK");
    }
    return await _channel.invokeMethod('introspectRefreshToken');
  }

  Future<String?> refreshTokens() async {
    if (this.isInitialized == false) {
      throw Exception("Cannot refresh tokens before initializing Okta SDK");
    }
    return await _channel.invokeMethod('refreshTokens');
  }
}
