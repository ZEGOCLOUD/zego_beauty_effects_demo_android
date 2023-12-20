package com.zegocloud.demo.bestpractice.activity;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.zegocloud.demo.bestpractice.R;
import com.zegocloud.demo.bestpractice.databinding.ActivityCallWaitBinding;
import com.zegocloud.demo.bestpractice.internal.ZEGOCallInvitationManager;
import com.zegocloud.demo.bestpractice.internal.business.UserRequestCallback;
import com.zegocloud.demo.bestpractice.internal.business.call.CallChangedListener;
import com.zegocloud.demo.bestpractice.internal.business.call.CallExtendedData;
import com.zegocloud.demo.bestpractice.internal.business.call.CallInviteUser;
import com.zegocloud.demo.bestpractice.internal.business.call.FullCallInfo;
import com.zegocloud.demo.bestpractice.internal.sdk.ZEGOSDKManager;
import com.zegocloud.demo.bestpractice.internal.utils.ToastUtil;
import im.zego.zegoexpress.callback.IZegoRoomLoginCallback;
import im.zego.zegoexpress.constants.ZegoScenario;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;
import timber.log.Timber;

public class CallWaitActivity extends AppCompatActivity {

    private ActivityCallWaitBinding binding;
    private FullCallInfo callInfo;
    private CallChangedListener listener;

    public static void startActivity(Context context, FullCallInfo fullCallInfo) {
        Intent intent = new Intent(context, CallWaitActivity.class);
        intent.putExtra("callInfo", fullCallInfo.toString());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallWaitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("CallWaitActivity");
        callInfo = FullCallInfo.parse(getIntent().getStringExtra("callInfo"));
        Timber.d("onCreate: " + callInfo);

        if (callInfo.isOutgoingCall) {
            binding.incomingCallAcceptButton.setVisibility(View.GONE);
            binding.incomingCallRejectButton.setVisibility(View.GONE);
            binding.outgoingCallCancelButton.setVisibility(View.VISIBLE);
            binding.waitingIcon.setLetter(callInfo.calleeUserID);
        } else {
            binding.incomingCallAcceptButton.setVisibility(View.VISIBLE);
            binding.incomingCallRejectButton.setVisibility(View.VISIBLE);
            binding.outgoingCallCancelButton.setVisibility(View.GONE);
            binding.waitingIcon.setLetter(callInfo.callerUserName);
        }

        List<String> permissions;
        if (callInfo.isVideoCall()) {
            permissions = Arrays.asList(permission.CAMERA, permission.RECORD_AUDIO);
        } else {
            permissions = Collections.singletonList(permission.RECORD_AUDIO);
        }
        requestPermissionIfNeeded(permissions, new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                @NonNull List<String> deniedList) {
                if (grantedList.contains(permission.CAMERA)) {
                    ZEGOSDKManager.getInstance().expressService.openCamera(true);
                    binding.videoView.startPreviewOnly();
                }
            }
        });

        if (callInfo.isVideoCall()) {
            binding.incomingCallAcceptButton.setImageResource(R.drawable.call_icon_video_accept);
        } else {
            binding.incomingCallAcceptButton.setImageResource(R.drawable.call_icon_voice_accept);
        }

        binding.incomingCallAcceptButton.setOnClickListener(v -> {
            ZEGOCallInvitationManager.getInstance().acceptCallRequest(callInfo.callID, new UserRequestCallback() {
                @Override
                public void onUserRequestSend(int errorCode, String requestID) {
                    if (errorCode == 0) {
                        if (callInfo.isVideoCall()) {
                            ZEGOSDKManager.getInstance().expressService.setRoomScenario(
                                ZegoScenario.STANDARD_VIDEO_CALL);
                        } else {
                            ZEGOSDKManager.getInstance().expressService.setRoomScenario(
                                ZegoScenario.STANDARD_VOICE_CALL);
                        }
                        ZEGOSDKManager.getInstance().expressService.loginRoom(callInfo.callID,
                            new IZegoRoomLoginCallback() {
                                @Override
                                public void onRoomLoginResult(int errorCode, JSONObject extendedData) {
                                    if (errorCode == 0) {
                                        CallInvitationActivity.startActivity(CallWaitActivity.this, callInfo);
                                    } else {
                                        ToastUtil.show(CallWaitActivity.this, "joinExpressRoom failed :" + errorCode);
                                    }
                                    finish();
                                }
                            });
                    } else {
                        ToastUtil.show(CallWaitActivity.this, "send invite failed :" + errorCode);
                    }
                }
            });
        });
        binding.incomingCallRejectButton.setOnClickListener(v -> {
            ZEGOCallInvitationManager.getInstance().rejectCallRequest(callInfo.callID, new UserRequestCallback() {
                @Override
                public void onUserRequestSend(int errorCode, String requestID) {
                    if (errorCode != 0) {
                        ToastUtil.show(CallWaitActivity.this, "send reject failed :" + errorCode);
                    }
                    finish();
                    ZEGOCallInvitationManager.getInstance().removeCallData();
                }
            });
        });

        binding.outgoingCallCancelButton.setOnClickListener(v -> {
            ZEGOCallInvitationManager.getInstance()
                .cancelCallRequest(callInfo.callID, callInfo.calleeUserID, new UserRequestCallback() {
                    @Override
                    public void onUserRequestSend(int errorCode, String requestID) {
                        if (errorCode != 0) {
                            ToastUtil.show(CallWaitActivity.this, "send cancel failed :" + errorCode);
                        }
                        finish();
                        ZEGOCallInvitationManager.getInstance().removeCallData();
                    }
                });
        });

        listener = new CallChangedListener() {
            @Override
            public void onReceiveNewCall(String requestID, String inviterUserID, CallExtendedData originalExtendedData,
                List<CallInviteUser> userList) {

            }

            @Override
            public void onBusyRejectCall(String requestID) {

            }

            @Override
            public void onInvitedUserRejected(String requestID, CallInviteUser rejectUser) {

            }

            @Override
            public void onCallEnded(String requestID) {
                if (requestID.equals(callInfo.callID)) {
                    finish();
                }
            }

            @Override
            public void onCallCancelled(String requestID) {
                if (requestID.equals(callInfo.callID)) {
                    finish();
                }
            }

            @Override
            public void onCallTimeout(String requestID) {
                if (requestID.equals(callInfo.callID)) {
                    finish();
                }
            }

            @Override
            public void onInvitedUserTimeout(String requestID, CallInviteUser timeoutUser) {

            }

            @Override
            public void onInvitedUserQuit(String requestID, CallInviteUser quitUser) {

            }

            @Override
            public void onInvitedUserAccepted(String requestID, CallInviteUser acceptUser) {
                if (requestID.equals(callInfo.callID)) {
                    if (callInfo.isVideoCall()) {
                        ZEGOSDKManager.getInstance().expressService.setRoomScenario(ZegoScenario.STANDARD_VIDEO_CALL);
                    } else {
                        ZEGOSDKManager.getInstance().expressService.setRoomScenario(ZegoScenario.STANDARD_VOICE_CALL);
                    }
                    ZEGOSDKManager.getInstance().expressService.loginRoom(callInfo.callID,
                        new IZegoRoomLoginCallback() {
                            @Override
                            public void onRoomLoginResult(int errorCode, JSONObject extendedData) {
                                if (errorCode == 0) {
                                    CallInvitationActivity.startActivity(CallWaitActivity.this, callInfo);
                                } else {
                                    ToastUtil.show(CallWaitActivity.this, "Join Room failed,errorCode:" + errorCode);
                                }
                                finish();
                            }
                        });
                }
            }
        };
        ZEGOCallInvitationManager.getInstance().addCallListener(listener);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            ZEGOCallInvitationManager.getInstance().removeCallListener(listener);
            ZEGOSDKManager.getInstance().expressService.openCamera(false);
            ZEGOSDKManager.getInstance().expressService.stopPreview();
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