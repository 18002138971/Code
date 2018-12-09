package com.miui.agingtesting.jingpin;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.miui.agingtesting.common.ATConfig.CONTACTSEARCH_LOOP;
import static com.miui.marmot.lib.Config.isHUAWEI;
import static com.miui.marmot.lib.Config.isOPPO;

/**
 * Created by tianxiao on 2018/3/2.
 */

public class Test_13_ContactSearch {
    private Marmot mm;
    private UiDevice mDevice;
    private String phoneName ;
    private boolean isHuawei = false;
    private boolean isOppo = false;
    private String contentDesc = "电话";
    private String contentText = "拨号";
    private String contentPeople = "联系人";
    private String search = "搜索";

    @Before
    public void init() throws Exception {
        mm = new Marmot();
        mDevice = mm.getUiDevice();
        phoneName = mDevice.getProductName();
        isHuawei = isHUAWEI(phoneName);
        isOppo = isOPPO(phoneName);

    }
    @Test
    public void ContactSearch() {
        for(int i=0;i<CONTACTSEARCH_LOOP ;i++){
            contactSerach();
        }

    }

    private void contactSerach(){
        mm.pressHome();
        mm.sleep(2000);
        try {
            if(isHuawei){
                //点击联系人到联系人界面
                UiObject ctp = new UiObject(new UiSelector().description(contentPeople));
                ctp.click();
                mm.sleep(2000);
                //找到搜索输入框
                UiObject searchInput = new UiObject(new UiSelector().textContains(search));
                searchInput.click();
                //xiaomi 点击输入框后到了另一个界面
//                UiObject input = new UiObject(new UiSelector().resourceId("android:id/input"));
                searchInput.setText("10086");
                mm.pressBack();
                mm.sleep(2000);
                UiObject result = new UiObject(new UiSelector().text("10086"));
                result.click();
                mm.sleep(5000);
                UiObject resultHuawei = new UiObject(new UiSelector().className("android.view.ViewGroup"));
                resultHuawei.click();
                mm.sleep(3000);
                //拨打电话
                UiObject callHuawei = new UiObject(new UiSelector().resourceId("com.android.contacts:id/primary_action_call_button_image"));
                callHuawei.click();
                mm.sleep(5000);

            }
            else if(isOppo){
                UiObject ct = new UiObject(new UiSelector().text(contentText));
                ct.click();
                mm.sleep(2000);
                //点击联系人到联系人界面
                UiObject ctp = new UiObject(new UiSelector().description(contentPeople));
                ctp.click();
                mm.sleep(2000);
                //找到搜索输入框
                UiObject searchInput = new UiObject(new UiSelector().textContains(search));
                searchInput.click();
                //xiaomi 点击输入框后到了另一个界面
                UiObject input = new UiObject(new UiSelector().resourceId("android:id/search_src_text"));
                input.setText("10086");
                mm.sleep(2000);
                UiObject result = new UiObject(new UiSelector().className("android.widget.TextView").textContains("10086"));
                result.click();
                mm.sleep(5000);
                UiObject call = new UiObject(new UiSelector().resourceId("com.android.contacts:id/call_number"));
                call.click();
                mm.sleep(3000);
            }
            else{
                UiObject ct = new UiObject(new UiSelector().description(contentDesc));
                ct.click();
                mm.sleep(2000);
                //点击联系人到联系人界面
                UiObject ctp = new UiObject(new UiSelector().text(contentPeople));
                ctp.click();
                mm.sleep(2000);
                //找到搜索输入框
                UiObject searchInput = new UiObject(new UiSelector().textContains(search));
                searchInput.click();
                //xiaomi 点击输入框后到了另一个界面
                UiObject input = new UiObject(new UiSelector().resourceId("android:id/input"));
                input.setText("10086");
                mm.sleep(2000);
                UiObject result = new UiObject(new UiSelector().text("10086"));
                result.click();
                mm.sleep(5000);
                UiObject call = new UiObject(new UiSelector().resourceId("com.android.contacts:id/primary_action_view"));
                call.click();
                mm.sleep(3000);
            }




        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

    }

    @After
    public void clear() {
        mDevice.pressHome();
    }
}
