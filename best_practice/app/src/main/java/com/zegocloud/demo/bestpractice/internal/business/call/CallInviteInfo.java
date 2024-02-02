package com.zegocloud.demo.bestpractice.internal.business.call;

import java.util.List;

public class CallInviteInfo {

    public String requestID;
    public String firstInviter;
    public String inviter;
    public List<CallInviteUser> userList;
    public int type;
    public boolean isOutgoingCall;

    @Override
    public String toString() {
        return "CallInviteInfo{" + "requestID='" + requestID + '\'' + ", firstInviter='" + firstInviter + '\''
            + ", inviter='" + inviter + '\'' + ", userList=" + userList + ", type=" + type + ", isOutgoingCall="
            + isOutgoingCall + '}';
    }

    public boolean isVideoCall() {
        return type == CallExtendedData.VIDEO_CALL;
    }

    public boolean isVoiceCall() {
        return type == CallExtendedData.VOICE_CALL;
    }
}
