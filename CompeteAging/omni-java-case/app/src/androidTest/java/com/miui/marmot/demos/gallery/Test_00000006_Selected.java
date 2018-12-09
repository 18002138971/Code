package com.miui.marmot.demos.gallery;

import android.graphics.Rect;
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
 * 相册-图库首页_多选状态下操作项：包括单选，多选，全选，全不选，滑动和横竖屏切换
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月16日 下午16:30:18
 */

@RunWith(JUnit4.class)
public class Test_00000006_Selected {
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
    public void test_00000006_Selected() throws UiObjectNotFoundException{
        Logger.i("Step1: Enter into the gallery.");
        mm.launchApp("com.miui.gallery");
        mm.getUiDevice().waitForWindowUpdate("com.miui.gallery",2000);

        Logger.i("Step2: Set as multiple choose.");
        Rect position = mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(4)).getBounds();
        mm.longClick(position.centerX(), position.centerY());

        Logger.i("Step3: Single choose.");
        if(!mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(5)).isChecked()){
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(5)).click();
            cc.assertTrue("The photo is selected.", mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.RelativeLayout").index(5))
                    .getChild(new UiSelector().className("android.widget.CheckBox")).isChecked());
        }
        else{
            Logger.i("The picture has been selected or it doesn't exist.");
        }

        Logger.i("Step4: Multiple choose.");
        if(!mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(6)).isChecked()) {
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(6)).click();
            cc.assertTrue("The photo is selected.", mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.RelativeLayout").index(6))
                    .getChild(new UiSelector().className("android.widget.CheckBox")).isChecked());
        }
        else{
            Logger.i("The picture has been selected or it doesn't exist.");
        }

        //RESUME
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(5)).click();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(6)).click();

        Logger.i("Step5: Select all.");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").text("全选")).click();
        String slc1 = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.Button").resourceId("android:id/button2")).getText();
        cc.assertThat("The pictures are all selected.", slc1, is(equalTo("全不选")));

        Logger.i("Scroll, turn and view.");
        UiScrollable view = new UiScrollable(new UiSelector().scrollable(true));
        view.setAsVerticalList();
        view.flingForward();
        mm.sleep(2000);
        try {
            mm.getUiDevice().setOrientationRight();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        view.flingForward();
        mm.sleep(2000);

        Logger.i("Cancel Select all.");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").text("全不选")).click();
        String slc2 = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.Button").resourceId("android:id/button2")).getText();
        cc.assertThat("The pictures are not selected.", slc2, is(equalTo("全选")));
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.Button").resourceId("android:id/button1").text("取消")).click();
        cc.assertTrue("Cancel selection.", !mm.getUiDevice().findObject(new UiSelector().text("取消")).exists());
    }

    @After
    public void clearEnvironment() {
        //Resume
        try {
            mm.getUiDevice().setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mm.sleep(2000);
        Logger.i("clear testing environment.");
        mm.pressBack();
        mm.pressHome(2);
    }

}
