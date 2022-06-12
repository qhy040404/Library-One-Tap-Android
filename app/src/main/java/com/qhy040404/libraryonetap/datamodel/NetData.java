package com.qhy040404.libraryonetap.datamodel;

import com.google.gson.Gson;

import java.io.Serializable;

public class NetData {
    Gson gson = new Gson();

    public String getFee(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.fee;
    }

    public String getDynamicUsedFlow(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.dynamicUsedFlow;
    }

    public String getDynamicRemainFlow(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.dynamicRemainFlow;
    }

    private static class GsonData implements Serializable {
        private String fee;
        private String dynamicRemainFlow;
        private String dynamicUsedFlow;
    }
}