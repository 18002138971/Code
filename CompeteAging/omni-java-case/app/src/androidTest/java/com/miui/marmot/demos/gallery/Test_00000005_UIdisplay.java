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

/**
 * 相册-图库首页_相册被隐藏的情况
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月16日 下午16:30:18
 */

@RunWith(JUnit4.class)
public class Test_00000005_UIdisplay {
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

    @Test
    public void test_00000005_UIdisplay() throws UiObjectNotFoundException{
        Logger.i("Step1: Enter into the gallery.");
        mm.launchApp("com.miui.gallery");
        mm.getUiDevice().waitForWindowUpdate("com.miui.gallery",2000);

        Logger.i("Set as multiple choose.");
        Rect position = mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(4)).getBounds();
        mm.longClick(position.centerX(), position.centerY());

        Logger.i("Check UI display.");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(5)).click();
        mm.sleep(5000);

        cc.assertTrue("The UI display is normal.", mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.RelativeLayout").index(5))
                    .getChild(new UiSelector()
                            .className("android.widget.CheckBox")).isChecked());
        mm.click(By.res("android:id/button1"));
    }

    @After
    public void clearEnvironment() {
        Logger.i("clear testing environment.");
        mm.pressBack(3);
        mm.pressHome(2);
    }
}
