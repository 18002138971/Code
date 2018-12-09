package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import com.miui.agingtesting.common.ATConfig;
import com.miui.agingtesting.common.Method;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Aging Test 02 : browser.
 *
 * @author 于淼 yumiao@xiaomi.com
 * @since 2017年5月18日 下午1:15:10
 */

@RunWith(AndroidJUnit4.class)
public class Test_02_Browser {
    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private int width;
    private int hight;

    @Before
    public void init() {
        mm = new Marmot();
        md = new Method();
        mDevice = mm.getUiDevice();
        width = mDevice.getDisplayWidth();
        hight = mDevice.getDisplayHeight();
    }

    @Test
    public void browser() {
        String[] addressList = new String[]{
                "http://www.dangdang.com",
                "http://www.taobao.com",
                "http://www.jd.com",
                "http://www.mi.com",
                "http://www.qunar.com",
                "https://www.ganji.com",
                "https://www.sina.cn",
                "http://www.hao123.com"
        };

        for(int i = 0; i < ATConfig.BROWSER_LOOP; i++) {
            for (String address : addressList) {
                launch(address);
            }
        }
    }

    public void launch(String address) {
        try {
            mDevice.pressHome();
            mDevice.executeShellCommand("am start -a android.intent.action.VIEW -d " + address);//启动浏览器打开一个网站
            mm.sleep(ATConfig.LAUNCH_TIME);
            md.chosebrowser();
            md.chooseOpenMethod();
            md.closeAgreeAndGoOnDialog();//第一次打开去除是否同意对话框

            mm.move(width / 2, hight * 2 / 3, width / 2, hight / 3, 10);
            mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
            mm.move(width / 2, hight / 3, width / 2, hight * 2 / 3, 10);
            mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
            mm.click(width / 2 + 10, hight / 2);
            mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
            mm.click(width / 2 + 10, hight / 2);
            mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void clear() {
        mDevice.pressHome();
    }
}
