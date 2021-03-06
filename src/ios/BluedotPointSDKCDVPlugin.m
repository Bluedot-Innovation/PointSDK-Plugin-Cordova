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
@interface BluedotPointSDKCDVPlugin() <BDPointDelegate>
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
    id  _callbackIdZoneInfo;
    id  _callbackIdCheckedIntoFence;
    id  _callbackIdCheckedIntoBeacon;

    id  _callbackIdCheckedOutOfFence;
    id  _callbackIdCheckedOutOfBeacon;

    id  _callbackIdStartRequiringUserInterventionForBluetooth;
    id  _callbackIdStopRequiringUserInterventionForBluetooth;
    id  _callbackIdStartRequiringUserInterventionForLocationServices;
    id  _callbackIdStopRequiringUserInterventionForLocationServices;

    //  A default date formatter
    NSDateFormatter  *_dateFormatter;

    //  Is currently authenticated
    BOOL  _authenticated;
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
    locationManager.locationDelegate = self;
    locationManager.sessionDelegate = self;

    //  Setup a generic date formatter
    _dateFormatter = [ NSDateFormatter new ];
    [ _dateFormatter setDateFormat: @"dd-MMM-yyyy HH:mm" ];

    //  Initialise identifiers - unneccessary but explicit
    _callbackIdAuthentication = nil;
    _callbackIdZoneInfo = nil;
    _callbackIdCheckedIntoFence = nil;
    _callbackIdCheckedIntoBeacon = nil;
    _callbackIdCheckedOutOfFence = nil;
    _callbackIdCheckedOutOfBeacon = nil;

    _callbackIdStartRequiringUserInterventionForLocationServices = nil;
    _callbackIdStopRequiringUserInterventionForLocationServices = nil;
    _callbackIdStartRequiringUserInterventionForBluetooth = nil;
    _callbackIdStopRequiringUserInterventionForBluetooth = nil;

    _authenticated = NO;
}

/*
 *  Entry method for authentication has 1 parameter: apiKey
 */
- (void)authenticate: (CDVInvokedUrlCommand *)command
{

    //  Ensure that the command has the minimum number of arguments
    if ( command.arguments.count < 1 )
    {
        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                            messageAsString: @"Incorrect number of arguments supplied to authenticate method." ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        return;
    }

    //  Retrieve the principle arguments for authentication
    NSString  *apiKey = command.arguments[0];
    BDAuthorizationLevel authorizationLevel = [command.arguments[1] isEqualToString:whenInUse] ? authorizedWhenInUse : authorizedAlways;

    //  If the app has already authenticated, then do not try to authenticate again
    if ( _authenticated == YES )
    {
        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                            messageAsString: @"Already authenticated - please log out from previous session." ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        return;
    }

    //  Ensure that the authentication process is thread-safe
    @synchronized( _callbackIdAuthentication )
    {
        if ( _callbackIdAuthentication != nil )
        {
            //  This is a duplicate call while authentication is already underway, return immediately
            CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                                messageAsString: @"Already authenticating..." ];

            [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
            return;
        }

        //  Set the authenticating callback
        _callbackIdAuthentication = command.callbackId;

        [BDLocationManager.instance authenticateWithApiKey: apiKey requestAuthorization: authorizationLevel];
    }
}

/*
 *  Entry method for logging out of a session.
 */
- (void)logOut: (CDVInvokedUrlCommand *)command
{

    //  If the app has not been authenticated, then do not try to log out
    if ( _authenticated == NO )
    {
        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                            messageAsString: @"There is no current session." ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: command.callbackId ];
        return;
    }

    //  Ensure that the log out process is thread-safe
    @synchronized( _callbackIdAuthentication )
    {
        _callbackIdAuthentication = command.callbackId;
        //  End the BD Point SDK session
        [ BDLocationManager.instance logOut ];
    }
}

/*
 *  Entry method for setting up the zone info delegate.
 */
- (void)zoneInfoCallback: (CDVInvokedUrlCommand *)command
{

    //  Set the zone info callback
    _callbackIdZoneInfo = command.callbackId;
}

/*
 *  Entry method for setting up the fence check-in delegate.
 */
- (void)checkedIntoFenceCallback: (CDVInvokedUrlCommand *)command
{

    //  Set the callback when triggering a fence
    _callbackIdCheckedIntoFence = command.callbackId;
}

/*
 *  Entry method for setting up the beacon check-in delegate.
 */
- (void)checkedIntoBeaconCallback: (CDVInvokedUrlCommand *)command
{

    //  Set the zone info callback when triggering a beacon
    _callbackIdCheckedIntoBeacon = command.callbackId;
}

/*
 *  Entry method for setting up the fence check-out delegate.
 */
- (void)checkedOutOfFenceCallback: (CDVInvokedUrlCommand *)command
{

    //  Set the callback when triggering a fence
    _callbackIdCheckedOutOfFence = command.callbackId;
}

/*
 *  Entry method for setting up the beacon check-out delegate.
 */
- (void)checkedOutOfBeaconCallback: (CDVInvokedUrlCommand *)command
{

    //  Set the zone info callback when triggering a beacon
    _callbackIdCheckedOutOfBeacon = command.callbackId;
}

/*
 *  Entry method for setting up the user intervention for Bluetooth delegates.
 */
- (void)startRequiringUserInterventionForBluetoothCallback: (CDVInvokedUrlCommand *)command
{

    //  Set the zone info callback when triggering a beacon
    _callbackIdStartRequiringUserInterventionForBluetooth = command.callbackId;
}

- (void)stopRequiringUserInterventionForBluetoothCallback: (CDVInvokedUrlCommand *)command
{

    //  Set the zone info callback when triggering a beacon
    _callbackIdStopRequiringUserInterventionForBluetooth = command.callbackId;
}

/*
 *  Entry method for setting up the user intervention for location services delegates.
 */
- (void)startRequiringUserInterventionForLocationServicesCallback: (CDVInvokedUrlCommand *)command
{

    //  Set the zone info callback when triggering a beacon
    _callbackIdStartRequiringUserInterventionForLocationServices = command.callbackId;
}

- (void)stopRequiringUserInterventionForLocationServicesCallback: (CDVInvokedUrlCommand *)command
{

    //  Set the zone info callback when triggering a beacon
    _callbackIdStopRequiringUserInterventionForLocationServices = command.callbackId;
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
        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
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

#pragma mark BDPointDelegate implementation begin

/*
 *  Called when an authentication is in process.
 */
- (void)willAuthenticateWithApiKey: (NSString *)apiKey
{
    NSLog( @"Authenticating Point service with [%@]", apiKey);;
}

/*
 *  Called when an authentication has been succesful.
 */
- (void)authenticationWasSuccessful
{

    //  Ensure that the authentication process is thread-safe
    @synchronized( _callbackIdAuthentication )
    {
        if ( _callbackIdAuthentication == nil )
        {
            NSLog( @"Internal error with authentication process" );
            return;
        }

        //  Authentication has been successful; on iOS there are no possible warning issues
        NSArray  *returnMultiPart = [ [ NSArray alloc ] initWithObjects: @( 0 ), nil ];

        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                                                         messageAsMultipart: returnMultiPart ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdAuthentication ];

        //  Reset the authentication callback
        _callbackIdAuthentication = nil;

        //  Session is authenticated
        _authenticated = YES;
    }
}

/*
 *  Called when an authentication has been denied from the server.
 */
- (void)authenticationWasDeniedWithReason: (NSString *)reason
{

    //  Ensure that the authentication process is thread-safe
    @synchronized( _callbackIdAuthentication )
    {
        if ( _callbackIdAuthentication == nil )
        {
            NSLog( @"Internal error with authentication process" );
            return;
        }

        //  Authentication has been succesful
        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                            messageAsString: reason ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdAuthentication ];

        //  Reset the authentication callback
        _callbackIdAuthentication = nil;

        //  Session is not authenticated
        _authenticated = NO;
    }
}

/*
 *  Called when a communications have failed during an authentication process.
 */
- (void)authenticationFailedWithError: (NSError *)error
{

    //  Ensure that the authentication process is thread-safe
    @synchronized( _callbackIdAuthentication )
    {
        if ( _callbackIdAuthentication == nil )
        {
            NSLog( @"Internal error with authentication process" );
            return;
        }

        //  Authentication has been succesful
        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                            messageAsString: error.localizedDescription ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdAuthentication ];

        //  Reset the authentication callback
        _callbackIdAuthentication = nil;

        //  Session is not authenticated
        _authenticated = NO;
    }
}

/*
 *  Called when the user logs out of an authenticated session.
 */
- (void)didEndSession
{
    NSLog( @"Logged out" );

    //  Ensure that the authentication process is thread-safe
    @synchronized( _callbackIdAuthentication )
    {
        //  Authentication has been succesful
        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK ];
        [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdAuthentication ];

        //  Reset the authentication callback
        _callbackIdAuthentication = nil;

        //  Session is not authenticated
        _authenticated = NO;
    }
}

- (void)didEndSessionWithError: (NSError *)error
{
    @synchronized (_callbackIdAuthentication)
    {
        if ( _callbackIdAuthentication == nil )
        {
            NSLog( @"Internal error with authentication process" );
            return;
        }

        //  Authentication has been succesful
        CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR
                                                            messageAsString: error.localizedDescription ];

        [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdAuthentication ];

        //  Reset the authentication callback
        _callbackIdAuthentication = nil;
    }
}

/*
 *  This method is passed the Zone information utilised by the Bluedot SDK.
 *
 *  Returning:
 *      Array of zones
 *          Array of strings identifying zone:
 *              name
 *              description
 *              ID
 */
- (void)didUpdateZoneInfo: (NSSet *)zones
{

    NSLog( @"Point service updated with %lu zones", (unsigned long)zones.count );

    //  Ensure that a delegate for zone info has been setup
    if ( _callbackIdZoneInfo == nil )
    {
        NSLog( @"Callback for zone information has not been setup." );
        return;
    }

    NSMutableArray  *returnZones = [ NSMutableArray new ];

    for( BDZoneInfo *zone in zones )
    {
        [ returnZones addObject: [ self zoneToArray: zone ] ];
    }

    CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                                                         messageAsArray: returnZones ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdZoneInfo ];
}

/*
 *  A fence with a Custom Action has been checked into.
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
- (void)didCheckIntoFence: (BDFenceInfo *)fence
                   inZone: (BDZoneInfo *)zone
               atLocation: (BDLocationInfo *)location
             willCheckOut: (BOOL)willCheckOut
           withCustomData: (NSDictionary *)customData
{

    NSLog( @"You have checked into fence '%@' in zone '%@', at %@%@",
          fence.name, zone.name, [ _dateFormatter stringFromDate: location.timestamp ],
          ( willCheckOut == YES ) ? @" and awaiting check out" : @"" );

    //  Ensure that a delegate for fence info has been setup
    if ( _callbackIdCheckedIntoFence == nil )
    {
        NSLog( @"Callback for fence check-ins has not been setup." );
        return;
    }

    NSArray  *returnFence = [ self fenceToArray: fence ];
    NSArray  *returnZone = [ self zoneToArray: zone ];
    NSArray  *returnLocation = [ self locationToArray: location ];

    NSArray  *returnMultiPart = [ [ NSArray alloc ] initWithObjects: returnFence, returnZone,
                                 returnLocation, @( willCheckOut ), customData, nil ];

    CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                                                     messageAsMultipart: returnMultiPart ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdCheckedIntoFence ];
}

/*
 *  A fence with a Custom Action has been checked out of.
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
- (void)didCheckOutFromFence: (BDFenceInfo *)fence
                      inZone: (BDZoneInfo *)zone
                      onDate: (NSDate *)date
                withDuration: (NSUInteger)checkedInDuration
              withCustomData: (NSDictionary *)customData
{

    NSLog( @"You left fence '%@' in zone '%@', after %u minutes",
          fence.name, zone.name, (unsigned int)checkedInDuration );

    //  Ensure that a delegate for fence info has been setup
    if ( _callbackIdCheckedOutOfFence == nil )
    {
        NSLog( @"Callback for fence check-outs has not been setup." );
        return;
    }

    NSArray  *returnFence = [ self fenceToArray: fence ];
    NSArray  *returnZone = [ self zoneToArray: zone ];
    NSTimeInterval  unixDate = [ date timeIntervalSince1970 ];

    NSArray  *returnMultiPart = [ [ NSArray alloc ] initWithObjects: returnFence, returnZone,
                                 @( unixDate ), @( checkedInDuration ), customData, nil ];

    CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                                                     messageAsMultipart: returnMultiPart ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdCheckedOutOfFence ];
}

/*
 *  A beacon with a Custom Action has been checked into.
 *
 *  Returns the following multipart status:
 *      Array identifying beacon:
 *          name (String)
 *          description (String)
 *          proximity UUID (String)
 *          major (Integer)
 *          minor (Integer)
 *          latitude (Double)
 *          longitude (Double)
 *      Array of strings identifying zone:
 *          name (String)
 *          description (String)
 *          ID (String)
 *      Array of double values identifying location:
 *          Date of check-in (Integer - UNIX timestamp)
 *          Latitude of beacon setting (Double)
 *          Longitude of beacon setting (Double)
 *          Bearing of beacon setting (Double)
 *          Speed of beacon setting (Double)
 *      Proximity of check-in to beacon (Integer)
 *          0 = Unknown
 *          1 = Immediate
 *          2 = Near
 *          3 = Far
 *      Beacon is awaiting check-out (BOOL)
 *      Custom fields setup in the <b>Point Access</b> web-interface.</p>
 */
- (void)didCheckIntoBeacon: (BDBeaconInfo *)beacon
                    inZone: (BDZoneInfo *)zone
                atLocation: (BDLocationInfo *)location
             withProximity: (CLProximity)proximity
              willCheckOut: (BOOL)willCheckOut
            withCustomData: (NSDictionary *)customData
{

    NSLog( @"You have checked into beacon '%@' in zone '%@' with proximity %d at %@%@",
          beacon.name, zone.name, (int)proximity, [ _dateFormatter stringFromDate: location.timestamp ],
          ( willCheckOut == YES ) ? @" and awaiting check out" : @"" );

    //  Ensure that a delegate for fence info has been setup
    if ( _callbackIdCheckedIntoBeacon == nil )
    {
        NSLog( @"Callback for beacon check-ins has not been setup." );
        return;
    }

    NSArray  *returnBeacon = [ self beaconToArray: beacon ];
    NSArray  *returnZone = [ self zoneToArray: zone ];
    NSArray  *returnLocation = [ self locationToArray: location ];

    NSArray  *returnMultiPart = [ [ NSArray alloc ] initWithObjects: returnBeacon, returnZone,
                                 returnLocation, @( proximity ), @( willCheckOut ), customData, nil ];

    CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                                                     messageAsMultipart: returnMultiPart ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdCheckedIntoBeacon ];
}

/*
 *  A beacon with a Custom Action has been checked out of.
 *
 *  Returns the following multipart status:
 *      Array identifying beacon:
 *          name (String)
 *          description (String)
 *          proximity UUID (String)
 *          major (Integer)
 *          minor (Integer)
 *          latitude (Double)
 *          longitude (Double)
 *      Array of strings identifying zone:
 *          name (String)
 *          description (String)
 *          ID (String)
 *      Proximity of check-in to beacon (Integer)
 *          0 = Unknown
 *          1 = Immediate
 *          2 = Near
 *          3 = Far
 *      Date of check-in (Integer - UNIX timestamp)
 *      Dwell time in minutes (Unsigned integer)
 */
- (void)didCheckOutFromBeacon: (BDBeaconInfo *)beacon
                       inZone: (BDZoneInfo *)zone
                withProximity: (CLProximity)proximity
                       onDate: (NSDate *)date
                 withDuration: (NSUInteger)checkedInDuration
               withCustomData: (NSDictionary *)customData
{

    NSLog( @"You have left beacon '%@' in zone '%@' with proximity %d at %@ after %u minutes",
          beacon.name, zone.name, (int)proximity, [ _dateFormatter stringFromDate: date ],
          (unsigned int)checkedInDuration );

    //  Ensure that a delegate for fence info has been setup
    if ( _callbackIdCheckedOutOfBeacon == nil )
    {
        NSLog( @"Callback for beacon check-outs has not been setup." );
        return;
    }

    NSArray  *returnBeacon = [ self beaconToArray: beacon ];
    NSArray  *returnZone = [ self zoneToArray: zone ];
    NSTimeInterval  unixDate = [ date timeIntervalSince1970 ];

    NSArray  *returnMultiPart = [ [ NSArray alloc ] initWithObjects: returnBeacon, returnZone,
                                 @( proximity ), @( unixDate ), @( checkedInDuration ), customData, nil ];

    CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK
                                                     messageAsMultipart: returnMultiPart ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult callbackId: _callbackIdCheckedOutOfBeacon ];
}

/*
 *  This method is part of the Bluedot location delegate and is called when Bluetooth is required by the SDK but is not
 *  enabled on the device; requiring user intervention.
 */
- (void)didStartRequiringUserInterventionForBluetooth
{
    NSLog( @"There are nearby Beacons which cannot be detected because Bluetooth is disabled."
          "Re-enable Bluetooth to restore full functionality." );

    //  Ensure that a delegate for Bluetooth user intervention has been setup
    if ( _callbackIdStartRequiringUserInterventionForBluetooth == nil )
    {
        NSLog( @"Callback for start requiring user intervention for Bluetooth has not been setup." );
        return;
    }

    CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult
                                 callbackId: _callbackIdStartRequiringUserInterventionForBluetooth ];
}

/*
 *  This method is part of the Bluedot location delegate; it is called if user intervention on the device had previously
 *  been required to enable Bluetooth and either user intervention has enabled Bluetooth or the Bluetooth service is
 *  no longer required.
 */
- (void)didStopRequiringUserInterventionForBluetooth
{
    NSLog( @"User intervention for Bluetooth is no longer required." );

    //  Ensure that a delegate for Bluetooth user intervention has been setup
    if ( _callbackIdStopRequiringUserInterventionForBluetooth == nil )
    {
        NSLog( @"Callback for stop requiring user intervention for Bluetooth has not been setup." );
        return;
    }

    CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult
                                 callbackId: _callbackIdStopRequiringUserInterventionForBluetooth ];
}

/*
 *  This method is part of the Bluedot location delegate and is called when Location Services are not enabled
 *  on the device; requiring user intervention.
 */
- (void)didStartRequiringUserInterventionForLocationServicesAuthorizationStatus: (CLAuthorizationStatus)authorizationStatus
{
    NSString  *authorizationStatusString = (authorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ? @"while in using" : @"disabled");
    NSLog( @"This App requires Location Services which are currently set to %@.", authorizationStatusString );

    //  Ensure that a delegate for Location Services user intervention has been setup
    if ( _callbackIdStartRequiringUserInterventionForLocationServices == nil )
    {
        NSLog( @"Callback for start requiring user intervention for Location Services has not been setup." );
        return;
    }

    CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult
                                 callbackId: _callbackIdStartRequiringUserInterventionForLocationServices ];
}

/*
 *  This method is part of the Bluedot location delegate; it is called if user intervention on the device had previously
 *  been required to enable Location Services and either Location Services has been enabled or the user is no longer
 *  within an authenticated session, thereby no longer requiring Location Services.
 */
- (void)didStopRequiringUserInterventionForLocationServicesAuthorizationStatus: (CLAuthorizationStatus)authorizationStatus
{
    NSLog( @"User intervention for location services is no longer required" );

    //  Ensure that a delegate for Location Services user intervention has been setup
    if ( _callbackIdStopRequiringUserInterventionForLocationServices == nil )
    {
        NSLog( @"Callback for stop requiring user intervention for Location Services has not been setup." );
        return;
    }

    CDVPluginResult  *pluginResult = [ CDVPluginResult resultWithStatus: CDVCommandStatus_OK ];

    //  Keep the callback after returning the result
    pluginResult.keepCallback = @(YES);

    [ self.commandDelegate sendPluginResult: pluginResult
                                 callbackId: _callbackIdStopRequiringUserInterventionForLocationServices ];
}

#pragma mark BDPointDelegate implementation end


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
 *  Return an array with extrapolated beacon details into Cordova variable types.
 *      Array identifying beacon:
 *          name (String)
 *          description (String)
 *          ID (String)
 *          isiBeacon (BOOL)
 *          proximity UUID (String)
 *          major (Integer)
 *          minor (Integer)
 *          MAC address (String)
 *          latitude (Double)
 *          longitude (Double)
 */
- (NSArray *)beaconToArray: (BDBeaconInfo *)beacon
{
    NSMutableArray  *objs = [ NSMutableArray new ];

    [ objs addObject: beacon.name ];
    [ objs addObject: ( beacon.description == nil ) ? @"" : beacon.description ];
    [ objs addObject: beacon.ID ];

    [ objs addObject: @(YES) ];
    [ objs addObject: beacon.proximityUuid ];
    [ objs addObject: @( beacon.major ) ];
    [ objs addObject: @( beacon.minor ) ];

    //  Arrays cannot contain nil, add an NSNULL object
    [ objs addObject: [ NSNull null ] ];

    [ objs addObject: @( beacon.location.latitude ) ];
    [ objs addObject: @( beacon.location.longitude ) ];

    return objs;
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

@end
