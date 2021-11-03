package com.farthestgate.suspensions.android.ui.camera;

import android.content.Context;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.OrientationEventListener;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.ui.suspensionMain.model.Suspension;
import com.farthestgate.suspensions.android.utils.DateUtils;
import com.farthestgate.suspensions.android.utils.PreferenceUtils;

import java.io.File;
import java.util.Date;

public class CameraActivity extends AppCompatActivity implements CameraFragment.OnCammeraAction{

    private PreferenceUtils preferenceUtils;
    private OrientationListener orientationListener;
    private int rotation = 0;

    private double latitude;
    private double longitude;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        preferenceUtils = new PreferenceUtils(this);
        latitude = Double.parseDouble(preferenceUtils.getString(AppConstant.LATITUDE));
        longitude = Double.parseDouble(preferenceUtils.getString(AppConstant.LONGITUDE));

        orientationListener = new OrientationListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportFragmentManager().beginTransaction().
                add( R.id.camera_container, CameraFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onStart() {
        orientationListener.enable();
        super.onStart();
    }

    @Override
    protected void onStop() {
        orientationListener.disable();
        super.onStop();
    }

    @Override
    public void OnPictureTaken(File file) {
        saveExif(file.getAbsolutePath(), file.getName());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class OrientationListener extends OrientationEventListener {
        final int ROTATION_O    = 6;
        final int ROTATION_90   = 1;
        final int ROTATION_180  = 8;
        final int ROTATION_270  = 3;

        //        private int rotation = 0;
        public OrientationListener(Context context) { super(context); }

        @Override
        public void onOrientationChanged(int orientation) {
            if( (orientation < 35 || orientation > 325) && rotation!= ROTATION_O){ // PORTRAIT
                rotation = ROTATION_O;
            }
            else if( orientation > 145 && orientation < 215 && rotation!=ROTATION_180){ // REVERSE PORTRAIT
                rotation = ROTATION_180;
            }
            else if(orientation > 55 && orientation < 125 && rotation!=ROTATION_270){ // REVERSE LANDSCAPE
                rotation = ROTATION_270;
            }
            else if(orientation > 235 && orientation < 305 && rotation!=ROTATION_90){ //LANDSCAPE
                rotation = ROTATION_90;
            }
        }
    }

    private void saveExif(String filePath, String fileName){
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            exifInterface.setAttribute("Make",
                    Build.BRAND);
            exifInterface.setAttribute("Model",
                    Build.MODEL);
            exifInterface.setAttribute(ExifInterface.TAG_IMAGE_LENGTH,
                    exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
            exifInterface.setAttribute(ExifInterface.TAG_IMAGE_WIDTH,
                    exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
            exifInterface.setAttribute(ExifInterface.TAG_DATETIME,
                    DateUtils.getISO8601DateTime(new Date()));
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                    String.valueOf(rotation));

            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convertToDegreeMinuteSeconds(latitude));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, getLatitudeRef(latitude));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convertToDegreeMinuteSeconds(longitude));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, getLongitudeRef(longitude));

            exifInterface.setAttribute("UserComment", "Filename:" + fileName + ",Rotation:" + rotation);

            exifInterface.saveAttributes();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * returns ref for latitude which is S or N.
     *
     * @param latitude
     * @return S or N
     */
    private static String getLatitudeRef(double latitude) {
        return latitude < 0.0d ? "S" : "N";
    }

    /**
     * returns ref for latitude which is S or N.
     *
     * @param longitude
     * @return W or E
     */
    private static String getLongitudeRef(double longitude) {
        return longitude < 0.0d ? "W" : "E";
    }

    /**
     * convert latitude into DMS (degree minute second) format. For instance<br/>
     * -79.948862 becomes<br/>
     * 79/1,56/1,55903/1000<br/>
     * It works for latitude and longitude<br/>
     *
     * @param latitude could be longitude.
     * @return
     */
    private static String convertToDegreeMinuteSeconds(double latitude) {
        latitude = Math.abs(latitude);
        int degree = (int) latitude;
        latitude *= 60;
        latitude -= (degree * 60.0d);
        int minute = (int) latitude;
        latitude *= 60;
        latitude -= (minute * 60.0d);
        int second = (int) (latitude * 1000.0d);

        StringBuilder sb = new StringBuilder();
        sb.append(degree);
        sb.append("/1,");
        sb.append(minute);
        sb.append("/1,");
        sb.append(second);
        sb.append("/1000,");
        return sb.toString();
    }

}
