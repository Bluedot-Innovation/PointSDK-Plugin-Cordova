class TempoBuilder {
    constructor() {
        this.channelId = "";
        this.channelName = "";
        this.androidNotificationTitle = "";
        this.androidNotificationContent = "";
        this.androidNotificationId = -1;
    }

    androidNotification = (channelId = "", channelName = "", title = "", content = "", id = -1) => {
        this.channelId = channelId;
        this.channelName = channelName;
        this.androidNotificationTitle = title;
        this.androidNotificationContent = content;
        this.androidNotificationId = id;

        return this 
    }

    start = (destinationId = "", onSuccess, onError) => {
        if (device.platform === "iOS") {
            io.bluedot.cordova.plugin.iOSStartTempoTracking(onSuccess, onError, destinationId);
        }

        if (device.platform === "Android") {
            io.bluedot.cordova.plugin.androidStartTempoTracking(
                onSuccess,
                onError,
                destinationId,
                this.channelId,
                this.channelName,
                this.androidNotificationTitle,
                this.androidNotificationContent,
                this.androidNotificationId
            );
        }
    }
}

module.exports = TempoBuilder