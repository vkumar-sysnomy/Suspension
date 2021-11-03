package com.farthestgate.suspensions.android.ui.suspensionMain;


import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.base.BaseFragment;
import com.farthestgate.suspensions.android.base.BaseToolBarActivity;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.ui.suspensionMain.model.Suspension;
import com.farthestgate.suspensions.android.utils.DialogUtils;
import com.farthestgate.suspensions.android.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */

public class AddVRMsFragment extends BaseFragment {
    @BindView(R.id.iv_addVRM)
    ImageView ivAddVRM;
    @BindView(R.id.btn_done)
    Button btnDone;
    @BindView(R.id.txtVRM1)
    EditText txtVRM1;
    @BindView(R.id.txtVRM2)
    EditText txtVRM2;
    @BindView(R.id.txtVRM3)
    EditText txtVRM3;
    @BindView(R.id.txtVRM4)
    EditText txtVRM4;
    @BindView(R.id.txtVRM5)
    EditText txtVRM5;
    @BindView(R.id.txtVRM6)
    EditText txtVRM6;
    @BindView(R.id.txtVRM7)
    EditText txtVRM7;
    @BindView(R.id.txtVRM8)
    EditText txtVRM8;
    @BindView(R.id.txtVRM9)
    EditText txtVRM9;
    @BindView(R.id.txtVRM10)
    EditText txtVRM10;

    private Suspension suspension;
    private List<JSONObject> vrmList = new ArrayList();

    public static AddVRMsFragment newInstance(){
        AddVRMsFragment addVRMsFragment = new AddVRMsFragment();
        return addVRMsFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_add_vrms;
    }

    @Override
    public void initializeUi() {
        ((BaseToolBarActivity)getActivity()).setupToolbar("Add VRMs");

        txtVRM1.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtVRM2.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtVRM3.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtVRM4.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtVRM5.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtVRM6.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtVRM7.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtVRM8.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtVRM9.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtVRM10.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        suspension = preferenceUtils.getObject(AppConstant.SUSPENSION, Suspension.class);

    }

    @OnClick(R.id.iv_addVRM)
    public void AddVRMs() {
        addVRMsToSuspension();

    }

    @OnClick(R.id.btn_done)
    public void MoveToCaptureEvidence(){
        addVRMsToSuspension();
        if (vrmList.size() > 0) {
            suspension.setVrmList(vrmList);
        }
        if(suspension.getVrmList().size() > 0){
            preferenceUtils.saveObject(AppConstant.SUSPENSION, suspension);
            replaceFragment(CaptureEvidenceFragment.newInstance());
        }else {
            DialogUtils.showToast(getContext(), "Please enter at least one VRM");
        }
    }

    private void addVRMsToSuspension(){
        if (!txtVRM1.getText().toString().isEmpty()) {
            addVRM(txtVRM1);
        }
        if (!txtVRM2.getText().toString().isEmpty()) {
            addVRM(txtVRM2);
        }
        if (!txtVRM3.getText().toString().isEmpty()) {
            addVRM(txtVRM3);
        }
        if (!txtVRM4.getText().toString().isEmpty()) {
            addVRM(txtVRM4);
        }
        if (!txtVRM5.getText().toString().isEmpty()) {
            addVRM(txtVRM5);
        }
        if (!txtVRM6.getText().toString().isEmpty()) {
            addVRM(txtVRM6);
        }
        if (!txtVRM7.getText().toString().isEmpty()) {
            addVRM(txtVRM7);
        }
        if (!txtVRM8.getText().toString().isEmpty()) {
            addVRM(txtVRM8);
        }
        if (!txtVRM9.getText().toString().isEmpty()) {
            addVRM(txtVRM9);
        }
        if (!txtVRM10.getText().toString().isEmpty()) {
            addVRM(txtVRM10);
        }
        txtVRM1.requestFocus();
    }

    private void addVRM(EditText editText){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("vrm", editText.getText().toString().trim());
            vrmList.add(jsonObject);
            editText.setText("");
        } catch (JSONException e) {
            FileUtils.logError(e);
        }
    }
}
