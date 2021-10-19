# OktaJWT

[![Version](https://img.shields.io/cocoapods/v/OktaJWT.svg?style=flat)](http://cocoapods.org/pods/OktaJWT)
[![License](https://img.shields.io/cocoapods/l/OktaJWT.svg?style=flat)](http://cocoapods.org/pods/OktaJWT)
[![Platform](https://img.shields.io/cocoapods/p/OktaJWT.svg?style=flat)](http://cocoapods.org/pods/OktaJWT)
[![Carthage compatible](https://img.shields.io/badge/Carthage-compatible-4BC51D.svg?style=flat)](https://github.com/Carthage/Carthage)
[![Platform](https://img.shields.io/badge/swift-4.2-green.svg?style=flat)](http://cocoapods.org/pods/OktaJWT)
[![Platform](https://img.shields.io/badge/swift-5.0-green.svg?style=flat)](http://cocoapods.org/pods/OktaJWT)

## Overview

This library handles decoding and validating a JSON Web Token (JWT) issued by an Okta authorization server. It provides an easy-to-use and customizable interface for ID Token validation based on [OIDC 3.1.3.7](http://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation) for iOS applications.

## Installation

### Cocoapods

OktaJWT is available through [CocoaPods](http://cocoapods.org). To install it, simply add the following line to your Podfile:

```ruby
pod 'OktaJWT'
```

### Carthage

To integrate this SDK into your Xcode project using [Carthage](https://github.com/Carthage/Carthage), specify it in your Cartfile:

```ruby
github "okta/okta-ios-jwt"
```

## Usage

This library supports **validating** JWTs by extending the [JSONWebToken](https://github.com/kreactive/JSONWebToken) Swift library. By default, it will fetch the public keys from the OAuth 2.0 `/keys` endpoint of the specified authorization server, validate the JWT signature, and verify the token against given assertions.

First, create a dictionary of verification options and instantiate the `OktaJWTValidator`:

```swift
let options = [
  "issuer": "https://{yourOktaDomain}.com/oauth2/default,
  "audience": "{aud}", // More info below
  ...
] as [String: Any]

let validator = OktaJWTValidator(options)
```

Finally, check to see if the JWT is valid:

```swift
let jwtString = "ey...."

do {
  let valid = try validator.isValid(jwtString)
  print("Valid: \(valid)")
} catch let error {
  print("Error: \(error)")
}
```

### `idToken` Validation

When using OpenID Connect as an authentication mechinism, it is important to verify the `idToken` returned from the `/token` endpoint. To ensure the token is valid, include the following in your verification options:

- `issuer`: OAuth 2.0 [authorization server](https://developer.okta.com/authentication-guide/implementing-authentication/set-up-authz-server) minting the `idToken`.
- `audience`: The `clientID` of your OpenID Connect Application. See [Implementing Authentication - Auth Code Flow (Okta)](https://developer.okta.com/authentication-guide/implementing-authentication/auth-code-pkce) for more information.
- `exp`: The JWT hasn't expired.
- `iat`: The JWT was not issued in the future.
- `nonce`: Cryptographic string generated at the time of authorization.

> To learn more about the verification cases and Okta's tokens, take a look at [Working with OAuth 2.0 Tokens](https://developer.okta.com/authentication-guide/tokens/validating-id-tokens).

```swift
let options = [
  "issuer": "https://{yourOktaDomain}.com/oauth2/default",
  "audience": "0abc123..",
  "exp": true,
  "iat": true,
  "leeway": 3000, // allow ~5 minutes for clock drift (exp and iat),
  "nonce": "1a2b3c4d..."
] as [String: Any]

let validator = OktaJWTValidator(options)

let idToken = "ey..."

do {
  let valid = try validator.isValid(idToken)
  print("Valid: \(valid)")
} catch let error {
  // Misc Error: {error}
}
```

To ensure proper error handling, you can catch, handle, and recover from specific errors:

```swift
do {
  let valid = try validator.isValid(idToken)
  print("Valid: \(valid)")
} catch OktaJWTVerificationError.malformedJWT {
  // Malformed idToken -> "ey.xx"
} catch OktaJWTVerificationError.nonSupportedAlg(let algType) {
  // Algorithm type {algType} not supported
} catch OktaJWTVerificationError.invalidIssuer {
  // idToken issuer != given issuer
} catch OktaJWTVerificationError.invalidAudience {
  // idToken audience != given audience
} catch OktaJWTVerificationError.invalidSignature {
  // Invalid signature
} catch OktaJWTVerificationError.expiredJWT {
  // idToken expired!
} catch OktaJWTVerificationError.issuedInFuture {
  // idToken issued in the future
} catch OktaJWTVerificationError.invalidNonce {
  // Invalid nonce
} catch let error {
  // Misc Error: {error}
}
```

### Custom Claim Validation

You can ask the verifier to assert a custom set of claims, provided that it can be validated as a String.

```swift
let options = [
  "issuer": "https://{yourOktaDomain}.com/oauth2/default",
  "audience": "0abc123..",
  "exp": true,
  "iat": true,
  "preferred_username": "username"
] as [String: Any]

let validator = OktaJWTValidator(options)

let jwtString = "ey..."

do {
  let valid = try validator.isValid(jwtString)
  print("Valid: \(valid)")
} catch OktaJWTVerificationError.invalidClaim(let claim) {
  // Claim {claim} not present
} catch let error {
  // Misc Error: {error}
}
```

### Advanced Options

#### Optional Validator Params

- `jwk`: Pass a [JSON Web Key](https://tools.ietf.org/html/rfc7517) (JWK) to be used over the ones provided by the `/keys` endpoint.
- `RSAKey`: Use an existing `RSAKey`

```swift
let options = [
  "issuer": "https://{yourOktaDomain}.com/oauth2/default",
  "audience": "0abc123..",
  ...
] as [String: Any]

let jwtString = "ey..."


// Use custom JWK
let givenJWK = [
  "alg": "RS256",
  "e": "AQAB",
  "n": "kR7T4d_6RrTLQ4rdhdexVsGs6D0UwY9gZotmC7BEMvFovvnB0U3fy7WpmUn3aL9ooUJuDj19h17l3" +
       "gENKTaZOLucmLVq6HlK8coukxzk8_zhllrWXXFVwB3TlB-zR2EfWi_FKnyHHrSQ0lb1RfO7wberhy" +
       "_FK6n6WA5lCMYVfOGVm3aV6vfAojS7y1QzyimytitCRsOnIW7QmlZ1ZtKcEKb0pGdwSAAj-OSldZL" +
       "uLBj9B_t6HMq0xPVNhWgtYGDFNARaCIcvuP236VpGsw3EH4zfeKVMpScHC2j3y5JvMefn_iVgBzW7" +
       "9qs6QPbC6Y1_yCJv-ZRfur3Tk92Hq82B4w",
  "kid": "someKeyId",
  "kty": "RSA",
  "use": "sig"
] as [String: Any]

let validator = OktaJWTValidator(options, jwk: givenJWK)

do {
  let valid = try validator.isValid(jwtString)
  print("Valid: \(valid)")
} catch let error {
  // Misc Error: {error}
}

//  -- OR --

// Use existing RSAKey
let rsaKey = RSAKey.registeredKeyWithTag("myKeyTag")

let validator = OktaJWTValidator(options, key: rsaKey)

do {
  let valid = try validator.isValid(jwtString)
  print("Valid: \(valid)")
} catch let error {
  // Misc Error: {error}
}
```
