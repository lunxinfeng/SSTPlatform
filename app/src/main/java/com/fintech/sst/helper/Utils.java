package com.fintech.sst.helper;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;
import com.fintech.sst.App;
import com.fintech.sst.R;
import com.fintech.sst.net.Constants;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import pub.devrel.easypermissions.AppSettingsDialog;


public class Utils {

    public static final String deviceInfo = android.os.Build.BRAND.concat("|").concat(android.os.Build.MODEL).concat("|").concat(android.os.Build.VERSION.RELEASE);

    public static String getDeviceInfo() {
        return deviceInfo;
    }

    /**
     * android.os.Build.BRAND
     * android.os.Build.MODEL
     * android.os.Build.VERSION.RELEASE
     */
    public static String getIMEI(Context context) {
        String deviceId;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "请将应用的所有权限开启", Toast.LENGTH_SHORT).show();
            } else {
                deviceId = tm.getDeviceId();
                if (!("000000000000000".equals(deviceId))) {
                    return deviceId;
                }
            }
        }
        SharedPreferences sp = App.getApplication().getSharedPreferences("imei", Context.MODE_PRIVATE);
        deviceId = sp.getString("imei", "");
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString().replace("-", "");
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("imei", deviceId);
            edit.commit();
        }
        return deviceId;
    }

    /**
     * android.os.Build.BRAND
     * android.os.Build.MODEL
     * android.os.Build.VERSION.RELEASE
     */
    public static String getIMEI(Activity activity) {
        String deviceId;
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                goPermissionSetting(activity);
            }
            deviceId = tm.getDeviceId();
            if (!("000000000000000".equals(deviceId))) {
                return deviceId;
            }
        }
        SharedPreferences sp = App.getApplication().getSharedPreferences("imei", Context.MODE_PRIVATE);
        deviceId = sp.getString("imei", "");
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString().replace("-", "");
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("imei", deviceId);
            edit.commit();
        }
        return deviceId;
    }

    public static Map<String, String> jsonObjectToMap(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String next = iterator.next();
            Object opt = jsonObject.opt(next);
            if (opt != null) {
                map.put(next, String.valueOf(opt));
            }
        }
        return map;
    }


    public static boolean isServiceAlive(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(Object obj) {
        return simpleDateFormat.format(obj);
    }


    public static void goPermissionSetting(Activity activity) {
        new AppSettingsDialog.Builder(activity)
                .setTitle(activity.getApplicationContext().getString(R.string.title_settings_perm))
                .setRationale(activity.getApplicationContext().getString(R.string.rationale_message))
                .setPositiveButton(activity.getApplicationContext().getString(R.string.setting))
                .setNegativeButton(activity.getApplicationContext().getString(R.string.cancel))
                .setRequestCode(Constants.RC_PERMISSION)
                .build()
                .show();
    }

    public static void launchAlipayAPP(Context context) {
        try {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            Intent intent = packageManager.
                    getLaunchIntentForPackage("com.eg.android.AlipayGphone");
            context.startActivity(intent);
        } catch (Exception e) {
            String url = "https://ds.alipay.com/?from=mobileweb";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }
    }


    public static void launchWechatAPP(Context context) {
        try {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            Intent intent = packageManager.
                    getLaunchIntentForPackage("com.tencent.mm");
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "未安装微信", Toast.LENGTH_SHORT).show();
        }
    }

    public static void killApp(Context context,String packageName) {
//        try {
//            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            am.killBackgroundProcesses(packageName);
//        } catch (Exception e) {
//            Toast.makeText(context, "未安装微信", Toast.LENGTH_SHORT).show();
//        }
    }
}
