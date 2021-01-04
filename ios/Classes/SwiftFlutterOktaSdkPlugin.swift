import Flutter
import UIKit

import OktaOidc
import OktaJWT

let CHANNEL_NAME: String! = "com.sonikro.flutter_okta_sdk";


struct FlutterOktaError: Error {
    let message: String

    init(message: String) {
        self.message = message
    }
}

extension FlutterOktaError: LocalizedError {
    var errorDescription: String? { return message }
}

public class SwiftFlutterOktaSdkPlugin: NSObject, FlutterPlugin {

  var _channel: FlutterMethodChannel;
  var oktaOidc: OktaOidc?
  var stateManager: OktaOidcStateManager?

  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: CHANNEL_NAME, binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterOktaSdkPlugin(channel: channel)
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  init(channel: FlutterMethodChannel) {
    _channel = channel;
    super.init();
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
      case "createConfig":
        guard let oktaInfo: Dictionary = call.arguments as? [String: Any?] else {
          result(-1);
          return;
        }
        let clientId: String? = oktaInfo["clientId"] as? String;
        let issuer: String? = oktaInfo["issuer"] as? String;
        let endSessionRedirectUri: String? = oktaInfo["endSessionRedirectUri"] as? String;
        let redirectUrl: String? = oktaInfo["redirectUrl"] as? String;
        let scopeArray: [String]? = oktaInfo["scopes"] as? [String];

        let scopes = scopeArray?.joined(separator: " ");
        
        let oktaConfigMap: [String: String] = [
          "clientId": clientId!,
          "issuer": issuer!,
          "logoutRedirectUri": endSessionRedirectUri!,
          "scopes": scopes!,
          "redirectUri": redirectUrl!,
        ] as [String: String];
        
        createConfig(configuration: oktaConfigMap, callback: { error in
          if(error != nil) {
            result(error);
            return
          }
          result(true);
        });
        break;
        
      case "signIn":
        signIn(callback: { error in
          if(error != nil) {
            let flutterError: FlutterError = FlutterError(code: "SignIn_Error", message: error?.localizedDescription, details: error.debugDescription);
            result(flutterError);
            return
          }
          result(true);
        });
        break;

      case "signOut":
        signOut(callback: { error in
          if(error != nil) {
            let flutterError: FlutterError = FlutterError(code: "SignOut_Error", message: error?.localizedDescription, details: error.debugDescription);
            result(flutterError)
            return
          }
          result(true);
        });
        break;

      case "getUser":
        getUser(callback: { user, error in
          if(error != nil) {
            let flutterError: FlutterError = FlutterError(code: "GetUser_Error", message: error?.localizedDescription, details: error.debugDescription);
            result(flutterError)
            return
          }
          result(user);
        })
        break;

      case "isAuthenticated":
        isAuthenticated(callback: { status in
          result(status);
        })
        break;

      case "getAccessToken":
        getAccessToken(callback: { token in
          result(token);
        })
        break;

      case "getIdToken":
        getIdToken(callback: { token in
          result(token);
        })
        break;

      case "revokeAccessToken":
        revokeAccessToken(callback: { isRevoked in
          result (isRevoked)
        })
        break;

      case "revokeIdToken":
        revokeIdToken(callback: { isRevoked in
          result (isRevoked)
        })
        break;

      case "revokeRefreshToken":
        revokeRefreshToken(callback: { isRevoked in
          result (isRevoked)
        })
        break;

      case "clearTokens":
        clearTokens(callback: {
          result(true);
        });
        break;

      case "introspectAccessToken":
        introspectAccessToken(callback: { message, error in
          if(error != nil) {
            let flutterError: FlutterError = FlutterError(code: "IntrospectAccessToken_Error", message: error?.localizedDescription, details: error.debugDescription);
            result(flutterError)
          } else {
            result(message)
          }
        })
        break;

      case "introspectIdToken":
        introspectIdToken(callback: { message, error in
          if(error != nil) {
            let flutterError: FlutterError = FlutterError(code: "IntrospectIdToken_Error", message: error?.localizedDescription, details: error.debugDescription);
            result(flutterError)
          } else {
            result(message)
          }
        })
        break;

      case "introspectRefreshToken":
        introspectRefreshToken(callback: { message, error in
          if(error != nil) {
            let flutterError: FlutterError = FlutterError(code: "IntrospectRefreshToken_Error", message: error?.localizedDescription, details: error.debugDescription);
            result(flutterError)
          } else {
            result(message)
          }
        })
        break;

      case "refreshTokens":
        refreshTokens(callback: { message, error in
          if(error != nil) {
            let flutterError: FlutterError = FlutterError(code: "RefreshToken_Error", message: error?.localizedDescription, details: error.debugDescription);
            result(flutterError)
          } else {
            result(message)
          }
        })
//        let flutterError: FlutterError = FlutterError(code: "RefreshToken_Error", message: "Cant refresh", details: "User not logged in, cannot refresh");
//        result(flutterError)

        break;

      default:
        NSLog("\(call.method)");
        result("iOS " + UIDevice.current.systemVersion)
    }
  }

  func createConfig(configuration: [String:String], callback: ((Error?) -> (Void))) {
    do {
      let oktaConfiguration: OktaOidcConfig = try OktaOidcConfig(with: configuration);
      self.oktaOidc = try OktaOidc(configuration: oktaConfiguration);
    } catch let error {
      print("okta object creation error \(error)");
      callback(error);
    }
    if let oktaOidc = oktaOidc,
         let _ = OktaOidcStateManager.readFromSecureStorage(for: oktaOidc.configuration)?.accessToken {
        self.stateManager = OktaOidcStateManager.readFromSecureStorage(for: oktaOidc.configuration)
      }
      callback(nil)
    }
  
  func signIn(callback: @escaping ((Error?) -> Void)) {
//    signInWithBrowser(callback: callback);
    if let oktaOidc = oktaOidc,
         let _ = OktaOidcStateManager.readFromSecureStorage(for: oktaOidc.configuration)?.accessToken {
        self.stateManager = OktaOidcStateManager.readFromSecureStorage(for: oktaOidc.configuration)

      let options = ["iss": self.oktaOidc!.configuration.issuer, "exp": "true"]
      let idTokenValidator = OktaJWTValidator(options)
      do {
          _ = try idTokenValidator.isValid(self.stateManager!.idToken!)
      } catch {
        signInWithBrowser(callback: callback);
      }
      callback(nil);

    } else {
      signInWithBrowser(callback: callback);
    }
  }
  
  func signInWithBrowser(callback: @escaping ((Error?) -> Void)) {
    let viewController: UIViewController =
                (UIApplication.shared.delegate?.window??.rootViewController)!;

    oktaOidc?.signInWithBrowser(from: viewController, callback: { [weak self] stateManager, error in
      if let error = error {
        print("Signin Error: \(error)");
        callback(error)
        return
      }
      self?.stateManager?.clear()
      self?.stateManager = stateManager
      self?.stateManager?.writeToSecureStorage()
      callback(nil)
    })
  }
  
  func signOut(callback: ((Error?) -> (Void))?) {
    let viewController: UIViewController =
                (UIApplication.shared.delegate?.window??.rootViewController)!;

    guard let oktaOidc = self.oktaOidc,
          let stateManager = self.stateManager else { return }
    
    oktaOidc.signOutOfOkta(stateManager, from: viewController, callback: { [weak self] error in
      if let error = error {
        callback?(error)
        return
      }
      self?.stateManager?.clear()
      callback?(nil);
    })

  }
  
  func getUser(callback: @escaping ((String?, Error?)-> (Void))) {
    stateManager?.getUser { response, error in
      guard let response = response else {
        let alert = UIAlertController(title: "Error", message: error?.localizedDescription, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        callback(nil, error)
        return
      }
      if let jsonData = try? JSONSerialization.data(withJSONObject: response, options: .prettyPrinted) {
      let jsonString = String(data: jsonData, encoding: .ascii)
        callback(jsonString, nil)
      } else {
        callback(nil, error)
      }
    }
  }
  
  func isAuthenticated(callback: ((Bool) -> (Void))?) {
    if  let oktaOidc = oktaOidc,
      let _ = OktaOidcStateManager.readFromSecureStorage(for: oktaOidc.configuration)?.accessToken {
      self.stateManager = OktaOidcStateManager.readFromSecureStorage(for: oktaOidc.configuration)
      callback?(true)
      return
    }
    callback?(false)
  }
  
  func getAccessToken(callback: ((String?) -> (Void))? ) {
    if let accessToken = stateManager?.accessToken {
      callback?(accessToken)
    }
    else { callback?(nil) }
  }
  
  func getIdToken(callback: ((String?) -> (Void))? ) {
    if let idToken = stateManager?.idToken {
      callback?(idToken)
    }
    else { callback?(nil) }
  }
  
  func revokeAccessToken(callback: ((Bool) ->(Void))?) {
    guard let accessToken = stateManager?.accessToken else { return }
    return _revokeToken(token: accessToken, callback: callback);
  }

  func revokeIdToken(callback: ((Bool) ->(Void))?) {
    guard let idToken = stateManager?.idToken else { return }
    return _revokeToken(token: idToken, callback: callback);
  }
  
  func revokeRefreshToken(callback: ((Bool) ->(Void))?) {
    guard let refreshToken = stateManager?.refreshToken else { return }
    return _revokeToken(token: refreshToken, callback: callback);
  }

  
  func _revokeToken(token: String?, callback: ((Bool) ->(Void))?) {
    stateManager?.revoke(token, callback: { isRevoked, error in
      guard isRevoked else {
        callback?(false)
        return
      }
      callback?(true)
    })
  }
  
  func clearTokens (callback: (() -> (Void))?) {
    stateManager?.clear();
    callback?();
  }
  
  func introspectAccessToken(callback: ((String?, Error?)->(Void))?) {
    guard let accessToken = stateManager?.accessToken else { return }
    return introspectToken(token: accessToken, callback: callback);
  }

  func introspectIdToken(callback: ((String?, Error?)->(Void))?) {
    guard let idToken = stateManager?.idToken else { return }
    return introspectToken(token: idToken, callback: callback);
  }

  func introspectRefreshToken(callback: ((String?, Error?)->(Void))?) {
    guard let refreshToken = stateManager?.refreshToken else { return }
    return introspectToken(token: refreshToken, callback: callback);
  }

  func introspectToken(token: String?, callback: ((String?, Error?)->(Void))?) {
      stateManager?.introspect(token: token, callback: { payload, error in
        guard let isValid = payload?["active"] as? Bool else {
          callback?(nil, error);
          return
        }
        callback?("Access token is \(isValid ? "valid" : "invalid")!", nil);
      })
  }
  
  func refreshTokens(callback: ((String?, Error?) -> (Void))?) {
    if let sm = stateManager {
        sm.renew { stateManager, error in
        if let error = error {
            callback?(nil, error)
            return
        }
        callback?("Token refreshed!", nil);
      }
    } else {
      //        let flutterError: FlutterError = FlutterError(code: "RefreshToken_Error", message: "Cannot refresh tokens", details: "User not logged in, cannot refresh");
      //        result(flutterError)

      callback?(nil, FlutterOktaError(message: "User not logged in, cannot refresh"));
    }
  }
}
