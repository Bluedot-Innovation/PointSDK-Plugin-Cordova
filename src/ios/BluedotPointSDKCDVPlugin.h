/****
 *  BluedotPointSDKCDVPlugin.h
 *
 *  This is the entry point into the plug-in; these methods provide access for both the
 *  the iOS and Android SDKs.
 *
 *  Roddy McNeill  5/10/15
 *  (c) Bluedot Innovations
 */

#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>

@interface BluedotPointSDKCDVPlugin : CDVPlugin

/*
 *  Common access methods to the Bluedot Point SDK.
 */
- (void)authenticate: (CDVInvokedUrlCommand *)command;
- (void)logOut: (CDVInvokedUrlCommand *)command;

//  Setup delegate functions for call backs from the SDK
- (void)zoneInfoCallback: (CDVInvokedUrlCommand *)command;
- (void)checkedIntoFenceCallback: (CDVInvokedUrlCommand *)command;
- (void)checkedIntoBeaconCallback: (CDVInvokedUrlCommand *)command;

- (void)startRequiringUserInterventionForBluetoothCallback: (CDVInvokedUrlCommand *)command;
- (void)stopRequiringUserInterventionForBluetoothCallback: (CDVInvokedUrlCommand *)command;
- (void)startRequiringUserInterventionForLocationServicesCallback: (CDVInvokedUrlCommand *)command;
- (void)stopRequiringUserInterventionForLocationServicesCallback: (CDVInvokedUrlCommand *)command;

@end