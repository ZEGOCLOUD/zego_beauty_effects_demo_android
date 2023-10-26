package com.zegocloud.demo.bestpractice.internal.sdk.effect.net;

import org.jetbrains.annotations.NotNull;

public interface IAsyncGetCallback<T>{
    void onResponse(int errorCode, @NotNull String message, T responseJsonBean);
}
