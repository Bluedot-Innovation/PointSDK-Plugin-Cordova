/****
 *  bdFunctions.js
 *
 *  This contains Javascript methods for usage in the HTML pages of the example app.
 *
 *
 * @author Bluedot Innovation
 * Copyright (c) 2018 Bluedot Innovation. All rights reserved.
 */

/*
 *  Setup session authentication credentials.
 *  These are obtained from creating an API in the Bluedot Point Access web site.
 */
const apiKey = "";

//  Set up enums
const zoneInfoEnum =
{
    name: 0,
    description: 1,
    ID: 2
}

const fenceInfoEnum =
{
    name: 0,
    description: 1,
    ID: 2
}

const beaconInfoEnum =
{
    name: 0,
    description: 1,
    ID: 2,
    isiBeacon: 3,
    proximityUUID: 4,
    major: 5,
    minor: 6,
    MACAddress: 7,
    lat: 8,
    lon: 9
}

const locationInfoEnum =
{
    timestamp: 0,
    latitude: 1,
    longitude: 2,
    bearing: 3,
    speed: 4
}

const proximityEnum =
{
    Unknown : 0,
    Immediate : 1,
    Near : 2,
    Far : 3,
    properties:
    {
        0: { name: "Unknown", value: 0, code: "U" },
        1: { name: "Immediate", value: 1, code: "I" },
        2: { name: "Near", value: 2, code: "N" },
        3: { name: "Far", value: 3, code: "F" }
    }
}

var metaDta = {};

/*
 *  Add text to the status area.
 */
function updateStatus( statusText )
{
    var textAreaId = document.getElementById( 'statusText' );

    textAreaId.value += statusText + "\n";
    textAreaId.scrollTop = textAreaId.scrollHeight
}

/*
 *  The delegate function for dealing with successful authentication.
 *  If authentication was successful but has an issue, then this is passed back to the app with
 *  an associated message.
 */
function authenticationSuccessful( errorCode, message )
{
    if ( typeof message === "undefined" || errorCode == 0 )
    {
        updateStatus( "Authentication successful" );
        metaDta['key'] = 'value';
        au.com.bluedot.setCustomEventMetaData(metaDta);
        //au.com.bluedot.setNotificationIDResourceID('app_icon');   //set icon as per requirement
    }
    else
    {
        updateStatus( "Authentication successful but " + message );
    }
}

/*
 *  The delegate function for dealing with a failed authentication.
 */
function authenticationFailed( error )
{
    updateStatus( "Authentication failed: " + error );
}

/*
 *  The delegate function for dealing with a successful log out from an authenticated session.
 */
function logOutSuccessful()
{
    updateStatus( "Log out successful" );
}

/*
 *  The delegate function for dealing with a failed log out from an authenticated session.
 */
function logOutFailed( error )
{
    updateStatus( "Log out failed: " + error );
}

/*
 *  Call the authentication process of the Bluedot Point SDK, passing in the details from the app.
 */
function doAuthenticate()
{
    //  Add the delegate functions for receiving data
    au.com.bluedot.zoneInfoCallback( zoneUpdate );
    au.com.bluedot.checkedIntoFenceCallback( fenceTrigger );
    au.com.bluedot.checkedIntoBeaconCallback( beaconTrigger );

    au.com.bluedot.checkedOutOfFenceCallback( fenceCheckOut );
    au.com.bluedot.checkedOutOfBeaconCallback( beaconCheckOut );

    var cordova = "Cordova";
    var title = "Location Based Notifications";
    var content = "--PLEASE CHANGE-- This app is utilizing the location to trigger alerts in both background and foreground modes when you visit your favourite locations."
    au.com.bluedot.foregroundNotification(cordova + "channelId", cordova + "channelName" , title , content , false);

    au.com.bluedot.authenticate( authenticationSuccessful, authenticationFailed, apiKey);
}

/*
 *  Call the log out process of the Bluedot Point SDK.
 */
function doLogOut()
{
    au.com.bluedot.logOut( logOutSuccessful, logOutFailed );
}

/*
 *  This delegate function receives an array of Zone Infos.
 *  Refer to bluedotPointSDKCDVPlugin.js for more information.
 */
function zoneUpdate( zoneInfos )
{
    updateStatus( "Zones info has been updated for " + zoneInfos.length + " zones" );

    //  Process each of the zones
    for( pos = 0; pos < zoneInfos.length; pos++ )
    {
        var zoneInfo = zoneInfos[ pos ];

        //  Extract details for a status update
        var name = zoneInfo[ zoneInfoEnum.name ];
        var description = zoneInfo[ zoneInfoEnum.description ];

        updateStatus( "Zone " + name + " : " + description );
    }
}

/*
 *  This delegate function receives the data of a fence with a Custom action that has been triggered by the SDK.
 *  Refer to bluedotPointSDKCDVPlugin.js for more information.
 */
function fenceTrigger( fenceInfo, zoneInfo, locationInfo, willCheckOut, customData )
{
    //  Extract details for a status update
    var fenceName = fenceInfo[ fenceInfoEnum.name ];
    var zoneName = zoneInfo[ zoneInfoEnum.name ];
    var lat = locationInfo [ locationInfoEnum.latitude ];
    var lon = locationInfo [ locationInfoEnum.longitude ];

    updateStatus( fenceName + " has been triggered in " + zoneName + " at " + lat + ":" + lon );
    updateStatus( JSON.stringify(customData) );

    if ( willCheckOut == true )
    {
        updateStatus( "Fence is awaiting check-out" );
    }
}

/*
 *  This delegate function receives the data of a fence with a Custom action that has been checked out of by the SDK.
 *  Refer to bluedotPointSDKCDVPlugin.js for more information.
 */
function fenceCheckOut( fenceInfo, zoneInfo, date, dwellTime, customData )
{
    //  Extract details for a status update
    var fenceName = fenceInfo[ fenceInfoEnum.name ];
    var zoneName = zoneInfo[ zoneInfoEnum.name ];

    updateStatus( fenceName + " has been left in " + zoneName + " after " + dwellTime + "minutes" );
    updateStatus( JSON.stringify(customData) );
}

/*
 *  This delegate function receives the data of a beacon with a Custom action that has been triggered by the SDK.
 *  Refer to bluedotPointSDKCDVPlugin.js for more information.
 */
function beaconTrigger( beaconInfo, zoneInfo, locationInfo, proximity, willCheckOut, customData )
{
    //  Extract details for a status update
    var beaconName = beaconInfo[ beaconInfoEnum.name ];
    var isiBeacon = beaconInfo[ beaconInfoEnum.isiBeacon ];
    var zoneName = zoneInfo[ zoneInfoEnum.name ];
    var proximityName = proximityEnum.properties[ proximity ].name;
    var lat = locationInfo [ locationInfoEnum.latitude ];
    var lon = locationInfo [ locationInfoEnum.longitude ];

    updateStatus( ( ( isiBeacon == true ) ? "iBeacon " : "" ) + beaconName + " has been triggered in " + zoneName + " with a proximity of " + proximityName + " at " + lat + ":" + lon  );
    updateStatus( JSON.stringify(customData) );

    if ( willCheckOut == true )
    {
        updateStatus( "Beacon is awaiting check-out" );
    }
}

/*
 *  This delegate function receives the data of a beacon with a Custom action that has been checked out of by the SDK.
 *  Refer to bluedotPointSDKCDVPlugin.js for more information.
 */
function beaconCheckOut( beaconInfo, zoneInfo, proximity, date, dwellTime, customData )
{
    //  Extract details for a status update
    var beaconName = beaconInfo[ beaconInfoEnum.name ];
    var isiBeacon = beaconInfo[ beaconInfoEnum.isiBeacon ];
    var zoneName = zoneInfo[ zoneInfoEnum.name ];
    var proximityName = proximityEnum.properties[ proximity ].name;

    updateStatus( ( ( isiBeacon == true ) ? "iBeacon " : "" ) + beaconName + " has been left in " + zoneName + " after " + dwellTime + " minutes" );
    updateStatus( JSON.stringify(customData) );
}

/*
 *  Setup the click processing for the Authentication button given that in-line Javascript is not
 *  allowed by default in the Cordova environment.
 *  Allowing for in-line Javascript causes the server connection to fail.
 */
document.addEventListener( 'DOMContentLoaded', function()
{
    document.getElementById( "authenticateButton" ).addEventListener( "click", doAuthenticate );
    document.getElementById( "logOutButton" ).addEventListener( "click", doLogOut );
} );
