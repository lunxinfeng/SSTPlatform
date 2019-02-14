package com.fintech.sst.other.netty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParamSignatureUtil {

    public static String createLinkString(final Map<String, Object> map) {
        final Map<String, Object> paraFilter = paraFilter(map);
        final ArrayList<String> list = new ArrayList<String>(paraFilter.keySet());
        Collections.sort(list);
        String s = "";
        for (int i = 0; i < list.size(); ++i) {
            final String s2 = list.get(i);
            final String value = String.valueOf(paraFilter.get(s2));
            if (i == list.size() - 1) {
                s = String.valueOf(s) + s2 + "=" + value;
            } else {
                s = String.valueOf(s) + s2 + "=" + value + "&";
            }
        }
        return s;
    }

    public static String createLinkString(final Map<String, Object> map, final String s) {
        final Map<String, Object> paraFilter = paraFilter(map, s);
        final ArrayList<String> list = new ArrayList<String>(paraFilter.keySet());
        Collections.sort(list);
        String s2 = "";
        for (int i = 0; i < list.size(); ++i) {
            final String s3 = list.get(i);
            final String value = String.valueOf(paraFilter.get(s3));
            if (i == list.size() - 1) {
                s2 = String.valueOf(s2) + s3 + "=" + value;
            } else {
                s2 = String.valueOf(s2) + s3 + "=" + value + "&";
            }
        }
        return s2;
    }

    public static String getSign(final Map<String, Object> map, String linkString) {
        map.put("privatekeys", linkString);
        linkString = createLinkString(map);
        map.remove("privatekeys");
        return getStringDigest(linkString, "MD5");
    }

    public static String getStringDigest(final String s, final String s2) {
        if (s2.equals("MD5")) {
            return MD5.md5(s);
        }
        if (s2.equals("SHA")) {
            return "";
        }
        return "";
    }

    public static Map<String, Object> paraFilter(final Map<String, Object> map) {
        final HashMap<String, Object> hashMap = new HashMap<String, Object>();
        if (map != null && map.size() > 0) {
            for (final String s : map.keySet()) {
                final String value = String.valueOf(map.get(s));
                if (value != null && !value.equals("") && !s.equalsIgnoreCase("sign")) {
                    hashMap.put(s, value);
                }
            }
        }
        return hashMap;
    }

    private static Map<String, Object> paraFilter(final Map<String, Object> map, final String s) {
        map.put("privateKey", s);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        if (map != null && map.size() > 0) {
            for (final String s2 : map.keySet()) {
                final String value = String.valueOf(map.get(s2));
                if (value != null && !value.equals("") && !s2.equalsIgnoreCase("sign")
                        && !s2.equalsIgnoreCase("sign_type") && !s2.equals("file")) {
                    hashMap.put(s2, value);
                }
            }
        }
        return hashMap;
    }

    public static boolean validateSign(final Map<String, Object> map) {
        return getStringDigest(createLinkString(map), "MD5").equals(String.valueOf(map.get("sign")));
    }
}