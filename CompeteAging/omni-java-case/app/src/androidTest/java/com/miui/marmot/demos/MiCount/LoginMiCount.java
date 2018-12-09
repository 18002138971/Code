package com.miui.marmot.demos.MiCount;

import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.miui.marmot.lib.Checker;
import com.miui.marmot.lib.Logger;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * 登陆小米账号
 * PREDICTIONS: 手机必须保证未登陆小米账号，首次使用手机需要预先同意条款声明
 * STEPS：进入设置->小米账号->输入账号密码登陆
 * EXPECTATIONS：成功登陆小米账号
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月25日 上午9:30:00
 */

@RunWith(JUnit4.class)
public class LoginMiCount {
    private Marmot mm = null;
    private Checker cc = null;
    private String productName = null;

    @Before
    public void initEnvironment(){
        mm = new Marmot();
        cc = new Checker();

        Logger.i("Prepare testing environment.");
        mm.pressHome(2);
    }

    @Test
    public void LoginMiCount() throws UiObjectNotFoundException{
        //采用从设置进入登陆小米账号的方式
        Logger.i("Step1: Enter into Settings.");
        mm.launchApp("com.android.settings");

        Logger.i("Step2: Enter into the log in page.");
        UiScrollable listview = new UiScrollable(new UiSelector().scrollable(true));
        listview.setAsVerticalList();
        listview.getChildByText(new UiSelector().className("android.widget.TextView"), "小米帐号").clickAndWaitForNewWindow();

        Logger.i("Step3: Check if log in or not.");
        //红米和小米的账号登陆成功显示界面不一致
        if(mm.getUiDevice().findObject(new UiSelector().className("android.widget.TextView").resourceId("miui:id/action_bar_title").text("我的小米")).exists()
                || mm.getUiDevice().findObject(new UiSelector().className("android.widget.TextView").resourceId("miui:id/action_bar_title").text("小米帐号")).exists()){
            Logger.i("Already log in MiCount.");
            mm.pressBack();
        }

        else{
            Logger.i("Step4: Input account and password.");
            if(mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.EditText").resourceId("com.xiaomi.account:id/et_account_name")).getText() != null){
                mm.getUiDevice().findObject(new UiSelector()
                        .className("android.widget.EditText").resourceId("com.xiaomi.account:id/et_account_password")).setText("19960505tian");
            }
            else{
                mm.getUiDevice().findObject(new UiSelector()
                        .className("android.widget.EditText").resourceId("com.xiaomi.account:id/et_account_name")).setText("15510179066");
                mm.getUiDevice().findObject(new UiSelector()
                        .className("android.widget.EditText").resourceId("com.xiaomi.account:id/et_account_password")).setText("19960505tian");
            }
            mm.sleep(1000);
            mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.Button").resourceId("com.xiaomi.account:id/btn_login")).clickAndWaitForNewWindow();

            Logger.i("Step5: Continue.");
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.LinearLayout").index(2))
                    .getChild(new UiSelector().className("android.widget.LinearLayout").resourceId("android:id/widget_frame")).click();
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").text("继续")).clickAndWaitForNewWindow();

            Logger.i("Step6: Check if log in successfully.");
            productName = mm.getUiDevice().getProductName();
            if(productName.equals("hydrogen") || productName.equals("kenzo") || productName.equals("meri")){
                listview.getChildByText(new UiSelector().className("android.widget.TextView"), "小米帐号").clickAndWaitForNewWindow();
                cc.assertTrue("Successfully log in.", mm.getUiDevice().findObject(new UiSelector()
                        .className("android.widget.TextView").resourceId("miui.system:id/vip_id_name")).getText()
                        .equals("1261649042")); //我的小米账号
            }
            else{
                listview.getChildByText(new UiSelector().className("android.widget.TextView"), "小米帐号").clickAndWaitForNewWindow();
                cc.assertTrue("Successfully log in.", mm.getUiDevice().findObject(new UiSelector()
                        .className("android.widget.TextView").resourceId("com.xiaomi.account:id/user_name")).getText()
                        .equals("1261649042")); //我的小米账号
            }

        }


    }

    @After
    public void clearEnvironment() {
        Logger.i("clear testing environment.");
        mm.pressBack(2);
        mm.pressHome(2);
    }
}
