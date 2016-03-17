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

import au.com.bluedot.point.ApplicationNotificationListener;
import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.ServiceStatusListener;
import au.com.bluedot.point.net.engine.BeaconInfo;
import au.com.bluedot.point.net.engine.ZoneInfo;
import au.com.bluedot.point.net.engine.ServiceManager;
import au.com.bluedot.point.BluetoothNotEnabledError;
import au.com.bluedot.point.LocationServiceNotEnabledError;
import au.com.bluedot.application.model.Proximity;
import au.com.bluedot.application.model.geo.Fence;
import au.com.bluedot.model.geo.BoundingBox;
import au.com.bluedot.model.geo.Circle;
import au.com.bluedot.model.geo.LineString;
import au.com.bluedot.model.geo.Point;

import android.location.Location;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

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
     * @param error
     */
    @Override
    public void onBlueDotPointServiceError(BDError bdError) {
        if (mAuthCallbackContext != null) {

            if(!bdError.isFatal()){
                errorCode = bdError.getErrorCode();
                errorMsg = bdError.getReason();
            } else if (bdError instanceof LocationServiceNotEnabledError) {
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
     * This callback happens when user is subscribed to Application Notification
     * and check into any fence under that Zone
     * @param fence      - Fence triggered
     * @param zoneInfo   - Zone information Fence belongs to
     * @param location   - geographical coordinate where trigger happened
     * @param isCheckOut - CheckOut will be tracked and delivered once device left the Fence
     */
    @Override
    public void onCheckIntoFence(Fence _fence, ZoneInfo _zoneInfo, Location _location, boolean _isCheckOut) {

        double _lat = _location.getLatitude();
        double _lon = _location.getLongitude();
        long _date =  _location.getTime();

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
        PluginResult isCheckOut = new PluginResult(PluginResult.Status.OK, "" + _isCheckOut);

        List < PluginResult > multipartMessages = new ArrayList < PluginResult > ();

        multipartMessages.add(fenceInfo);
        multipartMessages.add(zoneInfo);
        multipartMessages.add(lat);
        multipartMessages.add(lon);
        multipartMessages.add(date);
        multipartMessages.add(isCheckOut);
        PluginResult result = new PluginResult(PluginResult.Status.OK, multipartMessages);
        result.setKeepCallback(true);

        if (mAppNotifyFenceCallbackContext != null) {
            mAppNotifyFenceCallbackContext.sendPluginResult(result);
        }

    }


    /**
     * This callback happens when user is subscribed to Application Notification
     * and check into any beacon under that Zone
     * @param beaconInfo - Beacon triggered
     * @param zoneInfo   - Zone information Beacon belongs to
     * @param location   - geographical coordinate where trigger happened
     * @param proximity  - the proximity at which the trigger occurred
     * @param isCheckOut - CheckOut will be tracked and delivered once device left the Beacon advertisement range
     */
    @Override
    public void onCheckIntoBeacon(BeaconInfo _beaconInfo, ZoneInfo _zoneInfo, Location _location, Proximity _proximity, boolean _isCheckOut) {

        long _date = _location.getTime();
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
        PluginResult proximity = new PluginResult(PluginResult.Status.OK, "" + getIntForProximity(_proximity));
        PluginResult date = new PluginResult(PluginResult.Status.OK, "" + _date);
        PluginResult isCheckOut = new PluginResult(PluginResult.Status.OK, "" + _isCheckOut);

        List < PluginResult > multipartMessages = new ArrayList < PluginResult > ();

        multipartMessages.add(beaconInfo);
        multipartMessages.add(zoneInfo);
        multipartMessages.add(proximity);
        multipartMessages.add(date);
        multipartMessages.add(isCheckOut);
        PluginResult result = new PluginResult(PluginResult.Status.OK, multipartMessages);
        result.setKeepCallback(true);

        if (mAppNotifyBeaconCallbackContext != null) {
            mAppNotifyBeaconCallbackContext.sendPluginResult(result);
        }

    }

    /**
     * This callback happens when user is subscribed to Application Notification
     * and checked out from fence under that Zone
     * @param fence     - Fence user is checked out from
     * @param zoneInfo  - Zone information Fence belongs to
     * @param dwellTime - time spent inside the Fence; in minutes
     */
    @Override
    public void onCheckedOutFromFence(Fence _fence, ZoneInfo _zoneInfo, int _dwellTime) {
        long _date = System.currentTimeMillis();

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
        PluginResult date = new PluginResult(PluginResult.Status.OK, "" + _date);
        PluginResult dwellTime = new PluginResult(PluginResult.Status.OK, "" + _dwellTime);

        List < PluginResult > multipartMessages = new ArrayList < PluginResult > ();

        multipartMessages.add(fenceInfo);
        multipartMessages.add(zoneInfo);
        multipartMessages.add(date);
        multipartMessages.add(dwellTime);
        PluginResult result = new PluginResult(PluginResult.Status.OK, multipartMessages);
        result.setKeepCallback(true);

        if (mAppNotifyFenceCheckOutCallbackContext != null) {
            mAppNotifyFenceCheckOutCallbackContext.sendPluginResult(result);
        }
    }

    /**
     * This callback happens when user is subscribed to Application Notification
     * and checked out from beacon under that Zone
     * @param beaconInfo - Beacon is checked out from
     * @param zoneInfo   - Zone information Beacon belongs to
     * @param dwellTime  - time spent inside the Beacon area; in minutes
     */
    @Override
    public void onCheckedOutFromBeacon(BeaconInfo _beaconInfo, ZoneInfo _zoneInfo, int _dwellTime) {

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
}