package com.fintech.sst.helper;


import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;

import com.fintech.sst.App;

import java.util.Set;

public class PermissionUtil {

    /**
     * 通知栏监听权限
     */
    public static boolean isNotificationListenerEnabled() {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(App.getAppContext());
        return packageNames.contains(App.getAppContext().getPackageName());
    }

    public static void setNotificationListener(Activity activity, int requestCode){
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        }else {
            intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        }
        activity.startActivityForResult(intent,requestCode);
    }
}
