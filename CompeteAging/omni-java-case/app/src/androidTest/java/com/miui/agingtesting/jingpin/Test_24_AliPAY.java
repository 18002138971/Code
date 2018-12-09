package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.ATConfig;
import com.miui.agingtesting.common.Method;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Created by tianxiao on 2018/3/2.
 */

@RunWith(AndroidJUnit4.class)
public class Test_24_AliPAY {
    private Marmot mm;
    private UiDevice mDevice;
    private String aliPayActivity = "com.eg.android.AlipayGphone/.AlipayLogin";

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        mDevice = mm.getUiDevice();

    }

    @Test
    public void AliPay() {
        for (int i=0;i< ATConfig.ALIPAY_LOOP; i++){
            alipay();
        }
    }

    private void alipay(){
        mDevice.pressHome();
        try {
            mDevice.executeShellCommand("am force-stop com.eg.android.AlipayGphone");
            mm.sleep(3000);
            mDevice.executeShellCommand("am start -n  " + aliPayActivity);
            mm.sleep(5000);
            //点击付钱
            UiObject pay = new UiObject(new UiSelector().text("付钱"));
            pay.click();
            mm.sleep(3000);
            //返回
            mm.pressBack();
            //收钱
            UiObject collect = new UiObject(new UiSelector().text("收钱"));
            collect.click();
            mm.sleep(3000);
            mm.pressBack();

            mm.pressHome();

        } catch (IOException e) {
            e.printStackTrace();
            mm.pressBack(5);
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }


    @After
    public void clear() {
        mDevice.pressHome();
    }

}
