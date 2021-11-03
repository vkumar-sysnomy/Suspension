package com.farthestgate.suspensions.android.ui.suspensionMain;


import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.base.BaseFragment;
import com.farthestgate.suspensions.android.base.BaseToolBarActivity;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.ui.camera.CameraActivity;
import com.farthestgate.suspensions.android.ui.notes.NotesFragment;
import com.farthestgate.suspensions.android.ui.suspensionMain.model.Suspension;
import com.farthestgate.suspensions.android.utils.AppUtils;
import com.farthestgate.suspensions.android.utils.DateUtils;
import com.farthestgate.suspensions.android.utils.DialogUtils;
import com.farthestgate.suspensions.android.utils.FileUtils;
import com.farthestgate.suspensions.android.utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaptureEvidenceFragment extends BaseFragment {

    @BindView(R.id.main_layout)
    ConstraintLayout mainLayout;
    @BindView(R.id.suspension_number)
    TextView suspensionNumber;
    @BindView(R.id.btn_take_photos)
    Button btnTakePhotos;
    @BindView(R.id.btn_add_notes)
    Button btnAddNotes;
    @BindView(R.id.btn_add_vrms)
    Button btnAddVRM;
    @BindView(R.id.btn_complete_job)
    Button btnCompleteJobs;

    private Suspension suspension;
    private boolean isPutUp;


    public static CaptureEvidenceFragment newInstance(){
        CaptureEvidenceFragment completeJobFragment = new CaptureEvidenceFragment();
        return completeJobFragment;
    }


    @Override
    public int getLayout() {
        return R.layout.fragment_capture_evidence;
    }

    @Override
    public void initializeUi() {

    }

    @Override
    public void onResume() {
        super.onResume();

        suspension = preferenceUtils.getObject(AppConstant.SUSPENSION, Suspension.class);

        if(suspension.isPutUp())
            ((BaseToolBarActivity)getActivity()).setupToolbar("Sign Up - Capture Evidence");
        else
            ((BaseToolBarActivity)getActivity()).setupToolbar("Sign Down - Capture Evidence");

        suspensionNumber.setText(suspension.getSuspensionNumber());

        if(suspension != null && suspension.isPutUp()){
            btnAddVRM.setVisibility(View.VISIBLE);
        } else {
            btnAddVRM.setVisibility(View.GONE);
        }

        controlCompleteJobsButton();
    }

    @OnClick(R.id.btn_take_photos)
    public void takePhotos(){
        Intent intent = new Intent(getContext(), CameraActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_add_notes)
    public void addNotes(){
        replaceFragment(NotesFragment.newInstance());
    }

    @OnClick(R.id.btn_add_vrms)
    public void addVrm(){
        replaceFragment(AddVRMsFragment.newInstance());
    }


    private void controlCompleteJobsButton() {
        if (suspension != null && suspension.getPhotoCount() > 0) {
            btnCompleteJobs.setEnabled(true);
            btnCompleteJobs.setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent));
        } else {
            btnCompleteJobs.setEnabled(false);
            btnCompleteJobs.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_gray));
        }
    }

    @OnClick(R.id.btn_complete_job)
    public void postSuspensionData(){

        if(appController.isConnected(mainLayout)) {
            setPhotoWatermarked();

            List<MultipartBody.Part> multiPartBodies = new ArrayList<>();

            File[] files = FileUtils.getDirectory(AppConstant.PHOTOS_FOLDER).listFiles();
            for (File file : files) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                if(suspension.isPutUp())
                    multiPartBodies.add(MultipartBody.Part.createFormData("upload|SIGNUP_PHOTO|SIGNUP_PHOTO", file.getName(), requestFile));
                else
                    multiPartBodies.add(MultipartBody.Part.createFormData("upload|SIGN_DOWN_PHOTO|SIGN_DOWN_PHOTO", file.getName(), requestFile));
            }
            RequestBody formName;
            if(suspension.isPutUp()) {
                formName = createPartFromString("ISSUE_SUSPENSION");
            } else {
                formName = createPartFromString("SIGN_DOWN");
            }
            RequestBody productId = createPartFromString("product_id");
            RequestBody addProductId = createPartFromString("add_product_id");

            RequestBody userLoginId = createPartFromString( preferenceUtils.getString(AppConstant.OFFICER_ID));
            RequestBody orderId = createPartFromString(suspension.getSuspensionNumber());
            RequestBody dateTime = createPartFromString(DateUtils.getCurrentDate("dd/MM/yyyy HH:mm"));
            RequestBody notes = createPartFromString(suspension.getNotes().toString());
            RequestBody latitude = createPartFromString( preferenceUtils.getString(AppConstant.LATITUDE));
            RequestBody longitude = createPartFromString( preferenceUtils.getString(AppConstant.LONGITUDE));
            RequestBody vehicleDetails = createPartFromString(suspension.getVrmList().toString());
            RequestBody deviceId = createPartFromString(AppUtils.getUUID(getContext()));

            HashMap<String, RequestBody> paramMap = new HashMap<>();
            paramMap.put("formName", formName);
            paramMap.put("productId", productId);
            paramMap.put("addProductId", addProductId);
            paramMap.put("vehicleDetails", vehicleDetails);
            paramMap.put("userLoginId", userLoginId);
            paramMap.put("orderId", orderId);
            paramMap.put("dateTime", dateTime);
            paramMap.put("notes", notes);
            paramMap.put("latitude", latitude);
            paramMap.put("longitude", longitude);
            paramMap.put(AppConstant.DEVICE_ID, deviceId);


            Call<JSONObject> call = mApis.postSuspensionData("Y", multiPartBodies, paramMap);
            progressDialog.show();
            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    progressDialog.dismiss();

                    JSONObject responseObj = response.body();
                    try {
                        if (responseObj != null && responseObj.getString("success").equals("OK")) {
                            DialogUtils.showToast(getContext(), "Suspension data successfully uploaded");
                            replaceFragment(SelectActionFragment.newInstance());
                        } else if(responseObj != null && responseObj.getString("success").equals("NOK")){
                            DialogUtils.showToast(getContext(), "Unable to upload suspension data");
                        }
                    } catch (JSONException e){
                        FileUtils.logError(e);
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

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }


    private void setPhotoWatermarked() {
        for (File photoFile : FileUtils.getDirectory(AppConstant.PHOTOS_FOLDER).listFiles()) {
            ExifInterface exifInterface = readExifData(photoFile.getAbsolutePath());
            if (photoFile.exists()) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                ImageUtils.applyImageWatermark(photoFile, timeStamp);
                saveExifData(exifInterface, photoFile.getAbsolutePath());
            }
            Runtime.getRuntime().gc();
            Runtime.getRuntime().freeMemory();
        }
    }

    private ExifInterface readExifData(String filename) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exifInterface;
    }

    private void saveExifData(ExifInterface exif, String filePath){
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            exifInterface.setAttribute("Make",  exif.getAttribute("Make"));
            exifInterface.setAttribute("Model", exif.getAttribute("Model"));
            exifInterface.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
            exifInterface.setAttribute(ExifInterface.TAG_IMAGE_WIDTH,  exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
            exifInterface.setAttribute(ExifInterface.TAG_DATETIME, exif.getAttribute(ExifInterface.TAG_DATETIME));
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, exif.getAttribute(ExifInterface.TAG_ORIENTATION));

            //GPS latitude and longitude
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));

            exifInterface.setAttribute("UserComment", exif.getAttribute("UserComment"));

            exifInterface.saveAttributes();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
