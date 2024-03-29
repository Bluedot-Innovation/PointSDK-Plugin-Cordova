/****
 *  bluedotPointSDKCDVPlugin.js
 *
 *  This contains Javascript wrapper methods for accessing the Bluedot Point SDK.
 *
 * Bluedot Innovation
 * Copyright (c) 2021 Bluedot Innovation. All rights reserved.
 */

//  Setup a standard Cordova environment
var exec = require('cordova/exec');
        
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
    
exports.iOSStartGeoTriggering = function( success, fail )
{
    exec( success, fail, "BDPointSDK", "iOSStartGeoTriggering", [] );
}

exports.androidStartGeoTriggering = function( success,
                                              fail,
                                              channelId,
                                              channelName,
                                              androidNotificationTitle,
                                              androidNotificationContent,
                                              androidNotificationId )
{
    exec( success, fail, "BDPointSDK", "androidStartGeoTriggering",
        [channelId, channelName, androidNotificationTitle, androidNotificationContent, androidNotificationId] );
}
    
exports.iOSStartGeoTriggeringWithAppRestartNotification = function( success, fail, title, buttonText )
{
    exec( success, fail, "BDPointSDK", "iOSStartGeoTriggeringWithAppRestartNotification", [title, buttonText] );
}
    
exports.stopGeoTriggering = function( success, fail )
{
    exec( success, fail, "BDPointSDK", "stopGeoTriggering", [] );
}
    
exports.isGeoTriggeringRunning = function( success )
{
    exec( success, null, "BDPointSDK", "isGeoTriggeringRunning", [] );
}
    
exports.iOSStartTempoTracking = function( success, fail, destinationId )
{
    exec( success, fail, "BDPointSDK", "iOSStartTempoTracking", [ destinationId] );
}
              
exports.androidStartTempoTracking = function( 
    success,
    fail,
    destinationId,
    channelId,
    channelName,
    androidNotificationTitle,
    androidNotificationContent,
    androidNotificationId )
{
exec( success, fail, "BDPointSDK", "androidStartTempoTracking",
[destinationId, channelId, channelName, androidNotificationTitle, androidNotificationContent, androidNotificationId] );
}

exports.stopTempoTracking = function( success, fail )
{
    exec( success, fail, "BDPointSDK", "stopTempoTracking", [] );
}
    
exports.isTempoRunning = function( success )
{
    exec( success, null, "BDPointSDK", "isTempoRunning", [] );
}

exports.bluedotServiceDidReceiveErrorCallback = function( callback )
{
    exec( null, callback, "BDPointSDK", "bluedotServiceDidReceiveErrorCallback", [] );
}
    
exports.locationAuthorizationDidChangeCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "locationAuthorizationDidChangeCallback", [] );
}

exports.accuracyAuthorizationDidChangeCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "accuracyAuthorizationDidChangeCallback", [] );
}

exports.lowPowerModeDidChangeCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "lowPowerModeDidChangeCallback", [] );
}

exports.zoneInfoUpdateCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "zoneInfoUpdateCallback", [] );
}

exports.enteredZoneCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "enteredZoneCallback", [] );
}

exports.exitedZoneCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "exitedZoneCallback", [] );
}
    
exports.tempoStoppedWithErrorCallback = function( callback )
{
    exec( null, callback, "BDPointSDK", "tempoStoppedWithErrorCallback", [] );
}

exports.tempoTrackingExpiredCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "tempoTrackingExpiredCallback", [] );
}

exports.disableZone = function( zoneId )
{
    exec( null, null, "BDPointSDK", "disableZone", [ zoneId ] );
}

exports.enableZone = function( zoneId )
{
    exec( null, null, "BDPointSDK", "enableZone", [ zoneId ] );
}

exports.notifyPushUpdate = function( userInfo )
{
    exec( null, null, "BDPointSDK", "notifyPushUpdate", [ userInfo ] );
}

exports.setCustomEventMetaData = function( customMetaData )
{
    exec( null, null, "BDPointSDK", "setCustomEventMetaData", [ customMetaData ] );
}

exports.getZones = function(callback)
{
    exec( callback, null, "BDPointSDK", "getZones", [] );
}

exports.getSdkVersion = function(callback)
{
    exec( callback, null, "BDPointSDK", "getSdkVersion", [] );
}

exports.getInstallRef = function(callback)
{
    exec( callback, null, "BDPointSDK", "getInstallRef", [] );
}