package com.zegocloud.demo.bestpractice.components.deepar;

import ai.deepar.ar.ARErrorType;
import ai.deepar.ar.AREventListener;
import ai.deepar.ar.CameraResolutionPreset;
import ai.deepar.ar.DeepAR;
import ai.deepar.ar.DeepARImageFormat;
import ai.deepar.ar.DeepARPixelFormat;
import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import com.zegocloud.demo.bestpractice.components.deepar.CameraX.CameraCallback;
import com.zegocloud.demo.bestpractice.internal.ZEGOLiveStreamingManager;
import com.zegocloud.demo.bestpractice.internal.sdk.ZEGOSDKManager;
import im.zego.zegoexpress.callback.IZegoCustomVideoCaptureHandler;
import im.zego.zegoexpress.constants.ZegoPublishChannel;
import im.zego.zegoexpress.constants.ZegoVideoFrameFormat;
import im.zego.zegoexpress.constants.ZegoVideoMirrorMode;
import im.zego.zegoexpress.entity.ZegoVideoFrameParam;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class DeepARService {

    private static final class Holder {

        private static final DeepARService INSTANCE = new DeepARService();

    }

    private DeepARService() {

    }

    public static DeepARService getInstance() {
        return DeepARService.Holder.INSTANCE;
    }

    private DeepAR deepAR;

    ArrayList<String> effects;

    private CameraX cameraX = new CameraX();

    private boolean startVideoCapture;
    private int currentIndex = 0;
    public static final String DEEP_AR_LICENCE = ;

    private AREventListener arEventListener = new AREventListener() {
        @Override
        public void screenshotTaken(Bitmap bitmap) {

        }

        @Override
        public void videoRecordingStarted() {

        }

        @Override
        public void videoRecordingFinished() {

        }

        @Override
        public void videoRecordingFailed() {

        }

        @Override
        public void videoRecordingPrepared() {

        }

        @Override
        public void shutdownFinished() {

        }

        @Override
        public void initialized() {
            deepAR.startCapture();
        }

        @Override
        public void faceVisibilityChanged(boolean b) {

        }

        @Override
        public void imageVisibilityChanged(String s, boolean b) {

        }

        @Override
        public void frameAvailable(Image image) {
            if (startVideoCapture) {
                onFrameAvailable(image);
            }
        }

        @Override
        public void error(ARErrorType arErrorType, String s) {

        }

        @Override
        public void effectSwitched(String s) {

        }
    };

    public ArrayList<String> getAllEffects() {
        return effects;
    }

    public void initializeDeepAR(Activity activity) {
        deepAR = new DeepAR(activity.getApplicationContext());
        deepAR.setLicenseKey(DEEP_AR_LICENCE);
        deepAR.initialize(activity, arEventListener);
        deepAR.changeLiveMode(true);

        // portrait
        CameraResolutionPreset cameraResolutionPreset = cameraX.getCameraResolutionPreset();
        deepAR.setOffscreenRendering(cameraResolutionPreset.getHeight(), cameraResolutionPreset.getWidth(),
            DeepARPixelFormat.RGBA_8888);

        cameraX.setupCamera(activity);
        cameraX.setCallback(new CameraCallback() {
            @Override
            public void analyzeImage(ByteBuffer buffer, int width, int height, int orientation, boolean mirror,
                DeepARImageFormat imageFormat, int pixelStride) {
                if (deepAR != null) {
                    deepAR.receiveFrame(buffer, width, height, orientation, mirror, imageFormat, pixelStride);
                }
            }
        });

        initializeFilters();

        ZEGOSDKManager.getInstance().expressService.setVideoMirrorMode(ZegoVideoMirrorMode.NO_MIRROR,
            ZegoPublishChannel.MAIN);
        ZEGOLiveStreamingManager.getInstance().enableCustomVideoCapture(true);
        ZEGOLiveStreamingManager.getInstance().setCustomVideoCaptureHandler(new IZegoCustomVideoCaptureHandler() {
            @Override
            public void onStart(ZegoPublishChannel channel) {
                super.onStart(channel);
                startVideoCapture = true;
            }

            @Override
            public void onStop(ZegoPublishChannel channel) {
                super.onStop(channel);
                startVideoCapture = false;
            }
        });
        //        ZEGOSDKManager.getInstance().expressService.setVideoConfig(
        //            new ZegoVideoConfig(ZegoVideoConfigPreset.PRESET_360P));
    }

    private void initializeFilters() {
        effects = new ArrayList<>();
        effects.add("none");
        effects.add("viking_helmet.deepar");
        effects.add("MakeupLook.deepar");
        effects.add("Split_View_Look.deepar");
        effects.add("Emotions_Exaggerator.deepar");
        effects.add("Emotion_Meter.deepar");
        effects.add("Stallone.deepar");
        effects.add("flower_face.deepar");
        effects.add("galaxy_background.deepar");
        effects.add("Humanoid.deepar");
        effects.add("Neon_Devil_Horns.deepar");
        effects.add("Ping_Pong.deepar");
        effects.add("Pixel_Hearts.deepar");
        effects.add("Snail.deepar");
        effects.add("Hope.deepar");
        effects.add("Vendetta_Mask.deepar");
        effects.add("Fire_Effect.deepar");
        effects.add("burning_effect.deepar");
        effects.add("Elephant_Trunk.deepar");

    }


    private String getFilterPath(String filterName) {
        if (filterName.equals("none")) {
            return null;
        }
        return "file:///android_asset///deepAR/" + filterName;
    }

    public void switchEffect(int index) {
        currentIndex = index;
        deepAR.switchEffect("effect", getFilterPath(effects.get(index)));
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void switchCamera(Activity activity) {
        cameraX.switchCamera(activity);
    }

    public void openCamera(Activity activity) {
        cameraX.openCamera(activity);
    }

    public void closeCamera() {
        cameraX.closeCamera();
    }

    public void release() {
        cameraX.release();
        if (deepAR == null) {
            return;
        }
        switchEffect(0);
        deepAR.setAREventListener(null);
        deepAR.release();
        deepAR = null;
        startVideoCapture = false;
        ZEGOLiveStreamingManager.getInstance().enableCustomVideoCapture(false);
        ZEGOLiveStreamingManager.getInstance().setCustomVideoCaptureHandler(null);
    }

    public void onFrameAvailable(Image image) {
        if (image != null) {
            int imageRealWidth = image.getWidth();
            int imageRealHeight = image.getHeight();
            //            Timber.d("frameAvailable() called with: " + "image.getWidth() = [" + imageRealWidth + "], "
            //                + "image.getHeight() = [" + imageRealHeight + "], " + "image.getFormat() = [" + image.getFormat()
            //                + "], " + "image.getTimestamp() = [" + image.getTimestamp() + "], " + "image.getCropRect() = ["
            //                + image.getCropRect() + "]");
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer frameBuffer = planes[0].getBuffer();

            ZegoVideoFrameParam param = new ZegoVideoFrameParam();
            param.width = image.getWidth();
            param.height = image.getHeight();
            param.strides[0] = planes[0].getRowStride();
            param.format = ZegoVideoFrameFormat.RGBA32;

            ZEGOSDKManager.getInstance().expressService.sendCustomVideoCaptureRawData(frameBuffer,
                frameBuffer.capacity(), param, image.getTimestamp(), ZegoPublishChannel.MAIN);
        }
    }
}
