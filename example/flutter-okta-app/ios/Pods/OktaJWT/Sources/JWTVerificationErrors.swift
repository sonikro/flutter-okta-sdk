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

public enum OktaJWTVerificationError: Error {
    case malformedJWT
    case nonSupportedAlg(String)
    case invalidIssuer
    case invalidAudience
    case noKIDFromJWT
    case invalidKID
    case invalidSignature
    case invalidModulusOrExponent
    case expiredJWT
    case issuedInFuture
    case invalidNonce
    case invalidClaim(Any)
}

extension OktaJWTVerificationError: LocalizedError {
    public var errorDescription: String? {
        switch self {
            case .malformedJWT:
                return NSLocalizedString("String injected is not formatted as a JSON Web Token", comment: "")
            case .nonSupportedAlg(alg: let alg):
                return NSLocalizedString("The JWT algorithm \(alg) is not supported at this time", comment: "")
            case .invalidIssuer:
                return NSLocalizedString("Token issuer does not match the valid issuer", comment: "")
            case .invalidAudience:
                return NSLocalizedString("Token audience does not match the valid audience", comment: "")
            case .noKIDFromJWT:
                return NSLocalizedString("Could not retrieve kid from JWT", comment: "")
            case .invalidKID:
                return NSLocalizedString("Invalid Key ID", comment: "")
            case .invalidSignature:
                return NSLocalizedString("Signature validation failed", comment: "")
            case .invalidModulusOrExponent:
                return NSLocalizedString("Modulus or exponent from JWK could not be parsed", comment: "")
            case .expiredJWT:
                return NSLocalizedString("The JWT expired and is no longer valid", comment: "")
            case .issuedInFuture:
                return NSLocalizedString("The JWT was issued in the future", comment: "")
            case .invalidNonce:
                return NSLocalizedString("Invalid nonce", comment: "")
            case .invalidClaim(value: let value):
                return NSLocalizedString("JWT does not contain \"\(value)\" in the payload", comment: "")
            }
    }
}
