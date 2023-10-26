package com.zegocloud.demo.bestpractice.internal.sdk.components.effect;

import com.zegocloud.demo.bestpractice.internal.sdk.effect.bean.BeautyGroup;
import java.util.List;

public class BeautyTabInfo {

    public String title;
    public BeautyGroup beautyGroup;
    public List<BeautyItem> beautyItems;
    public int selectedPosition;

    public BeautyTabInfo(BeautyGroup beautyGroup, String title, List<BeautyItem> beautyItems) {
        this.title = title;
        this.beautyGroup = beautyGroup;
        this.beautyItems = beautyItems;
    }
}
