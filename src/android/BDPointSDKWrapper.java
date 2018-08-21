package au.com.bluedot;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import android.Manifest;
import android.content.pm.PackageManager;

import au.com.bluedot.point.ApplicationNotificationListener;
import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.ServiceStatusListener;
import au.com.bluedot.point.net.engine.BeaconInfo;
import au.com.bluedot.point.net.engine.FenceInfo;
import au.com.bluedot.point.net.engine.ZoneInfo;
import au.com.bluedot.point.net.engine.LocationInfo;
import au.com.bluedot.point.net.engine.ServiceManager;
import au.com.bluedot.point.BluetoothNotEnabledError;
import au.com.bluedot.point.LocationServiceNotEnabledError;
import au.com.bluedot.application.model.Proximity;
import au.com.bluedot.application.model.geo.Fence;
import au.com.bluedot.model.geo.BoundingBox;
import au.com.bluedot.model.geo.Circle;
import au.com.bluedot.model.geo.LineString;
import au.com.bluedot.model.geo.Point;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import android.location.Location;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

/*
 * @author Bluedot Innovation
 * Copyright (c) 2018 Bluedot Innovation. All rights reserved.
 */

public class BDPointSDKWrapper extends CordovaPlugin implements ServiceStatusListener, ApplicationNotificationListener {
    public static final String ACTION_AUTHENTICATE = "authenticate";
    public static final String ACTION_LOGOUT = "logOut";
    public static final String ACTION_APP_NOTIFICATION_FENCE = "checkedIntoFenceCallback";
    public static final String ACTION_APP_NOTIFICATION_BEACON = "checkedIntoBeaconCallback";
    public static final String ACTION_APP_NOTIFICATION_FENCE_CHECKOUT = "checkedOutOfFenceCallback";
    public static final String ACTION_APP_NOTIFICATION_BEACON_CHECKOUT = "checkedOutOfBeaconCallback";
    public static final String ACTION_ZONE_INFO_CALLBACK = "zoneInfoCallback";
    public static final String ACTION_REQUIRE_USER_INTERVENTION_FOR_BLUETOOTH = "startRequiringUserInterventionForBluetoothCallback";
    public static final String ACTION_REQUIRE_USER_INTERVENTION_FOR_LOCATION = "startRequiringUserInterventionForLocationServicesCallback";
    public static final String ACTION_DISABLE_ZONE = "disableZone";
    public static final String ACTION_ENABLE_ZONE = "enableZone";
    public static final String ACTION_NOTIFY_PUSH_UPDATE = "notifyPushUpdate";
    public static final String ACTION_FOREGROUND_NOTIFICATION = "foregourndNotification";

    private final static String TAG = "BDPointSDKWrapper";

    // An error code of 0 entails no additional warnings. For error code higher then zero please refer BDError.
    private int errorCode = 0;
    private String errorMsg = "";

    private ServiceManager mServiceManager;
    private CallbackContext mAuthCallbackContext;
    private CallbackContext mLogOutCallbackContext;
    private CallbackContext mAppNotifyFenceCallbackContext;
    private CallbackContext mAppNotifyBeaconCallbackContext;
    private CallbackContext mAppNotifyFenceCheckOutCallbackContext;
    private CallbackContext mAppNotifyBeaconCheckOutCallbackContext;
    private CallbackContext mZoneInfoCallbackContext;
    private CallbackContext mRequiringUserInterventionForLocationServicesCallbackContext;
    private CallbackContext mRequiringUserInterventionForBluetoothCallbackContext;
    private CallbackContext mZoneUpdateCallbackContext;
    private final int PERMISSION_REQ_CODE = 137;
    private String[] locationPermissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };

    Context context;
    private String apiKey;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        context = this.cordova.getActivity().getApplicationContext();
        mServiceManager = ServiceManager.getInstance(context);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean result = false;
        if (action.equals(ACTION_AUTHENTICATE)) {
          apiKey = args.getString(0);
          if(cordova.hasPermission(Manifest.permission_group.LOCATION)) {

            try {
                ProviderInstaller.installIfNeeded(context);
            } catch (GooglePlayServicesRepairableException e) {

            } catch (GooglePlayServicesNotAvailableException e) {

            }
            mServiceManager.sendAuthenticationRequest(apiKey, this);

            result = true;
          } else {
            cordova.requestPermissions(this, PERMISSION_REQ_CODE, locationPermissions);
            result = true;
          }
          mAuthCallbackContext = callbackContext;

        } else if (action.equals(ACTION_LOGOUT)) {
            mLogOutCallbackContext = callbackContext;
            mServiceManager.stopPointService();
            result = true;
        } else if (action.equals(ACTION_APP_NOTIFICATION_FENCE)) {
            mAppNotifyFenceCallbackContext = callbackContext;
            result = true;
        } else if (action.equals(ACTION_APP_NOTIFICATION_BEACON)) {
            mAppNotifyBeaconCallbackContext = callbackContext;
            result = true;
        } else if (action.equals(ACTION_APP_NOTIFICATION_FENCE_CHECKOUT)) {
            mAppNotifyFenceCheckOutCallbackContext = callbackContext;
            result = true;
        } else if (action.equals(ACTION_APP_NOTIFICATION_BEACON_CHECKOUT)) {
            mAppNotifyBeaconCheckOutCallbackContext = callbackContext;
            result = true;
        } else if (action.equals(ACTION_ZONE_INFO_CALLBACK)) {
            mZoneInfoCallbackContext = callbackContext;
            result = true;
        } else if (action.equals(ACTION_REQUIRE_USER_INTERVENTION_FOR_LOCATION)) {
            mRequiringUserInterventionForLocationServicesCallbackContext = callbackContext;
            result = true;
        } else if (action.equals(ACTION_REQUIRE_USER_INTERVENTION_FOR_BLUETOOTH)) {
            mRequiringUserInterventionForBluetoothCallbackContext = callbackContext;
            result = true;
        } else if (action.equals(ACTION_DISABLE_ZONE)) {
            String zoneId = args.getString(0);
            mZoneUpdateCallbackContext = callbackContext;
            updateZone(zoneId, true);
            result = true;
        } else if (action.equals(ACTION_ENABLE_ZONE)) {
            String zoneId = args.getString(0);
            mZoneUpdateCallbackContext = callbackContext;
            updateZone(zoneId, false);
            result = true;
        } else if (action.equals(ACTION_NOTIFY_PUSH_UPDATE)) {
            JSONObject jsonObject = args.getJSONObject(0);
            notifyPushUpdate(jsonObject);
            result = true;
        } else if(action.equals(ACTION_FOREGROUND_NOTIFICATION)) {
            String channelId, channelName, title, content;
            boolean targetAllAPIs = args.getBoolean(4);
            channelId = args.getString(0);
            channelName = args.getString(1);
            title = args.getString(2);
            content = args.getString(3);

            mServiceManager.setForegroundServiceNotification(createNotification(channelId, channelName, title, content) , targetAllAPIs);
            result = true;
        }

        return result;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                         int[] grantResults) throws JSONException
{
    for(int r:grantResults)
    {
        if(r == PackageManager.PERMISSION_DENIED)
        {
            mAuthCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "PERMISSION_DENIED_ERROR"));
            return;
        }
    }
    switch(requestCode)
    {
        case PERMISSION_REQ_CODE:
              mServiceManager.sendAuthenticationRequest(apiKey, this);
            break;

    }
}

    /**
     * <p>It is called when BlueDotPointService started successful, your app logic code using the Bluedot service could start from here.</p>
     * <p>This method is off the UI thread.</p>
     */
    @Override
    public void onBlueDotPointServiceStartedSuccess() {
        if (mAuthCallbackContext != null) {
        PluginResult prErrorCode,prErrorMsg;

        List < PluginResult > multipartMessages = new ArrayList < PluginResult > ();

            if(errorCode == 0){
                 prErrorCode = new PluginResult(PluginResult.Status.OK, "" + errorCode);
                 prErrorMsg = new PluginResult(PluginResult.Status.OK, "");
            } else{
                prErrorCode = new PluginResult(PluginResult.Status.OK, "" + errorCode);
                prErrorMsg = new PluginResult(PluginResult.Status.OK, errorMsg);
            }

            multipartMessages.add(prErrorCode);
            multipartMessages.add(prErrorMsg);
            PluginResult result = new PluginResult(PluginResult.Status.OK, multipartMessages);
            result.setKeepCallback(true);
            mAuthCallbackContext.sendPluginResult(result);
            errorCode = 0;
            errorMsg = "";
        }
        mServiceManager.subscribeForApplicationNotification(this);
    }

    /**
     * <p>This method notifies the client application that BlueDotPointService is stopped. Your app could release the resources related to Bluedot service from here.</p>
     * <p>It is called off the UI thread.</p>
     */
    @Override
    public void onBlueDotPointServiceStop() {
        if (mLogOutCallbackContext != null) {
            mLogOutCallbackContext.success();
        }
        mServiceManager.unsubscribeForApplicationNotification(this);
    }

    /**
     * <p>The method delivers the error from BlueDotPointService by a generic BDError. There are several types of error such as BDAuthenticationError, BDNetworkError etc, you could check the specific error instance.</p>
     * @see au.com.bluedot.point.BDAuthenticationError,au.com.bluedot.point.BDNetworkError,au.com.bluedot.point.BDNetworkError,au.com.bluedot.point.LocationServiceNotEnabledError
     * @param bdError
     */
    @Override
    public void onBlueDotPointServiceError(BDError bdError) {
        if (mAuthCallbackContext != null) {

            if (bdError instanceof LocationServiceNotEnabledError) {
                mRequiringUserInterventionForLocationServicesCallbackContext = mAuthCallbackContext;
                mRequiringUserInterventionForLocationServicesCallbackContext.error(bdError.getClass().getSimpleName() + " " + bdError.getReason());
            } else if (bdError instanceof BluetoothNotEnabledError) {
                mRequiringUserInterventionForBluetoothCallbackContext = mAuthCallbackContext;
                mRequiringUserInterventionForBluetoothCallbackContext.error(bdError.getClass().getSimpleName() + " " + bdError.getReason());
            } else if(!bdError.isFatal()) {
                errorCode = bdError.getErrorCode();
                errorMsg = bdError.getReason();
            } else {
                mAuthCallbackContext.error(bdError.getClass().getSimpleName() + " " + bdError.getReason());
            }

        }
    }

    /**
     * <p>The method deliveries the ZoneInfo list when the rules are updated. Your app is able to get the latest ZoneInfo when the rules are updated.</p>
     * @param zoneInfos
     */
    @Override
    public void onRuleUpdate(List < ZoneInfo > zoneInfos) {
        Log.d(TAG, "onRuleUpdate()");

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        if (zoneInfos != null) {
            try {

                for (int i = 0; i < zoneInfos.size(); i++) {
                    jsonObject = new JSONObject();
                    jsonObject.put("0", zoneInfos.get(i).getZoneName());
                    jsonObject.put("1", zoneInfos.get(i).getDescription());
                    jsonObject.put("2", zoneInfos.get(i).getZoneId());
                    jsonArray.put(jsonObject);
                    jsonObject = null;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mZoneInfoCallbackContext != null) {
            mZoneInfoCallbackContext.success(jsonArray);
        }

    }

    /**
     * This callback happens when user is subscribed to Application Notification
     * and check into any fence under that Zone
     * @param _fence      - Fence triggered
     * @param _zoneInfo   - Zone information Fence belongs to
     * @param _locationInfo   - geographical coordinate where trigger happened
     * @param _customData - custom data associated with this Custom Action
     * @param _isCheckOut - CheckOut will be tracked and delivered once device left the Fence
     */
    @Override
    public void onCheckIntoFence(FenceInfo _fence, ZoneInfo _zoneInfo, LocationInfo _locationInfo, Map<String, String> _customData, boolean _isCheckOut) {

        JSONObject jsonObjectFence = new JSONObject();

        try {
            jsonObjectFence.put("0", _fence.getName());
            jsonObjectFence.put("1", _fence.getDescription());
            jsonObjectFence.put("2", _fence.getId());
        } catch (Exception e) {
            jsonObjectFence = null;
        }

        JSONObject jsonObjectZone = new JSONObject();

        try {
            jsonObjectZone.put("0", _zoneInfo.getZoneName());
            jsonObjectZone.put("1", _zoneInfo.getDescription());
            jsonObjectZone.put("2", _zoneInfo.getZoneId());
        } catch (Exception e) {
            jsonObjectZone = null;
        }

        JSONObject jsonObjectLocation = new JSONObject();

        try {
            jsonObjectLocation.put("0", _locationInfo.getTimeStamp());
            jsonObjectLocation.put("1", _locationInfo.getLatitude());
            jsonObjectLocation.put("2", _locationInfo.getLongitude());
            jsonObjectLocation.put("3", _locationInfo.getBearing());
            jsonObjectLocation.put("4", _locationInfo.getSpeed());
        } catch (Exception e) {
            jsonObjectLocation = null;
        }

        PluginResult fenceInfo = new PluginResult(PluginResult.Status.OK, jsonObjectFence);
        PluginResult zoneInfo = new PluginResult(PluginResult.Status.OK, jsonObjectZone);
        PluginResult locationInfo = new PluginResult(PluginResult.Status.OK, jsonObjectLocation);
        PluginResult isCheckOut = new PluginResult(PluginResult.Status.OK, "" + _isCheckOut);

        List < PluginResult > multipartMessages = new ArrayList < PluginResult > ();

        multipartMessages.add(fenceInfo);
        multipartMessages.add(zoneInfo);
        multipartMessages.add(locationInfo);
        multipartMessages.add(isCheckOut);

        if ( _customData != null ) {
            JSONObject jsonObjectCustomData = new JSONObject(_customData);
            PluginResult customData = new PluginResult(PluginResult.Status.OK, jsonObjectCustomData);
            multipartMessages.add(customData);
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, multipartMessages);
        result.setKeepCallback(true);

        if (mAppNotifyFenceCallbackContext != null) {
            mAppNotifyFenceCallbackContext.sendPluginResult(result);
        }

    }


    /**
     * This callback happens when user is subscribed to Application Notification
     * and check into any beacon under that Zone
     * @param _beaconInfo - Beacon triggered
     * @param _zoneInfo   - Zone information Beacon belongs to
     * @param _locationInfo   - the location of the beacon as specified on the Bluedot backend
     * @param _proximity  - the proximity at which the trigger occurred
     * @param _customData - custom data associated with this Custom Action
     * @param _isCheckOut - CheckOut will be tracked and delivered once device left the Beacon advertisement range
     */
    @Override
    public void onCheckIntoBeacon(BeaconInfo _beaconInfo, ZoneInfo _zoneInfo, LocationInfo _locationInfo, Proximity _proximity, Map<String, String> _customData, boolean _isCheckOut) {

        int _txPower = _beaconInfo.getTxPower();

        JSONObject jsonObjectBeacon = new JSONObject();

        try {

            jsonObjectBeacon.put("0", _beaconInfo.getName());
            jsonObjectBeacon.put("1", _beaconInfo.getDescription());
            jsonObjectBeacon.put("2", _beaconInfo.getId());
            jsonObjectBeacon.put("3", false);
            jsonObjectBeacon.put("4", "");
            jsonObjectBeacon.put("5", 0);
            jsonObjectBeacon.put("6", 0);
            jsonObjectBeacon.put("7", _beaconInfo.getMacAddress());
            jsonObjectBeacon.put("8", _beaconInfo.getLocation().getLatitude());
            jsonObjectBeacon.put("9", _beaconInfo.getLocation().getLongitude());
        } catch (Exception e) {
            jsonObjectBeacon = null;
        }

        JSONObject jsonObjectZone = new JSONObject();

        try {
            jsonObjectZone.put("0", _zoneInfo.getZoneName());
            jsonObjectZone.put("1", _zoneInfo.getDescription());
            jsonObjectZone.put("2", _zoneInfo.getZoneId());
        } catch (Exception e) {
            jsonObjectZone = null;
        }

        JSONObject jsonObjectLocation = new JSONObject();

        try {
            jsonObjectLocation.put("0", _locationInfo.getTimeStamp());
            jsonObjectLocation.put("1", _locationInfo.getLatitude());
            jsonObjectLocation.put("2", _locationInfo.getLongitude());
            jsonObjectLocation.put("3", _locationInfo.getBearing());
            jsonObjectLocation.put("4", _locationInfo.getSpeed());
        } catch (Exception e) {
            jsonObjectLocation = null;
        }

        PluginResult beaconInfo = new PluginResult(PluginResult.Status.OK, jsonObjectBeacon);
        PluginResult zoneInfo = new PluginResult(PluginResult.Status.OK, jsonObjectZone);
        PluginResult proximity = new PluginResult(PluginResult.Status.OK, "" + getIntForProximity(_proximity));
        PluginResult locationInfo = new PluginResult(PluginResult.Status.OK, "" + jsonObjectLocation);
        PluginResult isCheckOut = new PluginResult(PluginResult.Status.OK, "" + _isCheckOut);

        List < PluginResult > multipartMessages = new ArrayList < PluginResult > ();

        multipartMessages.add(beaconInfo);
        multipartMessages.add(zoneInfo);
        multipartMessages.add(proximity);
        multipartMessages.add(locationInfo);
        multipartMessages.add(isCheckOut);

        if ( _customData != null ) {
            JSONObject jsonObjectCustomData = new JSONObject(_customData);
            PluginResult customData = new PluginResult(PluginResult.Status.OK, jsonObjectCustomData);
            multipartMessages.add(customData);
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, multipartMessages);
        result.setKeepCallback(true);

        if (mAppNotifyBeaconCallbackContext != null) {
            mAppNotifyBeaconCallbackContext.sendPluginResult(result);
        }

    }

    /**
     * This callback happens when user is subscribed to Application Notification
     * and checked out from fence under that Zone
     * @param _fence     - Fence user is checked out from
     * @param _zoneInfo  - Zone information Fence belongs to
     * @param _dwellTime - time spent inside the Fence; in minutes
     * @param _customData - custom data associated with this Custom Action
     */
    @Override
    public void onCheckedOutFromFence(FenceInfo _fence, ZoneInfo _zoneInfo, int _dwellTime, Map<String, String> _customData) {
        long _date = System.currentTimeMillis();

        JSONObject jsonObjectFence = new JSONObject();

        try {
            jsonObjectFence.put("0", _fence.getName());
            jsonObjectFence.put("1", _fence.getDescription());
            jsonObjectFence.put("2", _fence.getId());
        } catch (Exception e) {
            jsonObjectFence = null;
        }

        JSONObject jsonObjectZone = new JSONObject();

        try {
            jsonObjectZone.put("0", _zoneInfo.getZoneName());
            jsonObjectZone.put("1", _zoneInfo.getDescription());
            jsonObjectZone.put("2", _zoneInfo.getZoneId());
        } catch (Exception e) {
            jsonObjectZone = null;
        }

        PluginResult fenceInfo = new PluginResult(PluginResult.Status.OK, jsonObjectFence);
        PluginResult zoneInfo = new PluginResult(PluginResult.Status.OK, jsonObjectZone);
        PluginResult date = new PluginResult(PluginResult.Status.OK, "" + _date);
        PluginResult dwellTime = new PluginResult(PluginResult.Status.OK, "" + _dwellTime);

        List < PluginResult > multipartMessages = new ArrayList < PluginResult > ();

        multipartMessages.add(fenceInfo);
        multipartMessages.add(zoneInfo);
        multipartMessages.add(date);
        multipartMessages.add(dwellTime);

        if ( _customData != null ) {
            JSONObject jsonObjectCustomData = new JSONObject(_customData);
            PluginResult customData = new PluginResult(PluginResult.Status.OK, jsonObjectCustomData);
            multipartMessages.add(customData);
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, multipartMessages);
        result.setKeepCallback(true);

        if (mAppNotifyFenceCheckOutCallbackContext != null) {
            mAppNotifyFenceCheckOutCallbackContext.sendPluginResult(result);
        }
    }

    /**
     * This callback happens when user is subscribed to Application Notification
     * and checked out from beacon under that Zone
     * @param _beaconInfo - Beacon is checked out from
     * @param _zoneInfo   - Zone information Beacon belongs to
     * @param _dwellTime  - time spent inside the Beacon area; in minutes
     * @param _customData - custom data associated with this Custom Action
     */
    @Override
    public void onCheckedOutFromBeacon(BeaconInfo _beaconInfo, ZoneInfo _zoneInfo, int _dwellTime, Map<String, String> _customData) {

        long _date = System.currentTimeMillis();
        int _txPower = _beaconInfo.getTxPower();

        JSONObject jsonObjectBeacon = new JSONObject();

        try {

            jsonObjectBeacon.put("0", _beaconInfo.getName());
            jsonObjectBeacon.put("1", _beaconInfo.getDescription());
            jsonObjectBeacon.put("2", _beaconInfo.getId());
            jsonObjectBeacon.put("3", false);
            jsonObjectBeacon.put("4", "");
            jsonObjectBeacon.put("5", 0);
            jsonObjectBeacon.put("6", 0);
            jsonObjectBeacon.put("7", _beaconInfo.getMacAddress());
            jsonObjectBeacon.put("8", _beaconInfo.getLocation().getLatitude());
            jsonObjectBeacon.put("9", _beaconInfo.getLocation().getLongitude());
        } catch (Exception e) {
            jsonObjectBeacon = null;
        }

        JSONObject jsonObjectZone = new JSONObject();

        try {
            jsonObjectZone.put("0", _zoneInfo.getZoneName());
            jsonObjectZone.put("1", _zoneInfo.getDescription());
            jsonObjectZone.put("2", _zoneInfo.getZoneId());
        } catch (Exception e) {
            jsonObjectZone = null;
        }

        PluginResult beaconInfo = new PluginResult(PluginResult.Status.OK, jsonObjectBeacon);
        PluginResult zoneInfo = new PluginResult(PluginResult.Status.OK, jsonObjectZone);
        PluginResult proximity = new PluginResult(PluginResult.Status.OK, "" + 0);
        PluginResult date = new PluginResult(PluginResult.Status.OK, "" + _date);
        PluginResult dwellTime = new PluginResult(PluginResult.Status.OK, "" + _dwellTime);

        List < PluginResult > multipartMessages = new ArrayList < PluginResult > ();

        multipartMessages.add(beaconInfo);
        multipartMessages.add(zoneInfo);
        multipartMessages.add(proximity);
        multipartMessages.add(date);
        multipartMessages.add(dwellTime);

        if ( _customData != null ) {
            JSONObject jsonObjectCustomData = new JSONObject(_customData);
            PluginResult customData = new PluginResult(PluginResult.Status.OK, jsonObjectCustomData);
            multipartMessages.add(customData);
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, multipartMessages);
        result.setKeepCallback(true);

        if (mAppNotifyBeaconCheckOutCallbackContext != null) {
            mAppNotifyBeaconCheckOutCallbackContext.sendPluginResult(result);
        }
    }

    /**
     * This method enables and disables a Zone.
     * @param zoneId, flag
     */
    public void updateZone(String zoneId, boolean flag) {
        mServiceManager.setZoneDisableByApplication(zoneId, flag);
        if (mZoneUpdateCallbackContext != null) {
            mZoneUpdateCallbackContext.success();
        }
    }

    public void notifyPushUpdate(JSONObject jsonObject) {
        Map<String, String> data = new HashMap<String, String>();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            try {
                String key = iterator.next();
                data.put(key, jsonObject.getString(key));
            } catch (JSONException e) {}
        }
        mServiceManager.notifyPushUpdate(data);
    }

    private int getIntForProximity(Proximity value) {
        int result = 0;
        switch (value) {
            case Unknown:
                result = 0;
                break;
            case Immediate:
                result = 1;
                break;
            case Near:
                result = 2;
                break;
            case Far:
                result = 3;
                break;
        }
        return result;
    }

    private Notification createNotification(String channelId, String channelName, String title, String content) {
        Intent activityIntent = new Intent(cordova.getActivity().getIntent());
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT );

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(false);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Notification.Builder notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(new Notification.BigTextStyle().bigText(content))
                    .setSmallIcon(getApplicationIcon())
                    .setContentIntent(pendingIntent);

            return notification.build();
        } else {

            NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))                
                    .setSmallIcon(getApplicationIcon())
                    .setContentIntent(pendingIntent);

            return notification.build();
        }
    }


    private int getApplicationIcon() {
        String packageName = context.getPackageName();
        int icon =  android.R.drawable.ic_notification_overlay;
            try {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
                icon  = info.icon;
            } catch (PackageManager.NameNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        return icon;
    }
}
