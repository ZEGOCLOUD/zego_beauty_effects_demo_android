# **Advanced Beauty Effects**

## Prerequisites

Before you begin, make sure you complete the following:

* **Contact ZEGOCLOUD Technical Support to activate the advanced beauty effects**.

## Getting Started To Run ZEGOCLOUD SDKDemo

### Open The Project With Android Studio

if you open the project and get this error:
![]("https://github.com/ZEGOCLOUD/zego_beauty_effects_demo_android/tree/master/best_practice/pngs/AS_1.jpg")

you can fix it as follow:


open your Android Studio Settings,modify your Gradle JDK to java 11:
![]("https://github.com/ZEGOCLOUD/zego_beauty_effects_demo_android/tree/master/best_practice/pngs/AS_2.jpg")

### Input AppID and AppSign

input your ZEGO appID and appSign in class `com.zegocloud.demo.bestpractice.ZEGOSDKKeyCenter.java`

![]("https://github.com/ZEGOCLOUD/zego_beauty_effects_demo_android/tree/master/best_practice/pngs/demo1.jpg")



### Modify ApplicationID

When you activate the Effects SDK,you have to provide your package name,now replace the application with the package name you provided;

![]("https://github.com/ZEGOCLOUD/zego_beauty_effects_demo_android/tree/master/best_practice/pngs/demo2.jpg")

After that,you can build and run the demo project.


## Beauty Feature

### Enable Beauty Feature
you can enable the beauty features as follows:

```java
ZEGOSDKManager.getInstance().enableZEGOEffects(true);
```
which is called in `com.zegocloud.demo.bestpractice.activity.LoginActivity.java`
in the demo.

### 


### Remove Beauty Features

Advanced beauty currently supports a total of 12 types of features, including: basic beauty, advanced beauty, filters, lipstick, blush, eyeliner, eyeshadow, colored contacts, style makeup, stickers, and background segmentation.

![]("https://storage.zego.im/sdk-doc/Pics/zegocloud/beauty/features.png")

If you don't need a certain feature, you can remove it as follows:

1. remove the corresponding `BeautyGroup` type in the `beautyGroups` of `com.zegocloud.demo.bestpractice.internal.sdk.effect.ZegoEffectsService.java` file.

![]("https://github.com/ZEGOCLOUD/zego_beauty_effects_demo_android/tree/master/best_practice/pngs/demo3.jpg")

2. After you removed the specific beauty features, you can also delete their beauty resources. The beauty resources is in the `assets` folder of the project:
![]("https://github.com/ZEGOCLOUD/zego_beauty_effects_demo_android/tree/master/best_practice/pngs/demo4.jpg")
,As follow:

#### Basic

Do not need to delete resources.

#### Advanced

Do not need to delete resources.

#### Filters

If you don't need this feature, you can delete the following resources.

```
/BeautyResources/AdvancedResources/filterDreamyCozily.bundle
/BeautyResources/AdvancedResources/filterDreamySunset.bundle
/BeautyResources/AdvancedResources/filterDreamySweet.bundle
/BeautyResources/AdvancedResources/filterGrayFilmlike.bundle
/BeautyResources/AdvancedResources/filterGrayMonet.bundle
/BeautyResources/AdvancedResources/filterGrayNight.bundle
/BeautyResources/AdvancedResources/filterNaturalAutumn.bundle
/BeautyResources/AdvancedResources/filterNaturalBrighten.bundle
/BeautyResources/AdvancedResources/filterNaturalCreamy.bundle
/BeautyResources/AdvancedResources/filterNaturalFresh.bundle
```

#### Lipstick

If you don't need this feature, you can delete the following resources.

```
/BeautyResources/AdvancedResources/beautyMakeupLipstickCameoPink.bundle
/BeautyResources/AdvancedResources/beautyMakeupLipstickCoral.bundle
/BeautyResources/AdvancedResources/beautyMakeupLipstickRedVelvet.bundle
/BeautyResources/AdvancedResources/beautyMakeupLipstickRustRed.bundle
/BeautyResources/AdvancedResources/beautyMakeupLipstickSweetOrange.bundle
```

#### Blusher

If you don't need this feature, you can delete the following resources.

```
/BeautyResources/AdvancedResources/beautyMakeupBlusherAprocitPink.bundle
/BeautyResources/AdvancedResources/beautyMakeupBlusherMilkyOrange.bundle
/BeautyResources/AdvancedResources/beautyMakeupBlusherPeach.bundle
/BeautyResources/AdvancedResources/beautyMakeupBlusherSlightlyDrunk.bundle
/BeautyResources/AdvancedResources/beautyMakeupBlusherSweetOrange.bundle
```

#### Eyelashes

If you don't need this feature, you can delete the following resources.

```
/BeautyResources/AdvancedResources/beautyMakeupEyelashesCurl.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyelashesEverlong.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyelashesNatural.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyelashesTender.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyelashesThick.bundle
```

#### Eyeliner

If you don't need this feature, you can delete the following resources.

```
/BeautyResources/AdvancedResources/beautyMakeupEyelinerCatEye.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyelinerDignified.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyelinerInnocent.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyelinerNatural.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyelinerNaughty.bundle
```

#### Eyeshadow

If you don't need this feature, you can delete the following resources.

```
/BeautyResources/AdvancedResources/beautyMakeupEyeshadowBrightOrange.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyeshadowMochaBrown.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyeshadowPinkMist.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyeshadowShimmerPink.bundle
/BeautyResources/AdvancedResources/beautyMakeupEyeshadowTeaBrown.bundle
```

#### Colored Contacts

If you don't need this feature, you can delete the following resources.

```
/BeautyResources/AdvancedResources/beautyMakeupColoredContactsBrownGreen.bundle
/BeautyResources/AdvancedResources/beautyMakeupColoredContactsChocolateBrown.bundle
/BeautyResources/AdvancedResources/beautyMakeupColoredContactsDarknightBlack.bundle
/BeautyResources/AdvancedResources/beautyMakeupColoredContactsLightsBrown.bundle
/BeautyResources/AdvancedResources/beautyMakeupColoredContactsStarryBlue.bundle
```

#### StyleMakeup

If you don't need this feature, you can delete the following resources.

```
/BeautyResources/AdvancedResources/beautyStyleMakeupCutieCool.bundle
/BeautyResources/AdvancedResources/beautyStyleMakeupFlawless.bundle
/BeautyResources/AdvancedResources/beautyStyleMakeupInnocentEyes.bundle
/BeautyResources/AdvancedResources/beautyStyleMakeupMilkyEyes.bundle
/BeautyResources/AdvancedResources/beautyStyleMakeupPureSexy.bundle
```

#### Stickers

If you don't need this feature, you can delete the following resources.

```
/BeautyResources/StickerBaseResources.bundle
/BeautyResources/AdvancedResources/stickerAnimal.bundle
/BeautyResources/AdvancedResources/stickerCat.bundle
/BeautyResources/AdvancedResources/stickerClawMachine.bundle
/BeautyResources/AdvancedResources/stickerClown.bundle
/BeautyResources/AdvancedResources/stickerCoolGirl.bundle
/BeautyResources/AdvancedResources/stickerDeer.bundle
/BeautyResources/AdvancedResources/stickerDive.bundle
/BeautyResources/AdvancedResources/stickerSailorMoon.bundle
/BeautyResources/AdvancedResources/stickerWatermelon.bundle
```

#### Background segmentation

If you don't need this feature, you can delete the following resources.

```
/BeautyResources/BackgroundSegmentation.model
/BeautyResources/BackgroundImages/
```









