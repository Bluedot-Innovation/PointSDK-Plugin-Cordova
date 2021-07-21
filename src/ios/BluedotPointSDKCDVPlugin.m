/****
 *  BluedotPointSDKCDVPlugin.m
 *
 *  This is the entry point into the plug-in; these methods provide access for both the
 *  the iOS and Android SDKs.
 *
 * Bluedot Innovation
 * Copyright (c) 2018 Bluedot Innovation. All rights reserved.
 */

#import "BluedotPointSDKCDVPlugin.h"
@import BDPointSDK;

static const NSString *whenInUse = @"WhenInUse";
/*
 *  Anonymous category to implement the Bluedot point delegates, including:
 *      Session
 *      Location
 */
@interface BluedotPointSDKCDVPlugin() <BDPGeoTriggeringEventDelegate, BDPTempoTrackingDelegate>
@end


@implementation BluedotPointSDKCDVPlugin
{
    /*
     *  Session delegate callback funtion identifier.
     */
    id  _callbackIdAuthentication;

    /*
     *  Callback identifiers for the Bluedot Location delegates.
     */
    id  _callbackIdZoneInfoUpdate;
    id  _callbackIdEnteredZone;
    id  _callbackIdExitedZone;
    id  _callbackIdDidStopTracking;
    id  _callbackIdTempoTrackingExpired;

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
    locationManager.geoTriggeringEventDelegate = self;
    locationManager.tempoTrackingDelegate = self;

    //  Setup a generic date formatter
    _dateFormatter = [ NSDateFormatter new ];
    [ _dateFormatter setDateFormat: @"dd-MMM-yyyy HH:mm" ];

    //  Initialise identifiers - unneccessary but explicit
    _callbackIdAuthentication = nil;
    _callbackIdZoneInfoUpdate = nil;
    _callbackIdEnteredZone = nil;
    _callbackIdExitedZone = nil;
    _callbackIdDidStopTracking = nil;
    _callbackIdTempoTrackingExpired = nil;
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
                             messageAsString: [NSString stringWithFormat:
                @"Initialization successful"]
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

- (void)requestWhenInUseAuthorization:(CDVInvokedUrlCommand *)command
{
    [[BDLocationManager instance] requestWhenInUseAuthorization];
}

- (void)requestAlwaysAuthorization:(CDVInvokedUrlCommand *)command
{
    [[BDLocationManager instance] requestAlwaysAuthorization];
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
                             messageAsString: [NSString stringWithFormat:
                @"Reset successful"]
        ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        
        return;
    }];
}

- (void)startGeoTriggering:(CDVInvokedUrlCommand *)command
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
                             messageAsString: @"Start GeoTriggering successful"
        ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        
        return;
    }];
}

- (void)startGeoTriggeringWithAppRestartNotification:(CDVInvokedUrlCommand *)command
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
    NSString *buttonText = command.arguments[0];
    
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
                             messageAsString: [NSString stringWithFormat:
                @"startGeoTriggeringWithAppRestartNotification successful"]
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
                             messageAsString: [NSString stringWithFormat:
                @"stopGeoTriggering successful"]
        ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        
        return;
    }];
}

- (void)startTempoWithDestinationId:(CDVInvokedUrlCommand *)command
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
                             messageAsString: [NSString stringWithFormat:
                @"Start Tempo successful"]
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
                             messageAsString: [NSString stringWithFormat:
                @"Stop Tempo successful"]
        ];
        
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        
        return;
    }];
}

/*
 *  Disable a zone using the Zone Id.
 */
- (void)disableZone: (CDVInvokedUrlCommand *)command
{
    [ self setZone: command disableByApplication: YES ];
}

/*
 *  Re-enable a zone previously disabled by the app using the Zone Id.
 */
- (void)enableZone: (CDVInvokedUrlCommand *)command
{
    [ self setZone: command disableByApplication: NO ];
}


- (void)setZone: (CDVInvokedUrlCommand *)command disableByApplication: (BOOL)disable
{

    //  Ensure that the command has the minimum number of arguments
    if ( command.arguments.count < 1 )
    {
        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                            messageAsString: @"No zone id parameter supplied." ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        return;
    }

    //  Retrieve the principle arguments for authentication
    NSString  *zoneId = command.arguments[0];

    [ BDLocationManager.instance setZone: zoneId disableByApplication: disable ];

    CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK ];

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
}

- (void)notifyPushUpdate: (CDVInvokedUrlCommand *)command
{
    //  Ensure that the command has the minimum number of arguments
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
    //  Ensure that the command has the minimum number of arguments
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

/*
 *  Return an array with extrapolated zone details into Cordova variable types.
 */
- (NSArray *)zoneToArray: (BDZoneInfo *)zone
{
    NSMutableArray  *strings = [ NSMutableArray new ];

    [ strings addObject: zone.name ];
    [ strings addObject: ( zone.description == nil ) ? @"" : zone.description ];
    [ strings addObject: zone.ID ];

    return strings;
}

/*
 *  Return an array with extrapolated fence details into Cordova variable types.
 *      Array identifying fence:
 *          name (String)
 *          description (String)
 *          ID (String)
 */
- (NSArray *)fenceToArray: (BDFenceInfo *)fence
{
    NSMutableArray  *strings = [ NSMutableArray new ];

    [ strings addObject: fence.name ];
    [ strings addObject: ( fence.description == nil ) ? @"" : fence.description ];
    [ strings addObject: fence.ID ];

    return strings;
}

/*
 *  Return an array with extrapolated location details into Cordova variable types.
 *      Array identifying location:
 *          Date of check-in (Integer - UNIX timestamp)
 *          Latitude of check-in (Double)
 *          Longitude of check-in (Double)
 *          Bearing of check-in (Double)
 *          Speed of check-in (Double)
 */
- (NSArray *)locationToArray: (BDLocationInfo *)location
{
    NSMutableArray  *doubles = [ NSMutableArray new ];

    NSTimeInterval  unixDate = [ location.timestamp timeIntervalSince1970 ];
    [ doubles addObject: @( unixDate ) ];
    [ doubles addObject: @( location.latitude ) ];
    [ doubles addObject: @( location.longitude ) ];
    [ doubles addObject: @( location.bearing ) ];
    [ doubles addObject: @( location.speed ) ];

    return doubles;
}

#pragma mark BDPGeoTriggeringEventDelegate implementation begin

/*
 *  A zone update event has occured, and zoneInfo is passed into this function by the Bluedot SDK.
 *
 *  Returning:
 *      Array of zones
 *          Array of strings identifying zone:
 *              name
 *              description
 *              ID
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
        [ returnZones addObject: [ self zoneToArray: zone ] ];
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
        NSLog( @"Callback for fence check-ins has not been setup." );
        return;
    }

    NSArray  *returnFence = [ self fenceToArray: enterEvent.fence ];
    NSArray  *returnZone = [ self zoneToArray: enterEvent.zone ];
    NSArray  *returnLocation = [ self locationToArray: enterEvent.location ];

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

/*
 *  A Zone with a Custom Action has been exited from
 *
 *  Returns the following multipart status:
 *      Array identifying fence:
 *          name (String)
 *          description (String)
 *      Array of strings identifying zone:
 *          name (String)
 *          description (String)
 *          ID (String)
 *      Date of check-out (Integer - UNIX timestamp)
 *      Dwell time in minutes (Unsigned integer)
 */
- (void)didExitZone:(BDZoneExitEvent *)exitEvent
{
    NSLog( @"You left fence '%@' in zone '%@', after %u minutes",
          exitEvent.fence.name, exitEvent.zone.name, (unsigned int)exitEvent.duration );

    //  Ensure that a delegate for fence info has been setup
    if ( _callbackIdExitedZone == nil )
    {
        NSLog( @"Callback for fence check-outs has not been setup." );
        return;
    }

    NSArray  *returnFence = [ self fenceToArray: exitEvent.fence ];
    NSArray  *returnZone = [ self zoneToArray: exitEvent.zone ];
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

/*
 *  Entry method for setting up the zone info delegate.
 */
- (void)zoneInfoUpdateCallback: (CDVInvokedUrlCommand *)command
{
    //  Set the zone info callback
    _callbackIdZoneInfoUpdate = command.callbackId;
}

/*
 *  Entry method for setting up the zone check-in delegate.
 */
- (void)enteredZoneCallback: (CDVInvokedUrlCommand *)command
{
    //  Set the callback when triggering a zone
    _callbackIdEnteredZone = command.callbackId;
}


/*
 *  Entry method for setting up the fence check-out delegate.
 */
- (void)exitedZoneCallback: (CDVInvokedUrlCommand *)command
{
    //  Set the callback when triggering a fence
    _callbackIdExitedZone = command.callbackId;
}

#pragma mark BDPGeoTriggeringEventDelegate implementation end

#pragma mark BDPTempoTrackingDelegate implementation begin

/*
*   Tempo Tracking stopped with an error
*
*   Returns the following:
*      Error:
*          error (String)
*/
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
        CDVPluginResult resultWithStatus: CDVCommandStatus_OK
    ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdDidStopTracking];
}

/*
*   Tempo Tracking expired
*/
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
                         messageAsString: @"Tempo Tracking Expired"
    ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdTempoTrackingExpired];

}

/*
 *  Entry method for setting up the didStopTrackingWithError delegate.
 */
- (void)didStopTrackingWithErrorCallback: (CDVInvokedUrlCommand *)command
{
    //  Set the callback when triggering a fence
    _callbackIdDidStopTracking = command.callbackId;
}

/*
 *  Entry method for setting up the tempoTrackingDidExpire delegate.
 */
- (void)tempoTrackingExpiredCallback: (CDVInvokedUrlCommand *)command
{
    //  Set the callback when triggering a fence
    _callbackIdTempoTrackingExpired = command.callbackId;
}

#pragma mark BDPTempoTrackingDelegate implementation end


@end
