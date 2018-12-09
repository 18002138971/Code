package com.miui.agingtesting.jingpin;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.Method;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.miui.agingtesting.common.ATConfig.BROWSERVIEW_LOOP;

/**
 * Created by tianxiao on 2018/3/2.
 */

public class Test_18_Browser {
    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private int width;
    private int height;
    private String address = "http://www.baidu.com";


    @Before
    public void init() throws Exception {
        mm = new Marmot();
        mDevice = mm.getUiDevice();
        md = new Method();
        width = mDevice.getDisplayWidth();
        height = mDevice.getDisplayHeight();

    }

    @Test
    public void Browser() {
        for (int i=0;i<BROWSERVIEW_LOOP; i++ ){
            browser();
        }
    }

    private void browser(){
        mm.pressHome();
        mm.sleep(2000);

        try {
            //需要先 kill，防止已经在运行时获取不到控件
            mDevice.executeShellCommand("am start -a android.intent.action.VIEW -d " + address);//启动浏览器打开一个网站
            mm.sleep(2000);
            md.chosebrowser();
            md.chooseOpenMethod();
            md.closeAgreeAndGoOnDialog();//第一次打开去除是否同意对话框

            swipeUP(5);

            UiObject news = new UiObject(new UiSelector().className("android.view.View").instance(1));
            try {
                news.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
            mm.sleep(5000);
            swipeUP(5);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void swipeUP(int times){
        for (int i=0;i< times; i++){
            mDevice.swipe(width/2,height-300,width/2,height/2,20);
            mm.sleep(1000);
        }

    }

    @After
    public void clear() {
        mDevice.pressHome();
    }
}
