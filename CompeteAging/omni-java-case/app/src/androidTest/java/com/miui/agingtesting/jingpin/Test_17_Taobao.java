package com.miui.agingtesting.jingpin;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.Method;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.miui.agingtesting.common.ATConfig.TAOBAO_LOOP;

/**
 * Created by tianxiao on 2018/3/2.
 */

public class Test_17_Taobao {
    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private String taobaoWelActivity = "com.taobao.taobao/com.taobao.tao.welcome.Welcome";
    private String taobaoPkg = "com.taobao.taobao";
    private String searchText = "巧虎的魔法积木世界";
    private String searchTextResourceId = "com.taobao.taobao:id/home_searchedit";
    private String searchTextResourceIdInSearchPage = "com.taobao.taobao:id/searchEdit";
    private String searchBtnResourceId = "com.taobao.taobao:id/searchbtn";
    private String searchResultResourceId = "com.taobao.taobao:id/title";
    private String putInText = "加入购物车";
    private String shoppingCart = "购物车";
    private String selectAll = "全选";
    private String manageBtnResourceId = "com.taobao.taobao:id/button_manage";
    private String deleteBtnResourceId = "com.taobao.taobao:id/button_delete";
    private String confirmBtnResourceId = "com.taobao.taobao:id/ack_dialog_confirm_bnt_sure";


    @Before
    public void init() throws Exception {
        mm = new Marmot();
        mDevice = mm.getUiDevice();
        md = new Method();

    }

    @Test
    public void Taobao() {
        for(int i=0;i< TAOBAO_LOOP; i++)
        taobao();
    }

    private void taobao(){
        mm.pressHome();
        mm.sleep(2000);

        try {
            //需要先 kill，防止已经在运行时获取不到控件
            mDevice.executeShellCommand("am kill " + taobaoPkg);
            mm.sleep(2000);
            mDevice.executeShellCommand("am start -n " + taobaoWelActivity);
            mm.sleep(5000);
            //滑动
            md.swipeUpTimes(5);
            UiObject searchInput = new UiObject(new UiSelector().resourceId(searchTextResourceId));
            searchInput.click();
            mm.sleep(3000);
            UiObject searchInputInSearchPg = new UiObject(new UiSelector().resourceId(searchTextResourceIdInSearchPage));
            searchInputInSearchPg.setText(searchText);
            mm.sleep(2000);
            UiObject searchBtn = new UiObject(new UiSelector().resourceId(searchBtnResourceId));
            searchBtn.click();
            mm.sleep(5000);
            UiObject result = new UiObject(new UiSelector().resourceId(searchResultResourceId).textContains(searchText));
            result.click();
            mm.sleep(5000);
            //加入购物车
            UiObject putInBtn = new UiObject(new UiSelector().text(putInText));
            putInBtn.click();
            mm.sleep(3000);
            //到购物车界面，清空购物车
            UiObject shoppingCartBtn = new UiObject(new UiSelector().description(shoppingCart));
            shoppingCartBtn.click();
            mm.sleep(3000);
            //清空购物车
            UiObject selectAllBtn = new UiObject(new UiSelector().description(selectAll));
            selectAllBtn.click();
            mm.sleep(3000);
            //点击管理 or 编辑
            UiObject manageBtn = new UiObject(new UiSelector().resourceId(manageBtnResourceId));
            manageBtn.click();
            mm.sleep(3000);
            UiObject deleteBtn = new UiObject(new UiSelector().resourceId(deleteBtnResourceId));
            deleteBtn.click();
            //点击确定
            UiObject confirmBtn = new UiObject(new UiSelector().resourceId(confirmBtnResourceId));
            confirmBtn.click();

            mm.pressHome();


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
