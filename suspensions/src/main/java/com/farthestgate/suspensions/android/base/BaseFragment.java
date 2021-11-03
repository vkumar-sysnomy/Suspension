package com.farthestgate.suspensions.android.base;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.apiservices.ApiServices;
import com.farthestgate.suspensions.android.apiservices.Apis;
import com.farthestgate.suspensions.android.model.Config;
import com.farthestgate.suspensions.android.utils.AppUtils;
import com.farthestgate.suspensions.android.utils.DialogUtils;
import com.farthestgate.suspensions.android.utils.PreferenceUtils;

import butterknife.ButterKnife;

/**
 * Created by Suraj on 15/09/16.
 */
public abstract class BaseFragment extends Fragment implements BaseListener {

    protected BaseActivity mBaseActivity;
    protected AppController appController;
    protected Fragment mCurrentFragment;

    protected PreferenceUtils preferenceUtils;

    protected Apis mApis;
    protected ProgressDialog progressDialog;

    protected Config config;

    @Override
    public void onAttach(Context context) {
        mBaseActivity = (BaseActivity) context;
        appController = mBaseActivity.appController;
        preferenceUtils = mBaseActivity.preferenceUtils;
        config = mBaseActivity.config;
        AppUtils.hideKeyboardForce(context);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApis = new ApiServices().getApis();
        progressDialog = DialogUtils.getProgressDialog(getContext(), "Please wait..");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        initializeUi();
        AppUtils.hideKeyboardForce(getContext());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    protected void replaceFragment(Fragment fragment) {
        try {
            mCurrentFragment = fragment;
            String backStateName = fragment.getClass().getName();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mCurrentFragment, backStateName)
                    .addToBackStack(null).commitAllowingStateLoss();
        } catch (Exception e) {

        }
    }

    protected <T extends Fragment> T getFragmentByTag(Class<T> fragmentClass) {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(fragmentClass.getName());

        if (fragment != null)
            return fragmentClass.cast(fragment);
        else
            return null;
    }

    public void startActivity(Class cls) {
        Intent intent = new Intent(getContext(), cls);
        startActivity(intent);
    }

    public void startActivity(Class cls, int flags) {
        Intent intent = new Intent(getContext(), cls);
        intent.setFlags(flags);
        startActivity(intent);
    }

}
