package com.miui.marmot.demos.fm;

import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;

import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.marmot.lib.Checker;
import com.miui.marmot.lib.Logger;
import com.miui.marmot.lib.Marmot;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 收音机-未接耳机启动FM
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月9日 上午10:00:58
 */

@RunWith(AndroidJUnit4.class)
public class Test_00000001_launchRadio {
    private Marmot mm = null;
    private Checker cc = null;

    @Before
    public void initEnvironment(){
        mm = new Marmot();
        cc = new Checker();

        Logger.i("Clear the background application.");
        mm.pressMenu();
        try {
            mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.ImageView").resourceId("com.android.systemui:id/clearButton")).clickAndWaitForNewWindow();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        Logger.i("Prepare testing environment.");
        mm.pressHome(2);
    }

    //该方法用于判断是否是小数
    public static boolean isDecimal(String str){
        if(str!=null && str.matches("^[.\\d]*$"))
            return true;
        else
            return false;
    }

    @Test
    public void test_00000001_launchRadio() throws UiObjectNotFoundException{
        Logger.i("Step1: Launch radio app.");
        mm.launchApp("com.miui.fm");
        mm.getUiDevice().waitForWindowUpdate("com.miui.fm",2000);

        Logger.i("Step2: Check Result");
        //查看界面显示是否正常，调频是否正常
        //这里做判断是因为在循环测试时，非首次打开收音机时收音机有可能处于关闭状态，需要先开启
        if(mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.ImageButton").resourceId("com.miui.fm:id/btn_power_large")).exists()){
            mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.ImageButton").resourceId("com.miui.fm:id/btn_power_large")).clickAndWaitForNewWindow();
        }
        String text =  mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.fm:id/txt_frequency")).getText();
        cc.assertTrue("The radio is on.", isDecimal(text));
    }

    @After
    public void clearEnvironment() {
        Logger.i("clear testing environment.");
        mm.pressHome(2);
    }

}
