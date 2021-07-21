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

// Your Project Id
const projectId = "YOUR_PROJECT_ID";

// Your account will need to be Tempo Enabled to access this feature.
const destinationId = "YOUR_DESTINATION_ID"; 

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

const locationInfoEnum =
{
    timestamp: 0,
    latitude: 1,
    longitude: 2,
    bearing: 3,
    speed: 4
}

/*
 *  Add text to the status area.
 */
function updateStatus( statusText )
{
    var textAreaId = document.getElementById( 'statusText' );
    let timestamp = new Intl.DateTimeFormat('en',
                                            {
                                               month: 'long',
                                               day: 'numeric',
                                               year: 'numeric',
                                               hour: 'numeric',
                                               minute: 'numeric',
                                               second: 'numeric',
                                               hourCycle: 'h23'
                                            }
                                           ).format(new Date())
    
    textAreaId.value += timestamp + ": " +  statusText + "\n";
    textAreaId.scrollTop = textAreaId.scrollHeight
}

function startGeoTriggeringSuccessful( message )
{
    updateStatus( message );
}

function startGeoTriggeringFailed( error )
{
    updateStatus( error );
}

function stopGeoTriggeringSuccessful( message )
{
    updateStatus( message );
}

function stopGeoTriggeringFailed( error )
{
    updateStatus( error );
}

function startTempoTrackingSuccessful( message )
{
    updateStatus( message );
}

function startTempoTrackingFailed( error )
{
    updateStatus( error );
}

function stopTempoTrackingSuccessful( message )
{
    updateStatus( message );
}

function stopTempoTrackingFailed( error )
{
    updateStatus( error );
}

function isInitializedCallback( isInitialized )
{
    updateStatus( "Is Initialized: " + isInitialized );
}

function initializationSuccessful( message )
{
    updateStatus( message );
    au.com.bluedot.requestAlwaysAuthorization();
}

function initializationFailed( error )
{
    updateStatus( "Initialization failed: " + error );
}

/*
 *  The delegate function for dealing with a successful log out from an authenticated session.
 */
function resetSuccessful()
{
    updateStatus( "Reset successful" );
}

/*
 *  The delegate function for dealing with a failed log out from an authenticated session.
 */
function resetFailed( error )
{
    updateStatus( "Reset failed: " + error );
}

/*
 *  Call the authentication process of the Bluedot Point SDK, passing in the details from the app.
 */
function doInitialize()
{
    updateStatus( "Initializing..." );
    //  Add the delegate functions for receiving data
    au.com.bluedot.zoneInfoUpdateCallback( zoneUpdate );
    au.com.bluedot.enteredZoneCallback( zoneEntered );
    au.com.bluedot.exitedZoneCallback( zoneExited );
    au.com.bluedot.didStopTrackingWithErrorCallback( tempoTrackingStoppedWithError );
    au.com.bluedot.tempoTrackingExpiredCallback( tempoTrackingExpired );

    au.com.bluedot.setCustomEventMetaData( { "testKey": "testValue" } )
    updateStatus( "Set CustomEventMetadata { \"testKey\": \"testValue\" }" );
    //console(au.com.bluedot.BDAuthorizationLevel.WhenInUse);
    
    au.com.bluedot.initializeWithProjectId(initializationSuccessful, initializationFailed, projectId);
}

/*
 *  Call the isInitialized function of the Bluedot Point SDK.
 */
function doIsInitialized()
{
    au.com.bluedot.isInitialized( isInitializedCallback );
}


/*
 *  Call the reset function of the Bluedot Point SDK.
 */
function doReset()
{
    au.com.bluedot.reset( resetSuccessful, resetFailed );
}

/*
 *  Call the Start GeoTriggering function of the Bluedot Point SDK.
 */
function doStartGeoTriggering()
{
    updateStatus("Starting GeoTriggering...");
    au.com.bluedot.startGeoTriggering( startGeoTriggeringSuccessful, startGeoTriggeringFailed);
}

/*
 *  Call the Stop GeoTriggering function of the Bluedot Point SDK.
 */
function doStopGeoTriggering()
{
    updateStatus("Stopping GeoTriggering...");
    au.com.bluedot.stopGeoTriggering( stopGeoTriggeringSuccessful, stopGeoTriggeringFailed);
}

/*
 *  Call the Start Tempo Tracking function of the Bluedot Point SDK.
 */
function doStartTempoTracking()
{
    updateStatus("Starting Tempo...");
    au.com.bluedot.startTempoWithDestinationId( startTempoTrackingSuccessful, startTempoTrackingFailed, destinationId);
}

/*
 *  Call the Stop Tempo Tracking function of the Bluedot Point SDK.
 */
function doStopTempoTracking()
{
    updateStatus("Stopping Tempo...");
    au.com.bluedot.stopTempoTracking( stopTempoTrackingSuccessful, stopTempoTrackingFailed);
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

        console.log( "Zone " + name + " : " + description );
    }
}

/*
 *  This delegate function receives the data of a Zone with a Custom action that has been triggered by the SDK.
 *  Refer to bluedotPointSDKCDVPlugin.js for more information.
 */
function zoneEntered( fenceInfo, zoneInfo, locationInfo, willCheckOut, customData )
{
    //  Extract details for a status update
    var fenceName = fenceInfo[ fenceInfoEnum.name ];
    var zoneName = zoneInfo[ zoneInfoEnum.name ];
    var lat = locationInfo [ locationInfoEnum.latitude ];
    var lon = locationInfo [ locationInfoEnum.longitude ];

    updateStatus( fenceName + " has been triggered in " + zoneName + " at " + lat + ":" + lon );
    
    if(customData)
    {
        console.log( JSON.stringify(customData) );
    }

    if ( willCheckOut == true )
    {
        updateStatus( "Zone is awaiting check-out" );
    }
}

/*
 *  This delegate function receives the data of a fence with a Custom action that has been checked out of by the SDK.
 *  Refer to bluedotPointSDKCDVPlugin.js for more information.
 */
function zoneExited( fenceInfo, zoneInfo, date, dwellTime, customData )
{
    //  Extract details for a status update
    var fenceName = fenceInfo[ fenceInfoEnum.name ];
    var zoneName = zoneInfo[ zoneInfoEnum.name ];

    updateStatus( fenceName + " has been left in " + zoneName + " after " + dwellTime + "minutes" );
    updateStatus( JSON.stringify(customData) );
}

function tempoTrackingStoppedWithError(error)
{
    updateStatus(error);
}

function tempoTrackingExpired()
{
    updateStatus("Tempo Tracking Expired");
}

/*
 *  Setup the click processing for the Authentication button given that in-line Javascript is not
 *  allowed by default in the Cordova environment.
 *  Allowing for in-line Javascript causes the server connection to fail.
 */
document.addEventListener( 'DOMContentLoaded', function()
{
    document.getElementById( "initializeButton" ).addEventListener( "click", doInitialize );
    document.getElementById( "resetButton" ).addEventListener( "click", doReset );
    document.getElementById( "isInitializedButton" ).addEventListener( "click", doIsInitialized );
    document.getElementById( "startGeoTriggeringButton" ).addEventListener( "click", doStartGeoTriggering );
    document.getElementById( "stopGeoTriggeringButton" ).addEventListener( "click", doStopGeoTriggering  );
    document.getElementById( "startTempoTrackingButton" ).addEventListener( "click", doStartTempoTracking );
    document.getElementById( "stopTempoTrackingButton" ).addEventListener( "click", doStopTempoTracking );
});
