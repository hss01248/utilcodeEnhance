package com.hss.utils.enhance;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public interface BaseCallback<T> extends Observer<T>{
    @Override
   default void onSubscribe(@NonNull Disposable d){

    }

    @Override
   default void onComplete(){

    }
}
