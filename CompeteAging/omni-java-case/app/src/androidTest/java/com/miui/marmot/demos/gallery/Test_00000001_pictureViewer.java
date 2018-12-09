package com.miui.marmot.demos.gallery;

import android.graphics.Rect;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.test.espresso.core.deps.publicsuffix.PublicSuffixPatterns;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiCollection;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import com.miui.marmot.lib.Checker;
import com.miui.marmot.lib.Logger;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 * 相册-图片浏览
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月12日 下午13:33:33
 */

@RunWith(JUnit4.class)
public class Test_00000001_pictureViewer {
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

    public void meriClickCenter(){
        try {
            Rect frame = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.FrameLayout")).getBounds();
            mm.click(frame.centerX(), frame.centerY());
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

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
    public void test_00000001_pictureViewer() throws UiObjectNotFoundException{
        Logger.i("Step1: Enter into the gallery.");
        mm.launchApp("com.miui.gallery");
        mm.getUiDevice().waitForWindowUpdate("com.miui.gallery", 2000);

        Logger.i("Step2: Scan the photos.");
        UiScrollable view = new UiScrollable(new UiSelector().scrollable(true));
        view.setAsVerticalList();
        view.flingForward();
        mm.sleep(2000);

        Logger.i("Step3: Enter into the album to scan the photos.");
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").text("相册")).clickAndWaitForNewWindow();
        //随机进入一个相册浏览照片
        mm.getUiDevice().findObject(new UiSelector()
                .resourceId("com.miui.gallery:id/album_name").text("相机")).clickAndWaitForNewWindow();
        view.flingForward();
        mm.sleep(2000);

        Logger.i("Step4: Launch in camera and open gallery via thumbnail icon.");
        mm.pressHome();
        mm.launchApp("com.android.camera");
        mm.getUiDevice().waitForWindowUpdate("com.miui.camera", 2000);
        //拍照并查看详细信息
        mm.click(By.res("com.android.camera:id/v6_shutter_button_internal"));
        mm.sleep(1000);
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.ImageView").resourceId("com.android.camera:id/v6_thumbnail_image")).clickAndWaitForNewWindow();
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.Button").text("更多")).click();
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").text("详情")).clickAndWaitForNewWindow();
        String imageInfo = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/file_info_title")).getText();
        //RESUME
        mm.pressBack();
        mm.pressBack();

        //打开相册，检查该照片是否在图库中显示
        Logger.i("Check the photo just token is in the gallery.");
        mm.pressHome(2);
        mm.launchApp("com.miui.gallery");
        mm.getUiDevice().waitForWindowUpdate("com.miui.gallery", 2000);
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(4)).click();
        //判断小米5c
        if(productName.equals("meri")){
            mm.sleep(1000);
            meriClickCenter();
        }
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.Button").text("更多")).click();
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").text("详情")).click();
        String imageInfoCheck = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/file_info_title")).getText();
        cc.assertThat("The photo just token has saved in gallery.", imageInfo, is(equalTo(imageInfoCheck)));

    }

    @Test
    public void test_00000001_screenShot() throws UiObjectNotFoundException{
        Logger.i("Step5: Screenshot and check the photo");
        takeScreenShot();
        mm.pressHome();
        mm.launchApp("com.miui.gallery");
        mm.getUiDevice().waitForWindowUpdate("com.miui.gallery", 2000);
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").text("相册")).click();
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/album_name").text("截屏")).click();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(3)).click();
        //判断小米5c
        if(productName.equals("meri")){
            mm.sleep(1000);
            meriClickCenter();
        }
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.Button").text("更多")).click();
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").text("详情")).click();
        mm.sleep(2000);
        String shotInfo = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/file_info_title")).getText();
        boolean isScreenshot = shotInfo.startsWith("Screenshot");
        cc.assertTrue("The screenshot has saved in gallery.", isScreenshot);
    }

    @After
    public void clearEnvironment() {
        Logger.i("clear testing environment.");
        mm.pressBack(3);
        mm.pressHome(2);
    }

}
