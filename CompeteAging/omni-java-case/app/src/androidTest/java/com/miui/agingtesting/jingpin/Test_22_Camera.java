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

import static android.view.KeyEvent.KEYCODE_CAMERA;
import static com.miui.marmot.lib.Config.isHUAWEI;
import static com.miui.marmot.lib.Config.isOPPO;
import static com.miui.marmot.lib.Config.isXIAOMI;

/**
 * Created by tianxiao on 2018/3/2.
 */

@RunWith(AndroidJUnit4.class)
public class Test_22_Camera {
    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private String PhoneName;
    private String cameraActivity = "com.android.camera/.Camera";
    private String photos = "最近照片或视频";

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        md = new Method();
        mDevice = mm.getUiDevice();
        PhoneName=mDevice.getProductName();
        if(isOPPO(PhoneName)){
            cameraActivity = "com.oppo.camera/.Camera";
            photos = "最新照片";
        }
        else if(isHUAWEI(PhoneName)){
            cameraActivity = "com.huawei.camera/com.huawei.camera";
            photos = "最近拍摄照片";
        }
        else if(isXIAOMI(PhoneName))  {
            cameraActivity = "com.android.camera/.Camera";
        }
        else{
            throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
        }

    }

    @Test
    public void Camera() {
       for (int i=0;i<ATConfig.CAMERAUSE_LOOP; i++){
           camera();
       }
    }

    private void camera(){
        mDevice.pressHome();
        try {
            mDevice.executeShellCommand("am start -n  " + cameraActivity);
            mm.sleep(3000);
            md.closeAgreeAndGoOnDialog();//第一次打开去除是否同意对话框
            for (int i=0;i<20; i++)
            {
                mm.click(mDevice.getDisplayWidth() / 2, mDevice.getDisplayHeight() / 2);
                mDevice.executeShellCommand("input keyevent  " +  KEYCODE_CAMERA);
                mm.sleep(3000);
            }
            UiObject pts = new UiObject(new UiSelector().description(photos));
            pts.click();
            mm.sleep(3000);

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
