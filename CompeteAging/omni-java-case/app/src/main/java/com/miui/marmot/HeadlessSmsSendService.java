package com.miui.marmot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Yan on 2017/7/17.
 */

public class HeadlessSmsSendService extends Service {
    public HeadlessSmsSendService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
