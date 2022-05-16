package com.qhy040404.libraryonetap.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.qhy040404.libraryonetap.R;

public class PermissionUtils {
    private static void requestPermission(String[] s, Activity activity) {
        Toast.makeText(activity, R.string.promptPermission, Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(activity, s, 100);
    }

    public boolean checkPermission(Activity activity, String[] permission) {
        boolean hasPermission = true;
        int j = 0;
        for (String s : permission) {
            if (activity.checkSelfPermission(s) == PackageManager.PERMISSION_DENIED) {
                if (s != null) {
                    hasPermission = false;
                    requestPermission(new String[]{s}, activity);
                }
            }
        }
        return hasPermission;
    }
}
