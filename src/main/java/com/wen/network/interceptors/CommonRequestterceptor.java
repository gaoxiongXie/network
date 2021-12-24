package com.wen.network.interceptors;

import android.util.Log;

import com.wen.network.INetCommonRequiredInfo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CommonRequestterceptor implements Interceptor {

    private INetCommonRequiredInfo commonRequiredInfo;

    public CommonRequestterceptor(INetCommonRequiredInfo commonRequiredInfo) {
        this.commonRequiredInfo = commonRequiredInfo;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request originRequest = chain.request();
        Request.Builder builder = originRequest.newBuilder();

        if (commonRequiredInfo != null) {
            if (commonRequiredInfo.getCommonHeader() != null) {
                Set<Map.Entry<String, Object>> entry = commonRequiredInfo.getCommonHeader().entrySet();
                Iterator<Map.Entry<String, Object>> it = entry.iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Object> mp = it.next();
                    builder.addHeader(mp.getKey(), mp.getKey());
                }
            }

            if (commonRequiredInfo.getCommonParams() != null && originRequest.body() instanceof FormBody) {
                FormBody.Builder paramBuilder = new FormBody.Builder();
                FormBody formBody = (FormBody) originRequest.body();

                //把原参数，拷贝进去
                for (int i = 0; i < formBody.size(); i++) {
                    paramBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                }

                //公共参数
                Set<Map.Entry<String, String>> entry = commonRequiredInfo.getCommonParams().entrySet();
                Iterator<Map.Entry<String, String>> it = entry.iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> mp = it.next();
                    paramBuilder.addEncoded(mp.getKey(), mp.getValue());
                }
                FormBody finalBuild = paramBuilder.build();

                if (commonRequiredInfo.isDebug()) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < finalBuild.size(); i++) {
                        sb.append(finalBuild.name(i) + "=" + finalBuild.value(i) + ",");
                    }
                    Log.e("参数 --> ", sb.toString());
                }
                builder.post(finalBuild);
            }
        }
        return chain.proceed(builder.build());
    }
}
