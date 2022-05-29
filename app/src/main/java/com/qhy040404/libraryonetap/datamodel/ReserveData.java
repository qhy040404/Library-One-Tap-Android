package com.qhy040404.libraryonetap.datamodel;

import com.google.gson.Gson;

import java.io.Serializable;

public class ReserveData implements Serializable {
    Gson gson = new Gson();

    private static class GsonData {
        private boolean success;
        private DataBean data;

        public static class DataBean implements Serializable {
            private String addCode;
        }

        private String getAddCode() {
            String addCode = "";
            DataBean list = data;
            addCode = list.addCode;
            return addCode;
        }
    }

    public String getAddCode(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getAddCode();
    }
}
