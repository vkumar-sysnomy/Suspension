package com.farthestgate.suspensions.android.ui.startOfDay;


import android.widget.ImageButton;
import android.widget.TextView;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.base.BaseFragment;
import com.farthestgate.suspensions.android.base.BaseToolBarActivity;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.ui.suspensionMain.SelectActionFragment;
import com.farthestgate.suspensions.android.utils.DateUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class StartOfDayFragment extends BaseFragment {

    @BindView(R.id.tv_officer_id)
    TextView tvOfficerId;
    @BindView(R.id.tv_current_date)
    TextView tvCurrentDate;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.btnContinue)
    ImageButton btnContinue;

    public static StartOfDayFragment newInstance(){
        StartOfDayFragment startOfDayFragment = new StartOfDayFragment();
        return startOfDayFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_start_of_day;
    }

    @Override
    public void initializeUi() {
        ((BaseToolBarActivity)getActivity()).setupToolbar("Start Of Day");
        setData();
    }

    private void setData(){
        tvOfficerId.setText(preferenceUtils.getString(AppConstant.OFFICER_ID));
        tvCurrentDate.setText(DateUtils.getCurrentDate("dd/MM/yyyy"));
        tvTime.setText(DateUtils.getTime());
    }

    @OnClick(R.id.btnContinue)
    public void doContinue(){
        replaceFragment(SelectActionFragment.newInstance());
    }
}
