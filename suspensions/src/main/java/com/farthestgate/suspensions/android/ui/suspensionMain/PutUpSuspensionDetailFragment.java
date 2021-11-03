package com.farthestgate.suspensions.android.ui.suspensionMain;


import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import android.widget.EditText;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.base.BaseFragment;
import com.farthestgate.suspensions.android.base.BaseToolBarActivity;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.ui.suspensionMain.model.Suspension;

import butterknife.BindView;
import butterknife.OnClick;

public class PutUpSuspensionDetailFragment extends BaseFragment {

    @BindView(R.id.suspension_number_layout)
    TextInputLayout suspensionNumberLayout;
    @BindView(R.id.et_suspension_number)
    EditText etSuspensionNumber;
    @BindView(R.id.location_layout)
    TextInputLayout locationLayout;
    @BindView(R.id.et_location)
    EditText etLocation;
    @BindView(R.id.reason_layout)
    TextInputLayout reasonLayout;
    @BindView(R.id.et_reason)
    EditText etReason;
    @BindView(R.id.start_layout)
    TextInputLayout startLayout;
    @BindView(R.id.et_start)
    EditText etStart;
    @BindView(R.id.end_layout)
    TextInputLayout endLayout;
    @BindView(R.id.et_end)
    EditText etEnd;
    @BindView(R.id.num_of_bays_layout)
    TextInputLayout numOfbaysLayout;
    @BindView(R.id.et_num_of_bays)
    EditText etNumOfBays;
    @BindView(R.id.additional_instruction_layout)
    TextInputLayout additionalInstructionLayout;
    @BindView(R.id.et_additional_instructions)
    EditText etAddditionalInstructions;
    @BindView(R.id.btn_continue)
    Button btnContinue;
    @BindView(R.id.btn_back)
    Button btnBack;

    private Suspension suspension;

    public static PutUpSuspensionDetailFragment newInstance(){
        PutUpSuspensionDetailFragment putUpSuspensionDestailFragment = new PutUpSuspensionDetailFragment();
        return putUpSuspensionDestailFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_put_up_suspension_destail;
    }

    @Override
    public void initializeUi() {
        ((BaseToolBarActivity)getActivity()).setupToolbar("Suspension Details");

        suspension = preferenceUtils.getObject(AppConstant.SUSPENSION, Suspension.class);
        setData(suspension);

    }

    private void setData(Suspension suspension){
        if(suspension != null) {
            etSuspensionNumber.setText(suspension.getSuspensionNumber());
            etLocation.setText(suspension.getLocation());
            etReason.setText(suspension.getReason());
            etStart.setText(suspension.getStart());
            etEnd.setText(suspension.getEnd());
            etNumOfBays.setText(suspension.getBaysNumber());
            etAddditionalInstructions.setText(suspension.getAdditionalInstructions().isEmpty() ? "None" : suspension.getAdditionalInstructions());
        }
    }

    @OnClick(R.id.btn_back)
    public void goBack(){
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @OnClick(R.id.btn_continue)
    public void openCompleteJobFragment(){
        replaceFragment(CaptureEvidenceFragment.newInstance());
    }


}
