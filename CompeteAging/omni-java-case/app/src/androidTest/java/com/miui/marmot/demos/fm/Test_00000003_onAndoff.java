package com.miui.marmot.demos.fm;

import android.graphics.Rect;
import android.os.SystemClock;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.marmot.lib.Checker;
import com.miui.marmot.lib.Logger;
import com.miui.marmot.lib.Marmot;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 * 收音机-开关
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月9日 下午15:08:58
 */

@RunWith(JUnit4.class)
public class Test_00000003_onAndoff {
    private Marmot mm = null;
    private Checker cc = null;

    @Before
    public void initEnvironment(){
        mm = new Marmot();
        cc = new Checker();

        Logger.i("Prepare testing environment.");
        mm.pressHome(2);
    }
    public static boolean isDecimal(String str){
        if(str!=null && str.matches("^[.\\d]*$"))
            return true;
        else
            return false;
    }


    @Test
    public void test_00000003_onAndoff() throws UiObjectNotFoundException{
        Logger.i("Step1: Start the radio.");
        mm.launchApp("com.miui.fm","com.miui.fmradio.FmActivity");
        mm.getUiDevice().waitForWindowUpdate("com.miui.fm",2000);

        Logger.i("Step2: Check the close button.");
        mm.click(By.res("com.miui.fm:id/btn_power"));
        SystemClock.sleep(1000);

        Logger.i("Step3: Verify the radio is closed.");
        String text1 = mm.getUiObject(By.res("com.miui.fm:id/txt_label_off")).getText();
        cc.assertThat("The radio is closed.", text1, is(equalTo("点击开启收音机")));

        Logger.i("Step4: Reclick the button to start.");
        mm.click(By.res("com.miui.fm:id/btn_power_large"));
        mm.sleep(2000);

        Logger.i("Step5: Check the result.");
        String text2 = mm.getUiObject(By.res("com.miui.fm:id/txt_frequency")).getText();
        cc.assertTrue("The radio is on.", isDecimal(text2));

    }

    @After
    public void clearEnvironment(){
        Logger.i("clear testing environment.");
        mm.pressBack(3);
        mm.pressHome(2);
    }

}
