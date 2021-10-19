/*
 * Copyright (c) 2017, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

import Foundation

public class OktaKeychain: NSObject {

    /**
     Loads the stored JWK tag into a the Keychain so it can be cached and/or removed at
     a later time.
     - parameters:
         - tag: Hash to reference the stored Keychain item
     */
    public class func loadKey(tag: String) {
        if let storedKey = self.get("com.okta.jwt.keys") {
            self.remove(storedKey)
            RSAKey.removeKeyWithTag(tag)
        }

        self.set(key: "com.okta.jwt.keys", tag)
    }

    /**
     Stores an item securely in the Keychain.
     - parameters:
         - key: Hash to reference the stored Keychain item
         - object: Object to store inside of the keychain
     */
    internal class func set(key: String, _ object: String) {
        let objectData = object.data(using: .utf8)

        let q = [
            kSecClass as String: kSecClassGenericPassword as String,
            kSecValueData as String: objectData!,
            kSecAttrAccount as String: key
        ] as CFDictionary

        // Delete existing (if applicable)
        SecItemDelete(q)

        let sanityCheck = SecItemAdd(q, nil)
        if sanityCheck != noErr {
            print("Error Storing to Keychain: \(sanityCheck.description)")
        }
    }

    /**
     Retrieve the stored JWK information from the Keychain.
     - parameters:
         - key: Hash to reference the stored Keychain item
     */
    internal class func get(_ key: String) -> String? {
        let q = [
            kSecClass as String: kSecClassGenericPassword,
            kSecReturnData as String: kCFBooleanTrue,
            kSecMatchLimit as String: kSecMatchLimitOne,
            kSecAttrAccount as String: key
        ] as CFDictionary

        var ref: AnyObject? = nil

        let sanityCheck = SecItemCopyMatching(q, &ref)
        if sanityCheck != noErr { return nil }

        if let parsedData = ref as? Data {
            return String(data: parsedData, encoding: .utf8)
        }

        return nil
    }

    /**
     Remove the stored JWK information from the Keychain.
     - parameters:
         - key: Hash to reference the stored Keychain item
     */
    internal class func remove(_ key: String) {
        guard let object = self.get(key) else {
            return
        }

        let q = [
            kSecClass as String: kSecClassGenericPassword as String,
            kSecValueData as String: object.data(using: .utf8)!,
            kSecAttrAccount as String: key
        ] as CFDictionary

        // Delete existing (if applicable)
        let sanityCheck = SecItemDelete(q)
        if sanityCheck != noErr {
            print("Error deleting keychain item: \(sanityCheck.description)")
        }
    }

    /**
     Removes all entities from the Keychain.
     */
    internal class func clearAll() {
        let secItemClasses = [
            kSecClassGenericPassword,
            kSecClassInternetPassword,
            kSecClassCertificate,
            kSecClassKey,
            kSecClassIdentity
        ]

        for secItemClass in secItemClasses {
            let dictionary = [ kSecClass as String:secItemClass ] as CFDictionary
            SecItemDelete(dictionary)
        }
    }
}
