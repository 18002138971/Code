package com.miui.marmot.lib;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import junit.framework.Assert;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Checker is the set of assert methods. Messages are displayed after an assert.
 * You can use this class to assert object exist, image exist or log the cases' result.
 *
 * @author yumiao
 * @version 3.0.0
 * @since API Level 18
 */

public class Checker {

    /**
     * Use harmcrest Assert for self-defined Assertion, without screenshot.
     * Expect support mutilple methods. such as is\has\allof..
     *
     * @param reason
     * @param actual
     * @param matcher
     * @param <T>
     */
    public <T> void assertThat(String reason, T actual, Matcher<? super T> matcher) {
        Logger.i("[reason]" + reason);
        MatcherAssert.assertThat(reason, actual, matcher);
    }

    /**
     * Asserts that a condition is true. If it isn't it take a screenshot of
     * current window and throws an AssertionFailedError with the given message.
     *
     * @param message
     * @param condition
     */
    public void assertTrue(String message, boolean condition) {
        if (condition) {
            Logger.i("[PASS]" + message);
        } else {
            Logger.i("[FAIL]" + message);
            saveScreenshotForFail();
        }
        Assert.assertTrue(condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it take a screenshot of
     * current window and throws an AssertionFailedError with the given message.
     *
     * @param message
     * @param condition
     */
    public void assertFalse(String message, boolean condition) {
        if (!condition) {
            Logger.i("[PASS]" + message);
        } else {
            Logger.i("[FAIL]" + message);
            saveScreenshotForFail();
        }
        Assert.assertFalse(condition);
    }

    /**
     * Assert the UI element with specified selector criteria exist. If result is false,
     * take a screenshot of current window.
     *
     * @param selector the selector criteria
     */
    public void assertUiObjectExist(BySelector selector) {
        UiObject2 uiObject = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                .findObject(selector);
        if (uiObject != null) {
            Logger.i("[PASS]" + "UiObject exists.");
        } else {
            Logger.i("[FAIL]" + "UiObject does not exist.");
            saveScreenshotForFail();
            Assert.assertTrue(false);
        }
    }

    /**
     * Assert the UI element with specified selector criteria not exist. If result is true,
     * take a screenshot of current window.
     *
     * @param selector the selector criteria
     */
    public void assertUiObjectNotExist(BySelector selector) {
        UiObject2 uiObject = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                .findObject(selector);
        if (uiObject == null) {
            Logger.i("[PASS]" + "UiObject does not exist.");
        } else {
            Logger.i("[FAIL]" + "UiObject exists.");
            saveScreenshotForFail();
            Assert.assertTrue(false);
        }
    }

    /**
     * Assert the view with specified selector criteria exist. If result is false,
     * take a screenshot of current window.
     *
     * @param object the UI object
     */
    public void assertUiObjectExist(UiObject object) {
        if (object.exists()) {
            Logger.i("[PASS]" + "UiObject exists.");
        } else {
            Logger.i("[FAIL]" + "UiObject does not exist.");
            saveScreenshotForFail();
            Assert.assertTrue(false);
        }
    }

    /**
     * Assert the view with specified selector criteria not exist. If result is true,
     * take a screenshot of current window.
     *
     * @param object the UI object
     */
    public void assertUiObjectNotExist(UiObject object) {
        if (object.exists() == false) {
            Logger.i("[PASS]" + "UiObject does not exist.");
        } else {
            Logger.i("[FAIL]" + "UiObject exists.");
            saveScreenshotForFail();
            Assert.assertTrue(false);
        }
    }

    @Deprecated
    public void assertTextExist(String text){
        assertUiObjectExist(By.text(text));
    }

    @Deprecated
    public void assertTextNotExist(String text){
        assertUiObjectNotExist(By.text(text));
    }

    private void saveScreenshotForFail() {
        if (Config.SAVE_SCREENSHOT == Boolean.TRUE) {
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                    .takeScreenshot(new File(getFailedImgName()));
            SystemClock.sleep(Config.SCREENDHOT_TIMEOUT);
        }
    }

    private String getFailedImgName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String imageName = "failed_" + formatter.format(curDate).replaceAll("-|\\s|:", "") + ".png";

        return Config.CASE_PATH + imageName;
    }

}