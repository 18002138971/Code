package com.miui.marmot;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

/**
 * Created by Yan on 2017/7/17.
 */

public class MessageActivity extends Activity {

    @Override
    public void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= 20) {

            if (!"com.miui.marmot.test". equals(Telephony.Sms.getDefaultSmsPackage(this))) {
                // set me as default sms application

                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, "com.miui.marmot.test");
               startActivity(intent);
            }
        }

    }

}
