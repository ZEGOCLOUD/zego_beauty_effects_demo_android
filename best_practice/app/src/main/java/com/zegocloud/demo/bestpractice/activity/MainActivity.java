package com.zegocloud.demo.bestpractice.activity;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.faceunity.nama.FURenderer;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.zegocloud.demo.bestpractice.R;
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

//        binding.liveIdStreaming.getEditText().setText(Build.MANUFACTURER.toLowerCase());
        binding.startLiveStreaming.setOnClickListener(v -> {
            String liveID = binding.liveIdStreaming.getEditText().getText().toString();
            if (TextUtils.isEmpty(liveID)) {
                binding.liveIdStreaming.setError("please input liveID");
                return;
            }
            List<String> permissions = Arrays.asList(permission.CAMERA, permission.RECORD_AUDIO);
            requestPermissionIfNeeded(permissions, new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                    @NonNull List<String> deniedList) {
                    if (allGranted) {
                        Intent intent = new Intent(MainActivity.this, LiveStreamingActivity.class);
                        intent.putExtra("host", true);
                        intent.putExtra("liveID", liveID);
                        startActivity(intent);
                    }
                }
            });
        });

        binding.watchLiveStreaming.setOnClickListener(v -> {
            String liveID = binding.liveIdStreaming.getEditText().getText().toString();
            if (TextUtils.isEmpty(liveID)) {
                binding.liveIdStreaming.setError("please input liveID");
                return;
            }
            Intent intent = new Intent(MainActivity.this, LiveStreamingActivity.class);
            intent.putExtra("host", false);
            intent.putExtra("liveID", liveID);
            startActivity(intent);
        });

        binding.startLiveAudioroom.setOnClickListener(v -> {
            String liveID = binding.liveIdAudioRoom.getEditText().getText().toString();
            if (TextUtils.isEmpty(liveID)) {
                binding.liveIdAudioRoom.setError("please input liveID");
                return;
            }
            List<String> permissions = Arrays.asList(permission.RECORD_AUDIO);
            requestPermissionIfNeeded(permissions, new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                    @NonNull List<String> deniedList) {
                    if (allGranted) {
                        Intent intent = new Intent(MainActivity.this, LiveAudioRoomActivity.class);
                        intent.putExtra("host", true);
                        intent.putExtra("liveID", liveID);
                        startActivity(intent);
                    }
                }
            });
        });

        binding.watchLiveAudioroom.setOnClickListener(v -> {
            String liveID = binding.liveIdAudioRoom.getEditText().getText().toString();
            if (TextUtils.isEmpty(liveID)) {
                binding.liveIdAudioRoom.setError("please input liveID");
                return;
            }
            Intent intent = new Intent(MainActivity.this, LiveAudioRoomActivity.class);
            intent.putExtra("host", false);
            intent.putExtra("liveID", liveID);
            startActivity(intent);
        });

        binding.callUserId.getEditText().setText("samsung");
        binding.callUserVideo.setOnClickListener(v -> {
            String targetUserID = binding.callUserId.getEditText().getText().toString();
            if (TextUtils.isEmpty(targetUserID)) {
                binding.callUserId.setError("please input targetUserID");
                return;
            }
            List<String> permissions = Arrays.asList(permission.CAMERA, permission.RECORD_AUDIO);
            requestPermissionIfNeeded(permissions, new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                    @NonNull List<String> deniedList) {
                    if (allGranted) {
                        String[] split = targetUserID.split(",");
                        ZEGOCallInvitationManager.getInstance().inviteVideoCall(Arrays.asList(split), new ZIMCallInvitationSentCallback() {
                                @Override
                                public void onCallInvitationSent(String requestID, ZIMCallInvitationSentInfo info,
                                    ZIMError errorInfo) {
                                    if (errorInfo.code.value() == 0) {
                                        if (split.length > 1) {
                                            Intent intent = new Intent(MainActivity.this, CallInvitationActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(MainActivity.this, CallWaitActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });
                    }
                }
            });
        });

        binding.callUserAudio.setOnClickListener(v -> {
            String targetUserID = binding.callUserId.getEditText().getText().toString();
            if (TextUtils.isEmpty(targetUserID)) {
                binding.callUserId.setError("please input targetUserID");
                return;
            }
            List<String> permissions = Collections.singletonList(permission.RECORD_AUDIO);
            requestPermissionIfNeeded(permissions, new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                    @NonNull List<String> deniedList) {
                    if (allGranted) {
                        String[] split = targetUserID.split(",");
                        ZEGOCallInvitationManager.getInstance().inviteVoiceCall(Arrays.asList(split), new ZIMCallInvitationSentCallback() {
                                @Override
                                public void onCallInvitationSent(String requestID, ZIMCallInvitationSentInfo info,
                                    ZIMError errorInfo) {
                                    if (errorInfo.code.value() == 0) {
                                        if (split.length > 1) {
                                            Intent intent = new Intent(MainActivity.this, CallInvitationActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(MainActivity.this, CallWaitActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });
                    }
                }
            });
        });

        // if LiveStreaming,init after user login,can receive pk request.
        ZEGOLiveStreamingManager.getInstance().init();
        // if Call invitation,init after user login,can receive call request.
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

    private void requestPermissionIfNeeded(List<String> permissions, RequestCallback requestCallback) {
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
            }
        }
        if (allGranted) {
            requestCallback.onResult(true, permissions, new ArrayList<>());
            return;
        }

        PermissionX.init(this).permissions(permissions).onExplainRequestReason((scope, deniedList) -> {
            String message = "";
            if (permissions.size() == 1) {
                if (deniedList.contains(permission.CAMERA)) {
                    message = this.getString(R.string.permission_explain_camera);
                } else if (deniedList.contains(permission.RECORD_AUDIO)) {
                    message = this.getString(R.string.permission_explain_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(permission.CAMERA)) {
                        message = this.getString(R.string.permission_explain_camera);
                    } else if (deniedList.contains(permission.RECORD_AUDIO)) {
                        message = this.getString(R.string.permission_explain_mic);
                    }
                } else {
                    message = this.getString(R.string.permission_explain_camera_mic);
                }
            }
            scope.showRequestReasonDialog(deniedList, message, getString(R.string.ok));
        }).onForwardToSettings((scope, deniedList) -> {
            String message = "";
            if (permissions.size() == 1) {
                if (deniedList.contains(permission.CAMERA)) {
                    message = this.getString(R.string.settings_camera);
                } else if (deniedList.contains(permission.RECORD_AUDIO)) {
                    message = this.getString(R.string.settings_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(permission.CAMERA)) {
                        message = this.getString(R.string.settings_camera);
                    } else if (deniedList.contains(permission.RECORD_AUDIO)) {
                        message = this.getString(R.string.settings_mic);
                    }
                } else {
                    message = this.getString(R.string.settings_camera_mic);
                }
            }
            scope.showForwardToSettingsDialog(deniedList, message, getString(R.string.settings),
                getString(R.string.cancel));
        }).request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                @NonNull List<String> deniedList) {
                if (requestCallback != null) {
                    requestCallback.onResult(allGranted, grantedList, deniedList);
                }
            }
        });
    }
}