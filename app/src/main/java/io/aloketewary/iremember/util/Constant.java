package io.aloketewary.iremember.util;

/**
 * Created by AlokeT on 2/6/2018.
 */

public class Constant {
    // Message types sent from the threads Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_SERVER_CONNECTED = 7;

    // Key names received from the threads Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TIME_STAMP_FORMAT = "YYYY-MM-DD HH:MM:SS.SSS";

    public static final String COMPLETED_ONBOARDING_PREF_FOR_HOME = "IS_TUTORIAL_SHOWN";

    public static final String COMPLETED_ONBOARDING_PREF_FOR_BTPAIRED = "IS_TUTORIAL_SHOWN_FOR_BT";
    public static final String COMPLETED_ONBOARDING_PREF_FOR_GPS = "IS_TUTORIAL_SHOWN_FOR_GPS";
    public static final String COMPLETED_ONBOARDING_PREF_FOR_GPS_IF_LOCATIONS = "IS_TUTORIAL_SHOWN_FOR_GPS_IF_LOCATION_ADD";
}
