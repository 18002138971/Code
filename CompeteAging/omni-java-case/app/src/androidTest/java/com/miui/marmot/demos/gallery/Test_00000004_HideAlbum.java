package com.miui.marmot.demos.gallery;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
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
import org.junit.runners.Parameterized;

/**
 * 相册-图库首页_相册被隐藏的情况
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月16日 下午15:52:37
 */

@RunWith(JUnit4.class)
public class Test_00000004_HideAlbum {
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

    @Parameterized.Parameters
    public void del_album(String albumName){
        try {
            Rect toLongClick = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/album_name").text(albumName)).getBounds();
            mm.longClick(toLongClick.centerX(), toLongClick.centerY());
            mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("miui:id/title").text("删除")).click();
            mm.click(By.res("android:id/button1"));
            cc.assertTrue("The video and the album has been deleted.", !mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/album_name").text(albumName)).exists());
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String view_more_info() {
        String picInfo = null;
        try {
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").text("更多")).click();
            mm.getUiDevice().findObject(new UiSelector().resourceId("miui:id/title").text("详情")).click();
            mm.sleep(2000);
            picInfo = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/file_info_title").index(1)).getText();
            mm.pressBack(2);
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        return picInfo;
    }

    @Test
    public void test_00000004_HideAlbum() throws UiObjectNotFoundException{
        Logger.i("Step1: Enter into the gallery.");
        mm.launchApp("com.miui.gallery");
        mm.sleep(2000);

        Logger.i("Step2: Build a new album and add a photo to it.");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.TextView").text("相册")).click();
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.ImageView").resourceId("com.miui.gallery:id/create_album").index(1)).click();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.EditText")).setText("测试屏蔽");
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.Button").resourceId("android:id/button1").text("确定")).click();

        Logger.i("Step3: Add a photo to the new album.");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(4)).click();
        mm.click(By.res("android:id/button2"));

        Logger.i("Step4: Block the album.");
        Rect toLongClick = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/album_name").text("测试屏蔽")).getBounds();
        mm.longClick(toLongClick.centerX(), toLongClick.centerY());
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("miui:id/title").text("屏蔽")).click();

        Logger.i("Step5: Check the album has been blocked.");
        cc.assertTrue("The album has been blocked.", !mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/album_name").text("测试屏蔽")).exists());

        Logger.i("Step6: Set the hidden album can be seen on list.");
        mm.click(By.res("com.miui.gallery:id/more"));
        //判断小米5c
        if(productName.equals("meri")){
            mm.getUiDevice().findObject(new UiSelector().resourceId("android:id/text1").text("设置")).clickAndWaitForNewWindow();
        }
        else{
            mm.getUiDevice().findObject(new UiSelector().resourceId("com.miui.gallery:id/text").text("设置")).clickAndWaitForNewWindow();
        }
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.LinearLayout").index(7))
                .getChild(new UiSelector().className("android.widget.LinearLayout").index(2)).click();
        mm.pressBack();

        Logger.i("Step8: Check the hidden album if can be seen.");
        cc.assertTrue("The album has been blocked.", mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/album_name").text("测试屏蔽")).exists());
        del_album("测试屏蔽");

        Logger.i("Step9: Setting back.");
        mm.click(By.res("com.miui.gallery:id/more"));
        //判断小米5c
        if(productName.equals("meri")){
            mm.getUiDevice().findObject(new UiSelector().resourceId("android:id/text1").text("设置")).clickAndWaitForNewWindow();
        }
        else{
            mm.getUiDevice().findObject(new UiSelector().resourceId("com.miui.gallery:id/text").text("设置")).clickAndWaitForNewWindow();
        }
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.LinearLayout").index(7))
                .getChild(new UiSelector().className("android.widget.LinearLayout").index(2)).click();
    }

    @After
    public void clearEnvironment() {
        Logger.i("clear testing environment.");
        mm.pressBack(3);
        mm.pressHome(2);
    }
}
