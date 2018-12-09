package com.miui.agingtesting.jingpin;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.miui.agingtesting.common.ATConfig.DIALING_LOOP;
import static com.miui.marmot.lib.Config.isHUAWEI;
import static com.miui.marmot.lib.Config.isOPPO;

/**
 * Created by tianxiao on 2018/3/2.
 */

public class Test_14_Dialing {
    private Marmot mm;
    private UiDevice mDevice;
    private String phoneName ;
    private boolean isHuawei = false;
    private boolean isOppo = false;
    private String contentDesc = "电话";
    private String contentText = "拨号";
    private String dialing = "通话";
    private String resourceIdOneXiaomi = "com.android.contacts:id/one";
    private String resourceIdZeroXiaomi = "com.android.contacts:id/zero";
    private String resourceIdEightXiaomi = "com.android.contacts:id/eight";
    private String resourceIdSixXiaomi = "com.android.contacts:id/six";
    private String resourceIdNumsHuawei = "com.android.contacts:id/contacts_dialpad_key_number";
    private String dialResourceId = "com.android.contacts:id/sim_dial_btn";
    private UiObject one;
    private UiObject zero;
    private UiObject eight;
    private UiObject six;
    private String callBtnDesc = "拨打电话";




    @Before
    public void init() throws Exception {
        mm = new Marmot();
        mDevice = mm.getUiDevice();
        phoneName = mDevice.getProductName();
        isHuawei = isHUAWEI(phoneName);
        isOppo = isOPPO(phoneName);
        if(isHuawei){
            contentDesc = "拨号";
            dialing = "拨号";
            callBtnDesc = "拨打";
        }

    }
    @Test
    public void Dialing() {
        for (int i=0;i< DIALING_LOOP; i++){
            dialing();
        }
    }

    private void dialing(){
        mm.pressHome();
        mm.sleep(2000);
        UiObject ct;
        if(!isOppo)
        {
             ct = new UiObject(new UiSelector().description(contentDesc));
        }
        else{
             ct = new UiObject(new UiSelector().text(contentText));
        }
        try {
            ct.click();
            mm.sleep(2000);
            if(!isOppo) {//点击联系人到通话界面
                UiObject ctp=new UiObject(new UiSelector().text(dialing));
                ctp.click();
                mm.sleep(2000);
            }
            else{
                UiObject dl = new UiObject(new UiSelector().description(contentText));
                dl.click();
                mm.sleep(2000);
            }
            //拨号10086
            if(isHuawei){
                one = new UiObject(new UiSelector().resourceId(resourceIdNumsHuawei).text("1"));
                zero = new UiObject(new UiSelector().resourceId(resourceIdNumsHuawei).text("0"));
                eight = new UiObject(new UiSelector().resourceId(resourceIdNumsHuawei).text("8"));
                six = new UiObject(new UiSelector().resourceId(resourceIdNumsHuawei).text("6"));
            }
            else {
                one = new UiObject(new UiSelector().resourceId(resourceIdOneXiaomi));
                zero = new UiObject(new UiSelector().resourceId(resourceIdZeroXiaomi));
                eight = new UiObject(new UiSelector().resourceId(resourceIdEightXiaomi));
                six = new UiObject(new UiSelector().resourceId(resourceIdSixXiaomi));
            }
            one.click();
            zero.click();
            zero.click();
            eight.click();
            six.click();
            if(!isOppo) {
                UiObject dialing=new UiObject(new UiSelector().description(callBtnDesc));
                dialing.click();
            }
            else{
                UiObject dialing = new UiObject(new UiSelector().resourceId(dialResourceId));
                dialing.click();
            }

            mm.sleep(60000);

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @After
    public void clear() {
        mDevice.pressHome();
    }
}
