package com.miui.agingtesting.jingpin;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.miui.agingtesting.common.ATConfig.DUOKAN_LOOP;

/**
 * Created by tianxiao on 2018/3/2.
 */

public class Test_16_Duokan {
    private Marmot mm;
    private UiDevice mDevice;
    private int width;
    private int height;
    private String PhoneName;
    private String duokanActivity = "com.duokan.reader/.DkReaderActivity";
    private String duokanPkg = "com.duokan.reader";

    @Before
    public void init() throws Exception {
        mm = new Marmot();
        mDevice = mm.getUiDevice();
        PhoneName = mDevice.getProductName();
        width = mDevice.getDisplayWidth();
        height = mDevice.getDisplayHeight();
        if(PhoneName.equals("clover")){
            duokanPkg = "com.duokan.hdreader";
            duokanActivity = "com.duokan.hdreader/.DkReaderActivity";
        }

    }

    @Test
    public void DuoKan() {
        for (int i=0;i< DUOKAN_LOOP;i++){
            duokan();
        }
    }

    private void duokan(){
        mm.pressHome();
        mm.sleep(2000);

        try {
            //需要先 kill，防止已经在运行时获取不到控件
            mDevice.executeShellCommand("am force-stop " + duokanPkg);
            mm.sleep(5000);
            mDevice.executeShellCommand("am start -n " + duokanActivity);
            mm.sleep(5000);
            UiObject books = new UiObject(new UiSelector().text("书架"));
            books.click();
            mm.sleep(5000);
            UiObject book;
            if(PhoneName.equals("clover")) {
                book=new UiObject(new UiSelector().className("android.view.ViewGroup").instance(3));
            }
            else {
                book = new UiObject(new UiSelector().className("android.view.ViewGroup").instance(1));
            }
            book.click();
            //给加载的时间
            mm.sleep(20000);

            for (int i=0; i < 10; i++) {
                mDevice.swipe(width-20, height / 2, 0, height / 2, 20);
                mm.sleep(3000);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

    }

    @After
    public void clear() {
        mDevice.pressHome();
    }
}
