# Bluedot Point SDK

The Bluedot Point SDK combines low-energy Geolines™, Geofencing and proximity-Beacon capabilities to provide 'always-on' location awareness in indoor and outdoor scenarios on iOS and Android devices.

By integrating the simple Bluedot API, your apps will benefit from accurate location awareness (up to the maximum achievable precision of GPS), while substantially reducing battery consumption compared to Core Location and other standard SDK methods.

Point SDK connects to the Bluedot web interface, Point Access (www.pointaccess.bluedot.com.au), through which business rules comprising of Zones, Geolines™, Geofences, Beacons, Conditions and Actions can be configured.

Applications powered by the Point SDK will then download and execute such rules, both in the foreground and background.  The following provides a guide to integrate Point SDK with a Cordova or PhoneGap app across iOS and Android devices.

## Adding Bluedot Dynamic framework to the Bluedot plug-in
Contact Bluedot support <support@bluedotinnovation.com> and get the latest version of Bluedot Dynamic framework . Add BDPointSDK.framework under src\ios\ of cordova-bluedot-plugin.

## Installing the Bluedot plug-in
Run the following standard command to add the plug-in to your Cordova app:

    cordova plugin add bluedot-plugin

### Supported platforms
The Bluedot plugin supports iOS and Android platforms.  These can be added to your app with the following commands:

    cordova platform add ios
    cordova platform add android

### Provided wrappers
To assist in getting your app up and running as quickly as possible, Javascript function wrappers are provided in the Bluedot plugin repository at the following location:
js/bdFunctions.js

These functions provide sample code for the SDK functionality that can be utilised in their current form and expanded upon as the requirements of your app grow.  This file can be copied to your app's Javascript source directory and included in the <head></head> tags of your app's index.html:
<script type="text/javascript" src="<Javascript source directory>/bdFunctions.js"></script>

In summary, the functions relate to an HTML page that contains the following:
- A text area with an id of "statusText"

  This text area is updated with the responses from the Bluedot Point SDK.
- A button with an id of "authenticateButton"

  This will call the doAuthenticate() function with the following constants that are set at the top of the bdFunctions.js file:
    - apiKey

  This function will authenticate the SDK with the provided apiKey. For iOS project it also requires the authorization level `au.com.bluedot.BDAuthorizationLevel.WhenInUse` or `au.com.bluedot.BDAuthorizationLevel.Always`. It will instruct SDK to request appropriate authorization during the start. This parameter will be ignored in the Android project.

- A button with an id of "logOutButton"

  This will call the doLogOut() function

These functions are representative and should be adjusted for the use of your particular app.

## iOS Xcode Updates
There are some differences between a default Xcode project and the project requirements for an app running the Bluedot Point SDK.  In summary:
- The Bluedot Point SDK does not enforce Bit Code.
- The minimum supported version of iOS for Cordova and Bluedot Point SDK is 10.0 and above.
- There are required UIApplication delegate methods that are not implemented in the default Cordova app delegate that are required by the Bluedot Point SDK.

### Updating the Xcode Project
#### In the Xcode Project
- Use of BitCode must be set to **NO** in **Build Settings**.
- Minimum supported iOS version should be set to **10.0** in **General**.

#### In the info.plist file
- Locate the app's info.plist file in the Supporting Files folder of the Xcode project.
- Click on the '+' symbol to create another new row.
- Enter NSLocationAlwaysAndWhenInUseUsageDescription as the key, and select the type of String. Enter a usage description that denotes the use of location services by your app. For example, Your location is used by the app to get you deals closer to you while in use or in the background. This key is mandatory to support devices running iOS 11 and above.
- Click on the '+' symbol to create another new row.
- Enter NSLocationWhenInUseUsageDescription as the key, and select the type of String.
Enter a usage description that denotes the use of location services by your app. For example, Your location is used by the app to get you deals closer to you while in use. This key is mandatory to support devices running iOS 11 and above.

#### In the AppDelegate.m file
The following methods must be added to the app delegate should they not already be there:

    - (void)applicationWillEnterForeground:(UIApplication *)application
    {
        // This method implementation must be present in AppDelegate
        // when integrating Bluedot Point SDK v1.x, even if it is empty.
    }

    - (void)applicationDidEnterBackground:(UIApplication *)application
    {
        // This method implementation must be present in AppDelegate
        // when integrating Bluedot Point SDK v1.x, even if it is empty.
    }

    - (void)applicationWillResignActive:(UIApplication *)application
    {
        // For iOS9 (currently Beta) this method implementation must be present in AppDelegate
        // when integrating Bluedot Point SDK v1.x, even if it is empty.
    }

## Creating a minimal app

## Step-by-step Guide

#### 1. Run the following command to create an app in Cordova:

    cordova create minimalApp au.com.bluedot.minimalLocationApp MinimalLocationApp

- The first argument is the directory name for your app development environment (created within your current directory), this should not already exist.
- The second argument is an optional identifier for your app
- The third argument is the optional displayed name of your app

**NOTE**: Although the second and third argument are optional, they are difficult to change later as they are utilised throughout the configuration XML files and generated class names.

#### 2. Update some defaults in the top-level config.xml file:

  - \<description>
  - \<author>
  - **NOTE**: \<content> is overwritten by the platform specific config.xml.
  - **NOTE**: \<plugin> is not updated in the top-level config.xml with the Bluedot Point SDK plugin.  This is added to the platform specific config.xml files as a \<feature> automatically by the later steps.

#### 3. Add action buttons for session authentication to the main index.html file.
    <h1>Bluedot Plugin Location Demo</h1>
    <div class="formLayout">
        <form>
            <div class="formButtons">
                <input type="button" id="authenticateButton" value="Authenticate" />
                <input type="button" id="logOutButton" value="Log Out" />
            </div>
        </form>
    </div>
    <div class="statusArea">
        <textarea rows="12" cols="45" id="statusText" readonly ></textarea>
    </div>

#### 4. Copy the bdfunction.js file
Copy the Bluedot Javascript wrapper functions stored within the plugin repository in the **bdFunctions.js** file (to ensure default Cordova security aspects are fulfilled) to your app Javascript source folder.

These functions are for your use and adaptation.

#### 5. Add the import of the Bluedot Javascript bdFunctions.js file to your app's index.html:

    <script type="text/javascript" src="js/bdFunctions.js"></script>

#### 6. Run the following command to add the local Bluedot SDK plugin:

    cordova plugin add au.com.bluedot.bdpointsdk

#### 7.  Run the following command to add iOS and Android to the app:

    cordova platform add ios
    cordova platform add android

For Android, you will need to add the Android Support library, version 4 with the following command:

    cordova plugin add cordova-android-support-v4

#### 8. iOS additional steps
If you are building for iOS, the instructions in the [iOS Xcode Updates](#ios-xcode-updates) section must be followed prior to building your app.

#### 9. Build the app

    cordova build

#### 10. Run the emulator from the command line in a specific iOS simulator:

    cordova emulate ios

#### 11. Run the app on a physical iOS device:

    cordova run --device ios


## Support
Should you require support for any issues in the Bluedot Point plug-in, then please contact us using <support@bluedotinnovation.com>.



## BDPoint SDK Plug-in Methods
The following methods are available to your app to allow your app to utilise the low-energy, high accuracy Bluedot SDK.  These methods are provided in the **bluedotPointSDKCDVPlugin.js** file and provide cross-platform access to iOS and Android.

The iOS and Android plug-in source code provides everything required to compile and build your Cordova or Phonegap app, including the Bluedot SDK library files for each platform; you will need to register with the Bluedot <a href="https://www.pointaccess.bluedot.com.au/pointaccess-v1/login.html">Point Access</a> web-site to allow you to create the Geo-fences, Geolines™ and place beacons for your app to trigger.

Further information is also available within the Developer Resources within the <a href="http://www.bluedotinnovation.com/devresources/pages/">Bluedot Innovations</a> web-site.

The functions in the plug-in encapsulate cross-platform functionality.  Should more in-depth integration with the Point SDK be required, then please refer to the iOS SDK and Android SDK documentation within the Developer Resources within the <a href="http://www.bluedotinnovation.com/devresources/pages/">Bluedot Innovations</a> web-site.

- authenticate
- logOut
- zoneInfoCallback
- checkedIntoFenceCallback
- checkedOutOfFenceCallback
- checkedIntoBeaconCallback
- checkedOutOfBeaconCallback
- startRequiringUserInterventionForBluetoothCallback
- stopRequiringUserInterventionForBluetoothCallback
- startRequiringUserInterventionForLocationServicesCallback
- stopRequiringUserInterventionForLocationServicesCallback
- disableZone
- enableZone
- notifyPushUpdate
- foregroundNotification


## authenticate

    /*
     *  Authenticate a Bluedot Point session.
     *  The fail function call provides a string with the reason for failure.
     */
    exports.authenticate = function( success, fail, apiKey )
    {
        exec( success, fail, "BDPointSDK", "authenticate", [ apiKey ] );
    }

### Description
This function is utilised to start an authenticated session with the Bluedot Point SDK.  The credentials are returned from creating an App on the Bluedot <a href="https://www.pointaccess.bluedot.com.au/pointaccess-v1/login.html">Point Access</a> web site.

### Parameters

#### success (Function)
This is a function that will be called on successful authentication with the Bluedot Point Access back-end.  This entails a current authenticated session is now underway; an app only has to be authenticated once for the entire runtime of the app.

##### Function parameters
- None.

#### fail (Function)
This is a function that will be called if authentication with the Bluedot Point Access back-end fails.

##### Function parameters
- String - Reason for authentication failure.

#### apiKey (String)
The API Key is part of the credentials returned from creating an app on the Bluedot Point Access web site; an API Key is associated with each App that you create.

### Notes
You can obtain the credentials for each app after registering and logging in to the <a href="https://www.pointaccess.bluedot.com.au/pointaccess-v1/login.html">Point Access</a> web site.


## logOut

    /*
     *  Log out of an authenticated session with Bluedot Point.
     *  The fail function call provides a string with the reason for failure.
     */
    exports.logOut = function( success, fail )
    {
        exec( success, fail, "BDPointSDK", "logOut", [] );
    }

### Description
This function ends an authenticated session with the Bluedot Point Access back-end.

### Parameters

#### success (Function)
This is a function that will be called on successfully logging out from the Bluedot Point Access back-end.  This entails there is no longer an authenticated session available to your app.
##### Function parameters
- None.

#### fail (Function)
This is a function that will be called if logging out with the Bluedot Point Access back-end fails.
##### Function parameters
- String - Reason for logging out failure.


## zoneInfoCallback

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

### Description
This function provides a callback function to the SDK that will be called when zone information is downloaded to your app.  This can happen after initial authentication, after each Rule Time Interval has expired during the lifetime running of your app or when you have reached a geographical limit within a large set of Geofences.
This function should only be called after successful authentication to the Point Access back-end using the authenticate function.

### Parameters

#### callback (Function)
This is a function that will be called when an array of Zones has been downloaded to your app.
##### Function parameters
The callback function is passed one parameter as an array of Zone Information; each entry in this array contains an array of strings in the following order:
- Zone name
- Zone description
- Zone id

These strings can be accessed using an enum as demonstrated in the **bdFunctions.js** Javascript wrapper that is bundles with the Bluedot plug-in.

    const zoneInfoEnum =
    {
        name: 0,
        description: 1,
        ID: 2
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


## checkedIntoFenceCallback

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
     *     Parameter 3: Array of doubles identifying location which triggers fence:
     *          Date of check-in (Integer - UNIX timestamp)
     *          Latitude of check-in (Double)
     *          Longitude of check-in (Double)
     *          Bearing of check-in (Double)
     *          Speed of check-in (Double)
     *      Parameter 4: Fence is awaiting check-out (Boolean)
     *      Parameter 5: JSON Object of custom data (JSON Object)
     */
    exports.checkedIntoFenceCallback = function( callback )
    {
        exec( callback, null, "BDPointSDK", "checkedIntoFenceCallback", [] );
    }

### Description
This function provides a callback function to the SDK that will be called the device has triggered a Geofence or crossed a Geoline™.  Identifying information on the fence, the zone and location are passed back to the callback function as separate parameters.

### Parameters

#### callback (Function)
This is a function that will be called when the device has triggered a Geofence or crossed a Geoline™.

##### Function parameters
The callback function is passed 5 parameters of fence information, including the zone the fence is within; each entry contains the following information in the order provided:

- Parameter 1: Fence Info array
  - Fence name
  - Fence description
  - Fence id
- Parameter 2: Zone Info array
  - Zone name
  - Zone description
  - Zone id
- Parameter 3: Location Info array
  - Date of check-in; this is provided as a UNIX timestamp
  - Latitudinal co-ordinate at the point of check-in
  - Longitudinal co-ordinate at the point of check-in
  - Bearing degree of check-in
  - Speed of check-in
- Parameter 4: Fence is awaiting check-out (Boolean)
- Parameter 5: JSON Object of custom data (JSON Object)

These strings can be accessed using an enum as demonstrated in the bdFunctions.js Javascript wrapper that is bundled with the Bluedot plug-in.

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
     *  This delegate function receives the data of a fence with a Custom action that has been triggered by the SDK.
     *  Refer to bluedotPointSDKCDVPlugin.js for more information.
     */
    function fenceTrigger( fenceInfo, zoneInfo, locationInfo, willCheckOut, customData )
    {
        //  Extract details for a status update
        var fenceName = fenceInfo[ fenceInfoEnum.name ];
        var zoneName = zoneInfo[ zoneInfoEnum.name ];
        var lat = locationInfo[ locationInfoEnum.latitude ];
        var lon = locationInfo[ locationInfoEnum.longitude ];

        updateStatus( fenceName + " has been triggered in " + zoneName + " at " + lat + ":" + lon );
        updateStatus( JSON.stringify(customData) );

        if ( willCheckOut == true )
        {
            updateStatus( "Fence is awaiting check-out" )
        }
    }


## checkedOutOfFenceCallback

    /*
     *  Provide a multi-part callback for a fence with a Custom Action being checked out of.
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
     *      Parameter 3: Date of check-in (Integer - UNIX timestamp)
     *      Parameter 4: Dwell time in minutes
     *      Parameter 5: JSON Object of custom data (JSON Object)
     */
    exports.checkedOutOfFenceCallback = function( callback )
    {
        exec( callback, null, "BDPointSDK", "checkedOutOfFenceCallback", [] );
    }

### Description
This function provides a callback function to the SDK that will be called the device has left a Geofence in a Check-Out Zone that had been previously checked into.  Identifying information on the fence, the zone, the dwell time and custom data in minutes are passed back to the callback function as separate parameters.

### Parameters

#### callback (Function)
This is a function that will be called when the device has left a Geofence in a Check-Out Zone that had been previously checked into.

##### Function parameters
The callback function is passed 5 parameters of fence information, including the zone the fence is within; each entry contains the following information in the order provided:

- Parameter 1: Fence Info array
  - Fence name
  - Fence description
  - Fence id
- Parameter 2: Zone Info array
  - Zone name
  - Zone description
  - Zone id
- Parameter 3: Date of check-in; this is provided as a UNIX timestamp
- Parameter 4: Dwell time in minutes
- Parameter 5: JSON Object of custom data

These strings can be accessed using an enum as demonstrated in the bdFunctions.js Javascript wrapper that is bundled with the Bluedot plug-in.

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

    /*
     *  This delegate function receives the data of a fence with a Custom action that has been triggered by the SDK.
     *  Refer to BluedotPointSDKCDVPlugin.js for more information.
     */
    function fenceCheckOut( fenceInfo, zoneInfo, date, dwellTime, customData )
    {
        //  Extract details for a status update
        var fenceName = fenceInfo[ fenceInfoEnum.name ];
        var zoneName = zoneInfo[ zoneInfoEnum.name ];

        updateStatus( fenceName + " has been left in " + zoneName + " after " + dwellTime + " minutes" );
        updateStatus( JSON.stringify(customData) );
    }


## checkedIntoBeaconCallback

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
     *      Parameter 3: Array of double values identifying location:
     *          Date of check-in (Integer - UNIX timestamp)
     *          Latitude of beacon setting (Double)
     *          Longitude of beacon setting (Double)
     *          Bearing of beacon setting (Double)
     *          Speed of beacon setting (Double)
     *      Parameter 4: Proximity of check-in to beacon (Integer)
     *          0 = Unknown
     *          1 = Immediate
     *          2 = Near
     *          3 = Far
     *      Parameter 5: Beacon is awaiting check-out (Boolean)
     *      Parameter 6: JSON Object of custom data (JSON Object)
     */
    exports.checkedIntoBeaconCallback = function( callback )
    {
        exec( callback, null, "BDPointSDK", "checkedIntoBeaconCallback", [] );
    }

### Description
This function provides a callback function to the SDK that will be called the device has been triggered by the proximity of a beacon.  Identifying information on the beacon, the zone and the proximity at which the beacon caused a trigger are passed back to the callback function.

### Parameters

#### callback (Function)
This is a function that will be called when the device has been triggered a beacon at a configured proximity.

##### Function parameters
The callback function is passed 6 parameters of beacon information, including the zone the beacon is within; each entry contains the following information in the order provided:
- Parameter 1: Beacon Info array
  - Beacon name
  - Beacon description
  - Beacon id
  - Is an iBeacon - is set to true if the Beacon is an iBeacon
  - Proximity UUID of iBeacon
  - Major value of iBeacon
  - Minor value of iBeacon
  - MAC Address
  - Latitudinal co-ordinate of the beacon placement
- Parameter 2: Zone Info array
  - Zone name
  - Zone description
  - Zone id
- Parameter 3: Location Info array
  - Date of check-in; this is provided as a UNIX timestamp
  - Latitudinal co-ordinate at the point of beacon
  - Longitudinal co-ordinate at the point of beacon
  - Bearing degree at the point of beacon
  - Speed at the point of beacon
- Parameter 4: Proximity of the beacon at point of check-in
- Parameter 5: Beacon is awaiting check-out (Boolean)
- Parameter 6: JSON Object of custom data (JSON Object)

These strings can be accessed using an enum as demonstrated in the bdFunctions.js Javascript wrapper that is bundled with the Bluedot plug-in.

    const zoneInfoEnum =
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

    /*
     *  This delegate function receives the data of a fence with a Custom action that has been triggered by the SDK.
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
            updateStatus( "Beacon is awaiting check-out" )
        }
    }


## checkedOutOfBeaconCallback

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
     *      Parameter 5: Dwell time in minutes (Unsigned integer)
     *      Parameter 6: JSON Object of custom data (JSON Object)
     */
    exports.checkedOutOfBeaconCallback = function( callback )
    {
        exec( callback, null, "BDPointSDK", "checkedOutOfBeaconCallback", [] );
    }

### Description
This function provides a callback function to the SDK that will be called the device has left the detection range of a beacon.  Identifying information on the beacon, the zone, the proximity at which the beacon caused the initial trigger and the dwell time of the device within the range of the beacon are passed back to the callback function.

### Parameters

#### callback (Function)
This is a function that will be called when the device has left the detection range of a beacon.

##### Function parameters
The callback function is passed 5 parameters of beacon information, including the zone the beacon is within; each entry contains the following information in the order provided:
- Parameter 1: Beacon Info array
  - Beacon name
  - Beacon description
  - Beacon id
  - Is an iBeacon - is set to true if the Beacon is an iBeacon
  - Proximity UUID of iBeacon
  - Major value of iBeacon
  - Minor value of iBeacon
  - MAC Address
  - Latitudinal co-ordinate of the beacon placement
- Parameter 2: Zone Info array
  - Zone name
  - Zone description
  - Zone id
- Parameter 3: Proximity of the beacon at point of check-in
- Parameter 4: Date of check-in; this is provided as a UNIX timestamp
- Parameter 5: Dwell time in minutes
- Parameter 6: JSON Object of custom data

These strings can be accessed using an enum as demonstrated in the bdFunctions.js Javascript wrapper that is bundled with the Bluedot plug-in.

    const zoneInfoEnum =
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

    /*
     *  This delegate function receives the data of a fence with a Custom action that has been triggered by the SDK.
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


## startRequiringUserInterventionForBluetoothCallback

    /*
     *  Provide a callback to be notified when user intervention is required for Bluetooth on the device.
     */
    exports.startRequiringUserInterventionForBluetoothCallback = function( callback )
    {
        exec( callback, null, "BDPointSDK", "startRequiringUserInterventionForBluetoothCallback", [] );
    }

### Description
This callback allows your app to be notified if user intervention is required for activating Bluetooth on the device; this will generally occur if a beacon has been placed nearby on the Point Access web site and the device running your app does not have Bluetooth available.

### Parameters

#### callback (Function)
This is a function that will be called when Bluetooth is required by the device but is currently not active.

##### Function parameters
- None.


## stopRequiringUserInterventionForBluetoothCallback

    /*
     *  Provide a callback to be notified when user intervention is no longer required for Bluetooth on the device.
     */
    exports.stopRequiringUserInterventionForBluetoothCallback = function( callback )
    {
        exec( callback, null, "BDPointSDK", "stopRequiringUserInterventionForBluetoothCallback", [] );
    }

### Description
This callback allows your app to be notified if user intervention is no longer required for Bluetooth on the device; this will generally once a user has switched on Bluetooth or if there are no longer any beacons nearby on the Point Access web site.

### Parameters

#### callback (Function)
This is a function that will be called when Bluetooth is no longer required by the device.

##### Function parameters
- None.

### Notes
This function is only called on iOS devices.


## startRequiringUserInterventionForLocationServicesCallback

    /*
     *  Provide a callback to be notified when user intervention is required for Location Services on the device.
     */
    exports.startRequiringUserInterventionForLocationServicesCallback = function( callback )
    {
        exec( callback, null, "BDPointSDK", "startRequiringUserInterventionForLocationServicesCallback", [] );
    }

### Description
This callback allows your app to be notified if user intervention is required as Location Services are not currently enabled on the device; your app will not be able to trigger any fences if Location Services has not been enabled.

### Parameters

#### callback (Function)
This is a function that will be called when Location Services is currently not active.

##### Function parameters
The callback function is passed a parameter which indicate the current Location Service authorizationStatus.


## stopRequiringUserInterventionForLocationServicesCallback

    /*
     *  Provide a callback to be notified when user intervention is no longer required for Location Services on the device.
     */
    exports.stopRequiringUserInterventionForLocationServicesCallback = function( callback )
    {
        exec( callback, null, "BDPointSDK", "stopRequiringUserInterventionForLocationServicesCallback", [] );
    }

### Description
This callback allows your app to be notified if user intervention is no longer required to enable Location Services.

### Parameters

#### callback (Function)
This is a function that will be called when Location Services has been activated.

##### Function parameters
The callback function is passed a parameter which indicate the current Location Service authorizationStatus.

### Notes
This function is only called on iOS devices.


## disableZone

    /*
     *  Disable a zone from within the app using the Zone Id.
     */
    exports.disableZone = function( success, fail, zoneId )
    {
        exec( success, fail, "BDPointSDK", "disableZone", [ zoneId ] );
    }

### Description
An app may optionally disable (and later re-enable) Zones it receives from Point Access.

By calling this function with the zone Id, a zone can be disabled and re-enabled by an app. Note that this will not over-ride any excluding conditions entered via the Point Access web interface.

As an example; if you define a Zone in Point Access as active between 1pm and 3pm, calls to this function would have no apparent effect outside these times. During the Zone's active period (e.g. at 2.30pm), the Zone will be active unless disabled calling this function.

The zone identifiers are retrieved by implementing the zoneInfoCallback function and retrieving the ids from the zone information returned.

### Parameters

#### success (Function)
This is a function that will be called on successfully disabling a zone.

##### Function parameters
- None.

#### fail (Function)
This is a function that will be called if the app is unable to disable a zone; this could be from an invalid zone Id or from the zone already being disabled by this function.

##### Function parameters
- String - Reason for zone disablement failure.

#### zoneId (String)
The zone Id to use for the disable command.


## enableZone

    /*
     *  Enable a zone previously disabled from within the app using the Zone Id.
     */
    exports.enableZone = function( success, fail, zoneId )
    {
        exec( success, fail, "BDPointSDK", "enableZone", [ zoneId ] );
    }

### Description
An app may optionally disable (and later re-enable) Zones it receives from Point Access.

By calling this function with the zone Id, a zone previously disabled by calling the disableZone function will re-enabled by an app.

The zone identifiers are retrieved by implementing the zoneInfoCallback function and retrieving the ids from the zone information returned.

### Parameters

#### success (Function)
This is a function that will be called on successfully re-enabling a previously disabled zone.

##### Function parameters
- None.

#### fail (Function)
This is a function that will be called if the app is unable to re-enable a zone; this could be from an invalid zone Id or from the zone not being disabled by the disableZone function.

##### Function parameters
- String - Reason for zone re-enablement failure.

#### zoneId (String)
The zone Id to use for the enable command.


## foregroundNotification

    /*
    *  Sets notification for service to run in foreground, required for Android O and above   
    */
    exports.foregroundNotification = function( channelId, channelName, title, content, targetAllAPIs )
    {
    exec( null, null, "BDPointSDK", "foregroundNotification", [ channelId, channelName, title, content, targetAllAPIs ] );  
    }

### Description
This functions sets foreground notification for the Bluedot service for Android Oreo and above devices. Foreground notification is needed as per the <a href = "https://developer.android.com/about/versions/oreo/background">  background limitation requirements </a> for Android Orea and above. <b> This function needs to be called before making the authenticate() call, to provide the notification to the SDK for foreground notificaton </b>

### Parameters

#### channelId (String)
 The id of the channel. Must be unique per package. A current channel Id used by the application can be provided. 

#### channelName (String)
The name of the channel, recommended maximum length is 40 characters. A current channel name used by the applicattion can be provided.

#### title (String)
The title of the notification that will appear when the application is running.

#### content (String)
The content of the notification that will appear when the application is running.

#### targetAllAPIs (Bool)
A flag to show foreground service notification on devices running Android Nugget or lower.
