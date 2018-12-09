package com.miui.marmot.lib;

import android.util.Log;

/**
 * Created by Dell on 2017/3/20.
 */

public class Logger {
    /**
     * Record common info,such as basic operations
     */
    public static void i(String msg) {
        Log.i(Config.LOG_TAG, msg);
    }

    /**
     * Record info with error
     */
    public static void e(String errorMsg) {
        //
    }
}
