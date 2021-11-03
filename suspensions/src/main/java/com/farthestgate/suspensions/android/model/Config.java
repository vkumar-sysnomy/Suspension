package com.farthestgate.suspensions.android.model;

import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {

    public static Config config = null;

    private Config(){

    }

    @SerializedName("clientLogoText")
    @Expose
    public String clientLogoText;
    @SerializedName("image_x_size")
    @Expose
    public String imageXSize;
    @SerializedName("image_y_size")
    @Expose
    public String imageYSize;
    @Expose
    @SerializedName("suspension_base_url")
    public String suspensionBaseUrl;

    public static Config getConfigData() {
        if(config == null) {
            String config = FileUtils.readFile(AppConstant.CONFIG_DATA, "config.json");
            Config.config = new Gson().fromJson(config, Config.class);
        }
        return config;
    }
}
