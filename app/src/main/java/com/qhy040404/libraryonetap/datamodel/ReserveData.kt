package com.qhy040404.libraryonetap.datamodel;

import com.google.gson.Gson;

import java.io.Serializable;

public class ReserveData {
    Gson gson = new Gson();

    public String getAddCode(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getAddCode();
    }

    private static class GsonData implements Serializable {
        private boolean success;
        private DataBean data;

        private String getAddCode() {
            DataBean list = data;
            return list.addCode;
        }

        public static class DataBean implements Serializable {
            private String addCode;
        }
    }
}
