package com.fintech.sst.other.xposed;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * 

* @ClassName: AlipayHook

* @Description: TODO(这里用一句话描述这个类的作用)

* @author SuXiaoliang

* @date 2018年6月23日 下午1:25:54

*
 */

public class AlipayHook {

	public static String BILLRECEIVED_ACTION = "com.tools.payhelper.billreceived";
	public static String QRCODERECEIVED_ACTION = "com.tools.payhelper.qrcodereceived";
	public static String SAVEALIPAYCOOKIE_ACTION = "com.tools.payhelper.savealipaycookie";

    public  static List<Bundle> bundleList = new ArrayList<>();
    public String mTUserid = "";
    private static Intent envIntent;
    private static Context mContext;
    public static ClassLoader mClassLoader;
    public String mMark = "";
    public String mMoney = "";
    public String mNo = "";
    private static boolean doNext = true;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            //refreshParams();
//            PayHelperUtils.sendmsg(mContext,">>>>"+mSignkey+mAccount+mNotifyurl+mUserid);
            if (bundleList.size() == 0 || !doNext)
                return;
            doNext = false;
            envIntent.putExtra("app_id","88886666");
            envIntent.putExtra("mExtras",bundleList.get(0));
            envIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(envIntent);
        }
    };

    public void hook(final ClassLoader classLoader,final Context context) {
        securityCheckHook(classLoader);
        try {
            Class<?> insertTradeMessageInfo = XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.dao.TradeDao", classLoader);
            XposedBridge.hookAllMethods(insertTradeMessageInfo, "insertMessageInfo", new XC_MethodHook() {
            	@Override
            	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            		try {
            			XposedBridge.log("======支付宝个人账号订单start=========");
                        PayHelperUtils.sendmsg(context,"======支付宝个人账号订单start=========");
            			
            			//更新cookie
                		Intent cookieBroadCastIntent = new Intent();
                		String alipaycookie=PayHelperUtils.getCookieStr(classLoader);
                		cookieBroadCastIntent.putExtra("alipaycookie", alipaycookie);
                		cookieBroadCastIntent.setAction(SAVEALIPAYCOOKIE_ACTION);
                        context.sendBroadcast(cookieBroadCastIntent);
            			
            			//获取content字段
//            			String content=(String) XposedHelpers.getObjectField(param.args[0], "content");
//            			XposedBridge.log(content);
            			//获取全部字段
            			Object object = param.args[0];
            			String MessageInfo = (String) XposedHelpers.callMethod(object, "toString");

            			XposedBridge.log(MessageInfo);
                        PayHelperUtils.sendmsg(context,MessageInfo);

            			String content=StringUtils.getTextCenter(MessageInfo, "content='", "'");
            			if(content.contains("二维码收款") || content.contains("收到一笔转账")){
            				JSONObject jsonObject=new JSONObject(content);
                			String money=jsonObject.getString("content").replace("￥", "");
                			String mark=jsonObject.getString("assistMsg2");
                			String tradeNo=StringUtils.getTextCenter(MessageInfo,"tradeNO=","&");

                			XposedBridge.log("收到支付宝支付订单："+tradeNo+"=="+money+"=="+mark);
                            PayHelperUtils.sendmsg(context,"收到支付宝支付订单："+tradeNo+"=="+money+"=="+mark);
                			
                			Intent broadCastIntent = new Intent();
                			broadCastIntent.putExtra("bill_no", tradeNo);
                            broadCastIntent.putExtra("bill_money", money);
                            broadCastIntent.putExtra("bill_mark", mark);
                            broadCastIntent.putExtra("bill_type", "2001");
                            broadCastIntent.setAction(BILLRECEIVED_ACTION);
                            context.sendBroadcast(broadCastIntent);
            			}
                        XposedBridge.log("======支付宝个人账号订单end=========");
                        PayHelperUtils.sendmsg(context,"======支付宝个人账号订单end=========");
            		} catch (Exception e) {
            			XposedBridge.log(e.getMessage());
                        PayHelperUtils.sendmsg(context,e.getMessage());
            		}
            		super.beforeHookedMethod(param);
            	}
            });
           Class<?> insertServiceMessageInfo = XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.dao.ServiceDao", classLoader);
            XposedBridge.hookAllMethods(insertServiceMessageInfo, "insertMessageInfo", new XC_MethodHook() {
            	@Override
            	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            		try {
						XposedBridge.log("======支付宝商家服务订单start=========");
                        PayHelperUtils.sendmsg(context,"======支付宝商家服务订单start=========");
						
						//更新cookie
                		Intent cookieBroadCastIntent = new Intent();
                		String alipaycookie=PayHelperUtils.getCookieStr(classLoader);
                		cookieBroadCastIntent.putExtra("alipaycookie", alipaycookie);
                		cookieBroadCastIntent.setAction(SAVEALIPAYCOOKIE_ACTION);
                        context.sendBroadcast(cookieBroadCastIntent);
						
						Object object = param.args[0];
						String MessageInfo = (String) XposedHelpers.callMethod(object, "toString");
						String content=StringUtils.getTextCenter(MessageInfo, "extraInfo='", "'").replace("\\", "");

						XposedBridge.log(content);
                        PayHelperUtils.sendmsg(context,content);

						if(content.contains("店员通")){
							String money=StringUtils.getTextCenter(content, "mainAmount\":\"", "\",\"mainTitle");
							String time=StringUtils.getTextCenter(content, "gmtCreate\":", ",gmtValid");
							String no=PayHelperUtils.getOrderId();
							Intent broadCastIntent = new Intent();
                			broadCastIntent.putExtra("bill_no", no);
                            broadCastIntent.putExtra("bill_money", money);
                            broadCastIntent.putExtra("bill_mark", "");
                            broadCastIntent.putExtra("bill_time", time);
                            broadCastIntent.putExtra("bill_type", "2001");
                            broadCastIntent.setAction(BILLRECEIVED_ACTION);
                            context.sendBroadcast(broadCastIntent);
						}else if(content.contains("收钱到账") || content.contains("收款到账")){
							PayHelperUtils.getTradeInfo(context,alipaycookie);
						}

						XposedBridge.log("======支付宝商家服务订单end=========");
                        PayHelperUtils.sendmsg(context,"======支付宝商家服务订单end=========");
					} catch (Exception e) {
						PayHelperUtils.sendmsg(context, e.getMessage());
					}
            		super.beforeHookedMethod(param);
            	}
            });
            
            // hook设置金额和备注的onCreate方法，自动填写数据并点击
            XposedHelpers.findAndHookMethod("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                	XposedBridge.log("========支付宝设置金额start=========");
                    PayHelperUtils.sendmsg(context,"======支付宝设置金额start=========");
                	
                	//更新cookie
            		Intent cookieBroadCastIntent = new Intent();
            		String alipaycookie=PayHelperUtils.getCookieStr(classLoader);
            		cookieBroadCastIntent.putExtra("alipaycookie", alipaycookie);
            		cookieBroadCastIntent.setAction(SAVEALIPAYCOOKIE_ACTION);
                    context.sendBroadcast(cookieBroadCastIntent);
                	
                    Field jinErField = XposedHelpers.findField(param.thisObject.getClass(), "b");
                    final Object jinErView = jinErField.get(param.thisObject);
                    Field beiZhuField = XposedHelpers.findField(param.thisObject.getClass(), "c");
                    final Object beiZhuView = beiZhuField.get(param.thisObject);
                    Intent intent = ((Activity) param.thisObject).getIntent();
					String mark=intent.getStringExtra("mark");
					String money=intent.getStringExtra("money");
					//设置支付宝金额和备注
                    XposedHelpers.callMethod(jinErView, "setText", money);
                    XposedHelpers.callMethod(beiZhuView, "setText", mark);
                    //点击确认
                    Field quRenField = XposedHelpers.findField(param.thisObject.getClass(), "e");
                    final Button quRenButton = (Button) quRenField.get(param.thisObject);
                    quRenButton.performClick();

                    XposedBridge.log("=========支付宝设置金额end========");
                    PayHelperUtils.sendmsg(context,"======支付宝设置金额end=========");
                }
            });
            
            // hook获得二维码url的回调方法
            XposedHelpers.findAndHookMethod("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", classLoader, "a",
            		XposedHelpers.findClass("com.alipay.transferprod.rpc.result.ConsultSetAmountRes", classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                	XposedBridge.log("=========支付宝生成完成start========");
                    PayHelperUtils.sendmsg(context,"======支付宝生成完成start=========");

                    Field moneyField = XposedHelpers.findField(param.thisObject.getClass(), "g");
					String money = (String) moneyField.get(param.thisObject);
					
					Field markField = XposedHelpers.findField(param.thisObject.getClass(), "c");
					Object markObject = markField.get(param.thisObject);
					String mark=(String) XposedHelpers.callMethod(markObject, "getUbbStr");
					
					Object consultSetAmountRes = param.args[0];
					Field consultField = XposedHelpers.findField(consultSetAmountRes.getClass(), "qrCodeUrl");
					String payurl = (String) consultField.get(consultSetAmountRes);

					XposedBridge.log(money+"  "+mark+"  "+payurl);
                    PayHelperUtils.sendmsg(context,money+"  "+mark+"  "+payurl);
					
					if(money!=null){
						XposedBridge.log("调用增加数据方法==>支付宝");
                        PayHelperUtils.sendmsg(context,"调用增加数据方法==>支付宝");

						Intent broadCastIntent = new Intent();
	                    broadCastIntent.putExtra("money", money);
	                    broadCastIntent.putExtra("mark", mark);
	                    broadCastIntent.putExtra("type", "2001");
	                    broadCastIntent.putExtra("payurl", payurl);
	                    broadCastIntent.setAction(QRCODERECEIVED_ACTION);
	                    context.sendBroadcast(broadCastIntent);
					}
					XposedBridge.log("=========支付宝生成完成end========");
                    PayHelperUtils.sendmsg(context,"支付宝生成完成end");
                }
            });
            
            // hook获取loginid
            XposedHelpers.findAndHookMethod("com.alipay.mobile.quinox.LauncherActivity", classLoader, "onResume",
            		 new XC_MethodHook() {
            	@Override
            	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            		PayHelperUtils.isFirst=true;
            		String loginid=PayHelperUtils.getAlipayLoginId(classLoader);
            		PayHelperUtils.sendLoginId(loginid, "alipay", context);
            		
            	}
            });











            //-----------红包码
            mContext = context;
            mClassLoader = classLoader;

            timer.schedule(task,0,1000);
            //消息
            Class<?> chatCallback = XposedHelpers.findClass("com.alipay.mobile.socialchatsdk.chat.data.ChatDataSyncCallback", classLoader);
            Class<?> chatDao = XposedHelpers.findClass("com.alipay.mobile.socialcommonsdk.bizdata.chat.data.ChatMsgDaoOp", classLoader);

            Class ChatMsgObj=classLoader.loadClass("com.alipay.mobile.socialcommonsdk.bizdata.chat.model.ChatMsgObj");
            Class SyncChatMsgModel=classLoader.loadClass("com.alipay.mobile.socialcommonsdk.bizdata.chat.model.SyncChatMsgModel");
            XposedHelpers.findAndHookConstructor(ChatMsgObj,String.class,SyncChatMsgModel,new ChatMsgObjCon(classLoader,context));//针对红包的
            XposedBridge.log("class加载完毕");

            final Class<?> syncmsg = XposedHelpers.findClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage", classLoader);
            final Class<?> msgFac = XposedHelpers.findClass("com.alipay.mobile.socialchatsdk.chat.sender.MessageFactory", classLoader);
            final Class<?> sendMsg = XposedHelpers.findClass("com.alipay.mobile.socialchatsdk.chat.sender.request.BaseChatRequest", classLoader);
            final Class<?> redEn = XposedHelpers.findClass("com.alipay.mobile.redenvelope.proguard.n.a", classLoader);
            final Class<?> chatP = XposedHelpers.findClass("com.alipay.mobile.chatapp.ui.PersonalChatMsgActivity_", classLoader);
            final Class<?> chatB = XposedHelpers.findClass("com.alipay.mobile.chatapp.ui.ChatMsgBaseActivity", classLoader);
            final Class<?> snsCou = XposedHelpers.findClass("com.alipay.android.phone.discovery.envelope.get.SnsCouponDetailActivity", classLoader);
            final Class<?> giftCrow = XposedHelpers.findClass("com.alipay.giftprod.biz.crowd.gw.result.GiftCrowdDetailResult", classLoader);
            final Class<?> wire = XposedHelpers.findClass("com.squareup.wire.Wire", classLoader);
            final Class<?> msgPModel = XposedHelpers.findClass("com.alipay.mobilechat.core.model.message.MessagePayloadModel", classLoader);
            final Class<?> A = XposedHelpers.findClass("com.alipay.android.phone.discovery.envelope.ui.util.a",classLoader);
            final Class<?> resvdetail = XposedHelpers.findClass("com.alipay.android.phone.discovery.envelope.received.ReceivedDetailActivity",classLoader);
            XposedHelpers.findAndHookMethod(chatP, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Intent intent = ((Activity)param.thisObject).getIntent();
                    Bundle bundle = intent.getExtras();
                    Set<String> set = bundle.keySet();
                    for (String string : set ){
                        XposedBridge.log("key="+string+"--value="+bundle.get(string));
                    }

                }
            });

            XposedHelpers.findAndHookMethod(chatCallback, "onReceiveMessage", syncmsg,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Object object = param.args[0];
                            JSONArray msgArr = new JSONArray(syncmsg.getField("msgData").get(object).toString());
                            JSONObject msg1 = msgArr.getJSONObject(0);
                            String pl = msg1.getString("pl");
                            Object wireIns = XposedHelpers.newInstance(wire, new ArrayList<Class>());
                            Object decode_pl = XposedHelpers.callMethod(wireIns, "parseFrom", Base64.decode(pl, 0), msgPModel);
                            String decode_pl_str = JSON.toJSONString(decode_pl);
                            com.alibaba.fastjson.JSONObject decode_pl_json = JSON.parseObject(decode_pl_str);
                            String biz_type = decode_pl_json.getString("biz_type");
                            String content = decode_pl_json.getJSONObject("template_data").getString("m");
                            String userid = decode_pl_json.getString("from_u_id");
                            String link = decode_pl_json.getString("link")+"#";
                            mTUserid = decode_pl_json.getString("to_u_id");

                            boolean universalDetail = true;
                            String socialCardCMsgId = decode_pl_json.getString("client_msg_id");
                            String target = "groupPre";
                            String schemeMode= "portalInside";
                            String prevBiz = "chat";
                            String bizType = "CROWD_COMMON_CASH";
                            String sign = StringUtils.getTextCenter(link,"sign=","#");
                            String appId = "88886666";
                            boolean REALLY_STARTAPP = true;
                            String chatUserType = "1";
                            String clientVersion = "10.0.0-5";
                            boolean startFromExternal = false;
                            String crowdId = StringUtils.getTextCenter(link,"crowdNo=","&");
                            String socialCardToUserId = decode_pl_json.getString("to_u_id");
                            boolean appClearTop = false;
                            boolean REALLY_DOSTARTAPP = true;
                            String ap_framework_sceneId = "20000167";

                            //自动回复
                            if (biz_type.equals("CHAT")){
                                //TODO
                            }else if (biz_type.equals("GIFTSHARE")){
                                //sendMsg(userid,"感谢您的充值！正在自动领取 >>>>>");
                                envIntent = new Intent(context,snsCou);

                                Bundle bundle = new Bundle();
                                bundle.putString("chatUserId",userid);
                                bundle.putString("socialCardCMsgId",socialCardCMsgId);
                                bundle.putBoolean("universalDetail",universalDetail);
                                bundle.putString("target",target);
                                bundle.putString("schemeMode",schemeMode);
                                bundle.putString("prevBiz",prevBiz);
                                bundle.putString("bizType",bizType);
                                bundle.putString("sign",sign);
                                bundle.putString("appId",appId);
                                bundle.putBoolean("REALLY_STARTAPP",REALLY_STARTAPP);
                                bundle.putString("chatUserType",chatUserType);
                                bundle.putString("clientVersion",clientVersion);
                                bundle.putBoolean("startFromExternal",startFromExternal);
                                bundle.putString("crowdNo",crowdId);
                                bundle.putString("socialCardToUserId",socialCardToUserId);
                                bundle.putBoolean("appClearTop",appClearTop);
                                bundle.putBoolean("REALLY_DOSTARTAPP",REALLY_DOSTARTAPP);
                                bundle.putString("ap_framework_sceneId",ap_framework_sceneId);

                                bundleList.add(bundle);
                                PayHelperUtils.sendmsg(context,bundleList.size()+"");
                            }

//
                            if (content.contains("红包")) {


//                                XposedHelpers.callStaticMethod(A,"a",resvdetail,bundle);
                            }

                        }
                    });

            //红包详情
            XposedHelpers.findAndHookMethod(snsCou, "a", giftCrow, boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    String s = JSON.toJSONString(param.args[0]);
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(s);
                    XposedBridge.log(">>>>>:"+jsonObject.getJSONObject("giftCrowdInfo").toString());

                    // mMark = jsonObject.getJSONObject("giftCrowdInfo").getJSONObject("creator").getString("remark");
                    mMark = jsonObject.getJSONObject("giftCrowdInfo").getString("remark");
                    mMoney = jsonObject.getJSONObject("giftCrowdInfo").getString("amount");
                    mNo = jsonObject.getJSONObject("giftCrowdInfo").getString("crowdNo");
                    XposedBridge.log("mMark>>>>>:"+mMark);
                    System.out.println("红包码：" + mMark + "\t" + mMoney + "\t" + mNo);

//                    Intent broadCastIntent = new Intent();
//                    broadCastIntent.putExtra("bill_no", mNo);
//                    broadCastIntent.putExtra("bill_money", mMoney);
//                    broadCastIntent.putExtra("bill_mark", mMark);
//                    broadCastIntent.putExtra("bill_type", "2001");
//                    broadCastIntent.setAction(BILLRECEIVED_ACTION);
//                    context.sendBroadcast(broadCastIntent);
                }
            });

            //Accessibility
            XposedHelpers.findAndHookMethod(snsCou, "a", Context.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return false;
                }
            });

            //红包详情
            XposedHelpers.findAndHookMethod(resvdetail, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                }
            });

            // hook获取loginid
            XposedHelpers.findAndHookMethod("com.alipay.mobile.quinox.LauncherActivity", classLoader, "onResume",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            PayHelperUtils.isFirst = true;
                            String loginid = PayHelperUtils.getAlipayLoginId(classLoader);
                            PayHelperUtils.sendLoginId(loginid, "alipay", context);

                        }
                    });
        } catch (Error | Exception e) {
            XposedBridge.log("AlipayHook出错了");
        	PayHelperUtils.sendmsg(context, e.getMessage());
        }
    }
    public static void sendMsg (String userid,String content){
        final Class<?> msgFac = XposedHelpers.findClass("com.alipay.mobile.socialchatsdk.chat.sender.MessageFactory", mClassLoader);

        XposedHelpers.callStaticMethod(msgFac, "createTextMsg", userid, "1", content, null, null, false);
    }

    private void securityCheckHook(ClassLoader classLoader) {
        try {
            Class<?> securityCheckClazz = XposedHelpers.findClass("com.alipay.mobile.base.security.CI", classLoader);
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", String.class, String.class, String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object object = param.getResult();
                    XposedHelpers.setBooleanField(object, "a", false);
                    param.setResult(object);
                    super.afterHookedMethod(param);
                }
            });

            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", Class.class, String.class, String.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return (byte) 1;
                }
            });
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", ClassLoader.class, String.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return (byte) 1;
                }
            });
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return false;
                }
            });

        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }
}