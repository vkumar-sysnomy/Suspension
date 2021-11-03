package com.farthestgate.suspensions.android.ui.suspensionMain;


import android.content.Intent;
import android.widget.Button;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.base.BaseFragment;
import com.farthestgate.suspensions.android.base.BaseToolBarActivity;
import com.farthestgate.suspensions.android.ui.login.LoginActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectActionFragment extends BaseFragment {
    public static final String TAG = SelectActionFragment.class.getName();

    @BindView(R.id.btn_put_up_suspension)
    Button btnPutUpSuspension;
    @BindView(R.id.btn_take_down_suspension)
    Button btnTakeDownSuspension;
    @BindView(R.id.btn_logout)
    Button btnLogout;

    public static SelectActionFragment newInstance(){
        SelectActionFragment suspensionMainFragment = new SelectActionFragment();
        return suspensionMainFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_select_action;
    }

    @Override
    public void initializeUi() {
        ((BaseToolBarActivity)getActivity()).setupToolbar("Suspension");
    }

    @OnClick(R.id.btn_put_up_suspension)
    public void putUpSuspension(){
        replaceFragment(PutUpSuspensionFragment.newInstance());

    }

    @OnClick(R.id.btn_take_down_suspension)
    public void takeDownSuspension(){
        replaceFragment(TakeDownSuspensionFragment.newInstance());
    }

    @OnClick(R.id.btn_logout)
    public void doLogout(){
        preferenceUtils.clear();
        startActivity(LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
