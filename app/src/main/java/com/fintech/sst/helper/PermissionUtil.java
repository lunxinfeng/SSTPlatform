package com.fintech.sst.helper;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
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

    public static void toggleNotificationListenerService(Class<? extends NotificationListenerService> cls) {
        PackageManager pm = App.getAppContext().getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(App.getAppContext(), cls),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(App.getAppContext(), cls),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
