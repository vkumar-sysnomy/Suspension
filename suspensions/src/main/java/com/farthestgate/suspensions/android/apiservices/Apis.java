package com.farthestgate.suspensions.android.apiservices;

import com.farthestgate.suspensions.android.ui.suspensionMain.model.Suspension;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface Apis {

    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("hhtLogin")
    Call<JSONObject> doLogin(@FieldMap Map<String, String> fieldMap);

    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("hhtservice")
    Call<JSONObject> getSuspensionInfo(@Field(value = "serviceRef", encoded = false) String serviceRef,
                                       @Field(value = "ref", encoded = false) String ref,
                                       @Field(value = "userLoginId", encoded = false) String userLoginId,
                                       @Field(value = "deviceId", encoded = false) String deviceId);

    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("hhtservice")
    Call<JSONObject> getSuspensionList(@Field(value = "serviceRef", encoded = false) String serviceRef,
                                       @Field(value = "statusId", encoded = false) String statusId,
                                       @Field(value = "fromDate", encoded = false) String fromDate,
                                       @Field(value = "thruDate", encoded = false) String thruDate,
                                       @Field(value = "userLoginId", encoded = false) String userLoginId,
                                       @Field(value = "deviceId", encoded = false) String deviceId);

    @Multipart
    @Headers({
            "Content-Type: multipart/form-data; charset = utf-8; boundary=p1t7xPBs9WD$Kv8Cq20VEZtzl3ObAfhAey0EiVI"
    })
    @POST("hhtprocessform")
    Call<JSONObject> postSuspensionData(@Query("hasFile") String hasFile, @Part List<MultipartBody.Part> images, @PartMap Map<String, RequestBody> paramMap);
}