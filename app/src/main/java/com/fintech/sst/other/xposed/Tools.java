package com.fintech.sst.other.xposed;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Tools {


    public static String getUserId(ClassLoader classLoader) {

        try {

            Class AlipayApplication=classLoader.loadClass("com.alipay.mobile.framework.AlipayApplication");
            Object AlipayApplicationInstance= XposedHelpers.callStaticMethod(AlipayApplication,"getInstance");
            Object ApplicationContext=XposedHelpers.callMethod(AlipayApplicationInstance,"getMicroApplicationContext");


            Object AuthService=XposedHelpers.callMethod(ApplicationContext,"getExtServiceByInterface","com.alipay.mobile.framework.service.ext.security.AuthService");
            Object UserInfo=XposedHelpers.callMethod(AuthService,"getUserInfo");
            String LogonId=(String) XposedHelpers.callMethod(UserInfo,"getUserId");

            XposedBridge.log("UserId="+LogonId);
            return  LogonId;
        }catch (Exception e)
        {
            com.fintech.sst.other.xposed.hongbao.Tools.printException(e);
        }
        return  null;

    }

    public static Object receiveCrowd(ClassLoader classLoader,Object GiftCrowdReceiveReq)
    {
        try {


            Class GiftCrowdReceiveService = classLoader.loadClass(
                    "com.alipay.giftprod.biz.crowd.gw.GiftCrowdReceiveService");
            Object app=getAlipayApplication(classLoader);
            Object GiftCrowdReceiveServiceObj=getRpcProxy(app,GiftCrowdReceiveService);
            Object re=   XposedHelpers.callMethod(GiftCrowdReceiveServiceObj,"receiveCrowd",GiftCrowdReceiveReq);
            return  re;
        }catch (Exception e)
        {
            com.fintech.sst.other.xposed.hongbao.Tools.printException(e);
        }
        return null;
    }

    public static Object getAlipayApplication(ClassLoader classLoader)
    {
        try {
            Class AlipayApplication=classLoader.loadClass("com.alipay.mobile.framework.LauncherApplicationAgent");

            Object AlipayApplicationInstance= XposedHelpers.callStaticMethod(AlipayApplication,"getInstance");

            Object ApplicationContext=XposedHelpers.callMethod(AlipayApplicationInstance,"getMicroApplicationContext");
            XposedBridge.log("ApplicationContext cn:"+ApplicationContext.getClass().getName());
            return ApplicationContext;

        }catch (Exception e)
        {
            com.fintech.sst.other.xposed.hongbao.Tools.printException(e);
        }
        return null;
    }

    public static Object findServiceByInterface(Object AlipayApplication,String name)
    {
        try {


            return  XposedHelpers.callMethod(AlipayApplication,"findServiceByInterface", name);
        }catch (Exception e)
        {
            com.fintech.sst.other.xposed.hongbao.Tools.printException(e);
        }
        return null;
    }

    public static Object findAppById(Object AlipayApplication,String name)
    {
        try {


            return  XposedHelpers.callMethod(AlipayApplication,"findAppById", name);
        }catch (Exception e)
        {
            com.fintech.sst.other.xposed.hongbao.Tools.printException(e);
        }
        return null;
    }

    public static Object getRpcService(Object AlipayApplication)
    {
        try {

            //  Application application=(Application) XposedHelpers.callMethod(AlipayApplication,"getApplicationContext");
            ClassLoader classLoader=AlipayApplication.getClass().getClassLoader();
            Class RpcService= classLoader.loadClass("com.alipay.mobile.framework.service.common.RpcService");
            return  XposedHelpers.callMethod(AlipayApplication,"findServiceByInterface", RpcService.getName());
        }catch (Exception e)
        {           com.fintech.sst.other.xposed.hongbao.Tools.printException(e);
        }
        return null;
    }

    public static  Object getRpcProxy(Object AlipayApplication, Class face) {
        try {

            Object RpcService= getRpcService(AlipayApplication);
            return   XposedHelpers.callMethod(RpcService,"getRpcProxy",face);
        }catch (Exception e)
        {
            com.fintech.sst.other.xposed.hongbao.Tools.printException(e);
        }
        return null;
    }

}
