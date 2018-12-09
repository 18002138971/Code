package com.miui.agingtesting.jingpin;

import android.content.Context;
import android.net.wifi.WifiManager;
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

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Created by tianxiao on 2018/3/2.
 */

@RunWith(AndroidJUnit4.class)
public class Test_26_Map {
    private Marmot mm;
    private UiDevice mDevice;
    private Context context= getTargetContext();
    private String gaodeActivity = "com.autonavi.minimap/com.autonavi.map.activity.SplashActivity";

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        mDevice = mm.getUiDevice();

    }

    @Test
    public void Map() {
        for (int i=0;i< ATConfig.ANAP_LOOP; i++){
            map();
        }
    }

    private void map(){
        mDevice.pressHome();
        try {
            //这里必须用 force-stop
            mDevice.executeShellCommand("am force-stop com.autonavi.minimap");
            mm.sleep(3000);
            //关闭 wifi
            //无 simi 卡的环境下测试需要打开 wifi
//            setWifiState(false);
            mm.sleep(10000);
            mDevice.executeShellCommand("am start -n  " + gaodeActivity);
            mm.sleep(5000);
            UiObject path = new UiObject(new UiSelector().text("路线"));
            path.click();
            mm.sleep(3000);
            UiObject destInput = new UiObject(new UiSelector().text("输入终点"));
            destInput.click();
            destInput.setText("北京南站");
            mm.sleep(5000);
            UiObject result = new UiObject(new UiSelector().resourceId("com.autonavi.minimap:id/main_content_rl"));
            result.click();
            mm.sleep(5000);
            mm.pressHome();
            setWifiState(true);

        } catch (IOException e) {
            e.printStackTrace();
            mm.pressBack(5);
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setWifiState(boolean state){
        //首先，用Context通过getSystemService获取wifimanager
        WifiManager mWifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
        //调用WifiManager的setWifiEnabled方法设置wifi的打开或者关闭，只需把下面的state改为布尔值即可（true:打开 false:关闭）
        mWifiManager.setWifiEnabled(state);
    }

    @After
    public void clear() {
        mDevice.pressHome();
    }

}
