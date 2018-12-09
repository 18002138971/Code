package com.miui.marmot.lib;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import static android.support.test.InstrumentationRegistry.getContext;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.Tracer;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.support.test.uiautomator.UiObject;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Marmot provides access to state information about the device.
 * You can also use this class to simulate user actions on the device,
 * such as pressing the hard key, click UI elements, record information.
 *
 * @author yumiao
 * @version 3.0.0
 * @since API Level 21
 */

public class Marmot {
    protected UiDevice mDevice;
//    private UiDevice mDevice = null;
    private Instrumentation mInstrumentation = null;

    public Marmot() {
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mDevice = UiDevice.getInstance(mInstrumentation);

        Config.getCommandLineArgumentsAndInitSwitchers();

        if (Config.SAVE_LOGCAT || Config.SAVE_SCREENSHOT) {
            Config.CASE_PATH = Config.ROOT_PATH + getCaller() + "_" + getTimeStamp() + "/";
            File caseFolder = new File(Config.CASE_PATH);
            caseFolder.mkdirs();
        }
    }

    /**
     * return the UiDevice instance.
     *
     * @return UiDevice device
     */
    public UiDevice getUiDevice() {
        return mDevice;
    }

    /**
     * return the UiAutomation instance.
     *
     * @return UiAutomation uiAutomation
     */
    public UiAutomation getUiAutomation() {
        return mInstrumentation.getUiAutomation();
    }

    /**
     * Start the activity with specified launch-able class.
     *
     * @param pkgName launch-able package name
     * @param activityName launch-able activity name
     */
    public void launchApp(String pkgName, String activityName) {
        Intent intent = new Intent();
        intent.setClassName(pkgName, activityName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        mDevice.wait(Until.hasObject(By.pkg(pkgName).depth(0)), Config.LAUNCHAPP_TIMEOUT);
    }

    /**
     * Start the APP with specified package name.
     *
     * @param pkg package name
     */
    public void launchApp(String pkg) {
        Context context = getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkg);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        mDevice.wait(Until.hasObject(By.pkg(pkg).depth(0)), Config.LAUNCHAPP_TIMEOUT);
    }

    @Deprecated
    public void launchActivity(String launchableclass) {
        Intent intent = new Intent();
        String pkgName = launchableclass.split("/")[0];
        String atyName = launchableclass.split("/")[1];

        intent.setClassName(pkgName, atyName.startsWith(".") ? (pkgName + atyName) : atyName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        //mDevice.wait(Until.hasObject(By.pkg(pkgName).depth(0)), Config.LAUNCHAPP_TIMEOUT);
        sleep(3000);
    }

    /**
     * Start the activity with specified launch-able class.
     *
     * @param pkgName launch-able package name
     * @param activityName launch-able activity name
     */
    public void launchAppByAm(String pkgName, String activityName) {
        try {
            mDevice.executeShellCommand("am start -n " + pkgName + "/" + activityName);
        } catch (IOException e) {
            Logger.i("Launch App failed! " + pkgName + "/" + activityName);
            e.printStackTrace();
        }
        mDevice.wait(Until.hasObject(By.pkg(pkgName).depth(0)), Config.LAUNCHAPP_TIMEOUT);
    }


    /**
     * Perform a click at arbitrary coordinates specified by the user.
     *
     * @param x coordinate
     * @param y coordinate
     */
    public void click(int x, int y) {
        mDevice.click(x, y);
        sleep(Config.STEP_WAITTIME);
    }

    /**
     * Continuously Perform a click at arbitrary coordinates specified by the user.
     *
     * @param x coordinate
     * @param y coordinate
     * @param times the numbers of click
     */
    public void click(int x, int y, int times) {
        for (int iterator = 0; iterator < times; iterator++) {
            mDevice.click(x, y);
        }
    }

    /**
     * Continuously Perform a click at specified coordinates and time interval.
     *
     * @param x coordinate
     * @param y coordinate
     * @param times the numbers of click
     * @param sleepSeconds the time interval
     */
    public void click(int x, int y, int times, double sleepSeconds) {
        for (int iterator = 0; iterator < times; iterator++) {
            mDevice.click(x, y);
            sleep(Config.STEP_WAITTIME);
        }
    }

    /**
     * Performs a drag from one coordinate to another coordinate.
     * You can control the smoothness and speed of the swipe by specifying the number of steps.
     * Each step execution is throttled to 5 milliseconds per step,
     * so for a 100 steps, the swipe will take around 0.5 seconds to complete.
     *
     * @param startX X-axis value for the starting coordinate
     * @param startY Y-axis value for the starting coordinate
     * @param endX X-axis value for the ending coordinate
     * @param endY Y-axis value for the ending coordinate
     * @param steps is the number of steps for the swipe action
     * @return true if drag is performed, false if the operation fails or the coordinates are invalid
     */
    public boolean drag(int startX, int startY, int endX, int endY, int steps) {
        return mDevice.drag(startX, startY, endX, endY, steps);
    }

    @Deprecated
    public void move(Direction direction) {
        int rightBottomX = getDisplayWidth() - 5;
        int rightBottomY = getDisplayHeight() - 5;
        int centerX = (int)(getDisplayWidth() / 2);
        int centerY = (int)(getDisplayHeight() / 2);

        switch(direction){
            case UP:
                mDevice.swipe(centerX, rightBottomY, centerX, 5, 10);
                break;
            case DOWN:
                mDevice.swipe(centerX, 5, centerX, rightBottomY, 10);
                break;
            case LEFT:
                mDevice.swipe(rightBottomX, centerY, 5, centerY, 10);
                break;
            case RIGHT:
                mDevice.swipe(5, centerY, rightBottomX, centerY, 10);
                break;
            default:
                break;
        }
    }

    /**
     * Perform a long click at arbitrary coordinates specified by the user.
     *
     * @param x coordinate
     * @param y coordinate
     */
    public void longClick(int x, int y) {
        mDevice.drag(x, y, x, y, Config.SWIPE_STEPS);
    }

    /**
     * Performs a long click on the object which match the selector criteria.
     *
     * @param bySelector the selector criteria
     */
    public void longClick(BySelector bySelector) {
        Rect bounds = getUiObject(bySelector).getVisibleBounds();
        mDevice.drag(bounds.centerX(), bounds.centerY(), bounds.centerX(), bounds.centerY(), Config.SWIPE_STEPS);
    }

    /**
     * Performs a swipe from one coordinate to another coordinate.
     * You can control the smoothness and speed of the swipe by specifying the number of steps.
     * Each step execution is throttled to 5 milliseconds per step,
     * so for a 100 steps, the swipe will take around 0.5 seconds to complete.
     *
     * @param startX X-axis value for the starting coordinate
     * @param startY Y-axis value for the starting coordinate
     * @param endX X-axis value for the ending coordinate
     * @param endY Y-axis value for the ending coordinate
     * @param steps is the number of steps for the swipe action
     * @return true if swipe is performed, false if the operation fails or the coordinates are invalid
     */
    public boolean move(int startX, int startY, int endX, int endY, int steps) {
        return mDevice.swipe(startX, startY, endX, endY, steps);
    }

    /**
     * Simulates a short press on the HOME button.
     */
    public void pressHome() {
        mDevice.pressHome();
        sleep(Config.STEP_WAITTIME);
    }

    /**
     * Continuously simulates a short press on the HOME button.
     *
     * @param times the number of press
     */
    public void pressHome(int times) {
        int i = 0;
        while (i++ < times) {
            pressHome();
        }
    }

    /**
     * Simulates a short press on the BACK button.
     */
    public void pressBack() {
        mDevice.pressBack();
        sleep(Config.STEP_WAITTIME);
    }

    /**
     * Continuously simulates a short press on the BACK button.
     *
     * @param times times the number of press
     */
    public void pressBack(int times) {
        int i = 0;
        while (i++ < times) {
            pressBack();
        }
    }

    /**
     * Simulates a short press on the MENU button.
     */
    public void pressMenu() {
        mDevice.pressMenu();
        sleep(Config.STEP_WAITTIME);
    }

    /**
     * Continuously simulates a short press on the MENU button.
     *
     * @param times the number of press
     */
    public void pressMenu(int times) {
        int i = 0;

        while (i++ < times) {
            pressMenu();
        }

    }

    /**
     * Simulates a short press on the DELETE button.
     */
    public void pressDelete() {
        mDevice.pressDelete();
        sleep(Config.STEP_WAITTIME);
    }

    /**
     * Continuously simulates a short press on the DELETE button.
     *
     * @param times times the number of press
     */
    public void pressDelete(int times) {
        int i = 0;

        while (i++ < times) {
            pressDelete();
        }

    }

    /**
     * Simulates a short press on the SEARCH button.
     */
    public void pressSearch() {
        mDevice.pressSearch();
    }

    /**
     * Simulates a long press using a key code. See KeyEvent.
     *
     * @param keyCode the key code of the event.
     */
    public void longPress(int keyCode) {
        final long eventTime = SystemClock.uptimeMillis();
        KeyEvent downEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN,
                keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
                InputDevice.SOURCE_KEYBOARD);
        KeyEvent upEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP,
                keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
                InputDevice.SOURCE_KEYBOARD);
        injectEventSync(downEvent);
        SystemClock.sleep(500);
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            injectEventSync(downEvent);
        }
        injectEventSync(upEvent);
    }

    /**
     * Returns the first object to match the selector criteria.
     *
     * @param selector the selector criteria
     * @return UiObject2 object
     */
    public UiObject2 getUiObject(BySelector selector) {
        return mDevice.findObject(selector);
    }

    /**
     * Returns the first object to match the selector criteria.
     *
     * @param selector the selector criteria
     * @return UiObject object
     */
    public UiObject getUiObject(UiSelector selector) {
        return mDevice.findObject(selector);
    }

    /**
     * Check if UI element exists.
     *
     * @param selector the selector criteria
     * @return exist result
     */
    public boolean exist(BySelector selector) {
        return mDevice.findObject(selector) == null ? false : true;
    }

    /**
     * Check if UI element exists.
     *
     * @param selector the selector criteria
     * @return exist result
     */
    public boolean exist(UiSelector selector) {
        return mDevice.findObject(selector).exists();
    }

    /**
     * Clicks on the object with specified selector criteria.
     *
     * @param selector the selector criteria
     */
    public void click(BySelector selector) {
        mDevice.findObject(selector).click();
        sleep(Config.STEP_WAITTIME);
    }

    /**
     * Scrolls to the beginning of the first ListView element.
     */
    public void scrollListViewToBeginning() throws UiObjectNotFoundException {
        new UiScrollable(new UiSelector().className("android.widget.ListView")).scrollToBeginning(Config.SWIPE_STEPS);
    }

    /**
     * Scrolls to the end of the first ListView element.
     */
    public void scrollListViewToEnd() throws UiObjectNotFoundException {
        new UiScrollable(new UiSelector().className("android.widget.ListView")).scrollToEnd((Config.SWIPE_STEPS));
    }

    /**
     * Sets the text content if this object is an editable field.
     *
     * @param selector the selector criteria
     * @param text text content
     * @return set text success or not
     */
    public boolean setText(BySelector selector, String text) {
        UiObject2 editText = mDevice.findObject(selector);
        //check object class
        if (editText.getClassName().contains("EditText")) {
            //clear
            for (int iteration = 0; iteration < 3; iteration++) {
                editText.click();
                pressDelete(editText.getText().length());
                sleep(Config.STEP_WAITTIME);
            }
            //set
            editText.setText(text);
            sleep(Config.STEP_WAITTIME * 2);
            if (editText.getText().equals(text)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sets the text content if this object is an editable field.
     *
     * @param selector the selector criteria
     * @param text text content
     * @return set text success or not
     * @throws UiObjectNotFoundException
     */
    public boolean setText(UiSelector selector, String text) throws UiObjectNotFoundException {
        UiObject editText = mDevice.findObject(selector);
        editText.clearTextField();
        mDevice.pressDelete();
        editText.setText(text);
        sleep(Config.STEP_WAITTIME * 2);
        return editText.getText().equals(text) ? true : false;
    }

    /**
     * Gets the height of the display, in pixels.
     * The size is adjusted based on the current orientation of the display.
     *
     * @return height in pixels or zero on failure.
     */
    public int getDisplayHeight() {
        return mDevice.getDisplayHeight();
    }

    /**
     * Gets the width of the display, in pixels.
     * The width and height details are reported based on the current orientation of the display.
     *
     * @return width in pixels or zero on failure.
     */
    public int getDisplayWidth() {
        return mDevice.getDisplayWidth();
    }

    /**
     * Returns the current time in milliseconds.
     *
     * @return current system time, format yyyy-MM-dd HH:mm:ss
     */
    public String getSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());

        return formatter.format(curDate);
    }

    /**
     * Returns the current time stamp
     *
     * @return current system time, format MMddHHmmss
     */
    public String getTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());

        return formatter.format(curDate);
    }

    /**
     * Waits a given number of milliseconds (of uptimeMillis) before returning.
     *
     * @param ms to sleep before returning, in milliseconds of uptime.
     */
    public void sleep(long ms) {
        SystemClock.sleep(ms);
    }

    /**
     * Take a screenshot of current window, png is default format.image saved under the case folder
     * when SAVE_SCREENSHOT switcher is true.
     *
     * @param imgName
     * @return true if screen shot is created successfully, false otherwise
     */
    public boolean saveScreenshot(String imgName) {
        if (!Config.SAVE_SCREENSHOT) {
            return false;
        }
        String suffix = ".png";
        if (!imgName.endsWith(suffix)) {
            imgName += suffix;
        }
        return mDevice.takeScreenshot(new File(Config.CASE_PATH + "//" + imgName));
    }

    /**
     * Take a screenshot of current window and store it as PNG The screenshot is adjusted per screen
     * rotation
     *
     * @param storePath where the PNG should be written to
     * @param scale the screenshot down if needed; 1.0f for original size
     * @param quality of the PNG compression; range: 0-100
     * @return true if screen shot is created successfully, false otherwise
     */
    public boolean saveScreenshot(File storePath, float scale, int quality) {
        if (!Config.SAVE_SCREENSHOT) {
            return false;
        }

        boolean result = false;
        result = mDevice.takeScreenshot(storePath, scale, quality);
        mDevice.waitForIdle(1);

        return result;
    }

    /**
     * Save bugreport to file, need root permission
     *
     * @param bugreportFile
     * @throws IOException
     */
    @Deprecated
    public void saveBugreportToFile(File bugreportFile) throws IOException {
        ParcelFileDescriptor pfd = InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("bugreport");
        byte[] buf = new byte[512];
        int bytesRead;
        FileInputStream fis = new ParcelFileDescriptor.AutoCloseInputStream(pfd);
        StringBuffer stdout = new StringBuffer();

        FileOutputStream out = new FileOutputStream(bugreportFile);

        while ((bytesRead = fis.read(buf)) != -1) {
            out.write(new String(buf, 0, bytesRead).getBytes());
        }
        fis.close();
        out.close();
    }

    /**
     * A Method for injecting an arbitrary input event.
     *
     * @param event The event to inject.
     * @return Whether event injection succeeded.
     */
    private boolean injectEventSync(InputEvent event) {
        return getUiAutomation().injectInputEvent(event, true);
    }

    /**
     * Create a new, empty file or directory named by this abstract pathname if and only if a file
     * with this name does not yet exist.
     *
     * @param name the path name which to be created.
     * @return whether the file created.
     */
    private boolean createFile(String name) {
        File file = new File(name);

        if (!fileExist(name)) {
            if (file.isFile()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                file.mkdirs();
            }
        }

        if (fileExist(name) == true) {
            Logger.i("Create " + name);
            return true;
        }

        Logger.i("Create " + name + " failed.");

        return false;
    }

    /**
     * Deletes the file or directory denoted by this abstract pathname.
     *
     * @param dir the path name which to be deleted.
     * @return whether the file deleted.
     */
    private boolean deleteDirs(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return false;
        }

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDirs(file);
            }
        }
        dir.delete();

        if (dir.exists() == true) {
            return false;
        }

        return true;
    }

    /**
     * Tests whether the file or directory denoted by this abstract pathname exists.
     *
     * @return true if exists, false otherwise.
     */
    private boolean fileExist(String path) {
        if (new File(path).exists()) {
            return true;
        }

        return false;
    }

    /**
     * get current test case name
     *
     * @return case name
     */
    private String getCaller() {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();

        for (int index = 0; index < stack.length; index++) {
//            Logger.i(stack[index].getMethodName().toString() + "  _  " + stack[index].getFileName());
            if (stack[index].getMethodName().contains("<init>")) {
                return ++index < stack.length ? stack[index].getFileName().replace(".java", "") : "getCallerFailed";
            }
        }

        return null;
    }

}
