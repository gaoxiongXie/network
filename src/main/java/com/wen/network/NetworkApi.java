package com.wen.network;

import com.wen.network.errorhandler.HttpErrorHandler;
import com.wen.network.interceptors.CommonRequestterceptor;
import com.wen.network.interceptors.CommonResponseInterceptor;
import com.wen.network.rxadapter.RxJava3CallAdapterFactory;

import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class NetworkApi {
    private static INetCommonRequiredInfo iNetCommonRequiredInfo;

    private String mBaseUrl;
    private OkHttpClient mHttpClient;

    public NetworkApi(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    /**
     * 如果要设置 公共header和post参数，那就需要调用init方法
     *
     * @param requiredInfo
     */
    public static void init(INetCommonRequiredInfo requiredInfo) {
        iNetCommonRequiredInfo = requiredInfo;
    }

    private static HashMap<String, Retrofit> cacheRetrofitMap = new HashMap<>();

    public Retrofit getRetrofit(Class clazz) {
        String key = mBaseUrl + clazz.getName();
        if (cacheRetrofitMap.get(key) != null) {
            return cacheRetrofitMap.get(key);
        }

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(getOkHttpClient());
        builder.baseUrl(mBaseUrl);
        builder.addCallAdapterFactory(RxJava3CallAdapterFactory.create());
        builder.addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        cacheRetrofitMap.put(key, retrofit);
        return retrofit;
    }

    public <T> ObservableTransformer<T, T> applySchedulers(final Observer<T> observer) {
        return new ObservableTransformer<T, T>() {
            @Override
            public @NonNull ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                @NonNull Observable<T> observale = (Observable<T>) upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(getAppErrorHandler()).onErrorResumeNext(new HttpErrorHandler<T>());
                observale.subscribe(observer);
                return observale;
            }
        };
    }

    private OkHttpClient getOkHttpClient() {
        if (mHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (getInterceptor() != null) {
                builder.addInterceptor(getInterceptor());
            }

            if (iNetCommonRequiredInfo != null) {
                builder.addInterceptor(new CommonRequestterceptor(iNetCommonRequiredInfo));
                if (iNetCommonRequiredInfo.isDebug()) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                    builder.addInterceptor(loggingInterceptor);
                }
            }
            builder.addInterceptor(new CommonResponseInterceptor());
            mHttpClient = builder.build();
        }
        return mHttpClient;
    }

    protected abstract Interceptor getInterceptor();

    protected abstract <T> Function<T, T> getAppErrorHandler();
}
