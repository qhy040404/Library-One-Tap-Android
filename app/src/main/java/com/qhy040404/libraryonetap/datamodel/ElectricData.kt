package com.qhy040404.libraryonetap.datamodel;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class ElectricData {
    Gson gson = new Gson();

    public String getSSMC(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.dormitoryInfo_list.get(0).ssmc;
    }

    public String getResele(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.dormitoryInfo_list.get(0).resele;
    }

    private static class GsonData implements Serializable {
        private List<DormitoryInfoListBean> dormitoryInfo_list;

        public static class DormitoryInfoListBean implements Serializable {
            @SerializedName("SSMC")
            private String ssmc;
            private String resele;
        }
    }
}