/****
 *  BluedotPointSDKCDVPlugin.h
 *
 * Bluedot Innovation
 * Copyright (c) 2021 Bluedot Innovation. All rights reserved.
 */

#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>

@interface BluedotPointSDKCDVPlugin : CDVPlugin

/*
 *  Common access methods to the Bluedot Point SDK.
 */
- (void)initializeWithProjectId: (CDVInvokedUrlCommand *)command;
- (void)isInitialized: (CDVInvokedUrlCommand *)command;
- (void)reset: (CDVInvokedUrlCommand *)command;

- (void)iOSStartGeoTriggering: (CDVInvokedUrlCommand *)command;
- (void)iOSStartGeoTriggeringWithAppRestartNotification: (CDVInvokedUrlCommand *)command;
- (void)stopGeoTriggering: (CDVInvokedUrlCommand *)command;
- (void)isGeoTriggeringRunning: (CDVInvokedUrlCommand *)command;

- (void)iOSStartTempoTracking: (CDVInvokedUrlCommand *)command;
- (void)stopTempoTracking: (CDVInvokedUrlCommand *)command;
- (void)isTempoRunning: (CDVInvokedUrlCommand *)command;

//  Setup BluedotServiceDelegate functions for call backs from the SDK
- (void)bluedotServiceDidReceiveErrorCallback: (CDVInvokedUrlCommand *)command;
- (void)locationAuthorizationDidChangeCallback: (CDVInvokedUrlCommand *)command;
- (void)accuracyAuthorizationDidChangeCallback: (CDVInvokedUrlCommand *)command;
- (void)lowPowerModeDidChangeCallback: (CDVInvokedUrlCommand *)command;

//  Setup GeoTriggeringEventDelegate functions for call backs from the SDK
- (void)zoneInfoUpdateCallback: (CDVInvokedUrlCommand *)command;
- (void)enteredZoneCallback: (CDVInvokedUrlCommand *)command;
- (void)exitedZoneCallback: (CDVInvokedUrlCommand *)command;

//  Setup TempoTrackingDelegate functions for call backs from the SDK
- (void)tempoStoppedWithErrorCallback: (CDVInvokedUrlCommand *)command;
- (void)tempoTrackingExpiredCallback: (CDVInvokedUrlCommand *)command;

- (void)disableZone: (CDVInvokedUrlCommand *)command;
- (void)enableZone: (CDVInvokedUrlCommand *)command;
- (void)notifyPushUpdate: (CDVInvokedUrlCommand *)command;
- (void)setCustomEventMetaData: (CDVInvokedUrlCommand *)command;
- (void)getZones: (CDVInvokedUrlCommand *)command;
- (void)getSdkVersion: (CDVInvokedUrlCommand *)command;
- (void)getInstallRef: (CDVInvokedUrlCommand *)command;

@end
