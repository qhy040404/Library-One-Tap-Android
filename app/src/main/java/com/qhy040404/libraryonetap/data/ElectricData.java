package com.qhy040404.libraryonetap.data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ElectricData implements Serializable {
    Gson gson = new Gson();

    private static class GsonData {
        private List<DormitoryInfoListBean> dormitoryInfo_list;

        public static class DormitoryInfoListBean implements Serializable {
            @SerializedName("SSMC")
            private String ssmc;
            private String resele;
        }
    }

    public String getSSMC(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.dormitoryInfo_list.get(0).ssmc;
    }

    public String getResele(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.dormitoryInfo_list.get(0).resele;
    }
}
