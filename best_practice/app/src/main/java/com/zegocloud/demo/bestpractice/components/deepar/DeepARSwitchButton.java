package com.zegocloud.demo.bestpractice.components.deepar;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.demo.bestpractice.R;
import com.zegocloud.demo.bestpractice.internal.ZEGOLiveStreamingManager;
import com.zegocloud.demo.bestpractice.internal.sdk.components.express.SwitchCameraButton;

public class DeepARSwitchButton extends SwitchCameraButton {

    public DeepARSwitchButton(@NonNull Context context) {
        super(context);
    }

    public DeepARSwitchButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DeepARSwitchButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        setImageResource(R.drawable.call_icon_camera_flip, R.drawable.call_icon_camera_flip);
    }

    @Override
    public void open() {
        super.open();
        if (getContext() instanceof Activity) {
            DeepARService.getInstance().switchCamera((Activity) getContext());
        }
    }

    @Override
    public void close() {
        super.close();
        if (getContext() instanceof Activity) {
            DeepARService.getInstance().switchCamera((Activity) getContext());
        }
    }
}
