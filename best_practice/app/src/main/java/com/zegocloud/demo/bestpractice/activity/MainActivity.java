package com.zegocloud.demo.bestpractice.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.faceunity.nama.FURenderer;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.zegocloud.demo.bestpractice.R;
import com.zegocloud.demo.bestpractice.activity.call.CallEntryActivity;
import com.zegocloud.demo.bestpractice.activity.liveaudioroom.LiveAudioRoomEntryActivity;
import com.zegocloud.demo.bestpractice.activity.livestreaming.LiveStreamEntryActivity;
import com.zegocloud.demo.bestpractice.databinding.ActivityMainBinding;
import com.zegocloud.demo.bestpractice.faceunity.FaceUnityVideoProcess;
import com.zegocloud.demo.bestpractice.internal.ZEGOCallInvitationManager;
import com.zegocloud.demo.bestpractice.internal.ZEGOLiveStreamingManager;
import com.zegocloud.demo.bestpractice.internal.sdk.ZEGOSDKManager;
import com.zegocloud.demo.bestpractice.internal.sdk.basic.ZEGOSDKUser;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoPublishChannel;
import im.zego.zegoexpress.constants.ZegoVideoBufferType;
import im.zego.zegoexpress.entity.ZegoCustomVideoProcessConfig;
import im.zego.zim.callback.ZIMCallInvitationSentCallback;
import im.zego.zim.entity.ZIMCallInvitationSentInfo;
import im.zego.zim.entity.ZIMError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ZEGOSDKUser localUser = ZEGOSDKManager.getInstance().expressService.getCurrentUser();
        binding.liveUserinfoUserid.setText(localUser.userID);
        binding.liveUserinfoUsername.setText(localUser.userName);

        binding.buttonCall.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CallEntryActivity.class));
        });

        binding.buttonLivestreaming.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LiveStreamEntryActivity.class));
        });

        binding.buttonLiveaudioroom.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LiveAudioRoomEntryActivity.class));
        });
        // if use LiveStreaming,init after user login,can receive pk request.
        ZEGOLiveStreamingManager.getInstance().init();
        // if use Call invitation,init after user login,can receive call request.
        ZEGOCallInvitationManager.getInstance().init(this);

        initFaceUnity();
    }

    public void initFaceUnity(){
        FURenderer.getInstance().setup(this);
        ZegoCustomVideoProcessConfig config = new ZegoCustomVideoProcessConfig();
        config.bufferType = ZegoVideoBufferType.GL_TEXTURE_2D;
        ZEGOSDKManager.getInstance().expressService.enableCustomVideoProcessing(true, config, ZegoPublishChannel.MAIN);

        FaceUnityVideoProcess faceUnityVideoProcess = new FaceUnityVideoProcess();
        ZEGOSDKManager.getInstance().expressService.setCustomVideoProcessHandler(faceUnityVideoProcess);
    }

    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            ZEGOSDKManager.getInstance().disconnectUser();
            ZEGOLiveStreamingManager.getInstance().removeUserData();
            ZEGOLiveStreamingManager.getInstance().removeUserListeners();
            ZEGOCallInvitationManager.getInstance().removeUserData();
            ZEGOCallInvitationManager.getInstance().removeUserListeners();
        }
    }
}