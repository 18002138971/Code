package com.miui.marmot.demos.gallery;

import android.os.RemoteException;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 * 相册-图册picker_横竖屏切换
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月17日 下午16:00:00
 */

@RunWith(JUnit4.class)
public class Test_00000007_Turn {
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
    public void test_00000007_Turn() throws UiObjectNotFoundException{
        Logger.i("Step1: Enter into the gallery.");
        mm.launchApp("com.miui.gallery");
        mm.getUiDevice().waitForWindowUpdate("com.miui.gallery",2000);

        Logger.i("Step2: Turn the screen.");
        try {
            mm.getUiDevice().setOrientationRight();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        UiScrollable view = new UiScrollable(new UiSelector().scrollable(true));
        view.setAsVerticalList();
        view.flingForward();

        Logger.i("Check.");
        String name = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").index(0)).getText();
        cc.assertThat("The UI is normal.", name, is(equalTo("照片")));

    }

    @After
    public void clearEnvironment() {
        Logger.i("clear testing environment.");
        //Resume
        try {
            mm.getUiDevice().setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mm.pressBack(3);
        mm.pressHome(2);
    }

}
