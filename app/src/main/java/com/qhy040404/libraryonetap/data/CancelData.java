package com.qhy040404.libraryonetap.data;

import com.google.gson.Gson;

import java.io.Serializable;

public class CancelData implements Serializable {
    Gson gson = new Gson();

    /**
     * success : true
     * message : 用户在线！
     */
    private static class GsonData {
        private boolean success;
        private String message;
    }

    public String getMessage(String returnData) {
        GsonData gsonData = gson.fromJson(returnData, GsonData.class);
        return gsonData.message;
    }
}
