package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.ATConfig;
import com.miui.agingtesting.common.Method;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.miui.marmot.lib.Config.*;

/**
 * Aging Test 04 : market.
 *
 * @author 于淼 yumiao@xiaomi.com
 * @since 2017年5月18日 下午1:15:10
 * @modify 闫东 yandong@xiaomi.com 2017年7月26日  下午1：30：4
 *
 */

@RunWith(AndroidJUnit4.class)
public class Test_04_Market {
    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private String PhoneName;
    private String marketActivity;
    private int width;
    private int hight;

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        md = new Method();
        mDevice = mm.getUiDevice();
        width = mDevice.getDisplayWidth();
        hight = mDevice.getDisplayHeight();
        PhoneName=mDevice.getProductName();
        if(isOPPO(PhoneName)){
            marketActivity = "com.oppo.market/.activity.MainActivity";
        }
        else if(isHUAWEI(PhoneName)){
            marketActivity = "com.huawei.appmarket/com.huawei.appmarket.MainActivity";
        }
        else if(isXIAOMI(PhoneName)){
            marketActivity = "com.xiaomi.market/com.xiaomi.market.ui.MarketTabActivity";
        }
        else if(isVIVO(PhoneName)){
            marketActivity = "com.bbk.appstore/.ui.AppStore";
        }
        else{
            throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
        }
    }

    @Test
    public void market() {
        for(int i = 0; i < ATConfig.MARKET_LOOP; i++) {
            launchMarketAndClick(); //安装app
        }

    }

    public void launchMarketAndClick() {
        try {
            mDevice.pressHome();
            mDevice.executeShellCommand("am start -n  " + marketActivity);
            mm.sleep(ATConfig.LAUNCH_TIME);
            md.closeAgreeAndGoOnDialog();//第一次打开去除是否同意对话框

            if (isOPPO(PhoneName)){
                try{
                    UiObject select =mDevice.findObject(new UiSelector().textStartsWith("必玩"));//下载游戏列表中的app
                    select.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    UiScrollable installScroll=new UiScrollable(new UiSelector().className("android.widget.TextView"));//查找可以安装的app
                    UiObject install =mDevice.findObject(new UiSelector().text("安装"));
                    installScroll.scrollIntoView(install);
                    while(!install.exists()){
                        mm.move(width / 2, hight * 3 / 4, width / 2, hight / 2, 10);
                    }
                    install.click();
                    mm.sleep(ATConfig.INSTALL_TIME);//等待安装
                }catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }

            else if(isHUAWEI(PhoneName)){
                mm.sleep(ATConfig.LAUNCH_TIME);
                try {
                    UiObject select1 = mDevice.findObject(new UiSelector().className("android.widget.TextView").text("排行"));
                    select1.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    UiObject select2 = mDevice.findObject(new UiSelector().className("android.widget.TextView").text("更多"));
                    select2.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    UiScrollable installScroll = new UiScrollable(new UiSelector().resourceId("com.huawei.appmarket:id/downbtn"));
                    UiObject install = mDevice.findObject(new UiSelector().description("安装"));
                    installScroll.scrollIntoView(install);
                    while (!install.exists()) {
                        mm.move(width / 2, hight * 3 / 4, width / 2, hight / 2, 10);
                    }
                    install.click();
                    mm.sleep(ATConfig.INSTALL_TIME);//等待安装
                    mm.pressBack(2);
                    md.closeAgreeAndGoOnDialog();
                }catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                    }
            }

           else if(isXIAOMI(PhoneName)){
                try{
                    UiObject homepage =mDevice.findObject(new UiSelector().textStartsWith("首页"));
                    if(!homepage.exists()){
                        mm.pressBack();
                    }

                    UiScrollable installScroll=new UiScrollable(new UiSelector().className("android.view.View"));
                    UiObject install;
                    System.out.println(PhoneName);
                    if(PhoneName.equals("sagit") || PhoneName.equals("SKR-A0"))
                        install=mDevice.findObject(new UiSelector().className("android.view.View").descriptionContains("安装"));
                    else
                        install=mDevice.findObject(new UiSelector().className("android.view.View").textContains("安装"));
                    installScroll.scrollIntoView(install);
                    while(!install.exists()){
                        mm.move(width / 2, hight * 3 / 4, width / 2, hight / 4, 10);
                    }
                    install.click();

                    mm.sleep(ATConfig.INSTALL_TIME);//等待安装
                }catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }

            else if(isVIVO(PhoneName)){
                try{
                    UiObject homepage =mDevice.findObject(new UiSelector().resourceId("com.bbk.appstore:id/download_entry"));
                    if(!homepage.exists()){
                        mm.pressBack();
                    }
                    UiObject install=new UiScrollable(new UiSelector().text("下载"));
                    while(!install.exists()){
                        mm.move(width / 2, hight * 3 / 4, width / 2, hight / 4, 10);
                    }
                    install.click();

                    mm.sleep(ATConfig.INSTALL_TIME);//等待安装
                }catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }

            mm.pressBack(3);

        } catch (IOException e) {
            e.printStackTrace();
            mm.pressBack(5);
        }
    }

    @After
    public void clear() {
        mDevice.pressHome();
    }
}
