/****
 *  bluedotPointSDKCDVPlugin.js
 *
 *  This contains Javascript wrapper methods for accessing the Bluedot Point SDK.
 *
 * Bluedot Innovation
 * Copyright (c) 2018 Bluedot Innovation. All rights reserved.
 */

//  Setup a standard Cordova environment
var argscheck = require( 'cordova/argscheck' ),
    utils = require( 'cordova/utils' ),
    exec = require( 'cordova/exec' );
        
exports.initializeWithProjectId = function( success, fail, projectId )
{
    exec( success, fail, "BDPointSDK", "initializeWithProjectId", [ projectId ] );
}

exports.isInitialized = function( success )
{
    exec( success, null, "BDPointSDK", "isInitialized", [] );
}
    
exports.reset = function( success, fail )
{
    exec( success, fail, "BDPointSDK", "reset", [] );
}
    
exports.startGeoTriggering = function( success, fail )
{
    exec( success, fail, "BDPointSDK", "startGeoTriggering", [] );
}
    
exports.startGeoTriggeringWithAppRestartNotification = function( success, fail )
{
    exec( success, fail, "BDPointSDK", "startGeoTriggeringWithAppRestartNotification", [] );
}
    
exports.stopGeoTriggering = function( success, fail )
{
    exec( success, fail, "BDPointSDK", "stopGeoTriggering", [] );
}
    
exports.startTempoWithDestinationId = function( success, fail, destinationId )
{
    exec( success, fail, "BDPointSDK", "startTempoWithDestinationId", [ destinationId] );
}

exports.stopTempoTracking = function( success, fail )
{
    exec( success, fail, "BDPointSDK", "stopTempoTracking", [] );
}

/*
 *  Provide a callback to receive Zone Info updates.  The callback is called with the following parameters:
 *      Parameter 1: Array of zones
 *          Array of strings identifying each zone:
 *              name (String)
 *              description (String)
 *              ID (String)
 */
exports.zoneInfoUpdateCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "zoneInfoUpdateCallback", [] );
}

/*
 *  Provide a multi-part callback for a fence with a Custom Action being checked into.
 *
 *  Returns the following multipart status to the callback function:
 *      Parameter 1: Array identifying fence:
 *          name (String)
 *          description (String)
 *          ID (String)
 *      Parameter 2: Array of strings identifying zone containing fence:
 *          name (String)
 *          description (String)
 *          ID (String)
 *      Parameter 3: Array of doubles identifying location which triggers fence:
 *          Date of check-in (Integer - UNIX timestamp)
 *          Latitude of check-in (Double)
 *          Longitude of check-in (Double)
 *          Bearing of check-in (Double)
 *          Speed of check-in (Double)
 *      Parameter 4: Fence is awaiting check-out (Boolean)
 *      Parameter 5: JSON Object of custom data (JSON Object)
 */
exports.enteredZoneCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "enteredZoneCallback", [] );
}

/*
 *  A Zone with a Custom Action has been checked out of.
 *
 *  Returns the following multipart status:
 *      Parameter 1: Array identifying fence:
 *          name (String)
 *          description (String)
 *          ID (String)
 *      Parameter 2: Array of strings identifying zone containing fence:
 *          name (String)
 *          description (String)
 *          ID (String)
 *      Parameter 3: Date of check-out (Integer - UNIX timestamp)
 *      Parameter 4: Dwell time in minutes (Unsigned integer)
 *      Parameter 5: JSON Object of custom data (JSON Object)
 */
exports.exitedZoneCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "exitedZoneCallback", [] );
}

    
exports.didStopTrackingWithErrorCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "didStopTrackingWithErrorCallback", [] );
}

exports.tempoTrackingExpiredCallback = function()
{
    exec( null, null, "BDPointSDK", "tempoTrackingExpiredCallback", [] );
}
    
/*
 *  Disable a zone from within the app using the Zone Id.
 */
exports.disableZone = function( success, fail, zoneId )
{
    exec( null, null, "BDPointSDK", "disableZone", [ zoneId ] );
}

/*
 *  Enable a zone previously disabled from within the app using the Zone Id.
 */
exports.enableZone = function( success, fail, zoneId )
{
    exec( null, null, "BDPointSDK", "enableZone", [ zoneId ] );
}

exports.notifyPushUpdate = function( userInfo )
{
    exec( null, null, "BDPointSDK", "notifyPushUpdate", [ userInfo ] );
}
    
exports.requestWhenInUseAuthorization = function()
{
    exec( null, null, "BDPointSDK", "requestWhenInUseAuthorization", [] );
}
    
exports.requestAlwaysAuthorization = function()
{
    exec( null, null, "BDPointSDK", "requestAlwaysAuthorization", [] );
}

/*
 *  Sets notification for service to run in foreground, required for Android O and above
 *  channelId (String) - channel Id of notifications
 *  channelName (String) - channel name of notifications
 *  title (String) - title of the notification
 *  content (String) - content of the notification
 *  targetAllAPIs (Bool) - TRUE to display notification on All Android version, FALSE to display only on Android O and above.
 *
 */
exports.foregroundNotification = function( channelId, channelName, title, content, targetAllAPIs )
{
    exec( null, null, "BDPointSDK", "foregroundNotification", [ channelId, channelName, title, content, targetAllAPIs ] );
}

/*
 *  Sets notification ID Resource ID for service to run in foreground, required for Android O and above
 *  resId (int) - resource Id of notifications
 *
 */
exports.setNotificationIDResourceID = function( resId )
{
    exec( null, null, "BDPointSDK", "setNotificationIDResourceID", [ resId ] );
}

/*
 *  Sets CustomEventMetaData map to be used on Key Value basis by applications
 *  customMetaData (Map<String,String>) - CustomEventMetaData map to be used on Key Value basis by applications
 *
 */
exports.setCustomEventMetaData = function( customMetaData )
{
    exec( null, null, "BDPointSDK", "setCustomEventMetaData", [ customMetaData ] );
}