package com.qhy040404.libraryonetap.data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ElectricData implements Serializable {
    Gson gson = new Gson();

    /**
     * dormitoryInfo_list : [{"SSMC":"***","ZSBH":"***","flag":"success","resele":"155.43"}]
     * flag : success
     */
    private static class GsonData {
        private String flag;
        private List<DormitoryInfoListBean> dormitoryInfo_list;

        private String getSSMC() {
            DormitoryInfoListBean list = new DormitoryInfoListBean();
            list = dormitoryInfo_list.get(0);
            return list.ssmc;
        }

        private String getResele() {
            DormitoryInfoListBean list = new DormitoryInfoListBean();
            list = dormitoryInfo_list.get(0);
            return list.resele;
        }

        public static class DormitoryInfoListBean implements Serializable {
            /**
             * SSMC : ***
             * ZSBH : ***
             * flag : success
             * resele : 155.43
             */

            @SerializedName("SSMC")
            private String ssmc;
            @SerializedName("ZSBH")
            private String zsbh;
            private String flag;
            private String resele;
        }
    }

    public String getSSMC(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getSSMC();
    }

    public String getResele(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getResele();
    }
}
