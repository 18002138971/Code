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

import static com.miui.marmot.lib.Config.*;

/**
 * Aging Test 03 : gallery.
 *
 * @author 于淼 yumiao@xiaomi.com
 * @since 2017年5月18日 下午1:15:10
 */

@RunWith(AndroidJUnit4.class)
public class Test_03_Gallery {
    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private String PhoneName;
    private String galleryActivity;
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
            galleryActivity = "com.coloros.gallery3d/com.oppo.gallery3d.app.Gallery";
        }
        else if(isHUAWEI(PhoneName)){
            galleryActivity = "com.android.gallery3d/com.huawei.gallery.app.GalleryMain";
        }
        else if(isXIAOMI(PhoneName)) {
            galleryActivity = "com.miui.gallery/com.miui.gallery.activity.HomePageActivity";
        }
        else if(isVIVO(PhoneName)) {
            galleryActivity = "com.vivo.gallery/com.android.gallery3d.vivo.GalleryTabActivity";
        }
        else{
            throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
        }
    }

    @Test
    public void launchGallery()throws UiObjectNotFoundException, IOException {
        for(int i = 0; i < ATConfig.GALLERY_LOOP; i++) {
            gallery();
        }

    }

    public void gallery() throws UiObjectNotFoundException, IOException {

        mDevice.pressHome();
        mDevice.executeShellCommand("am start -n  " + galleryActivity);
        mm.sleep(ATConfig.LAUNCH_TIME);
        md.closeAgreeAndGoOnDialog();//第一次打开去除是否同意对话框
        if(isHUAWEI(PhoneName)){
            UiObject jump=mDevice.findObject(new UiSelector().textContains("跳过"));
            if(jump.exists())
                jump.click();
        }

        mm.move(width / 2, hight * 2 / 3, width / 2, hight / 3, 10);
        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
        mm.move(width / 2, hight * 2 / 3, width / 2, hight / 3, 10);
        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
        mm.click(width / 2 + 10, hight / 2);
        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
        mm.click(width / 2 - 10, hight / 2);
        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
        mm.pressBack(2);

        mm.pressBack(5);
        }

    @After
    public void clear() {
        mDevice.pressHome();
    }
}
