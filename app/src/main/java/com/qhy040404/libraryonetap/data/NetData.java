package com.qhy040404.libraryonetap.data;

import com.google.gson.Gson;

import java.io.Serializable;

public class NetData implements Serializable {
    Gson gson = new Gson();

    private static class GsonData {
        private String fee;
        private String dynamicRemainFlow;
        private String dynamicUsedFlow;
    }

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
}