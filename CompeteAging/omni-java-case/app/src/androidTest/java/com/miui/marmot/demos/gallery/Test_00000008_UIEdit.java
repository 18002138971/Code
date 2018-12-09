package com.miui.marmot.demos.gallery;

import android.graphics.Rect;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.marmot.lib.Checker;
import com.miui.marmot.lib.Logger;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * 相册-编辑界面显示
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月19日 上午9:33:00
 */

@RunWith(JUnit4.class)
public class Test_00000008_UIEdit {
    private Marmot mm = null;
    private Checker cc = null;
    private String productName = null;

    @Before
    public void initEnvironment(){
        mm = new Marmot();
        cc = new Checker();
        productName = mm.getUiDevice().getProductName();

        Logger.i("Prepare testing environment.");
        mm.pressHome(2);
    }

    public void takeScreenShot(){
        int startX, startY, endX, endY;
        try {
            Rect notify = mm.getUiDevice().findObject(new UiSelector().className("android.widget.FrameLayout")).getBounds();
            startX = notify.left + (notify.right - notify.left)/2;
            startY = 0;
            endX = startX;
            endY = notify.top + (notify.bottom - notify.top)/2;
            mm.getUiDevice().swipe(startX, startY, endX, endY, 10);
            mm.sleep(2000);
            mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("android:id/title").text("正在通过 USB 充电")).swipeDown(5);
            mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.android.systemui:id/toggle").text("截屏")).click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        mm.sleep(2000);
    }

    @Test
    public void test_00000008_UIEdit() throws UiObjectNotFoundException{
        Logger.i("Step1: Enter into the gallery.");
        mm.launchApp("com.miui.gallery");
        mm.getUiDevice().waitForWindowUpdate("com.miui.gallery",2000);

        Logger.i("Step2: Screenshot.");
        takeScreenShot();

        Logger.i("Step2: Enter into edit mode and check.");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.TextView").text("相册")).clickAndWaitForNewWindow();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.TextView").text("截屏")).click();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(3)).clickAndWaitForNewWindow();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").index(3)).clickAndWaitForNewWindow();

        int functNum = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.FrameLayout").resourceId("com.miui.gallery:id/bottom_panel").index(6))
                .getChild(new UiSelector().className("android.widget.LinearLayout").index(0))
                .getChildCount();
        //根据机型更改数字
        //这里需要加入判断机型，才能判断它的编辑图片功能个数，并且不同的机型功能名称也不太一致
        String productName = mm.getUiDevice().getProductName();
        if(productName.equals("prada") || productName.equals("santoni")){
            cc.assertTrue("The Filter function is normal.", functNum==6);
        }
        else{
            cc.assertTrue("The Filter function is normal.", functNum==5);
        }

    }

    @After
    public void clearEnvironment() {
        Logger.i("clear testing environment.");
        mm.pressBack(3);
        mm.pressHome(2);
    }

}
