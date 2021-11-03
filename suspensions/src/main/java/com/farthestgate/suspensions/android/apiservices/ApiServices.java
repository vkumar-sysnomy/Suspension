package com.farthestgate.suspensions.android.apiservices;

import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.model.Config;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Suraj Gopal on 1/3/2017.
 */
public class ApiServices {

    private Apis mApis;

    public ApiServices() {
        mApis = createService(Apis.class);
    }

    public <T> T createService(Class<T> serviceClass) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.getConfigData().suspensionBaseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(JSONConverterFactory.create())
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
                .build();

        return retrofit.create(serviceClass);
    }

    public Apis getApis() {
        return mApis;
    }

    public static <T> void enqueueCall(final Call<T> call, final Callback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        try {
                            callback.onFailure(call, new Throwable(response.errorBody().string()));
                        } catch (IOException | NullPointerException e) {
                            callback.onFailure(call, new Throwable("Unknown"));
                            e.printStackTrace();
                        }
                    } else
                        callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("Response Failed Code : " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                t.printStackTrace();
                callback.onFailure(call, t);

            }
        });
    }
}
