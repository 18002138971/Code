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
public class Test_25_SinaWeibo {
    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private String weiboActivity = "com.sina.weibo/.SplashActivity";
    private String weiboPkg = "com.sina.weibo";

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        md = new Method();
        mDevice = mm.getUiDevice();

    }

    @Test
    public void SinaWeibo() {
        for (int i=0;i< ATConfig.SINA_LOOP; i++){
            sina();
        }
    }

    private void sina(){
        mDevice.pressHome();
        try {
            mDevice.executeShellCommand("am force-stop  " + weiboPkg);
            mm.sleep(3000);
            mDevice.executeShellCommand("am start -n  " + weiboActivity);
            mm.sleep(6000);
            md.swipeUpTimes(5);
            mm.sleep(5000);
            UiObject parent = new UiObject(new UiSelector().resourceId("com.sina.weibo:id/mblogHeadtitle"));
            UiObject content = parent.getFromParent(new UiSelector().className("android.view.View").instance(1));
            content.click();
            mm.sleep(5000);
            md.swipeUpTimes(5);
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
