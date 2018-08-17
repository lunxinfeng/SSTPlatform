package com.fintech.sst.net;


import com.fintech.sst.net.bean.OrderList;
import com.fintech.sst.net.bean.PageList;
import com.fintech.sst.net.bean.UserInfoDetail;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
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

    // alipays or wechats
    @POST("/api/terminal/v1/heartbeat")
    Flowable<ResultEntity<Boolean>> heartbeat(
            @Body SignRequestBody body
    );

    @POST("/api/terminal/v1/merchant/info")
    Observable<ResultEntity<UserInfoDetail>> userInfo(
            @Body SignRequestBody body
    );

    @POST("/api/terminal/v1/merchant/orders")
    Observable<ResultEntity<PageList<OrderList>>> orders(
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
}