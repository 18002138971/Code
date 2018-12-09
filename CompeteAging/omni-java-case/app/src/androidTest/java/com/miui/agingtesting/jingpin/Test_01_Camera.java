package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import com.miui.agingtesting.common.ATConfig;
import com.miui.agingtesting.common.Method;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.view.KeyEvent.KEYCODE_CAMERA;
import static com.miui.marmot.lib.Config.*;

/**
 * Aging Test 01 : camera.
 *
 * @author 于淼 yumiao@xiaomi.com
 * @since 2017年5月18日 下午1:15:10
 */

@RunWith(AndroidJUnit4.class)
public class Test_01_Camera {
    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private String PhoneName;
    private String cameraActivity;

    @Before
    public void init() throws Exception {
        mm = new Marmot();
        md = new Method();
        mDevice = mm.getUiDevice();
        PhoneName=mDevice.getProductName();
        if(isOPPO(PhoneName)){
            cameraActivity = "com.oppo.camera/.Camera";
        }
        else if(isHUAWEI(PhoneName)){
            cameraActivity = "com.huawei.camera/com.huawei.camera";
        }
        else if(isXIAOMI(PhoneName))  {
            cameraActivity = "com.android.camera/.Camera";
        }
        else if(isVIVO(PhoneName)){
            cameraActivity = "com.android.camera/.CameraActivity";
        }
        else{
                throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
        }

    }

    @Test
    public void camera() {
        for(int i = 0; i < ATConfig.CAMERA_LOOP; i++) {
            mDevice.pressHome();
            launchCamera();
        }
    }

    public void launchCamera() {
        try {
            mDevice.executeShellCommand("am start -n  " + cameraActivity);
            mm.sleep(ATConfig.LAUNCH_TIME);
            md.closeAgreeAndGoOnDialog();//第一次打开去除是否同意对话框

            mm.click(mDevice.getDisplayWidth() / 2, mDevice.getDisplayHeight() / 2);
            mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
            mDevice.executeShellCommand("input keyevent  " +  KEYCODE_CAMERA);
            mm.sleep(ATConfig.SIMPLE_OPERATION_TIME * 3);
            mDevice.executeShellCommand("input keyevent  " +  KEYCODE_CAMERA);
            mm.sleep(ATConfig.SIMPLE_OPERATION_TIME * 3);
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
