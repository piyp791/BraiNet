package com.neurosky.mindwavemobiledemo.helper;

import android.Manifest;

/**
 * Created by peps on 6/24/17.
 */

public class Constants {

    public static String[] permissions = new String[] {
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.INTERNET
    };

    public static String CUSTOM_LOG_TYPE = "BrainNet-Logs";
    public static String HOST = "http://192.168.1.9:5000";
    //public static String HOST = "192.168.0.53";
    //public static int PORT = 19999;
    public static String INTENT_KEY = "INTENT";
    public static String LOGIN_INTENT = "LOGIN";
    public static String REGISTER_INTENT = "REGISTER";
    public static int TIMEOUT_TIME = 30000;
    public static int RECORDING_TIMEOUT = 60;
}

