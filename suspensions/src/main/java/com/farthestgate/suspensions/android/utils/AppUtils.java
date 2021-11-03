package com.farthestgate.suspensions.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;

/**
 * Created by Administrator on 1/25/2018.
 */

public class AppUtils {

    public static void hideStatusBar(AppCompatActivity activity) {
        View decorView = activity.getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
    }

    public static void hideKeyboardForce(Context context) {
        if (context != null && !((Activity) context).isFinishing()) {
            InputMethodManager im = (InputMethodManager) context.getApplicationContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(((Activity) context).getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public static void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static String getAppVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    context.getPackageName(), 0);
            String version = info.versionName + "." + info.versionCode;
            return version;
        }catch (Exception e){
           e.printStackTrace();
        }
        return "";
    }

    /*
     * getting screen width
     */
    public static int getScreenWidth(Context context) {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    public static String getUUID(Context context) {
        if (Build.VERSION.SDK_INT <= 28) {
            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tManager.getDeviceId();
        }else
        {
            return getImeiFIle();
        }

    }

    public static String getImeiFIle(){
        String imeiNumber = null;
        String rootDir = Environment.getExternalStorageDirectory().toString();
        File location = new File(rootDir + File.separator);
        for (File f : location.listFiles()) {
            if (f.getName().endsWith(".imei")) {
                imeiNumber = f.getName().replace(".imei","");
                break;
            }
        }
        return imeiNumber;
    }

    public static boolean isValid(TextInputLayout textInputLayout, EditText editText, String message) {
        boolean isValid;
        if(editText.getText().toString().trim().isEmpty()){
            textInputLayout.setError(message);
            isValid = false;
        } else {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
            isValid = true;
        }
        return isValid;
    }

}
