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

#import "NSData+HMAC.h"
#import "NSData+SHA.h"

FOUNDATION_EXPORT double OktaJWTVersionNumber;
FOUNDATION_EXPORT const unsigned char OktaJWTVersionString[];

