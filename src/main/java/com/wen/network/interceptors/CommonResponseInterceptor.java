package com.wen.network.interceptors;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class CommonResponseInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(chain.request());
        Log.e("请求时长：", (System.currentTimeMillis() - startTime) + "ms");
        return response;
    }
}
