package com.miui.agingtesting.jingpin;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import com.miui.agingtesting.common.SmsWriteOpUtil;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.miui.agingtesting.common.ATConfig.APP_UNINSTALL_INTERVAL;

/**
 * Created by tianxiao on 2018/3/2.
 */

@RunWith(AndroidJUnit4.class)
public class Test_99_ClearEnv {
    private Marmot mm;
    private UiDevice mDevice;
    private Context context= getTargetContext();

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        mDevice = mm.getUiDevice();

    }

    @Test
    public void clearEnv(){
        UninstallApp();
        clearMsg();
        clearContact();

    }

    private void clearMsg(){

        if (!SmsWriteOpUtil.isWriteEnabled(context)){
            SmsWriteOpUtil.setWriteEnabled(context, true);
            System.out.println("enabled");
        }
            try {
                ContentResolver CR = context.getContentResolver();
                int result = CR.delete(Uri.parse("content://sms"), null, null);

            } catch (Exception e) {
                System.out.println("exception " + e);
            }

    }

    private void clearContact(){
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        context.getContentResolver().delete(uri,"_id!=-1", null);

    }

    private void UninstallApp() {
        PackageManager packageManager= context.getPackageManager();
        long currentTs = System.currentTimeMillis();
        List<PackageInfo> list=packageManager.getInstalledPackages(0);
        for (PackageInfo p : list){
            long ts=p.firstInstallTime;

            if((currentTs - ts) < APP_UNINSTALL_INTERVAL){
                String pkg = p.packageName;
                try {
                    if((!pkg.equals("com.miui.marmot")) && (!(pkg.equals("com.miui.marmot.test")))){
                        mDevice.executeShellCommand("pm uninstall  " + pkg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @After
    public void clear() {
        mDevice.pressHome();
    }

}
