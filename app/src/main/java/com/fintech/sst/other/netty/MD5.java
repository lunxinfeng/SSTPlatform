package com.fintech.sst.other.netty;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public MD5() {
    }

    private static String encrypt(String var0, String var1) {
        if (var0 != null && !"".equals(var0.trim())) {
            String var2;
            label21: {
                if (var1 != null) {
                    var2 = var1;
                    if (!"".equals(var1.trim())) {
                        break label21;
                    }
                }

                var2 = "md5";
            }

            try {
                MessageDigest var5 = MessageDigest.getInstance(var2);
                var5.update(var0.getBytes("UTF8"));
                var0 = hex(var5.digest());
                return var0;
            } catch (NoSuchAlgorithmException var3) {
                var3.printStackTrace();
                return null;
            } catch (UnsupportedEncodingException var4) {
                var4.printStackTrace();
                return null;
            }
        } else {
            throw new IllegalArgumentException("请输入要加密的内容");
        }
    }

    private static String hex(byte[] var0) {
        StringBuffer var2 = new StringBuffer();

        for(int var1 = 0; var1 < var0.length; ++var1) {
            var2.append(Integer.toHexString(var0[var1] & 255 | 256), 1, 3);
        }

        return var2.toString();
    }

    public static void main(String[] var0) {
        System.out.println(Integer.parseInt("005"));
    }

    public static String md5(File var0) {
        String var2 = null;
        Object var3 = null;

        String var10;
        Exception var13;
        label48: {
            DigestInputStream var12;
            label47: {
                Exception var10000;
                label53: {
                    MessageDigest var4;
                    byte[] var9;
                    boolean var10001;
                    try {
                        if (!var0.exists()) {
                            return var2;
                        }

                        FileInputStream var11 = new FileInputStream(var0);
                        var9 = new byte[8192];
                        var12 = new DigestInputStream(var11, MessageDigest.getInstance("md5"));
                        var4 = var12.getMessageDigest();
                    } catch (Exception var8) {
                        var10000 = var8;
                        var10001 = false;
                        break label53;
                    }

                    int var1 = -2;

                    while(var1 != -1) {
                        try {
                            var1 = var12.read(var9);
                        } catch (Exception var7) {
                            var10000 = var7;
                            var10001 = false;
                            break label53;
                        }
                    }

                    try {
                        var10 = new String(hex(var4.digest()));
                        break label47;
                    } catch (Exception var6) {
                        var10000 = var6;
                        var10001 = false;
                    }
                }

                var13 = var10000;
                var10 = (String)var3;
                break label48;
            }

            try {
                var12.close();
                return var10;
            } catch (Exception var5) {
                var13 = var5;
            }
        }

        var13.printStackTrace();
        var2 = var10;
        return var2;
    }

    public static String md5(String var0) {
        return encrypt(var0, "md5");
    }

    public static String sha(String var0) {
        return encrypt(var0, "sha-1");
    }
}
