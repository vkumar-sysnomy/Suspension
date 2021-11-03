package com.farthestgate.suspensions.android.ui;

import android.location.Location;
import android.view.Menu;
import android.view.MenuItem;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.base.BaseToolBarActivity;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.helper.LocationService;
import com.farthestgate.suspensions.android.ui.startOfDay.StartOfDayFragment;
import com.farthestgate.suspensions.android.utils.PreferenceUtils;

public class MainActivity extends BaseToolBarActivity implements LocationService.OnLocationChangedListener {

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initializeUi() {
        LocationService locationService = new LocationService(this, this);

        if(locationService.canGetLocation()){
            preferenceUtils.saveString(AppConstant.LATITUDE, String.valueOf(locationService.getLatitude()));
            preferenceUtils.saveString(AppConstant.LONGITUDE, String.valueOf(locationService.getLongitude()));
        }else{
            locationService.showSettingsAlert();
        }

        addFragment(StartOfDayFragment.newInstance());
    }

    @Override
    public void onCurrentLocationChanged(Location location) {
        if (location != null) {
            preferenceUtils.saveString(AppConstant.LATITUDE, String.valueOf(location.getLatitude()));
            preferenceUtils.saveString(AppConstant.LONGITUDE, String.valueOf(location.getLongitude()));
        }
    }
}
