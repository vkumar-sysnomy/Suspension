package com.farthestgate.suspensions.android.ui.suspensionMain;


import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.apiservices.ApiServices;
import com.farthestgate.suspensions.android.base.BaseFragment;
import com.farthestgate.suspensions.android.base.BaseToolBarActivity;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.ui.suspensionMain.helper.RecyclerTouchListener;
import com.farthestgate.suspensions.android.ui.suspensionMain.helper.SuspensionTakeDownAdapter;
import com.farthestgate.suspensions.android.ui.suspensionMain.model.Suspension;
import com.farthestgate.suspensions.android.utils.AppUtils;
import com.farthestgate.suspensions.android.utils.DateUtils;
import com.farthestgate.suspensions.android.utils.DialogUtils;
import com.farthestgate.suspensions.android.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TakeDownSuspensionFragment extends BaseFragment {
    @BindView(R.id.layoutSuspensionTakeDown)
    ConstraintLayout layoutSuspensionTakeDown;
    @BindView(R.id.suspension_recycler_view)
    RecyclerView suspensionRecyclerView;
    @BindView(R.id.emptyView)
    TextView emptyView;

    SuspensionTakeDownAdapter suspensionTakeDownAdapter;
    private List<Suspension> suspensionList ;

    public static TakeDownSuspensionFragment newInstance(){
        TakeDownSuspensionFragment takeDownSuspensionFragment = new TakeDownSuspensionFragment();
        return takeDownSuspensionFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_take_down_suspension;
    }

    @Override
    public void initializeUi() {
        ((BaseToolBarActivity)getActivity()).setupToolbar("Take Down Suspensions");
        initializeRecyclerView();
        populateTakeDownSuspensionData();
    }

    private void initializeRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        suspensionRecyclerView.setLayoutManager(mLayoutManager);
        suspensionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        suspensionRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        suspensionRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), suspensionRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Suspension suspension = suspensionList.get(position);
                getSuspensionDetails(suspension.getSuspensionNumber());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void populateTakeDownSuspensionData() {
        suspensionList = new ArrayList<>();
        if(appController.isConnected(layoutSuspensionTakeDown)) {
//            String fromDate = DateUtils.getDateString(DateUtils.yesterday(), "dd/MM/yyyy");
//            String thruDate = DateUtils.getDateString(new Date(), "dd/MM/yyyy");

            String thruDate = DateUtils.getDateString(DateUtils.yesterday(), "dd/MM/yyyy");
            String fromDate = DateUtils.getDateString(DateUtils.getDefaultDate(), "dd/MM/yyyy");


            Call<JSONObject> call = mApis.getSuspensionList("listOfApprovedSuspension", "SIGN_UP", fromDate, thruDate,
                    preferenceUtils.getString(AppConstant.OFFICER_ID), AppUtils.getUUID(getContext()));
            progressDialog.show();
            ApiServices.enqueueCall(call, new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    JSONObject jsonObject = response.body();
                    if(jsonObject != null){
                        try {
                            JSONObject responseObj = jsonObject.getJSONObject("RESPONSE_DATA");
                            if(responseObj != null){
                                if(responseObj.getString("success").equalsIgnoreCase("OK")) {
                                    JSONArray approvedSusList = responseObj.getJSONArray("approvedSusList");
                                    if (approvedSusList.length() > 0) {
                                        Suspension suspension;
                                        for (int index = 0; index < approvedSusList.length(); index++) {
                                            try {
                                                Object jsonObj = approvedSusList.getJSONObject(index).get("json");
                                                JSONObject approvedSusObj = new JSONObject(jsonObj.toString());
                                                suspension = new Suspension();
                                                suspension.setSuspensionNumber(approvedSusObj.optString("orderId"));
                                                suspension.setLocation(approvedSusObj.optString("streetName"));
                                                suspension.setReason(approvedSusObj.optString("suspensionReason"));
                                                suspension.setStart(approvedSusObj.optString("startDate"));
                                                suspension.setEnd(approvedSusObj.optString("endDate"));
                                                suspension.setBaysNumber(approvedSusObj.optString("bayNumber"));
                                                suspension.setStatusId(approvedSusList.getJSONObject(index).optString("statusId"));
                                                suspensionList.add(suspension);
                                            } catch (JSONException e) {
                                                FileUtils.logError(e);
                                            }
                                        }
                                    }/*else{
                                        DialogUtils.showToast(getContext(), "No suspension found");
                                    }*/
                                }/* else if(responseObj.getString("success").equals("NOK")){
                                    DialogUtils.showToast(getContext(), "Suspension record not found");
                                }*/
                            }
                        } catch (Exception e) {
                            FileUtils.logError(e);
                        } finally {
                            progressDialog.dismiss();

                            if(suspensionList.isEmpty()){
                                emptyView.setVisibility(View.VISIBLE);
                                suspensionRecyclerView.setVisibility(View.GONE);
                            } else {
                                emptyView.setVisibility(View.GONE);
                                suspensionRecyclerView.setVisibility(View.VISIBLE);
                            }

                            Collections.sort(suspensionList);

                            suspensionTakeDownAdapter = new SuspensionTakeDownAdapter(suspensionList);
                            suspensionRecyclerView.setAdapter(suspensionTakeDownAdapter);
                        }

                    }else {
                        DialogUtils.showToast(getContext(), "Oops, Something Went Wrong.\nUnable to get list of approved suspensions");
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

    public void getSuspensionDetails(String suspensionNumber) {
        //Delete All the photos from internal storage
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                FileUtils.deleteDir(FileUtils.getDirectory(AppConstant.PHOTOS_FOLDER));
            }
        });

        if(appController.isConnected(layoutSuspensionTakeDown)) {

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
                                    suspension.setPutUp(false);

                                    if(suspension.getStatusId().equals("SIGN_UP")) {
                                        suspension.setEnd(permitDataObject.optString("endDate") + " " + permitDataObject.optString("eachDaySuspensionEndAt"));
                                        preferenceUtils.saveObject(AppConstant.SUSPENSION, suspension);
                                        replaceFragment(PutUpSuspensionDetailFragment.newInstance());
                                    } else {
                                        DialogUtils.showToast(getContext(), "Sign can not be put down for this suspension.");
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
}
