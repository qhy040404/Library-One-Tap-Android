package com.qhy040404.libraryonetap.web1;

import com.google.gson.Gson;

import java.io.Serializable;

public class CheckSession implements Serializable {
    Gson gson = new Gson();

    /**
     * success : true
     * message : 用户在线！
     * user_id : ***
     */
    private static class GsonData {
        private boolean success;
        private String message;
        private String user_id;

        private boolean getSuccess() {
            return success;
        }
    }

    public boolean isSuccess(String returnData) {
        GsonData gsonData = gson.fromJson(returnData, GsonData.class);
        return gsonData.getSuccess();
    }
}
