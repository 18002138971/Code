package com.miui.marmot.demos.fm;

import android.provider.Settings;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

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
 * 收音机-频道列表界面分别按HOME键、返回键
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月10日 下午13:33:33
 */

@RunWith(JUnit4.class)
public class Test_00000007_homeAndreturn {
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
    public void test_00000007_homeAndreturn() throws UiObjectNotFoundException {
        Logger.i("Step1: Start the radio.");
        mm.launchApp("com.miui.fm");
        mm.getUiDevice().waitForWindowUpdate("com.miui.fm",2000);

        Logger.i("Step2: Click the list menu.");
        mm.click(By.res("com.miui.fm:id/btn_stations_list"));

        Logger.i("Step3: Press the home button");
        mm.pressHome();

        Logger.i("Step4: Check if is the home screen.");
        String text1 = mm.getUiObject(new UiSelector()).getPackageName();
        cc.assertThat("Successfully exit the radio.", text1, is(equalTo("com.miui.home")));

        Logger.i("Step5: Return the previous page.");
        mm.launchApp("com.miui.fm");
        mm.getUiDevice().waitForWindowUpdate("com.miui.fm",2000);
        //机型的不同会导致这里操作不一致
        if(mm.getUiDevice().getProductName().equals("hydrogen")){ //小米和红米有所区别，这里添加小米的测试机名称即可
            mm.pressBack();
        }
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.ImageButton").resourceId("com.miui.fm:id/btn_stations_list")).click();
        mm.pressBack();

        Logger.i("Step6: Check if return successfully.");
        String text2 = mm.getUiObject(new UiSelector()).getPackageName();
        cc.assertThat("Successfully return to previous page.", text2, is(equalTo("com.miui.fm")));
    }

    @After
    public void clearEnvironment(){
        Logger.i("clear testing environment.");
        mm.pressBack(3);
        mm.pressHome(2);
    }
}
