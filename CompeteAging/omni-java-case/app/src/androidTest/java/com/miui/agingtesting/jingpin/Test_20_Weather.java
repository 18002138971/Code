package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.Method;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.miui.agingtesting.common.ATConfig.WEATHER_LOOP;
import static com.miui.marmot.lib.Config.isHUAWEI;
import static com.miui.marmot.lib.Config.isOPPO;
import static com.miui.marmot.lib.Config.isXIAOMI;

/**
 * Created by tianxiao on 2018/3/2.
 */

@RunWith(AndroidJUnit4.class)
public class Test_20_Weather {
    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private String PhoneName;
    private String weatherActivity = "com.miui.weather2/.ActivityWeatherMain";
    private String weatherMore = "15天趋势预报";

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        md = new Method();
        mDevice = mm.getUiDevice();
        PhoneName=mDevice.getProductName();
        if (isOPPO(PhoneName))            //OPPO
            weatherActivity = "com.coloros.weather/.OppoMainActivity";
        else if(isHUAWEI(PhoneName))      //华为
            weatherActivity = "com.huawei.android.totemweatherapp/.WeatherHome";
        else if(isXIAOMI(PhoneName))      //小米
            weatherActivity = "com.miui.weather2/.ActivityWeatherMain";
        else{
            throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
        }
    }

    @Test
    public void Weather() {
        for (int i=0;i< WEATHER_LOOP;i++){
            weather();
        }
    }

    private void weather(){
        mDevice.pressHome();
        try {
            mDevice.executeShellCommand("am start -n  " + weatherActivity);
            mm.sleep(2000);
            md.pullRefresh();
            mm.sleep(3000);
            //HUAWEI、oppo 手机没有15天趋势
            UiObject wm = new UiObject(new UiSelector().text(weatherMore));
            try {
                wm.click();
                mm.sleep(5000);
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @After
    public void clear() {
        mDevice.pressHome();
    }

}
