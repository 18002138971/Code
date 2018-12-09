package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.ATConfig;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.miui.marmot.lib.Config.*;

/**
 * Aging Test 10 : AppUninstall.
 *
 * @author 闫东 yandong@xiaomi.com
 * @since 2017年7月26日  下午1：30：4
 *
 * E30需要手动卸载app
 *
 */

@RunWith(AndroidJUnit4.class)
public class Test_05_AppUninstall {
    private Marmot mm;
    private UiDevice mDevice;
    private String PhoneName;
    private String marketActivity;
    private int width;
    private int hight;

    @Before
    public void init() throws Exception{
        mm = new Marmot();
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
        if(!PhoneName.equals("SKR-A0") ){  //E30不能通过应用商店卸载app
            for(int i = 0; i < ATConfig.MARKET_LOOP; i++) {
                UninstallApp();  //卸载app
            }
        }
    }

    public void UninstallApp(){
        try {
            mDevice.pressHome();
            mDevice.executeShellCommand("am start -n  " + marketActivity);
            mm.sleep(ATConfig.LAUNCH_TIME);

            if (isOPPO(PhoneName)){
                try{
                    UiObject select1 = mDevice.findObject(new UiSelector().className("android.widget.TextView").text("我的"));
                    select1.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    UiObject select2 = mDevice.findObject(new UiSelector().className("android.widget.TextView").text("应用卸载"));
                    select2.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);

                    UiObject select3=mDevice.findObject(new UiSelector().resourceId("com.oppo.market:id/tv_sort"));
                    select3.click();
                    UiObject select4=mDevice.findObject(new UiSelector().resourceId("android:id/text1").text("应用大小"));
                    select4.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);

                    UiScrollable installScroll=new UiScrollable(new UiSelector().resourceId("com.oppo.market:id/tv_name"));//查找可以安装的app
                    UiObject app=mDevice.findObject(new UiSelector().resourceId("com.oppo.market:id/tv_name"));
                    installScroll.scrollIntoView(app);
                    if(app.exists()&&!app.getText().equals("com.miui.marmot.test")&&!app.getText().equals("marmot")){
                        mm.click(width * 7 / 8, hight / 6);//应用
                        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                        UiObject select = mDevice.findObject(new UiSelector().className("android.widget.Button").text("卸载应用"));//确认卸载
                        select.click();
                        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    }

                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    mm.pressBack();
                    UiObject select5 =mDevice.findObject(new UiSelector().resourceId("com.oppo.market:id/title").textStartsWith("首页"));
                    select5.click();

                }catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }

            else if(isHUAWEI(PhoneName)){
                try {
                    UiObject select1 = mDevice.findObject(new UiSelector().className("android.widget.TextView").text("管理"));
                    select1.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    UiObject select2 = mDevice.findObject(new UiSelector().className("android.widget.TextView").text("安装管理"));
                    select2.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);

                    UiObject select3 = mDevice.findObject(new UiSelector().className("android.widget.TextView").text("批量卸载"));
                    select3.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    UiScrollable installScroll=new UiScrollable(new UiSelector().resourceId("com.huawei.appmarket:id/localpackage_item_name"));
                    UiObject app=mDevice.findObject(new UiSelector().resourceId("com.huawei.appmarket:id/localpackage_item_name"));
                    installScroll.scrollIntoView(app);
                    if(app.exists()){
                        if(app.getText().equals("com.miui.marmot.test")||app.getText().equals("marmot")){
                            mm.move(width / 2, hight * 3 / 5, width / 2, hight * 2 / 5, 5);

                        }
                        if(!app.getText().equals("com.miui.marmot.test")&&!app.getText().equals("marmot")){
                            mm.click(width / 2, hight / 6);
                            UiObject select4 = mDevice.findObject(new UiSelector().className("android.widget.TextView").text("卸载"));//点击卸载
                            select4.clickAndWaitForNewWindow(3000);
                            UiObject select5 = mDevice.findObject(new UiSelector().className("android.widget.Button").text("卸载"));//确定卸载
                            select5.click();
                            mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                        }
                    }

                    mm.pressBack();
                    UiObject select6 = mDevice.findObject(new UiSelector().className("android.widget.TextView").text("推荐"));//返回到主页面
                    select6.click();

                }catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }

            else if(isXIAOMI(PhoneName) ){
                try{
                    UiObject select1 = mDevice.findObject(new UiSelector().textContains("我的"));
                    select1.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    UiObject select2;
                    if(PhoneName.equals("polaris"))
                        select2 = mDevice.findObject(new UiSelector().className("android.view.View").textContains("应用卸载"));
                    else
                        select2 = mDevice.findObject(new UiSelector().className("android.view.View").description("应用卸载"));
                    select2.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    UiObject select3=mDevice.findObject(new UiSelector().resourceId("com.xiaomi.market:id/title"));
                    if(!select3.getText().equals("使用频率")){
                        select3.click();
                        UiObject select4=mDevice.findObject(new UiSelector().resourceId("com.xiaomi.market:id/spinner_text").text("使用频率"));
                        select4.click();
                        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    }
                    UiScrollable installScroll=new UiScrollable(new UiSelector().resourceId("com.xiaomi.market:id/name"));//查找可以安装的app
                    UiObject app=mDevice.findObject(new UiSelector().resourceId("com.xiaomi.market:id/name"));
                    installScroll.scrollIntoView(app);
                    if(app.exists()&&(app.getText().equals("com.miui.marmot.test")||app.getText().equals("marmot")))
                        mm.pressBack(3);
                    else{
                        app.click();
                        UiObject select5 = mDevice.findObject(new UiSelector().className("android.widget.Button").textContains("一键卸载"));//点击卸载
                        select5.clickAndWaitForNewWindow(3000);
                        UiObject select6 = mDevice.findObject(new UiSelector().className("android.widget.Button").textContains("选中应用"));//确定卸载
                        select6.click();

                        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    }

                }catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }

            else if(isVIVO(PhoneName) ){
                try{
                    UiObject select1 = mDevice.findObject(new UiSelector().text("管理"));
                    select1.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    UiObject select2 = mDevice.findObject(new UiSelector().text("应用卸载"));
                    select2.click();
                    mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);

                    UiObject app=new UiScrollable(new UiSelector().resourceId("com.bbk.appstore:id/installed_item"));
                    String appname = app.getChild(new UiSelector().resourceId("com.bbk.appstore:id/installed_title")).getText();
                    UiObject uninstall = app.getChild(new UiSelector().text("卸载"));
                    if(app.exists()&&(appname.equals("com.miui.marmot.test")||appname.equals("marmot")))
                        mm.pressBack(3);
                    else{
                        uninstall.click();
                        mDevice.findObject(new UiSelector().textContains("卸载应用")).clickAndWaitForNewWindow(3000);
                        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                    }

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

