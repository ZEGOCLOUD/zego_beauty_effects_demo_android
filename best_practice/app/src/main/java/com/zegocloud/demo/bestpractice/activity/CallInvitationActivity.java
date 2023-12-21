package com.zegocloud.demo.bestpractice.activity;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.zegocloud.demo.bestpractice.R;
import com.zegocloud.demo.bestpractice.components.ZEGOAudioVideoView;
import com.zegocloud.demo.bestpractice.databinding.ActivityCallInvitationBinding;
import com.zegocloud.demo.bestpractice.internal.ZEGOCallInvitationManager;
import com.zegocloud.demo.bestpractice.internal.business.call.CallChangedListener;
import com.zegocloud.demo.bestpractice.internal.business.call.CallInviteInfo;
import com.zegocloud.demo.bestpractice.internal.business.call.CallInviteUser;
import com.zegocloud.demo.bestpractice.internal.sdk.ZEGOSDKManager;
import com.zegocloud.demo.bestpractice.internal.sdk.basic.ZEGOSDKUser;
import com.zegocloud.demo.bestpractice.internal.sdk.express.IExpressEngineEventHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import timber.log.Timber;

public class CallInvitationActivity extends AppCompatActivity {

    private ActivityCallInvitationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallInvitationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("CallInvitationActivity");

        listenSDKEvent();

        CallInviteInfo callInviteInfo = ZEGOCallInvitationManager.getInstance().getCallInviteInfo();
        if (callInviteInfo.isVideoCall()) {
            binding.callCameraBtn.open();
            binding.callCameraBtn.setVisibility(View.VISIBLE);
            binding.callCameraSwitchBtn.setVisibility(View.VISIBLE);
        } else {
            binding.callCameraBtn.close();
            binding.callCameraBtn.setVisibility(View.GONE);
            binding.callCameraSwitchBtn.setVisibility(View.GONE);
        }

        binding.callHangupBtn.setOnClickListener(v -> {
            finish();
        });

        binding.callMicBtn.open();
        binding.callSpeakerBtn.open();

        List<String> permissions;
        if (callInviteInfo.isVideoCall()) {
            permissions = Arrays.asList(permission.CAMERA, permission.RECORD_AUDIO);
        } else {
            permissions = Collections.singletonList(permission.RECORD_AUDIO);
        }
        requestPermissionIfNeeded(permissions, new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                @NonNull List<String> deniedList) {
                // my video show in small view
                if (callInviteInfo.isOutgoingCall) {
                    binding.selfVideoView.setUserID(callInviteInfo.inviter);
                    binding.otherVideoView.setUserID(callInviteInfo.userList.get(0).getUserID());
                } else {
                    binding.selfVideoView.setUserID(callInviteInfo.userList.get(0).getUserID());
                    binding.otherVideoView.setUserID(callInviteInfo.inviter);
                }

                binding.selfVideoView.startPreviewOnly();
                if (callInviteInfo.isVideoCall()) {
                    binding.selfVideoView.showVideoView();
                } else {
                    binding.selfVideoView.showAudioView();
                }
                ViewGroup parent = (ViewGroup) binding.selfVideoView.getParent();
                parent.setVisibility(View.VISIBLE);

                ZEGOSDKUser currentUser = ZEGOSDKManager.getInstance().expressService.getCurrentUser();
                String currentRoomID = ZEGOSDKManager.getInstance().expressService.getCurrentRoomID();
                String streamID = ZEGOCallInvitationManager.getInstance()
                    .generateUserStreamID(currentUser.userID, currentRoomID);
                binding.selfVideoView.setStreamID(streamID);
                binding.selfVideoView.startPublishAudioVideo();
            }
        });

        binding.selfVideoView.setOnClickListener(v -> {
            ViewGroup selfVideoViewParent = (ViewGroup) binding.selfVideoView.getParent();
            ViewGroup otherVideoViewParent = (ViewGroup) binding.otherVideoView.getParent();
            if (otherVideoViewParent.getVisibility() != View.VISIBLE || callInviteInfo.isVoiceCall()) {
                return;
            }
            selfVideoViewParent.removeView(binding.selfVideoView);
            otherVideoViewParent.removeView(binding.otherVideoView);
            selfVideoViewParent.addView(binding.otherVideoView);
            otherVideoViewParent.addView(binding.selfVideoView);
        });
        binding.otherVideoView.setOnClickListener(v -> {
            ViewGroup selfVideoViewParent = (ViewGroup) binding.selfVideoView.getParent();
            ViewGroup otherVideoViewParent = (ViewGroup) binding.otherVideoView.getParent();
            if (selfVideoViewParent.getVisibility() != View.VISIBLE || callInviteInfo.isVoiceCall()) {
                return;
            }
            selfVideoViewParent.removeView(binding.selfVideoView);
            otherVideoViewParent.removeView(binding.otherVideoView);
            selfVideoViewParent.addView(binding.otherVideoView);
            otherVideoViewParent.addView(binding.selfVideoView);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            ZEGOCallInvitationManager.getInstance().endCall();
            ZEGOCallInvitationManager.getInstance().removeCallData();
        }
    }

    public void listenSDKEvent() {
        ZEGOCallInvitationManager.getInstance().addCallListener(new CallChangedListener() {
            @Override
            public void onReceiveNewCall(String requestID) {
                Timber.d("onReceiveNewCall() called with: requestID = [" + requestID + "]");
            }

            @Override
            public void onInviteNewUser(String requestID, CallInviteUser inviteUser) {
                Timber.d("onInviteNewUser() called with: requestID = [" + requestID + "], inviteUser = [" + inviteUser
                    + "]");
            }

            @Override
            public void onBusyRejectCall(String requestID) {
                Timber.d("onBusyRejectCall() called with: requestID = [" + requestID + "]");
            }

            @Override
            public void onInvitedUserRejected(String requestID, CallInviteUser rejectUser) {
                Timber.d(
                    "onInvitedUserRejected() called with: requestID = [" + requestID + "], rejectUser = [" + rejectUser
                        + "]");
            }

            @Override
            public void onCallEnded(String requestID) {
                Timber.d("onCallEnded() called with: requestID = [" + requestID + "]");
                finish();
            }

            @Override
            public void onCallCancelled(String requestID) {
                Timber.d("onCallCancelled() called with: requestID = [" + requestID + "]");
            }

            @Override
            public void onCallTimeout(String requestID) {
                Timber.d("onCallTimeout() called with: requestID = [" + requestID + "]");
            }

            @Override
            public void onInvitedUserTimeout(String requestID, CallInviteUser timeoutUser) {
                Timber.d(
                    "onInvitedUserTimeout() called with: requestID = [" + requestID + "], timeoutUser = [" + timeoutUser
                        + "]");
            }

            @Override
            public void onInvitedUserQuit(String requestID, CallInviteUser quitUser) {
                Timber.d(
                    "onInvitedUserQuit() called with: requestID = [" + requestID + "], quitUser = [" + quitUser + "]");
            }

            @Override
            public void onInvitedUserAccepted(String requestID, CallInviteUser acceptUser) {
                Timber.d(
                    "onInvitedUserAccepted() called with: requestID = [" + requestID + "], acceptUser = [" + acceptUser
                        + "]");
            }
        });

        ZEGOSDKManager.getInstance().expressService.addEventHandler(new IExpressEngineEventHandler() {

            @Override
            public void onUserEnter(List<ZEGOSDKUser> userList) {
                super.onUserEnter(userList);
                for (ZEGOSDKUser zegosdkUser : userList) {
                    ZEGOAudioVideoView audioVideoView = getAudioVideoViewByUserID(zegosdkUser.userID);
                    if (audioVideoView != null) {
                        audioVideoView.setUserID(zegosdkUser.userID);
                    }
                }
            }

            @Override
            public void onReceiveStreamAdd(List<ZEGOSDKUser> userList) {
                for (ZEGOSDKUser zegosdkUser : userList) {
                    ZEGOAudioVideoView audioVideoView = getAudioVideoViewByUserID(zegosdkUser.userID);
                    if (audioVideoView != null) {
                        audioVideoView.setUserID(zegosdkUser.userID);
                        audioVideoView.setStreamID(zegosdkUser.getMainStreamID());
                        audioVideoView.startPlayRemoteAudioVideo();
                    }
                }
            }

            @Override
            public void onReceiveStreamRemove(List<ZEGOSDKUser> userList) {
                for (ZEGOSDKUser zegosdkUser : userList) {
                    ZEGOAudioVideoView audioVideoView = getAudioVideoViewByUserID(zegosdkUser.userID);
                    if (audioVideoView != null) {
                        audioVideoView.setStreamID("");
                        ViewGroup parent = (ViewGroup) audioVideoView.getParent();
                        parent.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCameraOpen(String userID, boolean open) {
                super.onCameraOpen(userID, open);
                ZEGOAudioVideoView audioVideoView = getAudioVideoViewByUserID(userID);
                if (audioVideoView != null) {
                    ZEGOSDKUser user = ZEGOSDKManager.getInstance().expressService.getUser(userID);
                    if (user.isCameraOpen() || user.isMicrophoneOpen()) {
                        ViewGroup parent = (ViewGroup) audioVideoView.getParent();
                        parent.setVisibility(View.VISIBLE);
                        if (user.isCameraOpen()) {
                            audioVideoView.showVideoView();
                        } else {
                            audioVideoView.showAudioView();
                        }
                    } else {
                        ViewGroup parent = (ViewGroup) audioVideoView.getParent();
                        parent.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onMicrophoneOpen(String userID, boolean open) {
                super.onMicrophoneOpen(userID, open);
                ZEGOAudioVideoView audioVideoView = getAudioVideoViewByUserID(userID);
                if (audioVideoView != null) {
                    ZEGOSDKUser user = ZEGOSDKManager.getInstance().expressService.getUser(userID);
                    if (user.isCameraOpen() || user.isMicrophoneOpen()) {
                        ViewGroup parent = (ViewGroup) audioVideoView.getParent();
                        parent.setVisibility(View.VISIBLE);
                        if (user.isCameraOpen()) {
                            audioVideoView.showVideoView();
                        } else {
                            audioVideoView.showAudioView();
                        }
                    } else {
                        ViewGroup parent = (ViewGroup) audioVideoView.getParent();
                        parent.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onUserLeft(List<ZEGOSDKUser> userList) {
                finish();
            }

        });
    }

    private ZEGOAudioVideoView getAudioVideoViewByUserID(String userID) {
        if (Objects.equals(binding.selfVideoView.getUserID(), userID)) {
            return binding.selfVideoView;
        }
        if (Objects.equals(binding.otherVideoView.getUserID(), userID)) {
            return binding.otherVideoView;
        }
        return null;
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