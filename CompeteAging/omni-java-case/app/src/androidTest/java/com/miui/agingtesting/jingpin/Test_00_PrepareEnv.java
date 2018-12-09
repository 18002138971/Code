package com.miui.agingtesting.jingpin;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
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
import static com.miui.marmot.lib.Config.isHUAWEI;
import static com.miui.marmot.lib.Config.isOPPO;
import static com.miui.marmot.lib.Config.isXIAOMI;

/**
 * Test Enviroment prepare:
 *      1: fill 500 contacts, contain 10086
 *      2: fill 2000 messages, top 4 is bank, recharge, verification code, operator
 *
 *      //TODO 将常用的方法提取到 common 中，减少重复代码
 * Created by tianxiao on 2018/2/28.
 */
@RunWith(AndroidJUnit4.class)
public class Test_00_PrepareEnv {

    private Marmot mm;
    private UiDevice mDevice;
    private Context message= getTargetContext();
    private String PhoneName;
    private String emailServerList[] = {"@163.com", "@qq.com", "@126.com", "@sina.com", "gmail.com"};
    private static String bankNum = "95555";
    private static String bankMsg = "您账户1234于02月24日15：00入账工资，人民币10000.53元。[招商银行]";
    private static String chargeNum = "95533";
    private static String chargeMsg = "您的建设银行贷记卡1234于03月01日10：00，充值人民币100.00元，订单号123456789.[建设银行]";
    private static String validateNum = "106906164295118";
    private static String validateMsg = "【京东支付】验证码：379475，您正使用尾号8908进行京东支付，注意保密哦！";
    private static String operatorNum = "10086";
    private static String operatorMsg = "查询余额服务：您好，您的总账户余额为113.41元。感谢您的使用。快速了解手机上网及流量套餐办理方式，可发送“我要上网”至10086了解详情。【中国移动】";

    @Before
    //判断手机型号
    public void init() throws Exception{
        mm = new Marmot();
        mDevice = mm.getUiDevice();
        PhoneName=mDevice.getProductName();
        if(isOPPO(PhoneName) || isHUAWEI(PhoneName)){
            setSmsDefaultApplication();
            clickchange();
        }
        else if(isXIAOMI(PhoneName)){
            setSmsDefaultApplication();
            clickchange();
        }
        else{
            throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
        }

        // TODO 这里是否需要对手机中已有的联系人、短信进行清空， 防止长时间的测试带来的手机存储空间不足

    }

    //修改默认短信应用
    public  void setSmsDefaultApplication() {

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

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    //短信操作
    public void prepareEnv() {
        //填充短信
        Random random = new Random();
        for(int i=0; i < ATConfig.MESSAGE_COUNT; i++) {
            saveMessage(message,getRandomContactNum(),getRandomString(random.nextInt(200)), ATConfig.state[1] );
        }
        //填充 top 的几条特殊短信内容
        saveMessage(message, bankNum, bankMsg, ATConfig.state[1] );
        saveMessage(message, chargeNum, chargeMsg, ATConfig.state[1] );
        saveMessage(message, validateNum, validateMsg, ATConfig.state[1] );
        saveMessage(message, operatorNum, operatorMsg, ATConfig.state[1] );

        //填充手机联系人
        for(int i = 0; i < ATConfig.CONTACT_COUNT; i++) {
            addContact(getRandomString(random.nextInt(4)+2),getRandomContactNum(),getRandomString(random.nextInt(6)+5) + emailServerList[random.nextInt(emailServerList.length)]);
        }
        //填充 10086 为联系人，联系方式 10086
        addContact("10086","10086","");

    }

    //生成随机的联系方式
    private static String getRandomContactNum() {
        String base = "0123456789";
        int baseLen = base.length();
        Random random = new Random();
        StringBuffer cn = new StringBuffer();
        //避免出现首位为0的情况
        cn.append(base.charAt(random.nextInt(9)+1));
        for (int i=1; i < 11; i++){
            int number = random.nextInt(baseLen);
            cn.append(base.charAt(number));
        }
        return  cn.toString();
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

    //添加联系人
    public void addContact(String name,String number,String email) {
        try{
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();
        Uri rawContactUri = getTargetContext().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);// 内容类型
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);// 联系人名字
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);// 向联系人URI添加联系人名字
        getTargetContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getTargetContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);// 联系人的Email地址
        values.put(ContactsContract.CommonDataKinds.Email.DATA, email);// 电子邮件的类型
        values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);// 向联系人Email URI添加Email数据
        getTargetContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

    }
    catch (Exception e){
        System.out.println(e.toString());
        }
    }
}
