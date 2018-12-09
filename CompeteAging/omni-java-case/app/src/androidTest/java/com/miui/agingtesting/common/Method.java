package com.miui.agingtesting.common;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import java.io.IOException;

/**
 * @author 闫东 yandong@xiaomi.com
 * @since 2017年8月4日 下午2:20:10
 */

public class Method {

    public UiDevice mDevice;
    private Instrumentation mInstrumentation = null;
    private int width;
    private int height;

    public Method(){
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mDevice = UiDevice.getInstance(mInstrumentation);
        width = mDevice.getDisplayWidth();
        height = mDevice.getDisplayHeight();
    }

    /**
     * first time login app, close the agree button
     */
    public void closeAgreeAndGoOnDialog() {
        UiObject goOn1 = mDevice.findObject(new UiSelector().textStartsWith("同意"));
        UiObject goOn2 = mDevice.findObject(new UiSelector().textStartsWith("确定"));
        UiObject goOn3 = mDevice.findObject(new UiSelector().textStartsWith("允许"));
        UiObject goOn4 = mDevice.findObject(new UiSelector().textStartsWith("跳过"));
        UiObject goOn5 = mDevice.findObject(new UiSelector().textStartsWith("继续"));
        UiObject goOn6 = mDevice.findObject(new UiSelector().textStartsWith("立即开启"));
        try {
            if(goOn1.exists()) {
                goOn1.click();
                SystemClock.sleep(1000);
            }
            if(goOn2.exists()) {
                goOn2.click();
                SystemClock.sleep(1000);
            }
            if(goOn3.exists()) {
                goOn3.click();
                SystemClock.sleep(1000);
            }
            if(goOn4.exists()) {
                goOn4.click();
                SystemClock.sleep(1000);
            }
            if(goOn5.exists()) {
                goOn5.click();
                SystemClock.sleep(1000);
            }
            if(goOn6.exists()) {
                goOn6.click();
                SystemClock.sleep(1000);
            }

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *  version updata,updata later
     */
    public void updatalaterAndGoOn() {
        UiObject goOn = mDevice.findObject(new UiSelector().className("android.widget.TextView").textStartsWith("稍后"));

        try {
            if(goOn.exists()) {
                goOn.click();
                SystemClock.sleep(1000);
            }

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *  browswe choose the open method
     */
    public void chooseOpenMethod(){
        UiObject browser = mDevice.findObject(new UiSelector().className("android.widget.TextView").textStartsWith("浏览器"));
        UiObject option = mDevice.findObject(new UiSelector().resourceId("android.miui:id/always_option"));
        try {
            if(browser.exists()&&option.exists()) {
                option.click();
                browser.click();
                SystemClock.sleep(1000);
            }

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *  first time login yidian new,choose no interest and go on next
     */
    public void newSelectInterest() {
        UiObject next = mDevice.findObject(new UiSelector().resourceId("com.yidian.xiaomi:id/btnNext"));
        try {
            if(next.exists()) {
                next.click();
                SystemClock.sleep(1000);
            }

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *  first time login yidian new,choose allow satellite positioning
     */
    public void allowPosition() {
        UiObject allow = mDevice.findObject(new UiSelector().className("android.widget.Button").textStartsWith("允许"));
        try {
            if(allow.exists()) {
                allow.click();
                SystemClock.sleep(1000);
            }

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *  "huawei" first time login browser,choose open method
     */
    public void chosebrowser(){
        UiObject browser = mDevice.findObject(new UiSelector().className("android.widget.TextView").textStartsWith("  华为浏览器"));
        UiObject button = mDevice.findObject(new UiSelector().className("android.widget.Button").textStartsWith("始终"));
        try {
            if(browser.exists()&&button.exists()) {
//                browser.click();
//                SystemClock.sleep(500);
                button.click();
                SystemClock.sleep(1000);
            }

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *  first time login yidian news,choose some allow authority
     */
    public void allow()
    {
        UiObject button1 = mDevice.findObject(new UiSelector().className("android.widget.CheckBox").textStartsWith("禁止后不再询问"));
        UiObject button2 = mDevice.findObject(new UiSelector().className("android.widget.Button").textStartsWith("禁止"));
        try {
            while(button1.exists()&&button2.exists()) {
                button1.click();
                SystemClock.sleep(500);
                button2.click();
                SystemClock.sleep(1000);
            }

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *  close xiaomi Sync
     */
    public void closemiSync()throws UiObjectNotFoundException, IOException {
        UiObject opensync = mDevice.findObject(new UiSelector().textContains("暂不"));
        if(opensync.exists())
            opensync.click();
    }

    /**
     *  close sogou Sync
     */
    public void closesogou()throws UiObjectNotFoundException, IOException {
        UiObject allow = mDevice.findObject(new UiSelector().packageName("com.sohu.inputmethod.sogou.xiaomi").text("允许"));
        if(allow.exists())
            allow.click();
    }

    /**
     *  swipe up：向上滑动
     *  @param  times:  int , 次数
     */
    public void swipeUpTimes(int times){
        for (int i=0; i< times; i++)
            swipeUp();
    }

    public void swipeUp(){
        mDevice.swipe(width/2,height-200,width/2,height/2,20);
        SystemClock.sleep(2000);

    }

    /**
     *  swipe down: 下拉，调出下拉状态栏
     *
     */
    public void swipeDown(){
        mDevice.swipe(width / 2, 0, width / 2, height/2, 10);
    }

    /**
     * pull refresh: 下拉刷新,与 swipe down 的区别在于起点不同
     */
    public  void pullRefresh(){
        mDevice.swipe(width/2,height/3,width/2,height*3/4,10);
    }



}


