package com.farthestgate.suspensions.android.constant;

import android.Manifest;

public class AppConstant {
    //App config SD CARD constants
    public static final String ROOT_FOLDER = "fglsuspensions";
    public static final String CONFIG_DATA = ROOT_FOLDER + "/configdata";
    public static final String ERROR_FOLDER = ROOT_FOLDER + "/error";
    public static final String PHOTOS_FOLDER = ROOT_FOLDER + "/photos";

    //Login Activity
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String OFFICER_ID = "OfficerId";
    public static final String DEVICE_ID = "deviceId";

    //Notes Fragment
    public static final int NOTE_SIZE = 750;

    //Location -- Lat, Lng
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE ="Longitude";

    public static final  String SUSPENSION = "suspension";


    //permission
    public static String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};
    public static final int PERMISSION_ALL = 500;
}
