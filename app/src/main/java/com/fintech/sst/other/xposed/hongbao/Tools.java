package com.fintech.sst.other.xposed.hongbao;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import de.robv.android.xposed.XposedBridge;

public class Tools {

    public static  void  printException(Exception e)
    {
        StringWriter sw=new StringWriter();
        PrintWriter pw=new PrintWriter(sw);
        e.printStackTrace(pw);
        XposedBridge.log(sw.toString());
    }

    public static  void  printThrowable(Throwable e)
    {
        StringWriter sw=new StringWriter();
        PrintWriter pw=new PrintWriter(sw);
        e.printStackTrace(pw);
        XposedBridge.log(sw.toString());
    }

    public static void printBundle(Bundle bundle, String flag)
    {
        Iterator<String> iterator=bundle.keySet().iterator();

        while (iterator.hasNext())
        {
            String key=iterator.next();
            Object v= bundle.get(key);
            if(v==null)
            {
                continue;
            }
            XposedBridge.log(""+flag+"  "+key+":"+v.toString());
        }
    }

    public static void printFAllArg(Object[]args)
    {
        try {

            for (int a=0;a<args.length;a++)
            {
                Object arg=args[a];
                if(arg==null)
                {
                    XposedBridge.log("printFAllArg"+a+"==null");
                    continue;
                }

                XposedBridge.log("printFAllArg"+a+":"+ JSON.toJSONString(arg));
            }
        }catch (Exception e)
        {
            printException(e);
        }
    }

    public static void showMsg(Context context, String msg)
    {
       // new Handler(Looper.getMainLooper()).post(new MsgShow(context,msg));
    }
}
