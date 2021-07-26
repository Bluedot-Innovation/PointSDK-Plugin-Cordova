class GeoTriggeringBuilder {
    constructor() {
        // Android Foreground notification parameters
        this.channelId = "";
        this.channelName = "";
        this.androidNotificationTitle = "";
        this.androidNotificationContent = "";
        this.androidNotificationId = -1;
        
        // iOS App Restart notification parameters
        this.iOSAppRestartNotificationTitle = null;
        this.iOSAppRestartNotificationButtonText = null;
    }

    androidNotification = (channelId = "", channelName = "", title = "", content= "", id= -1) => {
        this.channelId = channelId;
        this.channelName = channelName;
        this.androidNotificationTitle = title;
        this.androidNotificationContent = content;
        this.androidNotificationId = id;

        return this 
    }

    iOSAppRestartNotification = (title, buttonText) => { 
        this.iOSAppRestartNotificationTitle = title;
        this.iOSAppRestartNotificationButtonText = buttonText;

        return this 
    }

    start = (onSuccess, onError) => {
        if (device.platform === "iOS") {
            console.log("Starting iOS GeoTriggering...");
            // With App Restart Notification
            if (this.iOSAppRestartNotificationTitle !== null && this.iOSAppRestartNotificationButtonText !== null) {
                io.bluedot.cordova.plugin.iOSStartGeoTriggeringWithAppRestartNotification(
                    onSuccess,
                    onError,
                    this.iOSAppRestartNotificationTitle,
                    this.iOSAppRestartNotificationButtonText
                );
                return 
            } 

            // With Completion
            io.bluedot.cordova.plugin.iOSStartGeoTriggering(onSuccess, onError);
        }

        if (device.platform === "Android") {
            console.log("Starting Android GeoTriggering...");
            io.bluedot.cordova.plugin.androidStartGeoTriggering(
                onSuccess, 
                onError,
                this.channelId,
                this.channelName,
                this.androidNotificationTitle,
                this.androidNotificationContent, 
                this.androidNotificationId
            );
        }
    }
}

module.exports = GeoTriggeringBuilder