## Getting Started To Run ZEGOCLOUD SDKDemo

### Open The Project With Android Studio

if you open the project and get this error:
<img src="https://github.com/ZEGOCLOUD/zegocloud_sdk_demo_android/blob/master/best_practice/pngs/AS_1.jpg">

you can fix it as follow:


open your Android Studio Settings,modify your Gradle JDK to java 11:
<img src="https://github.com/ZEGOCLOUD/zegocloud_sdk_demo_android/blob/master/best_practice/pngs/AS_2.jpg">

### Input AppID and AppSign

input your ZEGO appID and appSign in class `com.zegocloud.demo.bestpractice.ZEGOSDKKeyCenter.java`.


<img src="https://github.com/ZEGOCLOUD/zego_beauty_effects_demo_android/blob/master/best_practice/pngs/demo1.jpg">


Now,you can build and run project.







## Integrate DeepAR SDK with ZEGO Express SDK

ZEGO Express SDK provides various interfaces for integrating external beauty and filter functionalities, such as `enableCustomVideoCapture`, `enableCustomVideoProcessing`, and `enableCustomVideoRender`. Due to the interface limitations of DeepAR SDK, currently only the `enableCustomVideoCapture` method can be used for integration.

When CustomVideoCapture is set to true, the built-in camera video capture functionality of ZEGO Express SDK is disabled, and receive video frame data by third-party SDK to call `sendCustomVideoCaptureRawData` .

Therefore, we need to use the native CameraX interfaces to control the device's camera for data capture. The captured data is then processed by DeepAR SDK, and the processed data is sent to ZEGO Express SDK for rendering and transmission using `sendCustomVideoCaptureRawData`.



### Directly run the demo integrated with ZEGO Express SDK and DeepAR SDK
In this demo, the integration of DeepAR SDK has already been completed. Therefore, in addition to the ZEGO Express SDK part, you also need to apply for a DeepAR license :

1. fill it in `DEEP_AR_LICENCE` in `com.zegocloud.demo.bestpractice.components.deepar.DeepARService`. Please refer to the following link to apply for a DeepAR license: https://docs.deepar.ai/deepar-sdk/platforms/android/getting-started

2. Modify to `applicationId` in app level build.gradle to your own in DeepAR developer portal.
   
3. Run App.


### Integrate DeepAR SDK into your ZEGO Express SDK project
If you need to add DeepAR to your own project, you can follow these steps:

1. Add DeepAR dependencies and obtain DeepAR license.

Refer to: https://docs.deepar.ai/deepar-sdk/platforms/android/getting-started

In addition, since CameraX is required, you also need to add the dependency in the app-level build.gradle file:

```groovy

def camerax_version = "1.2.3" // cameraX
implementation "androidx.camera:camera-core:$camerax_version"
implementation "androidx.camera:camera-camera2:$camerax_version"
implementation "androidx.camera:camera-lifecycle:$camerax_version"
implementation "androidx.camera:camera-view:$camerax_version"

```


2. Copy the com.zegocloud.demo.bestpractice.components.deepar directory to your project code directory, and copy the assets/deepAR folder to the assets directory of your project. 

<img src="https://github.com/ZEGOCLOUD/zego_beauty_effects_demo_android/tree/feature/deepAR/best_practice/pngs/deepar_copy.jpg">

3. Initialize DeepAR SDK and native CameraX 
We need to initialize DeepAR SDK in the activity where we use it, by calling `DeepARService.getInstance().initializeDeepAR(this)`. 


4. Add CameraX camera control 
Since we are now using the native CameraX interfaces to control the device's camera for data capture, when rendering DeepAR visual effects, camera control needs to include calls to CameraX interfaces.

```java
    public void openCamera(Activity activity) {
        DeepARService.getInstance().openCamera(activity);
        ZEGOSDKManager.getInstance().expressService.openCamera(true);
    }

    public void closeCamera() {
        DeepARService.getInstance().closeCamera();
        ZEGOSDKManager.getInstance().expressService.openCamera(false);
    }

    public void useFrontCamera(Activity activity, boolean front) {
        DeepARService.getInstance().switchCamera(activity);
        ZEGOSDKManager.getInstance().expressService.useFrontCamera(front);
    }

    public void startCoHost(Activity activity) {
        openCamera(activity);
        coHostService.startCoHost();
    }

    public void endCoHost(Activity activity) {
        closeCamera();
        coHostService.endCoHost();
    }
```

5. Replace ToggleCameraButton and SwitchCameraButton widget
If you use `ToggleCameraButton` and `SwitchCameraButton` buttons to control the camera when using ZEGO Express SDK, you need to replace them with `com.zegocloud.demo.bestpractice.components.deepar.DeepARCameraButton` and `com.zegocloud.demo.bestpractice.components.deepar.DeepARSwitchButton` when rendering DeepAR visual effects to gain control over the camera.

6. Release DeepAR
Release DeepAR when exiting the activity:

```java
protected void onPause() {
    super.onPause();
    if (isFinishing()) {
        DeepARService.getInstance().release();
    }
}
```

### Supported AR Effects

The AR Effects we provide can be found in the "Free effects pack content" section of https://docs.deepar.ai/deepar-sdk/filters. You can use the `com.zegocloud.demo.bestpractice.components.deepar.DeepARService#switchEffect` interface to switch AR Effects.




