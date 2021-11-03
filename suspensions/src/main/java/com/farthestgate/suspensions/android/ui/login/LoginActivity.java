package com.farthestgate.suspensions.android.ui.login;

import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.apiservices.ApiServices;
import com.farthestgate.suspensions.android.base.BaseActivity;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.model.Config;
import com.farthestgate.suspensions.android.ui.MainActivity;
import com.farthestgate.suspensions.android.utils.AppUtils;
import com.farthestgate.suspensions.android.utils.DialogUtils;
import com.farthestgate.suspensions.android.utils.FileUtils;
import com.farthestgate.suspensions.android.utils.PermissionUtils;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.main_layout)
    ConstraintLayout mainLayout;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_logo_text)
    TextView tvLogoText;
    @BindView(R.id.officer_id_layout)
    TextInputLayout officerIdLayout;
    @BindView(R.id.et_officerId)
    EditText etOfficerId;
    @BindView(R.id.officer_password_layout)
    TextInputLayout officerPasswordLayout;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_app_version)
    TextView tvAppVersion;


    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initializeUi() {




        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtils.checkPermissions(this, AppConstant.PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, AppConstant.PERMISSIONS, AppConstant.PERMISSION_ALL);
            } else {
                buildLoginScreen();
            }
        }else{
            buildLoginScreen();
        }
    }

    private void buildLoginScreen(){
        //Log.e("IMEI number",AppUtils.getUUID(this));
        config = Config.getConfigData();

        File file = FileUtils.getFile(AppConstant.ROOT_FOLDER, "logo.png");
        if(file != null && file.exists()){
            ivLogo.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }

        tvLogoText.setText(config.clientLogoText);
        tvAppVersion.setText("Version: " + AppUtils.getAppVersion(this));

        setupFloatingLabelError(officerIdLayout, etOfficerId);
        setupFloatingLabelError(officerPasswordLayout, etPassword);
    }

    @OnClick(R.id.btn_login)
    public void doLogin(){
        if(!AppUtils.isValid(officerIdLayout, etOfficerId, "Enter Officer Id")){
            return;
        } else if(!AppUtils.isValid(officerPasswordLayout, etPassword, "Enter Password")){
            return;
        }

        if(appController.isConnected(mainLayout)) {
            Map<String, String> fieldMap = new HashMap<>();
            fieldMap.put(AppConstant.USERNAME, etOfficerId.getText().toString().trim());
            fieldMap.put(AppConstant.PASSWORD, etPassword.getText().toString().trim());
            fieldMap.put(AppConstant.DEVICE_ID, AppUtils.getUUID(this));

            Call<JSONObject> call = new ApiServices().getApis().doLogin(fieldMap);
            progressDialog.show();
            ApiServices.enqueueCall(call, new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = response.body();
                        if(jsonObject != null) {
                            if (jsonObject.getString("success").equals("OK")) {
                                preferenceUtils.saveString(AppConstant.OFFICER_ID, etOfficerId.getText().toString().trim());
                                startActivity(MainActivity.class);
                            } else if (jsonObject.getString("success").equals("NOK")) {
                                DialogUtils.showToast(LoginActivity.this, jsonObject.getString("message")== null ? "Oops, Something Went Wrong" : jsonObject.getString("message"));

                            }
                        } else {
                            DialogUtils.showToast(LoginActivity.this, "Oops, Something Went Wrong");
                        }
                    } catch (Exception e){
                        FileUtils.logError(e);
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    progressDialog.dismiss();
                    DialogUtils.showToast(LoginActivity.this, t.getMessage());
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConstant.PERMISSION_ALL:
                boolean isPermissionGranted = true;
                for(int granted : grantResults) {
                    if(granted != PackageManager.PERMISSION_GRANTED){
                        isPermissionGranted = false;
                        break;
                    }
                }
                //boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (isPermissionGranted) {
                    buildLoginScreen();
                } else {
                    DialogUtils.showExitDialog(this, "You have not given required permissions to run this application.", "Application close");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void setupFloatingLabelError(final TextInputLayout textInputLayout, final EditText editText) {
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0) {
                    textInputLayout.setError(editText.getHint());
                    textInputLayout.setErrorEnabled(true);
                } else {
                    textInputLayout.setErrorEnabled(false);
                    textInputLayout.setError(null);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}


