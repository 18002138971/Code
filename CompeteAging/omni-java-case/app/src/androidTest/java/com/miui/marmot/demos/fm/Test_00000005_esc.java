package com.miui.marmot.demos.fm;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 * 收音机-菜单-退出
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月10日 上午11:34:45
 */

@RunWith(JUnit4.class)
public class Test_00000005_esc {
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
    public void Test_00000005_esc() throws UiObjectNotFoundException{
        Logger.i("Step1: Start the radio.");
        mm.launchApp("com.miui.fm");
        mm.getUiDevice().waitForWindowUpdate("com.miui.fm",2000);

        Logger.i("Step2: Click the menu.");
        mm.click(By.res("com.miui.fm:id/btn_menu"));

        Logger.i("Step3: Click to esc.");
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").text("退出")).click();

        Logger.i("Step4: Check if exit successfully.");
        String text = mm.getUiObject(new UiSelector()).getPackageName();
        cc.assertThat("Successfully exit the radio.", text, is(equalTo("com.miui.home")));

    }

    @After
    public void clearEnvironment(){
        Logger.i("clear testing environment.");
        mm.pressBack(3);
        mm.pressHome(2);
    }
}
