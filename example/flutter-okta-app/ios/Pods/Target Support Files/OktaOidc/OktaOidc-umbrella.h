#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "AppAuthCore.h"
#import "OktaNetworkRequestCustomizationDelegate.h"
#import "OktaUserAgent.h"
#import "OKTAuthorizationRequest.h"
#import "OKTAuthorizationResponse.h"
#import "OKTAuthorizationService+IOS.h"
#import "OKTAuthorizationService+Mac.h"
#import "OKTAuthorizationService.h"
#import "OKTAuthState+IOS.h"
#import "OKTAuthState+Mac.h"
#import "OKTAuthState.h"
#import "OKTAuthStateChangeDelegate.h"
#import "OKTAuthStateErrorDelegate.h"
#import "OKTClientMetadataParameters.h"
#import "OKTDefines.h"
#import "OKTEndSessionRequest.h"
#import "OKTEndSessionResponse.h"
#import "OKTError.h"
#import "OKTErrorUtilities.h"
#import "OKTExternalUserAgent.h"
#import "OKTExternalUserAgentIOS.h"
#import "OKTExternalUserAgentIOSCustomBrowser.h"
#import "OKTExternalUserAgentMac.h"
#import "OKTExternalUserAgentNoSsoIOS.h"
#import "OKTExternalUserAgentRequest.h"
#import "OKTExternalUserAgentSession.h"
#import "OKTFieldMapping.h"
#import "OKTGrantTypes.h"
#import "OKTIDToken.h"
#import "OKTLoopbackHTTPServer.h"
#import "OKTRedirectHTTPHandler.h"
#import "OKTRegistrationRequest.h"
#import "OKTRegistrationResponse.h"
#import "OKTResponseTypes.h"
#import "OKTScopes.h"
#import "OKTScopeUtilities.h"
#import "OKTServiceConfiguration.h"
#import "OKTServiceDiscovery.h"
#import "OKTTokenRequest.h"
#import "OKTTokenResponse.h"
#import "OKTTokenUtilities.h"
#import "OKTURLQueryComponent.h"
#import "OKTURLSessionProvider.h"
#import "OktaOidc.h"

FOUNDATION_EXPORT double OktaOidcVersionNumber;
FOUNDATION_EXPORT const unsigned char OktaOidcVersionString[];

