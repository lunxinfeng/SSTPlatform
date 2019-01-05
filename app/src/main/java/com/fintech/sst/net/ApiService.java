package com.fintech.sst.net;


import com.fintech.sst.data.db.Notice;
import com.fintech.sst.net.bean.AisleInfo;
import com.fintech.sst.net.bean.OrderCount;
import com.fintech.sst.net.bean.OrderList;
import com.fintech.sst.net.bean.PageList;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface ApiService {

    @POST("/api/terminal/v1/merchant/logout")
    Observable<ResultEntity<Boolean>> logout(
            @Body SignRequestBody body
    );

    @POST("/api/terminal/v1/heartbeat")
    Observable<ResultEntity<Boolean>> heartbeat(
            @Body SignRequestBody body
    );

    @POST("/api/terminal/v1/trade/notify")
    Observable<ResultEntity<Notice>> notifyLog(
            @Body SignRequestBody body
    );

    @POST("/api/terminal/v1/trade/reNotify")
    Observable<ResultEntity<String>> reNotify(
            @Body SignRequestBody body
    );

//    @POST("/api/terminal/v1/merchant/info")
//    Observable<ResultEntity<UserInfoDetail>> userInfo(
//            @Body SignRequestBody body
//    );
    @POST("/api/terminal/v1/account/info")
    Observable<ResultEntity<AisleInfo>> userInfo(
            @Body SignRequestBody body
    );
    @POST("/api/terminal/v1/account/status")
    Observable<ResultEntity<String>> aisleStatus(
            @Body SignRequestBody body
    );
    @POST("/api/terminal/v1/redisMoney/removeAll")
    Observable<ResultEntity<String>> aisleRefresh(
            @Body SignRequestBody body
    );
    @POST("/api/terminal/v1/delete/account")
    Observable<ResultEntity<String>> aisleDelete(
            @Body SignRequestBody body
    );

    @POST("/api/terminal/v1/merchant/ordersNew")
    Observable<ResultEntity<PageList<OrderList>>> orders(
            @Body SignRequestBody body
    );
    @POST("/api/terminal/v1/getcount")
    Observable<ResultEntity<OrderCount>> orderCount(
            @Body SignRequestBody body
    );

    @POST("/api/terminal/v1/select/tradeNoticeLog")
    Observable<ResultEntity<List<String>>> smsList(
            @Body SignRequestBody body
    );

    //补单
    @POST("/api/terminal/v1/trade/replenish")
    Observable<ResultEntity<String>> sendOrderNotify(
            @Body SignRequestBody body
    );

    @Multipart
    @POST
    Observable<ResponseBody> upload(@Url String url,
                                    @PartMap Map<String, okhttp3.RequestBody> params,
                                    @Part MultipartBody.Part part);

    @POST("/api/auth/info")
    Observable<ResultEntity<Map<String, String>>> getAliLoginUrl();
    @POST("/api/auth/uid")
    Observable<ResultEntity<Map<String, String>>> postAliCode(@Body SignRequestBody uid);
    ///api/terminal/v1/merchant/qr_login
//    @POST("/api/terminal/v1/merchant/qr_login")
//    Observable<ResultEntity<Map<String, String>>> loginQR(
//            @Body SignRequestBody body
//    );
    @POST("/api/terminal/v1/merchant/newlogin")
    Observable<ResultEntity<Map<String, String>>> login(
            @Body SignRequestBody body
    );
    @POST("/api/auth/uidBinding")
    Observable<ResultEntity<Map<String, String>>> bindAli(
            @Body SignRequestBody body
    );

    @GET
    Observable<ResponseBody> getWXAccessToken(@Url String url);
}