package com.zegocloud.demo.bestpractice.internal.sdk.effect.net;



public interface IGetLicenseCallback {
    void onGetLicense(int code,String message, License license);
}
