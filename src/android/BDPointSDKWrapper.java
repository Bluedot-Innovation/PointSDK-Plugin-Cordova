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

import au.com.bluedot.point.ApplicationNotification;
import au.com.bluedot.point.ApplicationNotificationListener;
import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.ServiceStatusListener;
import au.com.bluedot.point.net.engine.BeaconInfo;
import au.com.bluedot.point.net.engine.ZoneInfo;
import au.com.bluedot.point.net.engine.ServiceManager;
import au.com.bluedot.point.BluetoothNotEnabledError;
import au.com.bluedot.point.LocationServiceNotEnabledError;

import au.com.bluedot.application.model.geo.Fence;
import au.com.bluedot.model.geo.BoundingBox;
import au.com.bluedot.model.geo.Circle;
import au.com.bluedot.model.geo.LineString;
import au.com.bluedot.model.geo.Point;
import au.com.bluedot.point.ApplicationNotification;
import au.com.bluedot.point.ApplicationNotificationListener;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

public class BDPointSDKWrapper extends CordovaPlugin implements ServiceStatusListener, ApplicationNotificationListener {
    public static final String ACTION_AUTHENTICATE = "authenticate";
    public static final String ACTION_LOGOUT = "logOut";
    public static final String ACTION_APP_NOTIFICATION_FENCE = "checkedIntoFenceCallback";
    public static final String ACTION_APP_NOTIFICATION_BEACON = "checkedIntoBeaconCallback";
    public static final String ACTION_ZONE_INFO_CALLBACK = "zoneInfoCallback";
    public static final String ACTION_REQUIRE_USER_INTERVENTION_FOR_BLUETOOTH = "startRequiringUserInterventionForBluetoothCallback";
    public static final String ACTION_REQUIRE_USER_INTERVENTION_FOR_LOCATION = "startRequiringUserInterventionForLocationServicesCallback";
    public static final String ACTION_DISABLE_ZONE = "disableZone";
    public static final String ACTION_ENABLE_ZONE = "enableZone";
    private final static String TAG = "BDPointSDKWrapper";

    private ServiceManager mServiceManager;
    private CallbackContext mAuthCallbackContext;
    private CallbackContext mLogOutCallbackContext;
    private CallbackContext mAppNotifyFenceCallbackContext;
    private CallbackContext mAppNotifyBeaconCallbackContext;
    private CallbackContext mZoneInfoCallbackContext;
    private CallbackContext mRequiringUserInterventionForLocationServicesCallbackContext;
    private CallbackContext mRequiringUserInterventionForBluetoothCallbackContext;
    private CallbackContext mZoneUpdateCallbackContext;

    Context context;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        context = this.cordova.getActivity().getApplicationContext();
        mServiceManager = ServiceManager.getInstance(context);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean result = false;
        if (action.equals(ACTION_AUTHENTICATE)) {
            String username = args.getString(0);
            String apiKey = args.getString(1);
            String packageName = args.getString(2);

            try {
                ProviderInstaller.installIfNeeded(context);
            } catch (GooglePlayServicesRepairableException e) {

            } catch (GooglePlayServicesNotAvailableException e) {

            }

            mServiceManager.sendAuthenticationRequest(packageName, apiKey, username, this);
            mAuthCallbackContext = callbackContext;
            result = true;
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
        }

        return result;
    }

    /**
     * <p>It is called when BlueDotPointService started successful, your app logic code using the Bluedot service could start from here.</p>
     * <p>This method is off the UI thread.</p>
     */
    @Override
    public void onBlueDotPointServiceStartedSuccess() {
        if (mAuthCallbackContext != null) {
            mAuthCallbackContext.success();
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
     * @param error
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
            } else {
                mAuthCallbackContext.error(bdError.getClass().getSimpleName() + " " + bdError.getReason());
            }

        }
    }

    /**
     * <p>The method deliveries the ZoneInfo list when the rules are updated. Your app is able to get the latest ZoneInfo when the rules are updated.</p>
     * @param zoneInfoList
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
     * <p>The method delivers the error from BlueDotPointService by a generic BDError. Invoked for fatal errors and notifies of service stop
     * @param error
     */
    @Override
    public void onBlueDotPointServiceStopWithError(final BDError bdError) {
        if (mAuthCallbackContext != null) {
            if (bdError instanceof LocationServiceNotEnabledError) {
                mRequiringUserInterventionForLocationServicesCallbackContext = mAuthCallbackContext;
                mRequiringUserInterventionForLocationServicesCallbackContext.error(bdError.getClass().getSimpleName() + " " + bdError.getReason());
            } else if (bdError instanceof BluetoothNotEnabledError) {
                mRequiringUserInterventionForBluetoothCallbackContext = mAuthCallbackContext;
                mRequiringUserInterventionForBluetoothCallbackContext.error(bdError.getClass().getSimpleName() + " " + bdError.getReason());
            } else {
                mAuthCallbackContext.error(bdError.getClass().getSimpleName() + " " + bdError.getReason());
            }

        }
        mServiceManager.unsubscribeForApplicationNotification(this);
    }

    /**
     * This callback happens when user is subscribed to Application Notification
     * and check into any fence under that Zone
     *
     * @param applicationNotification
     */
    @Override
    public void onCheckIntoFence(ApplicationNotification applicationNotification) {

        Fence _fence = applicationNotification.getFence();
        ZoneInfo _zoneInfo = applicationNotification.getZoneInfo();
        double _lat = applicationNotification.getLocation().getLatitude();
        double _lon = applicationNotification.getLocation().getLongitude();
        long _date = applicationNotification.getLocation().getTime();

        JSONObject jsonObjectFence = new JSONObject();

        try {
            jsonObjectFence.put("0", _fence.getName());
            jsonObjectFence.put("1", _fence.getDescription());
            jsonObjectFence.put("2", _fence.getID());
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
        PluginResult lat = new PluginResult(PluginResult.Status.OK, "" + _lat);
        PluginResult lon = new PluginResult(PluginResult.Status.OK, "" + _lon);
        PluginResult date = new PluginResult(PluginResult.Status.OK, "" + _date);

        List < PluginResult > multipartMessages = new ArrayList < PluginResult > ();

        multipartMessages.add(fenceInfo);
        multipartMessages.add(zoneInfo);
        multipartMessages.add(lat);
        multipartMessages.add(lon);
        multipartMessages.add(date);
        PluginResult result = new PluginResult(PluginResult.Status.OK, multipartMessages);
        result.setKeepCallback(true);

        if (mAppNotifyFenceCallbackContext != null) {
            mAppNotifyFenceCallbackContext.sendPluginResult(result);
        }

    }


    /**
     * This callback happens when user is subscribed to Application Notification
     * and check into any beacon under that Zone
     *
     * @param applicationNotification
     */
    @Override
    public void onCheckIntoBeacon(ApplicationNotification applicationNotification) {

        BeaconInfo _beaconInfo = applicationNotification.getBeaconInfo();
        ZoneInfo _zoneInfo = applicationNotification.getZoneInfo();
        long _date = applicationNotification.getLocation().getTime();
        int _txPower = applicationNotification.getBeaconInfo().getTxPower();

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
        PluginResult txPower = new PluginResult(PluginResult.Status.OK, "" + _txPower);
        PluginResult date = new PluginResult(PluginResult.Status.OK, "" + _date);

        List < PluginResult > multipartMessages = new ArrayList < PluginResult > ();

        multipartMessages.add(beaconInfo);
        multipartMessages.add(zoneInfo);
        multipartMessages.add(txPower);
        multipartMessages.add(date);
        PluginResult result = new PluginResult(PluginResult.Status.OK, multipartMessages);
        result.setKeepCallback(true);

        if (mAppNotifyBeaconCallbackContext != null) {
            mAppNotifyBeaconCallbackContext.sendPluginResult(result);
        }

    }

    /**
     * This method enables and disables a Zone. 
     * @param zoneId, flag
     */
    public void updateZone(String zoneId, boolean flag) {
        boolean status = false;
        status = mServiceManager.setZoneDisableByApplication(zoneId, flag);
        if (mZoneUpdateCallbackContext != null) {
            if (status) {
                mZoneUpdateCallbackContext.success();
            } else {
                mZoneUpdateCallbackContext.error("");
            }
        }
    }

}