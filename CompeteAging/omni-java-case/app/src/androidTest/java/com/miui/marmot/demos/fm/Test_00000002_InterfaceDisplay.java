package com.miui.marmot.demos.fm;

import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 * 收音机-FM界面显示
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月9日 上午10:44:44
 */

@RunWith(AndroidJUnit4.class)
public class Test_00000002_InterfaceDisplay {
    private Marmot mm = null;
    private Checker cc = null;

    @Before
    public void initEnvironment(){
        mm = new Marmot();
        cc = new Checker();

        Logger.i("Prepare testing environment.");
        mm.pressHome(2);
    }

    @Test
    public void test_00000002_Interface() throws UiObjectNotFoundException{
        Logger.i("Step1: Start the radio.");
        mm.launchApp("com.miui.fm");
        mm.getUiDevice().waitForWindowUpdate("com.miui.fm", 2000);

        Logger.i("Step2: Check the close button");
        mm.click(By.res("com.miui.fm:id/btn_power"));
        mm.sleep(3000); //此处是为了让收音机关闭停留一段时间，看能否正常处于关闭状态

        Logger.i("Step3: Verify the radio is closed");
        String text1 = mm.getUiObject(By.res("com.miui.fm:id/txt_label_off")).getText();
        cc.assertThat("The button is closed.", text1, is(equalTo("点击开启收音机")));
        //Resume
        mm.click(By.res("com.miui.fm:id/btn_power_large"));
        mm.sleep(2000);

        Logger.i("Step4: Check the menu");
        mm.click(By.res("com.miui.fm:id/btn_menu"));
        mm.sleep(2000);

        Logger.i("Step5: Verify the menu.");
        String text2 = mm.getUiObject(By.res("android:id/text1")).getText();
        cc.assertThat("The menu is valid.", text2, is(equalTo("睡眠模式")));
        //Resume
        mm.pressBack();

        Logger.i("Step6: Check the FM list button.");
        mm.click(By.res("com.miui.fm:id/btn_stations_list"));
        mm.sleep(15000);

        Logger.i("Step7: Verify the FM list button.");
        String text3 = mm.getUiDevice().findObject(new UiSelector().resourceId("miui:id/action_bar_title")).getText();
        cc.assertThat("The FM list button is valid." , text3, is(equalTo("电台列表")));
        mm.sleep(2000);
    }

    @After
    public void clearEnvironment() {
        Logger.i("clear testing environment.");
        mm.pressBack(3);
        mm.pressHome(2);
    }
}
