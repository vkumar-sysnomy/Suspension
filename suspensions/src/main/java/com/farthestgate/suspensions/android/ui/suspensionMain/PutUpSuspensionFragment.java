package com.farthestgate.suspensions.android.ui.suspensionMain;


import android.os.AsyncTask;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.apiservices.ApiServices;
import com.farthestgate.suspensions.android.base.BaseFragment;
import com.farthestgate.suspensions.android.base.BaseToolBarActivity;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.ui.suspensionMain.model.Suspension;
import com.farthestgate.suspensions.android.utils.AppUtils;
import com.farthestgate.suspensions.android.utils.DialogUtils;
import com.farthestgate.suspensions.android.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PutUpSuspensionFragment extends BaseFragment {

    @BindView(R.id.main_layout)
    RelativeLayout mainLayout;
    @BindView(R.id.et_suspension_number)
    EditText etSuspensionNumber;
    @BindView(R.id.btn_suspensions_search)
    Button btnSuspensionSearch;
    @BindView(R.id.btn_back)
    Button btnBack;

    public static PutUpSuspensionFragment newInstance(){
        PutUpSuspensionFragment putUpSuspensionFragment = new PutUpSuspensionFragment();
        return putUpSuspensionFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_put_up_suspension;
    }

    @Override
    public void initializeUi() {
        ((BaseToolBarActivity)getActivity()).setupToolbar("Put Up Suspension");
        etSuspensionNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
    }

    @OnClick(R.id.btn_suspensions_search)
    public void getSuspensionDetails() {
        //Delete All the photos from internal storage
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                FileUtils.deleteDir(FileUtils.getDirectory(AppConstant.PHOTOS_FOLDER));
            }
        });

        String suspensionNumber = etSuspensionNumber.getText().toString().trim();
        if(suspensionNumber.isEmpty()){
            DialogUtils.showSnackBar(mainLayout, "Please Enter Suspension Number");
            return;
        }

        if(appController.isConnected(mainLayout)) {

            Call<JSONObject> call = mApis.getSuspensionInfo("getPermitInfo", suspensionNumber, preferenceUtils.getString(AppConstant.OFFICER_ID),
                    AppUtils.getUUID(getContext()));
            progressDialog.show();
            ApiServices.enqueueCall(call, new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    progressDialog.dismiss();
                    JSONObject jsonObject = response.body();
                    if(jsonObject != null){
                        try {
                            JSONObject resultObj = jsonObject.getJSONObject("RESPONSE_DATA").getJSONObject("result");
                            if(resultObj != null){
                                if(resultObj.getString("success").equals("OK")){
                                    JSONObject permitDataObject = resultObj.getJSONObject("permitData");
                                    JSONObject permitSortedObject = resultObj.getJSONObject("permitSorted");

                                    Suspension suspension = new Suspension();
                                    suspension.setSuspensionNumber(permitDataObject.optString("orderId"));
                                    suspension.setLocation(permitDataObject.optString("streetName"));
                                    suspension.setReason(permitSortedObject.optString("suspensionReason"));
                                    suspension.setStart(permitDataObject.optString("startDate") + " " + permitDataObject.optString("eachDaySuspensionStartAt"));
                                    suspension.setEnd(permitDataObject.optString("endDate") + " " + permitDataObject.optString("eachDaySuspensionEndAt"));
                                    suspension.setBaysNumber(permitDataObject.optString("bayNumber"));
                                    suspension.setStatusId(permitDataObject.optString("statusId"));
                                    suspension.setAdditionalInstructions(permitDataObject.optString("addInstContractor"));
                                    suspension.setPutUp(true);

                                    if(suspension.getStatusId().equals("ORDER_APPROVED")) {
                                        preferenceUtils.saveObject(AppConstant.SUSPENSION, suspension);
                                        replaceFragment(PutUpSuspensionDetailFragment.newInstance());
                                    } else {
                                        DialogUtils.showToast(getContext(), "Sign can not be put up for this suspension.");
                                    }

                                } else if(resultObj.getString("success").equals("NOK")){
                                    DialogUtils.showToast(getContext(), getString(R.string.suspension_error_msg));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        DialogUtils.showToast(getContext(), "Oops, Something Went Wrong");
                    }

                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    progressDialog.dismiss();
                    DialogUtils.showToast(getContext(), t.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.btn_back)
    public void goBack(){
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
