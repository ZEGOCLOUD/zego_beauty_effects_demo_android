package com.zegocloud.demo.bestpractice.components.deepar;

import ai.deepar.ar.CameraResolutionPreset;
import ai.deepar.ar.DeepARImageFormat;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Surface;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutionException;
import timber.log.Timber;

public class CameraX {

    private final int defaultLensFacing = CameraSelector.LENS_FACING_FRONT;
    private int lensFacing = defaultLensFacing;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ByteBuffer[] buffers;
    private int currentBuffer = 0;
    private static final int NUMBER_OF_BUFFERS = 2;
    private CameraCallback callback;
    private CameraResolutionPreset cameraResolutionPreset = CameraResolutionPreset.P640x360;

    public void setupCamera(Activity activity) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(activity.getApplicationContext());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(activity, cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(activity.getApplicationContext()));
    }

    public CameraResolutionPreset getCameraResolutionPreset() {
        return cameraResolutionPreset;
    }

    private void bindImageAnalysis(Activity activity, @NonNull ProcessCameraProvider cameraProvider) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            cameraProvider.unbindAll();
            return;
        }
        int width;
        int height;
        int orientation = getScreenOrientation(activity);
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            || orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            width = cameraResolutionPreset.getWidth();
            height = cameraResolutionPreset.getHeight();
        } else {
            width = cameraResolutionPreset.getHeight();
            height = cameraResolutionPreset.getWidth();
        }
        Timber.d("bindImageAnalysis() called with: width = [" + width + "],height:" + height);

        Size cameraResolution = new Size(width, height);
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        buffers = new ByteBuffer[NUMBER_OF_BUFFERS];
        for (int i = 0; i < NUMBER_OF_BUFFERS; i++) {
            buffers[i] = ByteBuffer.allocateDirect(width * height * 4);
            buffers[i].order(ByteOrder.nativeOrder());
            buffers[i].position(0);
        }

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setOutputImageFormat(
                ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).setTargetResolution(cameraResolution)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(activity.getApplicationContext()), imageAnalyzer);

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle((LifecycleOwner) activity, cameraSelector, imageAnalysis);
    }

    private ImageAnalysis.Analyzer imageAnalyzer = new ImageAnalysis.Analyzer() {
        @Override
        public void analyze(@NonNull ImageProxy image) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            buffer.rewind();
            buffers[currentBuffer].put(buffer);
            buffers[currentBuffer].position(0);
            if (callback != null) {
                callback.analyzeImage(buffers[currentBuffer], image.getWidth(), image.getHeight(),
                    image.getImageInfo().getRotationDegrees(), lensFacing == CameraSelector.LENS_FACING_FRONT,
                    DeepARImageFormat.RGBA_8888, image.getPlanes()[0].getPixelStride());
            }
            currentBuffer = (currentBuffer + 1) % NUMBER_OF_BUFFERS;
            image.close();
        }
    };

    public void setCallback(CameraCallback callback) {
        this.callback = callback;
    }

    public void openCamera(Activity activity) {
        setupCamera(activity);
    }

    public void switchCamera(Activity activity) {
        lensFacing = lensFacing == CameraSelector.LENS_FACING_FRONT ? CameraSelector.LENS_FACING_BACK
            : CameraSelector.LENS_FACING_FRONT;
        //unbind immediately to avoid mirrored frame.
        closeCamera();
        setupCamera(activity);
    }

    public void closeCamera() {
        if (cameraProviderFuture != null) {
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
            get interface orientation from
            https://stackoverflow.com/questions/10380989/how-do-i-get-the-current-orientation-activityinfo-screen-orientation-of-an-a/10383164
         */
    private int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height > width
            || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    public void release() {
        closeCamera();
        setCallback(null);
        lensFacing = CameraSelector.LENS_FACING_FRONT;
    }

    interface CameraCallback {

        void analyzeImage(ByteBuffer buffer, int width, int height, int orientation, boolean mirror,
            DeepARImageFormat imageFormat, int pixelStride);
    }
}
