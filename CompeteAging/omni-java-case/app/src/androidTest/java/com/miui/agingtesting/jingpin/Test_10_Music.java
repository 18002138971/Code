package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
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
 * Aging Test 09 : music.
 *
 * @author 闫东 yandong@xiaomi.com
 * @since 2017年7月20日 下午15：54：30
 *
 * VIVO不跑这条case，无法拿到停止音乐按钮
 */

@RunWith(AndroidJUnit4.class)
public class Test_10_Music {
    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private String musicActivity;
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
            musicActivity = " com.oppo.music/.MainListActivity";
        else if(isHUAWEI(PhoneName))
            musicActivity = "com.android.mediacenter/.PageActivity";
        else if(isXIAOMI(PhoneName))
            musicActivity = "com.miui.player/.ui.MusicBrowserActivity";
        else if(isVIVO(PhoneName)){

        }
        else{
            throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
        }
    }

    @Test
    public void news() {
        if(!isVIVO(PhoneName)){
            for(int i = 0; i < ATConfig.MUSIC_LOOP; i++) {
                lanuchmusic();
            }
        }

    }

    public void lanuchmusic() {
        try {
            mDevice.pressHome();
            mDevice.executeShellCommand("am start -n  " + musicActivity);
            mm.sleep(ATConfig.LAUNCH_TIME);  //登陆时间
            md.closeAgreeAndGoOnDialog();//第一次打开去除是否同意对话框
            md.updatalaterAndGoOn();

            if(isOPPO(PhoneName)){
                mDevice.findObject(new UiSelector().resourceId("com.oppo.music:id/control_bar_pause")).click(); //播放
                mm.sleep(ATConfig.MUSIC_TIME);   //等待
                mDevice.findObject(new UiSelector().resourceId("com.oppo.music:id/control_bar_pause")).click(); //暂停
                mm.pressBack(2);
            }

            else if (isHUAWEI(PhoneName)) {
                mDevice.findObject(new UiSelector().resourceId("com.android.mediacenter:id/song_play")).click(); //播放
                mm.sleep(ATConfig.MUSIC_TIME);   //等待
                mDevice.findObject(new UiSelector().resourceId("com.android.mediacenter:id/song_play")).click(); //暂停
                mm.pressBack(2);

            }
            //C8音乐与其他不一样
            else if(mDevice.getProductName().equals("jason")||mDevice.getProductName().equals("riva")){
                mm.click(width  / 2, hight* 9 / 10); //点击播放音乐
                mm.click(width / 2, hight* 9 / 10);//点击开始
                mm.sleep(ATConfig.MUSIC_TIME);   //等待
                mm.click(width / 2, hight* 9 / 10); //点击暂停
                mm.pressBack(3);

            }

            else if(isXIAOMI(PhoneName)&&!(mDevice.getProductName().equals("jason")))
                {
                mm.click(width / 2, hight* 19 / 20); //点击音乐
                mm.click(width / 2, hight* 9 / 10);//点击开始
                mm.sleep(ATConfig.MUSIC_TIME);   //等待
                mm.click(width / 2, hight* 9 / 10);;//点击暂停
                mm.pressBack(3);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @After
    public void clear() {
        mDevice.pressHome();
    }

}
