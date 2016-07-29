/****
 *  bluedotPointSDKCDVPlugin.js
 *
 *  This contains Javascript wrapper methods for accessing the Bluedot Point SDK.
 *
 *  Roddy McNeill  5/10/15
 *  (c) Bluedot Innovations
 */

//  Setup a standard Cordova environment
var argscheck = require( 'cordova/argscheck' ),
    utils = require( 'cordova/utils' ),
    exec = require( 'cordova/exec' );


/*
 *  Authenticate a Bluedot Point session.
 *  The success function call provides an error code and an optional string for any warnings after successful
 *  authentication.
 *      An error code of 0 entails no additional warnings.
 *  The fail function call provides a string with the reason for failure.
 */
exports.authenticate = function( success, fail, username, apiKey, packageName )
{
    exec( success, fail, "BDPointSDK", "authenticate", [ username, apiKey, packageName ] );
}

/*
 *  Log out of an authenticated session with Bluedot Point.
 *  The fail function call provides a string with the reason for failure.
 */
exports.logOut = function( success, fail )
{
    exec( success, fail, "BDPointSDK", "logOut", [] );
}

/*
 *  Provide a callback to receive Zone Info updates.  The callback is called with the following parameters:
 *      Parameter 1: Array of zones
 *          Array of strings identifying each zone:
 *              name (String)
 *              description (String)
 *              ID (String)
 */
exports.zoneInfoCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "zoneInfoCallback", [] );
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
 *      Parameter 3: Latitude of check-in (Double)
 *      Parameter 4: Longitude of check-in (Double)
 *      Parameter 5: Date of check-in (Integer - UNIX timestamp)
 *      Parameter 6: Fence is awaiting check-out (Boolean)
 *      Parameter 7: JSON Object of custom data (JSON Object)
 */
exports.checkedIntoFenceCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "checkedIntoFenceCallback", [] );
}

/*
 *  A fence with a Custom Action has been checked out of.
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
exports.checkedOutOfFenceCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "checkedOutOfFenceCallback", [] );
}

/*
 *  Provide a callback for a beacon with a Custom Action being checked into.  The isiBeacon boolean is used to determine
 *  if the proximityUUID/major/minor should be utilised or the MAC Address.
 *
 *  Returns the following multipart status:
 *      Parameter 1: Array identifying beacon:
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
 *      Parameter 2: Array of strings identifying zone containing beacon:
 *          name (String)
 *          description (String)
 *          ID (String)
 *      Parameter 3: Proximity of check-in to beacon (Integer)
 *          0 = Unknown
 *          1 = Immediate
 *          2 = Near
 *          3 = Far
 *      Parameter 4: Date of check-in (Integer - UNIX timestamp)
 *      Parameter 5: Beacon is awaiting check-out (Boolean)
 *      Parameter 6: JSON Object of custom data (JSON Object)
 */
exports.checkedIntoBeaconCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "checkedIntoBeaconCallback", [] );
}

/*
 *  Provide a callback for a beacon with a Custom Action being checked out of.  The isiBeacon boolean is used to determine
 *  if the proximityUUID/major/minor should be utilised or the MAC Address.
 *
 *  Returns the following multipart status:
 *      Parameter 1: Array identifying beacon:
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
 *      Parameter 2: Array of strings identifying zone containing beacon:
 *          name (String)
 *          description (String)
 *          ID (String)
 *      Parameter 3: Proximity of check-in to beacon (Integer)
 *          0 = Unknown
 *          1 = Immediate
 *          2 = Near
 *          3 = Far
 *      Parameter 4: Date of check-in (Integer - UNIX timestamp)
 *      Parameter 5: Dwell time in minutes (Unsigned integer)
 *      Parameter 6: JSON Object of custom data (JSON Object)
 */
exports.checkedOutOfBeaconCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "checkedOutOfBeaconCallback", [] );
}

/*
 *  Provide a callback to be notified when user intervention is required for Bluetooth on the device.
 */
exports.startRequiringUserInterventionForBluetoothCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "startRequiringUserInterventionForBluetoothCallback", [] );
}

/*
 *  Provide a callback to be notified when user intervention is no longer required for Bluetooth on the device.
 */
exports.stopRequiringUserInterventionForBluetoothCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "stopRequiringUserInterventionForBluetoothCallback", [] );
}

/*
 *  Provide a callback to be notified when user intervention is required for Location Services on the device.
 */
exports.startRequiringUserInterventionForLocationServicesCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "startRequiringUserInterventionForLocationServicesCallback", [] );
}

/*
 *  Provide a callback to be notified when user intervention is no longer required for Location Services on the device.
 */
exports.stopRequiringUserInterventionForLocationServicesCallback = function( callback )
{
    exec( callback, null, "BDPointSDK", "stopRequiringUserInterventionForLocationServicesCallback", [] );
}

/*
 *  Disable a zone from within the app using the Zone Id.
 */
exports.disableZone = function( success, fail, zoneId )
{
    exec( success, fail, "BDPointSDK", "disableZone", [ zoneId ] );
}

/*
 *  Enable a zone previously disabled from within the app using the Zone Id.
 */
exports.enableZone = function( success, fail, zoneId )
{
    exec( success, fail, "BDPointSDK", "enableZone", [ zoneId ] );
}
