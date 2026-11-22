package com.sk.markethub.api;

import com.sk.markethub.util.TokenManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static String baseUrl = null;
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    public static synchronized void setBaseUrl(String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        baseUrl = url;
        retrofit = null;
        apiService = null;
    }

    public static String getBaseUrl() {
        if (baseUrl == null) {
            String savedIp = TokenManager.getServerIp();
            if (savedIp.startsWith("http://") || savedIp.startsWith("https://")) {
                baseUrl = savedIp.endsWith("/") ? savedIp : savedIp + "/";
            } else {
                baseUrl = "http://" + savedIp + ":8080/api/";
            }
        }
        return baseUrl;
    }

    public static synchronized ApiService getService() {
        if (apiService == null || retrofit == null) {
            String url = getBaseUrl();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder();
                            
                            String token = TokenManager.getToken();
                            if (token != null && !token.isEmpty()) {
                                requestBuilder.header("Authorization", "Bearer " + token);
                            }
                            
                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
