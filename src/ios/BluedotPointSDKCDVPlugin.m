/****
 *  BluedotPointSDKCDVPlugin.m
 *
 *
 * Bluedot Innovation
 * Copyright (c) 2021 Bluedot Innovation. All rights reserved.
 */

#import "BluedotPointSDKCDVPlugin.h"
@import BDPointSDK;


@interface BluedotPointSDKCDVPlugin() <BDPBluedotServiceDelegate, BDPGeoTriggeringEventDelegate, BDPTempoTrackingDelegate>
@end


@implementation BluedotPointSDKCDVPlugin
{
    id  _callbackIdZoneInfoUpdate;
    id  _callbackIdEnteredZone;
    id  _callbackIdExitedZone;
    id  _callbackIdDidStopTracking;
    id  _callbackIdTempoTrackingExpired;
    id  _callbackIdBluedotServiceDidReceiveError;
    id  _callbackIdLocationAuthorizationDidChange;
    id  _callbackIdAccuracyAuthorizationDidChange;
    id  _callbackIdlowPowerModeDidChange;

    //  A default date formatter
    NSDateFormatter  *_dateFormatter;
}

/*
 *  This is the initialisation method called implictly by Cordova.
 */
- (void)pluginInitialize
{
    /*
     *  Instantiate the location manager and assign the delegates to this class.
     */
    BDLocationManager  *locationManager = [ BDLocationManager instance ];

    //  Assign the delegates to this class
    locationManager.bluedotServiceDelegate = self;
    locationManager.geoTriggeringEventDelegate = self;
    locationManager.tempoTrackingDelegate = self;

    //  Setup a generic date formatter
    _dateFormatter = [ NSDateFormatter new ];
    [ _dateFormatter setDateFormat: @"dd-MMM-yyyy HH:mm" ];

    //  Initialise identifiers - unneccessary but explicit
    _callbackIdZoneInfoUpdate = nil;
    _callbackIdEnteredZone = nil;
    _callbackIdExitedZone = nil;
    _callbackIdDidStopTracking = nil;
    _callbackIdTempoTrackingExpired = nil;
    _callbackIdBluedotServiceDidReceiveError = nil;
    _callbackIdLocationAuthorizationDidChange = nil;
    _callbackIdAccuracyAuthorizationDidChange = nil;
    _callbackIdlowPowerModeDidChange = nil;
}

/*
 *  Entry method for initialize has 1 parameter: projectId
 */
- (void)initializeWithProjectId:(CDVInvokedUrlCommand *)command
{
    //  Ensure that the command has the minimum number of arguments
    if ( command.arguments.count != 1 )
    {
        CDVPluginResult  *pluginResult = [
            CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                             messageAsString: @"Incorrect number of arguments supplied to initializeWithProjectId method." ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        return;
    }
    
    NSString *projectId = command.arguments[0];
    
    [[BDLocationManager instance] initializeWithProjectId: projectId completion:^(NSError * error)
     {
        if(error != nil)
        {
            CDVPluginResult  *pluginResult = [
                CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                 messageAsString: [NSString stringWithFormat:
                    @"Initialization Failed with Error: %@", error.localizedDescription]
            ];
            
            [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
            
            return;
        }
        
        CDVPluginResult  *pluginResult = [
            CDVPluginResult resultWithStatus: CDVCommandStatus_OK
        ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        
        return;
    }];
}

- (void)isInitialized:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult* pluginResult = [CDVPluginResult
                     resultWithStatus: CDVCommandStatus_OK
                        messageAsBool: [[BDLocationManager instance] isInitialized]];
    [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
}

- (void)reset:(CDVInvokedUrlCommand *)command
{
    
    [[BDLocationManager instance] resetWithCompletion:^(NSError * _Nullable error) {
        if(error != nil)
        {
            CDVPluginResult  *pluginResult = [
                CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                 messageAsString: [NSString stringWithFormat:
                    @"Reset Failed with Error: %@", error.localizedDescription]
            ];
            
            [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
            
            return;
        }
        
        CDVPluginResult  *pluginResult = [
            CDVPluginResult resultWithStatus: CDVCommandStatus_OK
        ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        
        return;
    }];
}

- (void)iOSStartGeoTriggering:(CDVInvokedUrlCommand *)command
{
    [[BDLocationManager instance] startGeoTriggeringWithCompletion:^(NSError * _Nullable error) {
        if(error != nil)
        {
            CDVPluginResult  *pluginResult = [
                CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                 messageAsString: [NSString stringWithFormat:
                    @"Start GeoTriggering Failed with Error: %@", error.localizedDescription]
            ];
            
            [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
            
            return;
        }
        
        CDVPluginResult  *pluginResult = [
            CDVPluginResult resultWithStatus: CDVCommandStatus_OK
        ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        
        return;
    }];
}

- (void)iOSStartGeoTriggeringWithAppRestartNotification:(CDVInvokedUrlCommand *)command
{
    //  Ensure that the command has the correct number of arguments
    if ( command.arguments.count != 2 )
    {
        CDVPluginResult  *pluginResult = [
            CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                             messageAsString: @"Incorrect number of arguments supplied to startGeoTriggeringWithAppRestartNotification method." ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        return;
    }
    
    NSString *title = command.arguments[0];
    NSString *buttonText = command.arguments[1];
    
    [[BDLocationManager instance] startGeoTriggeringWithAppRestartNotificationTitle: title
                                   notificationButtonText: buttonText completion:^(NSError * _Nullable error){
        if(error != nil)
        {
            CDVPluginResult  *pluginResult = [
                CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                 messageAsString: [NSString stringWithFormat:
                    @"startGeoTriggeringWithAppRestartNotification Failed with Error: %@", error.localizedDescription]
            ];
            
            [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
            
            return;
        }
        
        CDVPluginResult  *pluginResult = [
            CDVPluginResult resultWithStatus: CDVCommandStatus_OK
        ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        
        return;
    }];
}

- (void)stopGeoTriggering:(CDVInvokedUrlCommand *)command
{
    [[BDLocationManager instance] stopGeoTriggeringWithCompletion:^(NSError * _Nullable error) {
        if(error != nil)
        {
            CDVPluginResult  *pluginResult = [
                CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                 messageAsString: [NSString stringWithFormat:
                    @"stopGeoTriggering Failed with Error: %@", error.localizedDescription]
            ];
            
            [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
            
            return;
        }
        
        CDVPluginResult  *pluginResult = [
            CDVPluginResult resultWithStatus: CDVCommandStatus_OK
        ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        
        return;
    }];
}

- (void)isGeoTriggeringRunning:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult* pluginResult = [CDVPluginResult
                     resultWithStatus: CDVCommandStatus_OK
                        messageAsBool: [[BDLocationManager instance] isGeoTriggeringRunning]];
    [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
}

- (void)iOSStartTempoTracking:(CDVInvokedUrlCommand *)command
{
    //  Ensure that the command has the correct number of arguments
    if ( command.arguments.count != 1 )
    {
        CDVPluginResult  *pluginResult = [
            CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                             messageAsString: @"Incorrect number of arguments supplied to startTempoWithDestinationId method." ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        return;
    }
    
    NSString *destinationId = command.arguments[0];
    
    [[BDLocationManager instance] startTempoTrackingWithDestinationId: destinationId completion:^(NSError * _Nullable error) {
        if(error != nil)
        {
            CDVPluginResult  *pluginResult = [
                CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                 messageAsString: [NSString stringWithFormat:
                    @"Start Tempo Failed with Error: %@", error.localizedDescription]
            ];
            
            [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
            
            return;
        }
        
        CDVPluginResult  *pluginResult = [
            CDVPluginResult resultWithStatus: CDVCommandStatus_OK
        ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        
        return;
    }];
}

- (void)stopTempoTracking:(CDVInvokedUrlCommand *)command
{
    [[BDLocationManager instance] stopTempoTrackingWithCompletion: ^(NSError * _Nullable error) {
        if(error != nil)
        {
            CDVPluginResult  *pluginResult = [
                CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                 messageAsString: [NSString stringWithFormat:
                    @"Stop Tempo Failed with Error: %@", error.localizedDescription]
            ];
            
            [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
            
            return;
        }
        
        CDVPluginResult  *pluginResult = [
            CDVPluginResult resultWithStatus: CDVCommandStatus_OK
        ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        
        return;
    }];
}

- (void)isTempoRunning:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult* pluginResult = [CDVPluginResult
                     resultWithStatus: CDVCommandStatus_OK
                        messageAsBool: [[BDLocationManager instance] isTempoRunning]];
    [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
}

- (void)disableZone: (CDVInvokedUrlCommand *)command
{
    [ self setZone: command disableByApplication: YES ];
}

- (void)enableZone: (CDVInvokedUrlCommand *)command
{
    [ self setZone: command disableByApplication: NO ];
}


- (void)setZone: (CDVInvokedUrlCommand *)command disableByApplication: (BOOL)disable
{

    if ( command.arguments.count < 1 )
    {
        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                            messageAsString: @"No zone id parameter supplied." ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        return;
    }

    NSString  *zoneId = command.arguments[0];

    [ BDLocationManager.instance setZone: zoneId disableByApplication: disable ];

    CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK ];

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
}

- (void)notifyPushUpdate: (CDVInvokedUrlCommand *)command
{
    if ( command.arguments.count < 1 )
    {
        CDVPluginResult  *pluginResult = [
            CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                             messageAsString: @"No user info parameter supplied." ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        return;
    }

    NSDictionary *data = command.arguments[0];

    [ BDLocationManager.instance notifyPushUpdateWithData: data ];
}

- (void)setCustomEventMetaData: (CDVInvokedUrlCommand *)command
{
    if ( command.arguments.count < 1 )
    {
        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                            messageAsString: @"No CustomEvent Meta Data parameter supplied." ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        return;
    }
    
    NSDictionary *data = command.arguments[0];
    
    [ BDLocationManager.instance setCustomEventMetaData: data ];
}

- (void)getZones: (CDVInvokedUrlCommand *)command
{
    NSSet *zoneInfos = [ BDLocationManager.instance zoneInfos ];
    NSMutableArray *returnZones = [ NSMutableArray new ];
        
    for( BDZoneInfo *zone in zoneInfos )
    {
        [ returnZones addObject: [ self zoneToDict: zone ] ];
    }
        
    CDVPluginResult  *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                          messageAsArray: returnZones
    ];

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
}

- (void)getSdkVersion: (CDVInvokedUrlCommand *)command
{
    CDVPluginResult  *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                          messageAsString: [ BDLocationManager.instance sdkVersion ]
    ];

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
}

- (void)getInstallRef: (CDVInvokedUrlCommand *)command
{
    CDVPluginResult  *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                          messageAsString: [ BDLocationManager.instance installRef ]
    ];

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
}

#pragma mark BDPBluedotServiceDelegate implementation begin

- (void)bluedotServiceDidReceiveError:(NSError *)error
{
    NSLog(@"bluedotServiceDidReceiveError: %@", error.localizedDescription);

    //  Ensure that a delegate for zone info has been setup
    if ( _callbackIdBluedotServiceDidReceiveError == nil )
    {
        NSLog( @"Callback for bluedotServiceDidReceiveError has not been setup." );
        return;
    }

    CDVPluginResult  *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                         messageAsString: [NSString stringWithFormat:
                                           @"Bluedot Service Received Error: %@", error.localizedDescription]
    ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdBluedotServiceDidReceiveError ];
}

- (void)locationAuthorizationDidChangeFromPreviousStatus:(CLAuthorizationStatus)previousAuthorizationStatus toNewStatus:(CLAuthorizationStatus)newAuthorizationStatus
{
    NSLog(@"CLAuthorizationStatus did change from %d to %d", previousAuthorizationStatus, newAuthorizationStatus);

    //  Ensure that a delegate for zone info has been setup
    if ( _callbackIdLocationAuthorizationDidChange == nil )
    {
        NSLog( @"Callback for locationAuthorizationDidChangeFromPreviousStatus has not been setup." );
        return;
    }
    
    CDVPluginResult *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                         messageAsMultipart:@[@(previousAuthorizationStatus), @(newAuthorizationStatus)]
    ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdLocationAuthorizationDidChange ];

}

- (void)accuracyAuthorizationDidChangeFromPreviousAuthorization:(CLAccuracyAuthorization)previousAccuracyAuthorization toNewAuthorization:(CLAccuracyAuthorization)newAccuracyAuthorization
{
    NSLog(@"CLAccuracyAuthorization did change from %ld to %ld", (long)previousAccuracyAuthorization, newAccuracyAuthorization);

    //  Ensure that a delegate for zone info has been setup
    if ( _callbackIdAccuracyAuthorizationDidChange == nil )
    {
        NSLog( @"Callback for accuracyAuthorizationDidChangeFromPreviousAuthorization has not been setup." );
        return;
    }
    
    CDVPluginResult *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                         messageAsMultipart: @[@(previousAccuracyAuthorization), @(newAccuracyAuthorization)]
    ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdAccuracyAuthorizationDidChange ];
}

- (void)lowPowerModeDidChange:(bool)isLowPowerMode
{
    NSLog(@"lowPowerModeDidChange did change to %@", isLowPowerMode ? @"true" : @"false");

    //  Ensure that a delegate for zone info has been setup
    if ( _callbackIdlowPowerModeDidChange == nil )
    {
        NSLog( @"Callback for lowPowerModeDidChange has not been setup." );
        return;
    }
    
    CDVPluginResult *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                           messageAsBool: isLowPowerMode
    ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdlowPowerModeDidChange ];

}

- (void)bluedotServiceDidReceiveErrorCallback: (CDVInvokedUrlCommand *)command
{
    _callbackIdBluedotServiceDidReceiveError = command.callbackId;
}

- (void)locationAuthorizationDidChangeCallback: (CDVInvokedUrlCommand *)command
{
    _callbackIdLocationAuthorizationDidChange = command.callbackId;
}

- (void)accuracyAuthorizationDidChangeCallback: (CDVInvokedUrlCommand *)command
{
    _callbackIdAccuracyAuthorizationDidChange = command.callbackId;
}

- (void)lowPowerModeDidChangeCallback: (CDVInvokedUrlCommand *)command
{
    _callbackIdlowPowerModeDidChange = command.callbackId;
}

#pragma mark BDPBluedotServiceDelegate implementation end

#pragma mark BDPGeoTriggeringEventDelegate implementation begin

/*
 *  A zone update event has occured, and zoneInfo is passed into this function by the Bluedot SDK.
 *
 *  Returning:
 *      Array of zones
 *          Array of strings identifying zone:
 *              name
 *              ID
 *              Custom fields setup in the <b>Canvas</b> web-interface.</p>
 */
- (void)onZoneInfoUpdate:(NSSet<BDZoneInfo *> *)zoneInfos
{
    NSLog( @"Point service updated with %lu zones", (unsigned long)zoneInfos.count );

    //  Ensure that a delegate for zone info has been setup
    if ( _callbackIdZoneInfoUpdate == nil )
    {
        NSLog( @"Callback for zone information has not been setup." );
        return;
    }

    NSMutableArray  *returnZones = [ NSMutableArray new ];

    for( BDZoneInfo *zone in zoneInfos )
    {
        [ returnZones addObject: [ self zoneToDict: zone ]  ];
    }

    CDVPluginResult  *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                          messageAsArray: returnZones
    ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdZoneInfoUpdate ];
}

/*
 *  A Zone with a Custom Action has been checked into.
 *
 *  Returns the following multipart status:
 *      Array identifying fence:
 *          name (String)
 *          description (String)
 *      Array of strings identifying zone:
 *          name (String)
 *          description (String)
 *          ID (String)
 *      Array of double values identifying location:
 *          Date of check-in (Integer - UNIX timestamp)
 *          Latitude of check-in (Double)
 *          Longitude of check-in (Double)
 *          Bearing of check-in (Double)
 *          Speed of check-in (Double)
 *      Fence is awaiting check-out (BOOL)
 *      Custom fields setup in the <b>Point Access</b> web-interface.</p>
 */
- (void)didEnterZone:(BDZoneEntryEvent *)enterEvent
{
    NSLog( @"You have checked into fence '%@' in zone '%@', at %@%@",
          enterEvent.fence.name, enterEvent.zone.name, [ _dateFormatter stringFromDate: enterEvent.location.timestamp ],
          ( enterEvent.zone.checkOut == YES ) ? @" and awaiting check out" : @"" );

    //  Ensure that a delegate for fence info has been setup
    if ( _callbackIdEnteredZone == nil )
    {
        NSLog( @"Callback for zone check-ins has not been setup." );
        return;
    }

    NSDictionary *returnFence = [ self fenceToDict: enterEvent.fence ];
    NSDictionary *returnZone = [ self zoneToDict: enterEvent.zone ];
    NSDictionary *returnLocation = [ self locationToDict: enterEvent.location ];

    NSArray  *returnMultiPart = [ [ NSArray alloc ] initWithObjects:
                                 returnFence,
                                 returnZone,
                                 returnLocation,
                                 @( enterEvent.zone.checkOut ),
                                 enterEvent.zone.customData,
                                 nil ];

    CDVPluginResult  *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                      messageAsMultipart: returnMultiPart
    ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdEnteredZone ];
}

- (void)didExitZone:(BDZoneExitEvent *)exitEvent
{
    NSLog( @"You left fence '%@' in zone '%@', after %u minutes",
          exitEvent.fence.name, exitEvent.zone.name, (unsigned int)exitEvent.duration );

    //  Ensure that a delegate for fence info has been setup
    if ( _callbackIdExitedZone == nil )
    {
        NSLog( @"Callback for zone check-outs has not been setup." );
        return;
    }

    NSDictionary *returnFence = [ self fenceToDict: exitEvent.fence ];
    NSDictionary *returnZone = [ self zoneToDict: exitEvent.zone ];
    NSTimeInterval  unixDate = [ exitEvent.date timeIntervalSince1970 ];

    NSArray  *returnMultiPart = [ [ NSArray alloc ] initWithObjects:
                                 returnFence,
                                 returnZone,
                                 @( unixDate ),
                                 @( exitEvent.duration ),
                                 exitEvent.zone.customData,
                                 nil
    ];

    CDVPluginResult  *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                      messageAsMultipart: returnMultiPart
    ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdExitedZone ];
}

- (void)zoneInfoUpdateCallback: (CDVInvokedUrlCommand *)command
{
    _callbackIdZoneInfoUpdate = command.callbackId;
}

- (void)enteredZoneCallback: (CDVInvokedUrlCommand *)command
{
    _callbackIdEnteredZone = command.callbackId;
}

- (void)exitedZoneCallback: (CDVInvokedUrlCommand *)command
{
    _callbackIdExitedZone = command.callbackId;
}

#pragma mark BDPGeoTriggeringEventDelegate implementation end

#pragma mark BDPTempoTrackingDelegate implementation begin

- (void)didStopTrackingWithError:(NSError *)error
{
    NSLog( @"didStopTrackingWithError: %@",
          error.localizedDescription);

    //  Ensure that a delegate for fence info has been setup
    if ( _callbackIdDidStopTracking == nil )
    {
        NSLog( @"Callback for didStopTrackingWithError has not been setup." );
        return;
    }
    
    CDVPluginResult  *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
        messageAsString: [NSString stringWithFormat:
                          @"Tempo Stopped with Error: %@", error.localizedDescription]
    ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdDidStopTracking];
}

- (void)tempoTrackingDidExpire
{
    NSLog( @"tempoTrackingDidExpire");

    //  Ensure that a delegate for fence info has been setup
    if ( _callbackIdTempoTrackingExpired == nil )
    {
        NSLog( @"Callback for tempoTrackingDidExpire has not been setup." );
        return;
    }
    
    CDVPluginResult  *pluginResult = [
        CDVPluginResult resultWithStatus: CDVCommandStatus_OK
    ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdTempoTrackingExpired];

}

- (void)tempoStoppedWithErrorCallback: (CDVInvokedUrlCommand *)command
{
    _callbackIdDidStopTracking = command.callbackId;
}

- (void)tempoTrackingExpiredCallback: (CDVInvokedUrlCommand *)command
{
    _callbackIdTempoTrackingExpired = command.callbackId;
}

#pragma mark BDPTempoTrackingDelegate implementation end

/*
 *  Return an NSDictionary with extrapolated zone details
 */
- (NSDictionary *)zoneToDict: (BDZoneInfo *)zone
{
    NSMutableDictionary  *dict = [ NSMutableDictionary new ];

    [ dict setObject:zone.name forKey:@"name"];
    [ dict setObject:zone.ID forKey:@"ID"];
    [ dict setObject:zone.customData != nil ? zone.customData : @{} forKey:@"customData"];
    return dict;
}

/*
 *  Return a NSDictionary with extrapolated fence details into
 */
- (NSDictionary *)fenceToDict: (BDFenceInfo *)fence
{
    NSMutableDictionary  *dict = [ NSMutableDictionary new ];

    [ dict setObject:fence.name forKey:@"name"];
    [ dict setObject:fence.ID forKey:@"ID"];

    return dict;
}

/*
 *  Return an NSDictionary with extrapolated location details into
 */
- (NSDictionary *)locationToDict: (BDLocationInfo *)location
{
    NSMutableDictionary  *dict = [ NSMutableDictionary new ];
    NSTimeInterval  unixDate = [ location.timestamp timeIntervalSince1970 ];
    
    [ dict setObject:@( unixDate ) forKey:@"unixDate"];
    [ dict setObject:@( location.latitude ) forKey:@"latitude"];
    [ dict setObject:@( location.longitude ) forKey:@"longitude"];
    [ dict setObject:@( location.bearing ) forKey:@"bearing"];
    [ dict setObject:@( location.speed ) forKey:@"speed"];

    return dict;
}

@end
