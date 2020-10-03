# Flutter Okta SDK

The Flutter Okta SDK library makes it easy to add authentication to your Flutter app.
This library is a wrapper around [Okta OIDC Android](https://github.com/okta/okta-oidc-android) and [Okta OIDC iOS](https://github.com/okta/okta-oidc-ios).


This library follows the current best practice for native apps using:

* [OAuth 2.0 Authorization Code Flow](https://tools.ietf.org/html/rfc6749#section-1.3.1)
* [Proof Key for Code Exchange (PKCE)](https://tools.ietf.org/html/rfc763

This library also exposes APIs to interact with [Authentication API](https://developer.okta.com/docs/api/resources/authn) directly to implement native UI for authentication.

## Todos

This library is under construction. These are the next steps:

### Android
    ~~setup~~
    ~~signIn~~
    ~~singOut~~
    getAuthClient
    authenticate
    isAuthenticated
    getAccessToken
    getIdToken
    getUser
    getUserFromIdToken
    revokeAccessToken
    revokeIdToken
    revokeRefreshToken
    clearTokens
    introspectAccessToken
    introspectIdToken
    introspectRefreshToken
    refreshTokens

### iOS

    setup
    signIn
    singOut
    getAuthClient
    authenticate
    isAuthenticated
    getAccessToken
    getIdToken
    getUser
    getUserFromIdToken
    revokeAccessToken
    revokeIdToken
    revokeRefreshToken
    clearTokens
    introspectAccessToken
    introspectIdToken
    introspectRefreshToken
    refreshTokens

## Prerequisites

* If you do not already have a **Developer Edition Account**, you can create one at [https://developer.okta.com/signup/](https://developer.okta.com/signup/).

## Add an OpenID Connect Client in Okta

In Okta, applications are OpenID Connect clients that can use Okta Authorization servers to authenticate users.
Your Okta Org already has a default authorization server, so you just need to create an OIDC client that will use it.

* Log into the Okta Developer Dashboard, click **Applications** then **Add Application**.
* Choose **Native** as the platform, then submit the form the default values, which should look similar to this:

| Setting             | Value                                        |
| ------------------- | -------------------------------------------- |
| App Name            | My Native App                                |
| Login redirect URIs | com.mynativeapp:/                            |
| Grant Types Allowed | Authorization Code, Refresh Token            |

After you have created the application there are two more values you will need to gather:

| Setting       | Where to Find                                                                  |
| ------------- | ------------------------------------------------------------------------------ |
| Client ID     | In the applications list, or on the "General" tab of a specific application.   |
| Org URL       | On the home screen of the developer dashboard, in the upper right.             |

**Note:** *As with any Okta application, make sure you assign Users or Groups to the OpenID Connect Client. Otherwise, no one can use it.*

These values will be used in your Flutter application to setup the OpenID Connect flow with Okta.

## Getting started

**TODO: (Need to publish this lib in pub .dev)**

Add this to your package's pubspec.yaml file:

´´´
dependencies:
  flutter_okta_sdk: ^0.1.0
´´´

You can install packages from the command line:

with Flutter:
```
flutter pub get
```

Now in your Dart code, you can use:

import 'package:flutter_okta_sdk/flutter_okta_sdk.dart';

### Setup Android

For Android, there is one steps that you must take:
1. [Add a redirect scheme to your project.](#add-redirect-scheme)

#### Add redirect scheme

1. Defining a redirect scheme to capture the authorization redirect. In `android/app/build.gradle`, under `android` -> `defaultConfig`, add:
```
manifestPlaceholders = [
  appAuthRedirectScheme: 'com.sampleapplication'
]
```

2. Make sure your `minSdkVersion` is `19`.

### Setup iOS
**TODO: (Need to do the iOS bridge)**

## Usage

You will need the values from the OIDC client that you created in the previous step to set up.
You will also need to know your Okta Org URL, which you can see on the home page of the Okta Developer console.

Before calling any other method, it is important that you call `setup` to set up the configuration properly on the native modules.

**TODO: Add an example**

### `setup`

This method will create a configured client on the native modules.

**Note**: `requireHardwareBackedKeyStore` is a configurable setting only on android devices.
If you're a developer testing on android emulators, set this field to `false`.

### `signin`

**TODO: Add an example**

### `signout`

**TODO: Add an example**




