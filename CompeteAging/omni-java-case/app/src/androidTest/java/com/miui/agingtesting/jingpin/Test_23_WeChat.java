package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.ATConfig;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.miui.marmot.lib.Config.isHUAWEI;

/**
 * Created by tianxiao on 2018/3/2.
 */

@RunWith(AndroidJUnit4.class)
public class Test_23_WeChat {
    private Marmot mm;
    private UiDevice mDevice;
    private String PhoneName;
    private String weChatActivity = "com.tencent.mm/.ui.LauncherUI";
    private String weChatPkg = "com.tencent.mm";
    private String contactText = "通讯录";
    private String friendsName = "测试";
    private String sendMsgText = "发消息";
    private String msgInputResourceId = "com.tencent.mm:id/aab";
    private String sendBtnText = "发送";

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        mDevice = mm.getUiDevice();
        PhoneName=mDevice.getProductName();
        if(isHUAWEI(PhoneName)){
            msgInputResourceId = "com.tencent.mm:id/aac";
        }

    }

    @Test
    public void weChat() {
        for (int i=0;i< ATConfig.WECHAT_LOOP; i++){
            wechat();
        }
    }

    private void wechat(){
        mDevice.pressHome();
        try {
            mDevice.executeShellCommand("am force-stop  " + weChatPkg);
            mm.sleep(10000);
            mDevice.executeShellCommand("am start -n  " + weChatActivity);
            mm.sleep(5000);
            //到 通讯录 页面
            UiObject contact = new UiObject(new UiSelector().text(contactText));
            contact.click();
            mm.sleep(3000);
//            UiObject friend = new UiObject(new UiSelector().resourceId(friendsResourceId).text(friendsName));
            UiObject friend = new UiObject(new UiSelector().text(friendsName));

            friend.click();
            mm.sleep(3000);
            UiObject sendMsgBtn = new UiObject(new UiSelector().text(sendMsgText));
            sendMsgBtn.click();
            mm.sleep(3000);
            //发消息页面
            UiObject msgInput = new UiObject(new UiSelector().resourceId(msgInputResourceId));
            msgInput.click();
            mm.sleep(3000);
            for (int i=0;i < 10; i++)
            {
                msgInput.setText("测试信息" + String.valueOf(i) );
                UiObject sendBtn = new UiObject(new UiSelector().text(sendBtnText));
                sendBtn.click();
                mm.sleep(2000);
            }

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
