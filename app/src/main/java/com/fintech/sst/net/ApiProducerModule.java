package com.fintech.sst.net;



import com.fintech.sst.helper.ExpansionKt;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiProducerModule {

    public static volatile OkHttpClient okHttpClient;

//    public static PersistentCookieJar cookieJar;
//
//    public static CookieJar getCookieJar() {
//        return cookieJar;
//    }


    public static OkHttpClient produceOkHttpClient() {
        if (okHttpClient == null) {
            final OkHttpClient.Builder builder = new OkHttpClient.Builder();

            try {
                builder.connectTimeout(12, TimeUnit.SECONDS)
                        .readTimeout(12, TimeUnit.SECONDS)
//                        .sslSocketFactory(HttpsUtil.getSSLSocketFactory(), new HttpsUtil.MyX509TrustManager())
//                        .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
//                        .hostnameVerifier(new HostnameVerifier() {
//                            @Override
//                            public boolean verify(String hostname, SSLSession session) {
//                                return true;
//                            }
//                        })
                ;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (ExpansionKt.getDEBUG()) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(logging);
            }

//            cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(App.getApplication()));
//            builder.cookieJar(cookieJar);

            okHttpClient = builder.build();
        }

        return okHttpClient;
    }

    public static Retrofit produceRestAdapter() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(produceOkHttpClient())
                .baseUrl(Constants.baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
        return builder.build();
    }

    public static <T> T create(final Class<T> service) {
        return produceRestAdapter().create(service);
    }
}
