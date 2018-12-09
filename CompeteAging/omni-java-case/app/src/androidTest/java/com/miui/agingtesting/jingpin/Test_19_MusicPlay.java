package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.ATConfig;
import com.miui.agingtesting.common.Method;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.miui.marmot.lib.Config.isHUAWEI;
import static com.miui.marmot.lib.Config.isOPPO;
import static com.miui.marmot.lib.Config.isXIAOMI;

/**
 * @author tianxiao
 *
 */

@RunWith(AndroidJUnit4.class)
public class Test_19_MusicPlay {
    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private String musicActivity;
    private String PhoneName;
    private int width;
    private int hight;
    private String musicPkg = "com.miui.player";

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        md = new Method();
        mDevice = mm.getUiDevice();
        width = mDevice.getDisplayWidth();
        hight = mDevice.getDisplayHeight();
        PhoneName=mDevice.getProductName();
        if (isOPPO(PhoneName)){          //OPPO
            musicActivity = " com.oppo.music/.MainListActivity";
            musicPkg = "com.oppo.music";
        }
        else if(isHUAWEI(PhoneName)) {     //华为
            musicActivity = "com.android.mediacenter/.PageActivity";
            musicPkg = "com.android.mediacenter";
        }
        else if(PhoneName.equals("clover")){
            musicActivity = "com.miui.player/.pad.ui.MusicMainActivity";
            musicPkg = "com.miui.player";
        }
        else if(isXIAOMI(PhoneName)) {   //小米
            musicActivity = "com.miui.player/.ui.MusicBrowserActivity";
            musicPkg = "com.miui.player";
        }
        else{
            throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
        }
    }

    @Test
    public void music() {
        for(int i = 0; i < ATConfig.MUSICPLAY_LOOP; i++) {
            lanuchmusic();
            mm.sleep(2000);
            exitMusic();
        }
    }

    public void lanuchmusic() {
        try {
            mDevice.pressHome();
            mDevice.executeShellCommand("am start -n  " + musicActivity);
            mm.sleep(ATConfig.LAUNCH_TIME);  //登陆时间
            md.closeAgreeAndGoOnDialog();//第一次打开去除是否同意对话框
            md.updatalaterAndGoOn();

            //OPPO
            if(isOPPO(PhoneName)){
                mDevice.findObject(new UiSelector().resourceId("com.oppo.music:id/control_bar_pause")).click(); //播放
            }
            //华为
            else if (isHUAWEI(PhoneName)) {
                mDevice.findObject(new UiSelector().resourceId("com.android.mediacenter:id/song_play")).click(); //播放
            }
            //C8音乐与其他不一样
            else if(mDevice.getProductName().equals("jason")||mDevice.getProductName().equals("riva")){
                mm.click(width  / 2, hight* 9 / 10); //点击播放音乐
                mm.click(width / 2, hight* 9 / 10);//点击开始
            }
            //小米
            else if(PhoneName.equals("clover")){
                mDevice.findObject(new UiSelector().resourceId("com.miui.player:id/pause")).click();
            }
            else if(isXIAOMI(PhoneName)&&!(mDevice.getProductName().equals("jason")))
                {
                mm.click(width / 2, hight* 19 / 20); //点击音乐
                mm.click(width / 2, hight* 9 / 10);//点击开始

            }
            mDevice.pressHome();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exitMusic(){
        try {
            //再次打开
            mDevice.executeShellCommand("am start -n  " + musicActivity);
            //OPPO
            if(isOPPO(PhoneName)){
                mm.pressBack(2);
            }
            //华为
            else if (isHUAWEI(PhoneName)) {
                mm.pressBack(2);
            }
            //C8音乐与其他不一样
            else if(mDevice.getProductName().equals("jason")||mDevice.getProductName().equals("riva")){
                mm.pressBack(3);

            }
            //小米
            else if(isXIAOMI(PhoneName)&&!(mDevice.getProductName().equals("jason")))
            {
                mm.pressBack(3);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @After
    public void clear() {
        mDevice.pressHome();
    }

}
