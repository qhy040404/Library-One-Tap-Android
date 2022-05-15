package com.qhy040404.libraryonetap.data;

import com.google.gson.Gson;

import java.io.Serializable;

public class NetData implements Serializable {
    Gson gson = new Gson();

    /**
     * fee : 47.15
     * flag : success
     * dynamicRemainFlow : 71.56
     * dynamicUsedFlow : 78.44
     */
    private static class GsonData {
        private String fee;
        private String flag;
        private String dynamicRemainFlow;
        private String dynamicUsedFlow;

        private String getFee() {
            return fee;
        }

        private String getDynamicRemainFlow() {
            return dynamicRemainFlow;
        }
    }

    public String getFee(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getFee();
    }

    public String getDynamicRemainFlow(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getDynamicRemainFlow();
    }
}
