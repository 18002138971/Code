package com.miui.marmot.demos;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiCollection;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import com.miui.marmot.lib.Checker;
import com.miui.marmot.lib.Config;
import com.miui.marmot.lib.Logger;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

/**
 * Marmot测试用例展示
 *
 * @author 于淼 yumiao@xiaomi.com
 * @since 2017年3月30日 上午10:35:10
 */

@RunWith(AndroidJUnit4.class)
public class Test_00000002_BasicSample {
    private Marmot mm = null;
    private Checker cc = null;

    @Before
    public void initEnvironment() {
        mm = new Marmot();
        cc = new Checker();

        Logger.i("Prepare testing environment.");
        mm.pressHome(2);
        mm.getUiDevice().wait(Until.hasObject(By.pkg(getHomeScreenPackage()).depth(0)), Config.LAUNCHAPP_TIMEOUT);
    }

    @Test
    public void test_00000002_basicSample() throws UiObjectNotFoundException {
        Logger.i("Step1: Launch calculator app.");
        mm.launchApp("com.miui.calculator");

        Logger.i("Step2: Enter an equation: 2 + 3 = ?.");
        mm.click(By.res("com.miui.calculator:id/btn_2"));
        mm.click(By.res("com.miui.calculator:id/btn_plus"));
        mm.click(By.res("com.miui.calculator:id/btn_3"));
        int imagesCount = new UiCollection(new UiSelector().className("android.widget.FrameLayout"))
                .getChildCount(
                new UiSelector().className("android.widget.ImageView"));
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.ImageView")
                    .instance(imagesCount - 1)).click();

        Logger.i("Step3: Verify the result = 5.");
        int horizontalScrollViewCount = new UiCollection(new UiSelector()
                .className("android.widget.FrameLayout"))
                .getChildCount(new UiSelector().className("android.widget.HorizontalScrollView"));
        UiSelector horizontalScrollView = new UiSelector()
                .className("android.widget.HorizontalScrollView")
                .instance(horizontalScrollViewCount - 1);
        String text = new UiCollection(horizontalScrollView).getChildByInstance(new UiSelector()
                    .className("android.widget.TextView"), 1).getText();
        cc.assertTrue("3 + 2 = 5", text.equals("5"));
    }

    @After
    public void clearEnvironment() {
        Logger.i("clear testing environment.");
        mm.pressBack(3);
        mm.pressHome(2);
    }

    private String getHomeScreenPackage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager pm = getInstrumentation().getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

}
