package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.ATConfig;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.miui.marmot.lib.Config.*;

/**
 * Aging Test 11 : DeletePhoto.
 *
 * @author 闫东 yandong@xiaomi.com
 * @since 2017年9月25日 下午16：45：22
 *
 * OPPO、VIVO需要定期手动删除照片
 */

@RunWith(AndroidJUnit4.class)
public class Test_11_DeletePhoto {

        private Marmot mm;
        private UiDevice mDevice;
        private String PhoneName;


        @Before
        public void init() throws Exception{
            mm = new Marmot();
            mDevice = mm.getUiDevice();
            PhoneName=mDevice.getProductName();

        }

        @Test
        public void DeletePhoto() {
            try {
                if(!isOPPO(PhoneName) && !isVIVO(PhoneName))
                    mDevice.executeShellCommand("rm -r /sdcard/DCIM/");

            } catch (Exception e) {
                e.printStackTrace();
                mm.pressBack(5);
            }
        }

        @After
        public void clear() {
            mDevice.pressHome();
        }

}
