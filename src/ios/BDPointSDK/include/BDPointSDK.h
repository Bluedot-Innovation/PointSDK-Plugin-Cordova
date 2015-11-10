/****
 *    BDPointSDK.h
 *
 *    This header file encapsulates the public functionality available from the Bluedot location manager and additional functionality
 *    within the SDK.
 *
 *    Copyright (C) 2015 Bluedot.  All rights reserved.
 */

#ifdef __APPLE__
#import <UIKit/UIKit.h>
#import "BDLocationManager.h"
#import "BDLocationManager+BDPointSDK.h"
#import "BDPointOverlayRendererFactory.h"
#import "BDPMKShape.h"
#import "MKMapView+BDPointSDK.h"
#endif

// Bluedot classes
#import "BDAuthenticationState.h"
#import "BDGeometry.h"
#import "BDBoundingBox.h"
#import "BDCircle.h"
#import "BDPoint.h"
#import "BDPolygon.h"
#import "BDLocation.h"
#import "BDZoneInfo.h"
#import "BDFenceInfo.h"
#import "BDBeaconInfo.h"
#import "BDPointOverlayRendererFactory.h"

// Bluedot protocols
#import "BDPointDelegate.h"
#import "BDPSessionDelegate.h"
#import "BDPDeepCopy.h"
#import "BDPGeometry.h"
#import "BDPLocationDelegate.h"
#import "BDPolygonal.h"
#import "BDPValidatable.h"
#import "BDPRestartAlertDelegate.h"
#import "BDPSpatialObjectInfo.h"
#import "BDPSpatialObject.h"

// Bluedot categories
#import "NSString+BDURLEncoding.h"

//! Project version number for BDPointSDK.
FOUNDATION_EXPORT double BDPointSDKVersionNumber;

//! Project version string for BDPointSDK.
FOUNDATION_EXPORT const unsigned char BDPointSDKVersionString[];
