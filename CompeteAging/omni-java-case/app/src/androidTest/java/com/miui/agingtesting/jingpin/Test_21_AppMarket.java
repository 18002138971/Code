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

import static com.miui.agingtesting.common.ATConfig.APP_INSTALL_NUM;
import static com.miui.marmot.lib.Config.isHUAWEI;
import static com.miui.marmot.lib.Config.isOPPO;
import static com.miui.marmot.lib.Config.isXIAOMI;

/**
 * Created by tianxiao on 2018/3/2.
 */

@RunWith(AndroidJUnit4.class)
public class Test_21_AppMarket {
    private Marmot mm;
    private UiDevice mDevice;
    private int height;
    private Method md;
    private String PhoneName;
    private String appMarketActivity = "com.xiaomi.market/.ui.MarketTabActivity";
    private String rankList = "榜单";
    private String appMarketPkg = "com.xiaomi.market";

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        md = new Method();
        mDevice = mm.getUiDevice();
        height = mDevice.getDisplayHeight();
        PhoneName=mDevice.getProductName();
        if (isOPPO(PhoneName))  {          //OPPO
            appMarketActivity = "com.oppo.market/.activity.MainActivity";
            appMarketPkg = "com.oppo.market";
        }
        else if(isHUAWEI(PhoneName)){      //华为
            appMarketActivity = "com.huawei.appmarket/.MainActivity";
            rankList = "排行";
            appMarketPkg = "com.huawei.appmarket";
        }
        else if(isXIAOMI(PhoneName))      //小米
            appMarketActivity = "com.xiaomi.market/.ui.MarketTabActivity";
        else{
            throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
        }
    }

    @Test
    public void appMarket() {
        for (int i=0;i< ATConfig.APPMARKET_LOOP;i++){
            appmarket();
        }

    }

    private void appmarket(){
        mDevice.pressHome();
        try {
            mDevice.executeShellCommand("am force-stop  " + appMarketPkg);
            mm.sleep(3000);
            mDevice.executeShellCommand("am start -n  " + appMarketActivity);
            mm.sleep(5000);
            UiObject rl;
            if("clover".equals(PhoneName)){
                rl = new UiObject(new UiSelector().text("排行"));
            }
            else if(!isOPPO(PhoneName)){
                rl = new UiObject(new UiSelector().text(rankList));
            }
            else {
                rl=new UiObject(new UiSelector().className("android.view.View").instance(4));
            }
            try {
                rl.click();
                mm.sleep(5000);
                for (int i=0; i<APP_INSTALL_NUM; i++){
                    UiObject app = null;
                    if(isHUAWEI(PhoneName)){
                        app = new UiObject(new UiSelector().description("安装"));
                    }
                    else{
                        app = new UiObject(new UiSelector().text("安装"));
                    }

                    int swipeTimes = 0; //用来结束循环，防止陷入死循环
                    while(app == null || (!app.exists()) || (!app.isEnabled())){
                        md.swipeUp();
                        swipeTimes ++;
                        mm.sleep(2000);
                        if(isHUAWEI(PhoneName)){
                            app = new UiObject(new UiSelector().description("安装"));
                        }
                        else{
                            app = new UiObject(new UiSelector().text("安装"));
                        }

                        if (swipeTimes>10)
                            break;
                    }
                    //部分机型 “安装” 按钮 似露非露的时候，容易误点，所以上滑一下比较稳妥
                    if(app!=null &&  app.exists() && (height - app.getBounds().bottom )< 350){
                        md.swipeUp();
                    }

                    mm.sleep(2000);
                    app.click();
                    mm.sleep(2000);
                }
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
