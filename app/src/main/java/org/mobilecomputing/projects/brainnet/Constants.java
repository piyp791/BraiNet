package org.mobilecomputing.projects.brainnet;

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
    //public static String HOST = "192.168.0.15";
    public static String HOST = "192.168.0.53";
    public static int PORT = 19999;
}

