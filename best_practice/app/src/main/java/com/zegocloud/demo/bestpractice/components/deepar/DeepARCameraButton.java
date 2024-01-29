package com.zegocloud.demo.bestpractice.components.deepar;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.demo.bestpractice.R;
import com.zegocloud.demo.bestpractice.internal.ZEGOLiveStreamingManager;
import com.zegocloud.demo.bestpractice.internal.sdk.components.express.ToggleCameraButton;

public class DeepARCameraButton extends ToggleCameraButton {

    public DeepARCameraButton(@NonNull Context context) {
        super(context);
    }

    public DeepARCameraButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DeepARCameraButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        setImageResource(R.drawable.call_icon_camera_on, R.drawable.call_icon_camera_off);
    }

    @Override
    public void open() {
        super.open();
        if (getContext() instanceof Activity) {
            ZEGOLiveStreamingManager.getInstance().openCamera((Activity) getContext());
        }
    }

    @Override
    public void close() {
        super.close();
        if (getContext() instanceof Activity) {
            ZEGOLiveStreamingManager.getInstance().closeCamera();
        }
    }
}
