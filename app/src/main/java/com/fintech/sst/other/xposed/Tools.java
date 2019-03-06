package com.fintech.sst.other.xposed;

import com.alibaba.fastjson.JSON;

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

    public static Object getContactAccount(ClassLoader classLoader,String userid) {

        try {

            Class AlipayApplication=classLoader.loadClass("com.alipay.mobile.framework.AlipayApplication");
            Object AlipayApplicationInstance= XposedHelpers.callStaticMethod(AlipayApplication,"getInstance");
            Object ApplicationContext=XposedHelpers.callMethod(AlipayApplicationInstance,"getMicroApplicationContext");


            Object socialSdkContactService=XposedHelpers.callMethod(ApplicationContext,"getExtServiceByInterface","com.alipay.mobile.personalbase.service.SocialSdkContactService");
            Object ContactAccount =XposedHelpers.callMethod(socialSdkContactService,"queryAccountById",userid);

            XposedBridge.log("ContactAccount ="+JSON.toJSONString(ContactAccount));
            return  ContactAccount;
        }catch (Exception e)
        {
            com.fintech.sst.other.xposed.hongbao.Tools.printException(e);
        }
        return  null;

    }


    public static void  delectContact(ClassLoader mClassLoader){
    /*    HandleRelationReq handleRelationReq = new HandleRelationReq();
        handleRelationReq.targetUserId = profileSettingActivity.k.userId;
        handleRelationReq.bizType = "2";
        handleRelationReq.alipayAccount = profileSettingActivity.k.account;
        BaseResult handleRelation = profileSettingActivity.u.handleRelation(handleRelationReq);*/
        String userid = getUserId(mClassLoader);
        Object contactAccount= Tools.getContactAccount(mClassLoader,userid);

        Object handleRelationReq =XposedHelpers.newInstance(XposedHelpers.findClass("com.alipay.mobilerelation.biz.shared.req.HandleRelationReq",mClassLoader));
        String userId =XposedHelpers.getObjectField(contactAccount,"userId")+"";
        String account =XposedHelpers.getObjectField(contactAccount,"account")+"";
        XposedHelpers.setObjectField(handleRelationReq,"targetUserId",userId);
        XposedHelpers.setObjectField(handleRelationReq,"alipayAccount",account);
        XposedHelpers.setObjectField(handleRelationReq,"bizType","2");
        Object service =  Tools.getRpcService( Tools.getAlipayApplication(mClassLoader));
        XposedBridge.log("service "+service);
        Object alipayRelationManageService= XposedHelpers.callMethod(service,"getRpcProxy",XposedHelpers.findClass("com.alipay.mobilerelation.biz.shared.rpc.AlipayRelationManageService",mClassLoader));
        Object re=XposedHelpers.callMethod(alipayRelationManageService,"handleRelation",handleRelationReq);
        XposedBridge.log("删除好友结果"+JSON.toJSONString(re));
    }
//    public static Object mapp = null;
//    public static String sendBillMsg( ClassLoader mClassLoader,String userid,String money, String remark ) {
//        Class appclazz = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", mClassLoader);
//        Object mapp2 =   XposedHelpers.callStaticMethod(appclazz, "getInstance");
//        Object bundleContext =XposedHelpers.callMethod(mapp2,"getBundleContext");
//        ClassLoader classload = (ClassLoader) XposedHelpers.callMethod(bundleContext,"findClassLoaderByBundleName","android-phone-wallet-socialpayee");
//        Object service =  Tools.getRpcService( Tools.getAlipayApplication(mClassLoader));
//        XposedBridge.log("service "+service);
//        Object SingleCollectRpc= XposedHelpers.callMethod(service,"getRpcProxy",XposedHelpers.findClass("com.alipay.android.phone.personalapp.socialpayee.rpc.SingleCollectRpc",classload));
////        XposedBridge.log("SingleCollectRpc "+SingleCollectRpc);
//        Object contactAccount= Tools.getContactAccount(classload,userid);
//        String name =XposedHelpers.getObjectField(contactAccount,"name")+"";
//        String userId =XposedHelpers.getObjectField(contactAccount,"userId")+"";
//        String logonId =XposedHelpers.getObjectField(contactAccount,"account")+"";
//        Object singleCreateReq =  XposedHelpers.newInstance(XposedHelpers.findClass("com.alipay.android.phone.personalapp.socialpayee.rpc.req.SingleCreateReq",classload));
//        XposedBridge.log(" singleCreateReq "+JSON.toJSONString(singleCreateReq));
//        Field[] fields = singleCreateReq.getClass().getDeclaredFields();
//        for (Field field:fields){
//            XposedBridge.log(" "+field.getName()+" "+field.getType());
//
//        }
//        XposedHelpers.setObjectField(singleCreateReq,"userName",name);
//        XposedHelpers.setObjectField(singleCreateReq,"userId",userId);
//        XposedHelpers.setObjectField(singleCreateReq,"logonId",logonId);
//        XposedHelpers.setObjectField(singleCreateReq,"desc",remark);
//        XposedHelpers.setObjectField(singleCreateReq,"payAmount", money );
//        XposedHelpers.setObjectField(singleCreateReq,"billName","个人收款" );
//        XposedHelpers.setObjectField(singleCreateReq,"source","chat" );
//        Object o =XposedHelpers.callMethod(SingleCollectRpc,"createBill",singleCreateReq);
//        XposedBridge.log(" 结果 "+JSON.toJSONString(o));
//
//        return  JSON.toJSONString(o);
//    }
}
