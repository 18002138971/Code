package com.miui.agingtesting.jingpin;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.miui.agingtesting.common.ATConfig.VIEWMESSAGE_LOOP;
import static com.miui.marmot.lib.Config.isHUAWEI;
import static com.miui.marmot.lib.Config.isOPPO;

/**
 * Created by tianxiao on 2018/3/2.
 */

public class Test_15_ViewMessage {
    private Marmot mm;
    private UiDevice mDevice;
    private String phoneName;
    private boolean isHuawei = false;
    private boolean isOppo = false;
    private String contentDesc = "短信";
    private String backBtnText = "返回短信列表";
    private String msgClassName = "android.view.ViewGroup";


    @Before
    public void init() throws Exception {
        mm = new Marmot();
        mDevice = mm.getUiDevice();
        phoneName = mDevice.getProductName();
        isHuawei = isHUAWEI(phoneName);
        isOppo = isOPPO(phoneName);
        if (isHuawei){
            contentDesc = "信息";
            msgClassName = "android.widget.RelativeLayout";
            backBtnText = "向上导航";
        }
        if(isOppo){
            contentDesc = "信息";
            backBtnText = "通知信息";

        }

    }

    @Test
    public void ViewMessage() {
        for(int i=0; i< VIEWMESSAGE_LOOP;i++){
            viewMessages();
        }

    }

    private void viewMessages(){
        mm.pressHome();
        mm.sleep(2000);
        UiObject ct;
        if(isOppo){
            ct = new UiObject(new UiSelector().text(contentDesc));
        }
        else {
            ct=new UiObject(new UiSelector().description(contentDesc));
        }
        try {
            ct.click();
            mm.sleep(2000);
            if(isHuawei || isOppo){
                //华为手机可能需要再点击 通知信息
                UiObject noticeInfo = new UiObject(new UiSelector().text("通知信息"));
                noticeInfo.click();
                mm.sleep(3000);
            }
            for(int i=1; i<5; i++) {
                viewOneMessage(i);
            }

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void viewOneMessage(int messageIndex) {
        UiObject msg;
        if(isOppo){
            messageIndex = messageIndex -1;
            msg = new UiObject(new UiSelector().resourceId("com.android.mms:id/subject").instance(messageIndex));
        }
        else {
            msg=new UiObject(new UiSelector().className(msgClassName).instance(messageIndex));
        }
        try {
            msg.click();
            mm.sleep(5000);
            //短信界面的返回按钮
            if(!isOppo){
            UiObject backBtn = new UiObject(new UiSelector().description(backBtnText));
            backBtn.click();
            }
            else{
                UiObject backBtn = new UiObject(new UiSelector().text(backBtnText));
                backBtn.click();
            }
            mm.sleep(2000);
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

}

    @After
    public void clear() {
        mDevice.pressHome();
    }
}
