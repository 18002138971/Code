package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.ATConfig;
import com.miui.agingtesting.common.Method;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.miui.marmot.lib.Config.*;

/**
 * Aging Test 08: recorder.
 *
 * @author 闫东 yandong@xiaomi.com
 * @since 2017年7月20日 上午11：24：30
 */

@RunWith(AndroidJUnit4.class)
public class Test_09_Recorder {

    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private String recordActivity;
    private String PhoneName;
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
        if (isOPPO(PhoneName))
            recordActivity = "com.coloros.soundrecorder/oppo.multimedia.soundrecorder.RecorderActivity";
        else if(isHUAWEI(PhoneName))
            recordActivity = "com.android.soundrecorder/.SoundRecorder";
        else if(isXIAOMI(PhoneName) && !PhoneName.equals("SKR-A0"))
            recordActivity = "com.android.soundrecorder/.SoundRecorder";
        else if(PhoneName.equals("SKR-A0")) //E30
            recordActivity = "com.blackshark.recorder/.MainActivity";
        else if(isVIVO(PhoneName))
            recordActivity = "com.android.bbksoundrecorder/.SoundRecorder";
        else{
            throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
        }
    }

    @Test
    public void news() {
        for(int i = 0; i < ATConfig.RECORDER_LOOP; i++) {
            lanuchrecorder();
        }
    }

    public void lanuchrecorder(){
        try{
            mDevice.pressHome();
            mDevice.executeShellCommand("am start -n  " + recordActivity);
            mm.sleep(ATConfig.LAUNCH_TIME);  //登陆时间
            md.closeAgreeAndGoOnDialog();//第一次打开去除是否同意对话框

            //OPPO
            if(isOPPO(PhoneName))
            {
                mDevice.findObject(new UiSelector().resourceId("com.coloros.soundrecorder:id/middle_control")).click(); //点击开始
                mm.sleep(ATConfig.RECORDER_TIME);    //开始录音
                mDevice.findObject(new UiSelector().resourceId("com.coloros.soundrecorder:id/middle_control")).clickAndWaitForNewWindow(4000); //点击暂停
                mDevice.findObject(new UiSelector().resourceId("com.coloros.soundrecorder:id/left_control")).clickAndWaitForNewWindow(4000); //点击完成
                mDevice.findObject(new UiSelector().resourceId("com.coloros.soundrecorder:id/right_control")).clickAndWaitForNewWindow(4000); //点击录音记录
                mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);   //等待
                mm.click(width / 2, hight / 7);
                mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);   //等待

            }
            //华为
            else if(isHUAWEI(PhoneName))
            {
                UiObject begin;
                if(PhoneName.equals("ALP-AL00"))
                    begin = mDevice.findObject(new UiSelector().descriptionContains("开始"));
                else
                    begin = mDevice.findObject(new UiSelector().textContains("开始"));
                begin.click(); //点击开始
                mm.sleep(ATConfig.RECORDER_TIME);    //开始录音
                mDevice.findObject(new UiSelector().textContains("完成")).clickAndWaitForNewWindow(2000); //点击完成
                mDevice.findObject(new UiSelector().className("android.widget.Button").textContains("保存")).clickAndWaitForNewWindow(2000);//保存录音
                mDevice.findObject(new UiSelector().textContains("录音文件")).clickAndWaitForNewWindow(2000);//保存录音
                mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);   //等待
                mm.click(width / 2, hight / 5);
                mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);   //等待

            }
            //小米
            else if(isXIAOMI(PhoneName) && !PhoneName.equals("SKR-A0"))
            {
                mDevice.findObject(new UiSelector().textContains("录音")).click(); //点击开始
                mm.sleep(ATConfig.RECORDER_TIME);    //开始录音
                mDevice.findObject(new UiSelector().textContains("完成")).clickAndWaitForNewWindow(3000); //点击完成
                mDevice.findObject(new UiSelector().textContains("确定")).clickAndWaitForNewWindow(3000);//保存录音
                mDevice.findObject(new UiSelector().textContains("录音记录")).clickAndWaitForNewWindow(3000);//保存录音
                mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);   //等待
                mm.click(width / 2, hight / 4);
                mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);   //等待

            }

            else if(PhoneName.equals("SKR-A0")) //E30
            {
                if(mDevice.findObject(new UiSelector().text("允许开启“免打扰”的应用"))!=null)
                    mm.pressBack();
                mDevice.findObject(new UiSelector().resourceId("com.blackshark.recorder:id/btn_action")).click(); //点击开始
                mm.sleep(ATConfig.RECORDER_TIME);    //开始录音
                mDevice.findObject(new UiSelector().resourceId("com.blackshark.recorder:id/btn_action")).click(); //点击暂停
                mDevice.findObject(new UiSelector().resourceId("com.blackshark.recorder:id/btn_finish")).click(); //点击完成

            }

            else if(isVIVO(PhoneName))
            {
                mDevice.findObject(new UiSelector().resourceId("com.android.bbksoundrecorder:id/play_control")).click(); //点击开始
                mm.sleep(ATConfig.RECORDER_TIME);    //开始录音
                mDevice.findObject(new UiSelector().resourceId("com.android.bbksoundrecorder:id/done_control")).clickAndWaitForNewWindow(); //点击完成
                mDevice.findObject(new UiSelector().text("保存")).clickAndWaitForNewWindow();//保存录音

            }

            mm.pressBack(3);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @After
    public void clear() {
        mDevice.pressHome();
    }
}
