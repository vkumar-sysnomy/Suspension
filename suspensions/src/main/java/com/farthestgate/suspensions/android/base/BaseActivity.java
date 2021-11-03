package com.farthestgate.suspensions.android.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.apiservices.ApiServices;
import com.farthestgate.suspensions.android.apiservices.Apis;
import com.farthestgate.suspensions.android.model.Config;
import com.farthestgate.suspensions.android.utils.AppUtils;
import com.farthestgate.suspensions.android.utils.DialogUtils;
import com.farthestgate.suspensions.android.utils.FileUtils;
import com.farthestgate.suspensions.android.utils.PreferenceUtils;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity implements
        BaseListener {

    private Fragment mCurrentFragment;
    protected Toolbar toolbar;

    protected AppController appController;
    protected PreferenceUtils preferenceUtils;
    protected ProgressDialog progressDialog;

    protected Config config;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        appController = (AppController) getApplication();
        preferenceUtils = new PreferenceUtils(this);
        progressDialog = DialogUtils.getProgressDialog(this, "Please wait..");

        if (getLayout() != 0) {
            setContentView(getLayout());
            ButterKnife.bind(this);
            initializeUi();
            AppUtils.hideKeyboardForce(this);
            handleGlobalException();
        }
    }

    protected void addFragment(Fragment fragment) {
        try {
            mCurrentFragment = fragment;
            String backStateName  = fragment.getClass().getName();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mCurrentFragment, backStateName).addToBackStack(null).commitAllowingStateLoss();
        } catch (Exception e) {
            FileUtils.logError(e);
        }
    }

    protected void replaceFragment(Fragment fragment, int containerId, boolean isContainsTag) {
        try {
            mCurrentFragment = fragment;
            if(isContainsTag) {
                String backStateName = fragment.getClass().getName();
                getSupportFragmentManager().beginTransaction().replace(containerId, mCurrentFragment)
                        .addToBackStack(backStateName).commitAllowingStateLoss();
            } else {
                getSupportFragmentManager().beginTransaction().replace(containerId, mCurrentFragment)
                        .addToBackStack(null).commitAllowingStateLoss();
            }
        } catch (Exception e) {
            FileUtils.logError(e);
        }
    }

    protected <T extends Fragment> T getFragmentByTag(Class<T> fragmentClass) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentClass.getName());

        if (fragment != null)
            return fragmentClass.cast(fragment);
        else
            return null;
    }

    public void startActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void startActivity(Class cls, int flags) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(flags);
        startActivity(intent);
    }

    private void handleGlobalException(){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                FileUtils.logError(paramThrowable);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

    }
}
