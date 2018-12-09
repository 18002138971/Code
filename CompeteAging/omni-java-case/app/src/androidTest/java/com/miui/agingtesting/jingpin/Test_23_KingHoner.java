package com.miui.agingtesting.jingpin;

import android.os.RemoteException;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import com.miui.agingtesting.common.ATConfig;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Created by tianxiao on 2018/3/2.
 */

@RunWith(AndroidJUnit4.class)
public class Test_23_KingHoner {
    private Marmot mm;
    private UiDevice mDevice;
    private String kingHonerActivity = "com.tencent.tmgp.sgame/.SGameActivity";
    private String kingHonerPkg= "com.tencent.tmgp.sgame";

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        mDevice = mm.getUiDevice();

    }

    @Test
    public void kingHoner() {
        try {
            mDevice.executeShellCommand("am force-stop  " + kingHonerPkg );
        } catch (IOException e) {
            e.printStackTrace();
        }
        mm.sleep(5000);

        for (int i=0;i< ATConfig.KINGHONER_LOOP;i++) {
            mDevice.pressHome();
            try {
                mDevice.setOrientationNatural();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mm.sleep(3000);
            try {
                mDevice.executeShellCommand("am start -n  " + kingHonerActivity);
                mm.sleep(20000);

            } catch (IOException e) {
                e.printStackTrace();
                mm.pressBack(5);
            }
        }
    }



    @After
    public void clear() {

        mDevice.pressHome();

        try {
            mDevice.setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
