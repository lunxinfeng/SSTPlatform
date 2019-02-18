package com.fintech.sst.other.xposed;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class ChatMsgObjCon extends XC_MethodHook {

    ClassLoader classLoader;
    Context context;
    public ChatMsgObjCon(ClassLoader classLoader, Context context)
    {
        this.classLoader=classLoader;
        this.context= context;
    }
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        String fromId=(String) param.args[0];
        Object SyncChatMsgModel=param.args[1];

        if(SyncChatMsgModel==null)
        {
            return;
        }
       String bizType=(String) XposedHelpers.getObjectField(SyncChatMsgModel,"bizType");
        String bizMemo=(String) XposedHelpers.getObjectField(SyncChatMsgModel,"bizMemo");
        String fromUId=(String) XposedHelpers.getObjectField(SyncChatMsgModel,"fromUId");
        String fromLoginId=(String) XposedHelpers.getObjectField(SyncChatMsgModel,"fromLoginId");
        String clientMsgId=(String) XposedHelpers.getObjectField(SyncChatMsgModel,"clientMsgId");
        String link=(String) XposedHelpers.getObjectField(SyncChatMsgModel,"link");
        if(TextUtils.isEmpty(bizType)||!bizType.equals("GIFTSHARE"))
        {
            return;
        }

        if(TextUtils.isEmpty(link))
        {
            return;
        }

        XposedBridge.log("SyncChatMsgModel bizType:"+bizType);
        XposedBridge.log("SyncChatMsgModel clientMsgId:"+clientMsgId);
        XposedBridge.log("SyncChatMsgModel link:"+link);
        XposedBridge.log("SyncChatMsgModel bizMemo:"+bizMemo);
        XposedBridge.log("SyncChatMsgModel fromUId:"+fromUId);
        XposedBridge.log("SyncChatMsgModel fromId:"+fromId);
        XposedBridge.log("SyncChatMsgModel fromLoginId:"+fromLoginId);
      //  XposedBridge.log("SyncChatMsgModel :"+new Gson().toJson(SyncChatMsgModel));
       Uri uri= Uri.parse(link);
       String crowdNo=uri.getQueryParameter("crowdNo");
        String prevBiz=uri.getQueryParameter("prevBiz");
        String sign=uri.getQueryParameter("sign");
        XposedBridge.log("SyncChatMsgModel crowdNo:"+crowdNo);
        XposedBridge.log("SyncChatMsgModel prevBiz:"+prevBiz);
        XposedBridge.log("SyncChatMsgModel sign:"+sign);
        new ReceiveCrowdTask(
                classLoader,crowdNo,null,
                sign,prevBiz,clientMsgId,
                "1","",
                null,null,context).execute();

    }
}
