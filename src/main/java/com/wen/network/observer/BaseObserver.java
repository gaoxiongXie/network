package com.wen.network.observer;

import com.wen.base.model.MvvmBaseModel;
import com.wen.base.model.MvvmNetworkObserver;
import com.wen.network.errorhandler.ExceptionHandler;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class BaseObserver<T> implements Observer<T> {
    MvvmBaseModel baseModel;
    MvvmNetworkObserver<T> mvvmNetworkObserver;

    public BaseObserver(MvvmBaseModel baseModel, MvvmNetworkObserver<T> mvvmNetworkObserver) {
        this.baseModel = baseModel;
        this.mvvmNetworkObserver = mvvmNetworkObserver;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        if(baseModel!=null){
            baseModel.addDisposable(d);
        }
    }

    @Override
    public void onNext(@NonNull T t) {
        mvvmNetworkObserver.onSuccess(t, false );
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (e instanceof ExceptionHandler.ResponseThrowable) {
            mvvmNetworkObserver.onFailure(e);
        } else {
            mvvmNetworkObserver.onFailure(new ExceptionHandler.ResponseThrowable(e, ExceptionHandler.ERROR.UNKNOWN));
        }
    }

    @Override
    public void onComplete() {

    }
}
