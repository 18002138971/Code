package com.miui.agingtesting.jingpin;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.ATConfig;
import com.miui.marmot.lib.Marmot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.miui.marmot.lib.Config.*;

/**
 * Aging Test 06 : message.
 *
 * @author 闫东 yandong@xiaomi.com
 * @since 2017年7月19日 下午 1:55:38
 */

@RunWith(AndroidJUnit4.class)
public class Test_07_Message {

        private Marmot mm;
        private  UiDevice mDevice;
        private Context message= getTargetContext();
        private String PhoneName;

        @Before
        //判断手机型号
        public void init() throws Exception{
                mm = new Marmot();
                mDevice = mm.getUiDevice();
                PhoneName=mDevice.getProductName();
                if(isOPPO(PhoneName)){
                        setSmsDefaultApplication();
                        clickchange();
                }
                else if(isHUAWEI(PhoneName)) {
                        setSmsDefaultApplication();
                        clickchange();
                }
                else if(isVIVO(PhoneName)) {
                        setSmsDefaultApplication();
                        clickchange();
                }
                else if(isXIAOMI(PhoneName)){

                }
                else{
                        throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
                }

        }

        //修改默认短信应用
        public  void setSmsDefaultApplication() {
//                mm.pressHome();
//                mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                if (Build.VERSION.SDK_INT >= 20) {
                        if (!"com.miui.marmot".equals(Telephony.Sms.getDefaultSmsPackage(message))) {
                                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, "com.miui.marmot");
                                message.startActivity(intent);

                        }

                }
        }

        //点击同意修改默认程序
        public void clickchange(){
                try{
                        if( isOPPO(PhoneName)){
                                UiObject change =mDevice.findObject(new UiSelector().resourceId("android:id/button2"));
                                if(change.exists()){
                                        change.click();
                                        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                                }
                        }
                        else if(isHUAWEI(PhoneName)){
                                UiObject change =mDevice.findObject(new UiSelector().resourceId("android:id/button1"));
                                if(change.exists()){
                                        change.click();
                                        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                                }
                        }
                        else if(isVIVO(PhoneName)){
                                UiObject change =mDevice.findObject(new UiSelector().text("更换"));
                                UiObject admin =mDevice.findObject(new UiSelector().text("允许"));
                                if(change.exists()){
                                        change.click();
                                        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                                }
                                if(admin.exists()){
                                        admin.click();
                                        mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                                }
                        }

                }catch (Exception e) {
                        e.printStackTrace();
                }

        }

        @Test
        //短信操作
        public void message() {

                for(int i = 0; i < ATConfig.MESSAGE_LOOP; i++) {
                        saveMessage(message,ATConfig.PHONENUMBER[0],getRandomString(ATConfig.MESSAGELENGTH),ATConfig.state[1]);
                        saveMessage(message,ATConfig.PHONENUMBER[1],getRandomString(ATConfig.MESSAGELENGTH),ATConfig.state[1]);
                }

        }

        public static String getRandomString(int length) {
                String base = "abcdefghijklmnopqrstuvwxyz0123456789";
                Random random = new Random();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < length; i++) {
                        int number = random.nextInt(base.length());
                        sb.append(base.charAt(number));
                }
                return sb.toString();
        }

        //数据库写入短信
        public  void saveMessage(Context context,  String phoneNo, String message,boolean isMT) {
                try {
                        if (phoneNo.length()>0 && message.length()>0) {
                                ContentValues values = new ContentValues();
                                values.put("address", phoneNo);
                                values.put("body", message);
                                if (isMT) {
                                        context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                                } else {
                                        context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
                                }
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

}
