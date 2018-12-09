package com.miui.marmot.demos.fm;

import android.graphics.Rect;
import android.os.SystemClock;
import android.support.test.espresso.core.deps.guava.base.Strings;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject;
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
 * 收音机-菜单-加入列表
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月9日 下午15:08:58
 */

@RunWith(JUnit4.class)
public class Test_00000004_addTolist {
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
    public void test_00000004_addTolist() throws UiObjectNotFoundException{
        Logger.i("Step1: Start the radio.");
        mm.launchApp("com.miui.fm");
        mm.getUiDevice().waitForWindowUpdate("com.miui.fm",2000);

        Logger.i("Step2: Click the list menu.");
        mm.click(By.res("com.miui.fm:id/btn_stations_list"));
        mm.sleep(15000);//等待搜索到电台

        Logger.i("Step3: Click adding new radio station.");
        UiObject addNewStation = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.Button").text("新建电台"));
        addNewStation.click();
        mm.sleep(1000);

        //取消添加新的电台并检验
        Logger.i("Step4: Cancel adding a new station.");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.EditText")
                .resourceId("com.miui.fm:id/station_freq")).setText("97.4");
        //输入完频率后，点击取消按钮
        mm.click(By.res("android:id/button2"));
        mm.sleep(2000);

        Logger.i("Step5: Check the cancelation.");
        UiObject cancelStation = mm.getUiDevice().findObject(new UiSelector()
                .text("97.4"));
        cc.assertTrue("Cancelation is valid.", (!cancelStation.exists()));

        //添加新的电台，并检验是否属于其他频道
        Logger.i("Step4: Add new radio station.");
        //先点击新建电台按钮
        addNewStation.click();
        SystemClock.sleep(1000);
        //弹出添加框后，输入频率和名称
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.EditText")
                .resourceId("com.miui.fm:id/station_freq")).setText("97.4");
        mm.sleep(1000);
        mm.click(By.res("com.miui.fm:id/station_label"));
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.EditText")
                .resourceId("com.miui.fm:id/station_label")).setText("北京音乐广播");
        //点击确认按钮
        mm.click(By.res("android:id/button1"));
        //确认之后会自动回到主界面，进入下一步之前需要再进入菜单
        mm.sleep(2000);
        mm.launchApp("com.miui.fm");
        mm.sleep(2000);
        mm.click(By.res("com.miui.fm:id/btn_stations_list"));

        Logger.i("Step5: Check the new station is added into list and under 'Other Channel'.");
        Rect newStationPosition = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").text("97.4")).getBounds();
        Rect otherChanel = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").text("其他频道")).getBounds();
        boolean exist = false;
        if(newStationPosition.centerY() > otherChanel.centerY()){
            exist = true;
        }
        mm.sleep(1000);
        cc.assertTrue("The new station has already been added under other chanel.", exist);

        //将新电台添加到收藏
        Logger.i("Step6: Add the station to favorite chanel.");
        //长按出来新的菜单
        mm.longClick(newStationPosition.centerX(), newStationPosition.centerY());
        mm.click(By.res("miui:id/title").text("添加到收藏"));

        Logger.i("Step7: Check if the station is in favorate list.");
        newStationPosition = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").text("97.4")).getBounds();
        Rect starChanel = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").text("收藏频道")).getBounds();
        boolean favorate = false;
        if(newStationPosition.centerY() > starChanel.centerY()){
            favorate = true;
        }
        cc.assertTrue("The new station has already been added into favorate list.", favorate);

        Logger.i("Step8: Delete the chanel.");
        mm.longClick(newStationPosition.centerX(), newStationPosition.centerY());
        mm.click(By.res("miui:id/title").text("删除"));
        mm.sleep(1000);
        mm.click(By.res("android:id/button1").text("确定"));
        mm.sleep(2000);
        cc.assertTrue("The chanel has been deleted.", (!cancelStation.exists()));
    }

    @After
    public void clearEnvironment(){
        Logger.i("clear testing environment.");
        mm.pressBack(2);
        mm.pressHome(2);
    }
}
