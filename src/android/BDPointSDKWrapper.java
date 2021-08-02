package io.bluedot.cordova.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import android.Manifest;
import android.content.pm.PackageManager;

import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.net.engine.BluedotServiceReceiver;
import au.com.bluedot.point.net.engine.GeoTriggeringEventReceiver;
import au.com.bluedot.point.net.engine.GeoTriggeringService;
import au.com.bluedot.point.net.engine.GeoTriggeringStatusListener;
import au.com.bluedot.point.net.engine.ResetResultReceiver;
import au.com.bluedot.point.net.engine.FenceInfo;
import au.com.bluedot.point.net.engine.InitializationResultListener;
import au.com.bluedot.point.net.engine.TempoService;
import au.com.bluedot.point.net.engine.TempoServiceStatusListener;
import au.com.bluedot.point.net.engine.TempoTrackingReceiver;
import au.com.bluedot.point.net.engine.ZoneEntryEvent;
import au.com.bluedot.point.net.engine.ZoneExitEvent;
import au.com.bluedot.point.net.engine.ZoneInfo;
import au.com.bluedot.point.net.engine.LocationInfo;
import au.com.bluedot.point.net.engine.ServiceManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import static android.app.Notification.PRIORITY_MAX;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * @author Bluedot Innovation
 * Copyright (c) 2021 Bluedot Innovation. All rights reserved.
 */

public class BDPointSDKWrapper extends CordovaPlugin implements InitializationResultListener, ResetResultReceiver {
    public static final String ACTION_INITIALIZE_SDK = "initializeWithProjectId";
    public static final String ACTION_RESET_SDK = "reset";
    public static final String ACTION_IS_INITIALIZED = "isInitialized";
    public static final String ACTION_START_GEOTRIGGERING = "androidStartGeoTriggering";
    public static final String ACTION_STOP_GEOTRIGGERING = "stopGeoTriggering";
    public static final String ACTION_IS_GEOTRIGGERING_RUNNING = "isGeoTriggeringRunning";
    public static final String ACTION_ENTERED_ZONE_CALLBACK = "enteredZoneCallback";
    public static final String ACTION_EXITED_ZONE_CALLBACK = "exitedZoneCallback";
    public static final String ACTION_ZONE_INFO_UPDATE_CALLBACK = "zoneInfoUpdateCallback";    
    public static final String ACTION_START_TEMPO_TRACKING = "androidStartTempoTracking";
    public static final String ACTION_STOP_TEMPO_TRACKING = "stopTempoTracking";
    public static final String ACTION_IS_TEMPO_RUNNING = "isTempoRunning";
    public static final String ACTION_TEMPO_STOPPED_WITH_ERROR_CALLBACK = "tempoStoppedWithErrorCallback";
    public static final String ACTION_DISABLE_ZONE = "disableZone";
    public static final String ACTION_ENABLE_ZONE = "enableZone";
    public static final String ACTION_NOTIFY_PUSH_UPDATE = "notifyPushUpdate";
    public static final String ACTION_SET_CUSTOMEVENT_METADATA = "setCustomEventMetaData";
    public static final String ACTION_GET_ZONES = "getZones";
    public static final String ACTION_GET_SDK_VERSION = "getSdkVersion";
    public static final String ACTION_GET_INSTALL_REF = "getInstallRef";
    public static final String ACTION_BLUEDOT_SERVICE_RECEIVED_ERROR = "bluedotServiceDidReceiveErrorCallback";

    private final static String TAG = "BDPointSDKWrapper";

    private ServiceManager mServiceManager;
    private CallbackContext mInitializeCallbackContext;
    private CallbackContext mResetCallbackContext;
    private CallbackContext mStartGeoTriggeringCallbackContext;
    private CallbackContext mStopGeoTriggeringCallbackContext;
    private CallbackContext mStartTempoTrackingCallbackContext;
    private static CallbackContext mEnteredZoneCallbackContext;
    private static CallbackContext mExitedZoneCallbackContext;
    private static CallbackContext mZoneInfoUpdateCallbackContext;
    private static CallbackContext mBlueDotErrorReceiverCallbackContext;
    private static CallbackContext mTempoStoppedWithErrorCallbackContext;
    private final int PERMISSION_REQ_CODE = 137;
    private String[] locationPermissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };

    Context context;
    private String projectId;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        context = this.cordova.getActivity().getApplicationContext();
        mServiceManager = ServiceManager.getInstance(context);
    }

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        try{
            if (action.equals(ACTION_INITIALIZE_SDK)) {
                initializeSDK(args, callbackContext);
            } else if (action.equals(ACTION_RESET_SDK)) {
                resetSDK(args, callbackContext);
            } else if (action.equals(ACTION_IS_INITIALIZED)) {
                isInitialized(args, callbackContext);
            } else if (action.equals(ACTION_START_GEOTRIGGERING)) {
                startGeoTriggering(args, callbackContext);
            } else if (action.equals(ACTION_STOP_GEOTRIGGERING)) {
                stopGeoTriggering(args, callbackContext);
            } else if (action.equals(ACTION_IS_GEOTRIGGERING_RUNNING)) {
                isGeoTriggeringRunning(args, callbackContext);
            } else if (action.equals(ACTION_START_TEMPO_TRACKING)) {
                startTempoTracking(args, callbackContext);
            } else if (action.equals(ACTION_STOP_TEMPO_TRACKING)) {
                stopTempoTracking(args, callbackContext);
            } else if (action.equals(ACTION_IS_TEMPO_RUNNING)) {
                isTempoRunning(args, callbackContext);
            } else if (action.equals(ACTION_ENTERED_ZONE_CALLBACK)) {
                enteredZoneCallback(args, callbackContext);
            } else if (action.equals(ACTION_EXITED_ZONE_CALLBACK)) {
                exitedZoneCallback(args, callbackContext);
            } else if (action.equals(ACTION_ZONE_INFO_UPDATE_CALLBACK)) {
                zoneInfoUpdateCallback(args, callbackContext);
            } else if (action.equals(ACTION_TEMPO_STOPPED_WITH_ERROR_CALLBACK)) {
                tempoStoppedWithErrorCallback(args, callbackContext);
            } else if (action.equals(ACTION_DISABLE_ZONE)) {
                disableZone(args, callbackContext);
            } else if (action.equals(ACTION_ENABLE_ZONE)) {
                enableZone(args, callbackContext);
            } else if (action.equals(ACTION_NOTIFY_PUSH_UPDATE)) {
                notifyPushUpdate(args, callbackContext);
            } else if (action.equals(ACTION_BLUEDOT_SERVICE_RECEIVED_ERROR)) {
                bluedotServiceReceivedErrorCallback(args, callbackContext);
            } else if (action.equals(ACTION_SET_CUSTOMEVENT_METADATA)) {
                setCustomEventMetaData(args, callbackContext);
            } else if (action.equals(ACTION_GET_ZONES)) {
                getZones(args, callbackContext);
            } else if (action.equals(ACTION_GET_SDK_VERSION)) {
                getSdkVersion(args, callbackContext);
            } else if (action.equals(ACTION_GET_INSTALL_REF)) {
                getInstallRef(args, callbackContext);
            } else {
                return false;
            }
        } catch (JSONException exception) {
            Log.e("bluedot-cordova", "JSONException", exception);
            return false;
        }
        return true;
    }

    private void initializeSDK(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        mInitializeCallbackContext = callbackContext;
        projectId = args.getString(0);

        if(cordova.hasPermission(Manifest.permission_group.LOCATION)) {
            mServiceManager.initialize(projectId, this);
        } else {
            cordova.requestPermissions(this, PERMISSION_REQ_CODE, locationPermissions);
        }
    }

    private void resetSDK(final JSONArray args, final CallbackContext callbackContext)
    {
        mResetCallbackContext = callbackContext;
        mServiceManager.reset(this);
    }

    private void isInitialized(final JSONArray args, final CallbackContext callbackContext)
    {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, mServiceManager.isBluedotServiceInitialized());
        callbackContext.sendPluginResult(pluginResult);
    }

    private void startGeoTriggering(final JSONArray args, final CallbackContext callbackContext) throws JSONException
    {
        mStartGeoTriggeringCallbackContext = callbackContext;
        String channelId = args.getString(0);
        String channelName = args.getString(1);
        String androidNotificationTitle = args.getString(2);
        String androidNotificationContent = args.getString(3);
        Integer androidNotificationId = args.getInt(4);

        GeoTriggeringStatusListener statusListener = error -> {
            if (error != null){
                PluginResult result = new PluginResult(
                        PluginResult.Status.ERROR,
                        "Start GeoTriggering Failed with Error: " + error.getReason()
                );
                result.setKeepCallback(true);
                mStartGeoTriggeringCallbackContext.sendPluginResult(result);
                return;
            }

            PluginResult result = new PluginResult(PluginResult.Status.OK);
            result.setKeepCallback(true);
            mStartGeoTriggeringCallbackContext.sendPluginResult(result);
        };

        //Start as With FG Service
        if (!androidNotificationTitle.isEmpty() && !androidNotificationContent.isEmpty()) {

            if ((channelId.isEmpty()) || (channelName.isEmpty())) {
                PluginResult pluginResult = new PluginResult(
                        PluginResult.Status.ERROR,
                        "Missing channelId and channelName for Notification"
                );
                mStartGeoTriggeringCallbackContext.sendPluginResult(pluginResult);
            }

            Notification fgNotification =
                    createNotification(channelId, channelName, androidNotificationTitle,
                            androidNotificationContent);

            if (androidNotificationId != -1) {
                //Set notificationId for GeoTriggerService
                GeoTriggeringService.builder()
                        .notification(fgNotification)
                        .notificationId(androidNotificationId)
                        .start(context, statusListener);
            } else {
                //Use default notificationId set by PointSDK
                GeoTriggeringService.builder()
                        .notification(fgNotification)
                        .start(context, statusListener);
            }
        } else {
            //Start as No FG Service
            GeoTriggeringService.builder()
                    .start(context, statusListener);
        }
    }

    private void stopGeoTriggering(final JSONArray args, final CallbackContext callbackContext)
    {
        mStopGeoTriggeringCallbackContext = callbackContext;

        //triggered variable used to avoid crash caused by double invocation of success callback
        //due to GeoTriggeringStatusListener being invoked twice from SDK when stopping in background mode.
        //TODO: Remove it when SDK fixes this issue
        AtomicBoolean triggered = new AtomicBoolean(false);

        GeoTriggeringStatusListener statusListener = error -> {
            if (triggered.get()) {
                return;
            }

            if (error != null){
                PluginResult result = new PluginResult(
                        PluginResult.Status.ERROR,
                        "Stop GeoTriggering Failed with Error: " + error.getReason()
                );
                result.setKeepCallback(true);
                mStopGeoTriggeringCallbackContext.sendPluginResult(result);
                return;
            }
            triggered.set(true);
            PluginResult result = new PluginResult(PluginResult.Status.OK);
            result.setKeepCallback(true);
            mStopGeoTriggeringCallbackContext.sendPluginResult(result);
        };

        GeoTriggeringService.stop(context, statusListener);
    }

    private void isGeoTriggeringRunning(final JSONArray args, final CallbackContext callbackContext)
    {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, GeoTriggeringService.isRunning());
        callbackContext.sendPluginResult(pluginResult);
    }

    private void zoneInfoUpdateCallback(final JSONArray args, final CallbackContext callbackContext)
    {
        BDPointSDKWrapper.mZoneInfoUpdateCallbackContext = callbackContext;
    }

    private void enteredZoneCallback(final JSONArray args, final CallbackContext callbackContext)
    {
        BDPointSDKWrapper.mEnteredZoneCallbackContext = callbackContext;
    }

    private void exitedZoneCallback(final JSONArray args, final CallbackContext callbackContext)
    {
        BDPointSDKWrapper.mExitedZoneCallbackContext = callbackContext;
    }

    private void startTempoTracking(final JSONArray args, final CallbackContext callbackContext) throws JSONException
    {
        mStartTempoTrackingCallbackContext = callbackContext;
        String destinationId = args.getString(0);
        String channelId = args.getString(1);
        String channelName = args.getString(2);
        String androidNotificationTitle = args.getString(3);
        String androidNotificationContent = args.getString(4);
        Integer androidNotificationId = args.getInt(5);

        if (destinationId.isEmpty()) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "destinationId is null");
            callbackContext.sendPluginResult(pluginResult);
            return;
        }

        if ((channelId.isEmpty()) || (channelName.isEmpty()) || (androidNotificationTitle.isEmpty()) || (androidNotificationContent.isEmpty())) {
            PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.ERROR,
                    "Missing param from channelId/channelName/androidNotificationTitle/androidNotificationContent");
            callbackContext.sendPluginResult(pluginResult);
            return;
        }

        TempoServiceStatusListener tempoStatusListener = error -> {
            if (error != null){
                PluginResult result = new PluginResult(
                        PluginResult.Status.ERROR,
                        "Start Tempo Failed with Error: " + error.getReason()
                );
                result.setKeepCallback(true);
                mStartTempoTrackingCallbackContext.sendPluginResult(result);
                return;
            }

            PluginResult result = new PluginResult(PluginResult.Status.OK);
            result.setKeepCallback(true);
            mStartTempoTrackingCallbackContext.sendPluginResult(result);
        };

        Notification fgNotification =
                createNotification(channelId, channelName, androidNotificationTitle,
                        androidNotificationContent);

        if (androidNotificationId != -1) {
            TempoService.builder()
                    .notificationId(androidNotificationId)
                    .notification(fgNotification)
                    .destinationId(destinationId)
                    .start(context, tempoStatusListener);
        } else {
            TempoService.builder()
                    .notification(fgNotification)
                    .destinationId(destinationId)
                    .start(context, tempoStatusListener);
        }
    }

    private void stopTempoTracking(final JSONArray args, final CallbackContext callbackContext)
    {
        BDError error = TempoService.stop(context);
        if(error != null)
        {
            PluginResult result = new PluginResult(
                    PluginResult.Status.ERROR,
                    "Stop Tempo Tracking Failed with Error: " + error.getReason()
            );
            callbackContext.sendPluginResult(result);
            return;
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK);
        callbackContext.sendPluginResult(result);
    }

    private void isTempoRunning(final JSONArray args, final CallbackContext callbackContext)
    {
        PluginResult result = new PluginResult(PluginResult.Status.OK, TempoService.isRunning(context));
        callbackContext.sendPluginResult(result);
    }

    private void tempoStoppedWithErrorCallback(final JSONArray args, final CallbackContext callbackContext)
    {
        BDPointSDKWrapper.mTempoStoppedWithErrorCallbackContext = callbackContext;
    }

    private void disableZone(final JSONArray args, final CallbackContext callbackContext) throws JSONException
    {
        String zoneId = args.getString(0);
        mServiceManager.setZoneDisableByApplication(zoneId, true);
    }

    private void enableZone(final JSONArray args, final CallbackContext callbackContext) throws JSONException
    {
        String zoneId = args.getString(0);
        mServiceManager.setZoneDisableByApplication(zoneId, false);
    }

    private void notifyPushUpdate(final JSONArray args, final CallbackContext callbackContext) throws JSONException
    {
        JSONObject jsonObject = args.getJSONObject(0);

        Map<String, String> data = new HashMap<String, String>();
        Iterator<String> iterator = jsonObject.keys();

        while (iterator.hasNext()) {
            String key = iterator.next();
            data.put(key, jsonObject.getString(key));
        }

        mServiceManager.notifyPushUpdate(data);
    }

    private void setCustomEventMetaData(final JSONArray args, final CallbackContext callbackContext) throws JSONException
    {
        JSONObject object = args.getJSONObject(0);
        Map<String, String> customMetaData = new HashMap<String, String>();
        try {
            Iterator<String> keysItr = object.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                String value = object.getString(key);
                customMetaData.put(key, value);
            }
        } catch (Exception exp) {

        }
        mServiceManager.setCustomEventMetaData(customMetaData);
    }

    public void getZones(final JSONArray args, final CallbackContext callbackContext)
    {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

        List<ZoneInfo> list = mServiceManager.getZonesAndFences();
        if(list != null) {
            try {

                for (int i = 0; i < list.size(); i++) {
                    jsonObject = new JSONObject();
                    jsonObject.put("name", list.get(i).getZoneName());
                    jsonObject.put("id", list.get(i).getZoneId());
                    jsonArray.put(jsonObject);
                }

            } catch (Exception e) {
                Log.e("bluedot-cordova", "Exception", e);
            }
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, jsonArray);
        callbackContext.sendPluginResult(result);
    }

    public void getSdkVersion(final JSONArray args, final CallbackContext callbackContext)
    {
        PluginResult result = new PluginResult(PluginResult.Status.OK, mServiceManager.getSdkVersion());
        callbackContext.sendPluginResult(result);
    }

    public void getInstallRef(final JSONArray args, final CallbackContext callbackContext)
    {
        PluginResult result = new PluginResult(PluginResult.Status.OK, mServiceManager.getInstallRef());
        callbackContext.sendPluginResult(result);
    }


    private void bluedotServiceReceivedErrorCallback(final JSONArray args, final CallbackContext callbackContext)
    {
        BDPointSDKWrapper.mBlueDotErrorReceiverCallbackContext = callbackContext;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException
    {
        for(int r:grantResults)
        {
            if(r == PackageManager.PERMISSION_DENIED)
            {
                mInitializeCallbackContext.sendPluginResult(new PluginResult(
                        PluginResult.Status.ERROR,
                        "Bluedot Point SDK requires Location Services to start. Therefore granting the Location permission is mandatory, without that the SDK will not initialize."));
                return;
            }
        }
        switch(requestCode)
        {
            case PERMISSION_REQ_CODE:
                mServiceManager.initialize(projectId, this);
                break;
        }
    }

    private Notification createNotification(String channelId,String channelName,String title, String content) {
        Intent activityIntent = new Intent(cordova.getActivity().getIntent());
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT );

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel =
                        new NotificationChannel(channelId, channelName,
                                NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(false);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            Notification.Builder notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(new Notification.BigTextStyle().bigText(content))
                    .setOngoing(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(getApplicationIcon());
            return notification.build();
        } else {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setOngoing(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setPriority(PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(getApplicationIcon());
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

    @Override
    public void onInitializationFinished(@Nullable BDError bdError) {
        if (mInitializeCallbackContext != null) {

            if (bdError != null){
                PluginResult result = new PluginResult(
                        PluginResult.Status.ERROR,
                        "Initialization Failed with Error: " + bdError.getReason()
                );
                result.setKeepCallback(true);
                mInitializeCallbackContext.sendPluginResult(result);
                return;
            }

            PluginResult result = new PluginResult(PluginResult.Status.OK);
            result.setKeepCallback(true);
            mInitializeCallbackContext.sendPluginResult(result);
        }
    }

    @Override
    public void onResetFinished(@Nullable BDError bdError) {
        if (mResetCallbackContext != null) {

            if (bdError != null){
                PluginResult result = new PluginResult(
                        PluginResult.Status.ERROR,
                        "Reset Failed with Error: " + bdError.getReason()
                );
                result.setKeepCallback(true);
                mResetCallbackContext.sendPluginResult(result);
                return;
            }

            PluginResult result = new PluginResult(PluginResult.Status.OK);
            result.setKeepCallback(true);
            mResetCallbackContext.sendPluginResult(result);
        }
    }

    public static class BluedotErrorReceiver extends BluedotServiceReceiver {

        @Override
        public void onBluedotServiceError(@NotNull BDError bdError, @NotNull Context context) {
            PluginResult result = new PluginResult(
                    PluginResult.Status.ERROR,
                    "Bluedot Service Error: " + bdError.getReason()
            );
            result.setKeepCallback(true);
            mBlueDotErrorReceiverCallbackContext.sendPluginResult(result);
        }
    }

    public static class BluedotGeoTriggerReceiver extends GeoTriggeringEventReceiver {

        @Override
        public void onZoneInfoUpdate(@NotNull List<ZoneInfo> zoneInfos, @NotNull Context context) {
            Log.d(TAG, "onRuleUpdate()");

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject;
            if (zoneInfos != null) {
                try {

                    for (int i = 0; i < zoneInfos.size(); i++) {
                        jsonObject = new JSONObject();
                        jsonObject.put("name", zoneInfos.get(i).getZoneName());
                        jsonObject.put("ID", zoneInfos.get(i).getZoneId());
                        jsonObject.put("customData", zoneInfos.get(i).getCustomData() != null ? zoneInfos.get(i).getCustomData() : new HashMap<String, String>());
                        jsonArray.put(jsonObject);
                    }

                } catch (Exception e) {
                    Log.e("bluedot-cordova", "Exception", e);
                }
            }

            if (BDPointSDKWrapper.mZoneInfoUpdateCallbackContext != null) {
                BDPointSDKWrapper.mZoneInfoUpdateCallbackContext.success(jsonArray);
            }
        }

        @Override
        public void onZoneEntryEvent(@NotNull ZoneEntryEvent zoneEntryEvent, @NotNull Context context) {
            Log.d(TAG, "onZoneEntryEvent()");

            FenceInfo _fence = zoneEntryEvent.getFenceInfo();
            ZoneInfo _zoneInfo = zoneEntryEvent.getZoneInfo();
            LocationInfo _locationInfo = zoneEntryEvent.getLocationInfo();
            Map<String, String> _customData = _zoneInfo.getCustomData();

            JSONObject jsonObjectFence = new JSONObject();

            try {
                jsonObjectFence.put("name", _fence.getName());
                jsonObjectFence.put("ID", _fence.getId());
            } catch (Exception e) {
                jsonObjectFence = null;
            }

            JSONObject jsonObjectZone = new JSONObject();

            try {
                jsonObjectZone.put("name", _zoneInfo.getZoneName());
                jsonObjectZone.put("ID", _zoneInfo.getZoneId());
            } catch (Exception e) {
                jsonObjectZone = null;
            }

            JSONObject jsonObjectLocation = new JSONObject();

            try {
                jsonObjectLocation.put("unixDate", _locationInfo.getTimeStamp());
                jsonObjectLocation.put("latitude", _locationInfo.getLatitude());
                jsonObjectLocation.put("longitude", _locationInfo.getLongitude());
                jsonObjectLocation.put("bearing", _locationInfo.getBearing());
                jsonObjectLocation.put("speed", _locationInfo.getSpeed());
            } catch (Exception e) {
                jsonObjectLocation = null;
            }

            PluginResult fenceInfo = new PluginResult(PluginResult.Status.OK, jsonObjectFence);
            PluginResult zoneInfo = new PluginResult(PluginResult.Status.OK, jsonObjectZone);
            PluginResult locationInfo = new PluginResult(PluginResult.Status.OK, jsonObjectLocation);
            PluginResult isCheckOut = new PluginResult(PluginResult.Status.OK, _zoneInfo.isCheckOut());

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

            if (BDPointSDKWrapper.mEnteredZoneCallbackContext != null) {
                BDPointSDKWrapper.mEnteredZoneCallbackContext.sendPluginResult(result);
            }
        }

        @Override
        public void onZoneExitEvent(@NotNull ZoneExitEvent zoneExitEvent, @NotNull Context context) {
            Log.d(TAG, "onZoneExitEvent()");

            FenceInfo _fence = zoneExitEvent.getFenceInfo();
            ZoneInfo _zoneInfo = zoneExitEvent.getZoneInfo();
            Map<String, String> _customData = _zoneInfo.getCustomData();

            long _date = System.currentTimeMillis();
            JSONObject jsonObjectFence = new JSONObject();

            try {
                jsonObjectFence.put("name", _fence.getName());
                jsonObjectFence.put("ID", _fence.getId());
            } catch (Exception e) {
                jsonObjectFence = null;
            }

            JSONObject jsonObjectZone = new JSONObject();

            try {
                jsonObjectZone.put("name", _zoneInfo.getZoneName());
                jsonObjectZone.put("ID", _zoneInfo.getZoneId());
            } catch (Exception e) {
                jsonObjectZone = null;
            }

            PluginResult fenceInfo = new PluginResult(PluginResult.Status.OK, jsonObjectFence);
            PluginResult zoneInfo = new PluginResult(PluginResult.Status.OK, jsonObjectZone);
            PluginResult date = new PluginResult(PluginResult.Status.OK, "" + _date);
            PluginResult dwellTime = new PluginResult(PluginResult.Status.OK, "" + zoneExitEvent.getDwellTime());

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

            if (BDPointSDKWrapper.mExitedZoneCallbackContext != null) {
                BDPointSDKWrapper.mExitedZoneCallbackContext.sendPluginResult(result);
            }

        }
    }

    public static class BluedotTempoReceiver extends TempoTrackingReceiver
    {
        @Override
        public void tempoStoppedWithError(@NotNull BDError bdError, @NotNull Context context) {
            if (mTempoStoppedWithErrorCallbackContext != null) {
                PluginResult result = new PluginResult(
                        PluginResult.Status.ERROR,
                        "Tempo Stopped with Error: " + bdError.getReason()
                );
                result.setKeepCallback(true);
                mTempoStoppedWithErrorCallbackContext.sendPluginResult(result);
            }
        }
    }
}
