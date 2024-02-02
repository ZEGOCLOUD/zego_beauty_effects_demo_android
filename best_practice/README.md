## Getting Started To Run ZEGOCLOUD SDKDemo

### Open The Project With Android Studio

if you open the project and get this error:
<img src="https://github.com/ZEGOCLOUD/zegocloud_sdk_demo_android/blob/master/best_practice/pngs/AS_1.jpg">

you can fix it as follow:


open your Android Studio Settings,modify your Gradle JDK to java 11:
<img src="https://github.com/ZEGOCLOUD/zegocloud_sdk_demo_android/blob/master/best_practice/pngs/AS_2.jpg">

### Input AppID and AppSign

input your ZEGO appID and appSign in class `com.zegocloud.demo.bestpractice.ZEGOSDKKeyCenter.java`.


<img src="https://github.com/ZEGOCLOUD/zegocloud_sdk_demo_android/blob/master/best_practice/pngs/demo1.jpg">


Now,you can build and run project



## How to Integrate FaceUnity SDK with ZEGO

Assume that you have already get FaceUnity authpack.java and add faceunity dependence.


1.  File - import module - choose source directory - select `faceunity` module in the demo.
2.  Add `com.zegocloud.demo.bestpractice.faceunity.FaceUnityVideoProcess` to your project.
3.  Init FaceUnity SDK after you signed in,and enable CustomVideoProcessing for ZEGO SDK before
publish any streams.

    ```java
    public void initFaceUnity(){
        FURenderer.getInstance().setup(this);
        ZegoCustomVideoProcessConfig config = new ZegoCustomVideoProcessConfig();
        config.bufferType = ZegoVideoBufferType.GL_TEXTURE_2D;
        ZEGOSDKManager.getInstance().expressService.enableCustomVideoProcessing(true, config, ZegoPublishChannel.MAIN);

        FaceUnityVideoProcess faceUnityVideoProcess = new FaceUnityVideoProcess();
        ZEGOSDKManager.getInstance().expressService.setCustomVideoProcessHandler(faceUnityVideoProcess);
    }
    ```
1. Add `com.faceunity.nama.ui.FaceUnityView` to any layout xml files to show FaceUnity beauty widget.
   
2. Bind `FaceUnityView` to FaceUnity SDK:
    ```java
    FaceUnityDataFactory mFaceUnityDataFactory = FURenderer.getInstance().getFaceUnityDataFactory();
    binding.faceUnityView.bindDataFactory(mFaceUnityDataFactory);
    ```
3. Now you can click `FaceUnityView` widget to change Beauty effects


