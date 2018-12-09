package com.miui.agingtesting.jingpin;

import android.content.ContentUris;
import android.content.ContentValues;

import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.support.test.runner.AndroidJUnit4;
import com.miui.agingtesting.common.ATConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Random;
import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Aging Test 07 : contact.
 *
 * @author 闫东 yandong@xiaomi.com
 * @since 2017年7月19日 下午1:55:38
 */

@RunWith(AndroidJUnit4.class)
public class Test_08_Contact {

    @Test
    public void contact() {
        for(int i = 0; i < ATConfig.CONTACT_LOOP; i++)
            addContact(getRandomString(ATConfig.CONTACTNAMELENGTH),"55662322","123456@xiaomi.com");
    }

    //随机生成联系人姓名
    public static String getRandomString(int length) {
        String base1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String base2 = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuffer name = new StringBuffer();
        int number = random.nextInt(base1.length());
        name.append(base1.charAt(number));
        for (int i = 0; i < length-1; i++) {
            int num = random.nextInt(base2.length());
            name.append(base2.charAt(num));
        }
        return name.toString();
    }

    //添加联系人
    public void addContact(String name,String number,String email) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();
        Uri rawContactUri = getTargetContext().getContentResolver().insert(RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        values.put(Data.RAW_CONTACT_ID, rawContactId);// 内容类型
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);// 联系人名字
        values.put(StructuredName.GIVEN_NAME, name);// 向联系人URI添加联系人名字
        getTargetContext().getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();

        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE);
        values.put(Phone.NUMBER, number);
        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
        getTargetContext().getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();

        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);// 联系人的Email地址
        values.put(Email.DATA, email);// 电子邮件的类型
        values.put(Email.TYPE, Email.TYPE_WORK);// 向联系人Email URI添加Email数据
        getTargetContext().getContentResolver().insert(Data.CONTENT_URI, values);

    }


}
