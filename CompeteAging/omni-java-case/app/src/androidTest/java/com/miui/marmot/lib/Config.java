package com.miui.marmot.lib;

import android.os.Bundle;
import android.support.test.InstrumentationRegistry;

import com.miui.agingtesting.common.ATConfig;

/**
 * null
 *
 * @author yumiao
 * @version 3.0.0
 * @since API Level 21
 */

public final class Config {
    public static final String LOG_TAG = "MARMOT";
    public static final String ROOT_PATH = "/sdcard/MIUI/autotest/"; //Auto test workspace.

    protected static String CASE_PATH = "/sdcard/"; //Current case folder to save screenshots and logs.
    protected static Boolean SAVE_LOGCAT = true;
    protected static Boolean SAVE_SCREENSHOT = true;

    public static long LAUNCHAPP_TIMEOUT = 5000;
    public static long SCREENDHOT_TIMEOUT = 3000;
    public static long STEP_WAITTIME = 500;
    public static int SWIPE_STEPS = 10;

    public static void getCommandLineArgumentsAndInitSwitchers() {
        String commandlineSaveLogKey = "log";
        String commandLineSaveScreenshotKey = "screenshot";
        Bundle arguments = InstrumentationRegistry.getArguments();

        if (arguments.containsKey(commandlineSaveLogKey)) {
            boolean saveLogSwitcher = arguments.getBoolean(commandlineSaveLogKey);
            if (saveLogSwitcher == true) {
                SAVE_LOGCAT = true;
            }
        }

        if (arguments.containsKey(commandLineSaveScreenshotKey)) {
            boolean saveScreenshotSwitcher = arguments.getBoolean(commandLineSaveScreenshotKey);
            if (saveScreenshotSwitcher == true) {
                SAVE_SCREENSHOT = true;
            }
        }

        Logger.i("Config : \n" +
                "    SAVE_LOGCAT = " + Config.SAVE_LOGCAT + "\n" +
                "    SAVE_SCREENSHOT = " + Config.SAVE_SCREENSHOT);
    }

    public static void setSaveScreenshot(Boolean switcher) {
        SAVE_LOGCAT = switcher;
    }

    public static String getCaseWorkPath() {
        return CASE_PATH;
    }

    public static boolean isOPPO (String PhoneName){
        return ATConfig.OPPO.contains(PhoneName);
    }
    public static boolean isHUAWEI (String PhoneName){
        return ATConfig.HUAWEI.contains(PhoneName);
    }
    public static boolean isXIAOMI (String PhoneName){
        return ATConfig.XIAOMI.contains(PhoneName);
    }
    public static boolean isVIVO (String PhoneName){
        return ATConfig.VIVO.contains(PhoneName);
    }



}
