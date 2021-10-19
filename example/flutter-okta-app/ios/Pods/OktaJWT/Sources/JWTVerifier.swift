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

open class OktaJWTVerifier: NSObject {

    /**
     Validates the issuer against the well-known endpoint or given issuer.
     - parameters:
         - dirtyIssuer: Issuer claim from the JWT
         - validIssuer: Issuer to call the well-known endpoint against for verification
     - returns:
     True is the issuer is valid
     */
    open class func hasValidIssuer(_ dirtyIssuer: String?, validIssuer: String?) -> Bool {
        if validIssuer == nil {
            return true
        }

        if dirtyIssuer == nil {
            return false
        }

        // Try to get issuer from well-known endpoint first
        if let wellKnownIssuer = RequestsAPI.getIssuerEndpoint(issuer: validIssuer!) {
            if dirtyIssuer! == wellKnownIssuer {
                return true
            } else {
                return false
            }
        }

        // If the issuer given does not have a 'well-known' endpoint, use the 'validIssuer'
        if dirtyIssuer! == validIssuer! {
            return true
        }

        return false
    }

    /**
     Validates the audience against the given audience.
     - parameters:
         - dirtyAudience: Audience claim from the JWT (converted to a String Array)
         - validAudience: Audience to perform the verification against
     - returns:
     True is the audience is valid
     */
    open class func hasValidAudience(_ dirtyAudience: [String]?, validAudience: String?) -> Bool {
        if validAudience == nil {
            return true
        }

        guard let jwtAudience = dirtyAudience else {
            return false
        }

        for aud in jwtAudience {
            if aud == validAudience {
                return true
            }
        }

        return false
    }

    /**
     Validates the expriation against the current time +/- leeway.
     - parameters:
         - exp: Expiration claim of JWT
         - leeway: Time (in seconds) permitted for clockskew
     - returns:
     True is the exp is valid
     */
    open class func isExpired(_ exp: Date?, leeway: Int?) -> Bool {
        if exp == nil {
            return false
        }

        let now = Date()
        if let givenLeeway = leeway {
            // Use given leeway
            if (now.addingTimeInterval(Double(givenLeeway) * -1) > exp!) {
                return true
            }
        } else if now > exp! {
            return true
        }

        return false
    }
    
    /**
     Validates the issued at time against the current time +/- leeway.
     - parameters:
         - iat: Issued at time claim of JWT
         - leeway: Time (in seconds) permitted for clockskew
     - returns:
     True is the iat is valid
     */
    open class func isIssuedInFuture(_ iat: Date?, leeway: Int?) -> Bool {
        if iat == nil {
            return false
        }

        let now = Date()
        if let givenLeeway = leeway {
            // Use given leeway
            if (now.addingTimeInterval(Double(givenLeeway)) < iat!) {
                return true
            }
        } else if now < iat! {
            return true
        }
        
        return false
    }

    /**
     Validates the nonce against the given nonce.
     - parameters:
         - dirtyNonce: Nonce claim from the JWT
         - validNonce: Nonce to perform the verification against
     - returns:
     True is the nonce is valid
     */
    open class func hasValidNonce(_ dirtyNonce: String, validNonce: String?) -> Bool {
        if validNonce == nil {
            return true
        }

        if dirtyNonce == validNonce! {
            return true
        }

        return false
    }

    /**
     Validates the JWT payload claim against the given claim.
     - parameters:
         - payloadClaim: Claim from the JWT
         - validClaim: Given claim to perform the verification against
     - returns:
     True is the claim is valid
     */
    open class func hasValue(_ payloadClaim: String?, validClaim: String?) -> Bool {
        if payloadClaim == nil || validClaim == nil {
            return false
        }

        if payloadClaim! == validClaim! {
            return true
        }

        return false
    }
}
