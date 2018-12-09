package com.miui.agingtesting.jingpin;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.view.KeyEvent.KEYCODE_APP_SWITCH;
import static com.miui.agingtesting.common.ATConfig.DESKTOP_LOOP;
import static com.miui.marmot.lib.Config.isHUAWEI;
import static com.miui.marmot.lib.Config.isOPPO;


/**
 * Created by tianxiao on 2018/3/1.
 */
@RunWith(AndroidJUnit4.class)
public class Test_12_Desktop {
    private Marmot mm;
    private UiDevice mDevice;
    private int width;
    private int height;
    private String phoneName ;
    private Context context = getTargetContext();
    private boolean isHuawei = false;
    private boolean isOppo = false;
    private List<String> wifiNameList = Arrays.asList("MIOffice","MIUI","Xiaomi_MIUI");


    @Before
    public void init() throws Exception {
        mm = new Marmot();
        mDevice = mm.getUiDevice();
        width = mDevice.getDisplayWidth();
        height = mDevice.getDisplayHeight();
        phoneName = mDevice.getProductName();
        isHuawei = isHUAWEI(phoneName);
        isOppo = isOPPO(phoneName);

    }

    @Test
    public void Desktop() {
        for (int i=0;i < DESKTOP_LOOP;i++)
        {
            desktop();
        }

    }

    private void desktop(){
        try {
            mDevice.sleep();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mm.sleep(5 * 1000);
        try {
            mDevice.wakeUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mm.sleep(2000);
        //解锁
        unLock();
        mm.sleep(10000);

        //打开最近任务
        if(isHuawei || isOppo){
            mDevice.pressKeyCode(KEYCODE_APP_SWITCH);
        }
        else{
            mm.pressMenu();
        }


        mm.sleep(5000);
        //点击 home 键
        mm.pressHome();

        if (!isOppo) {
            //下拉状态栏
            mDevice.swipe(width / 2, 0, width / 2, height / 2, 10);
            mm.sleep(5000);

        }
        else{
            //上拉
            mDevice.swipe(width / 2, height -10, width / 2, height / 2, 10);
        }

        //调节 wifi
        setWifi();

        //调节亮度
        setBrightness();
    }

    private void setBrightness() {

        setBrightness(100);
        mm.sleep(5000);
        if(!isOppo) {
            //调节亮度后，下拉菜单会自动收起，所以需要再次下拉
            mDevice.swipe(width / 2, 0, width / 2, height / 2, 10);
        }
        setBrightness(30);
        mm.sleep(5000);
        if(!isOppo){
        mDevice.swipe(width / 2, 0, width / 2, height/2, 10);
        }
        setBrightness(50);
        mm.sleep(5000);
        mm.pressHome();
        mm.sleep(3000);

    }

    private void setBrightness(int ratio) {
        UiObject brn ;
        if(isHuawei){
            brn = new UiObject(new UiSelector().text("屏幕亮度"));
        }
        else if(isOppo){
            brn = new UiObject(new UiSelector().description("屏幕亮度"));
        }
        else {
            brn = new UiObject(new UiSelector().description("亮度滑块"));
        }
        int brightness = getSystemBrightness();
        try {
            Rect rect = brn.getBounds();
            int y = rect.centerY();
            int right = rect.right;
            int left = rect.left;
            if(!isOppo) {
                brn.dragTo(left + (right - left) * ratio / 100, y, 20);
            }
            else{
                //oppo 手机只能这样，dragTo 不生效
                mDevice.swipe(left + (right-left)*brightness/255, y,left + (right - left) * ratio / 100,y,20);
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

    }

    private int getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    private void setWifi(){
        //设置 wifi
        clickWifiIcon(false);
        mm.sleep(5000);
        clickWifiIcon(true);
        mm.sleep(5000);
    }

    private void clickWifiIcon(boolean isEnableWifi) {
        UiObject wf = null ;
        //Xiaomi 按照 wifi 名来查找，其他手机需要再看
        for(String wifiname : wifiNameList) {
            if (!isEnableWifi) {

                if (isHuawei) {
                    wf=new UiObject(new UiSelector().textContains(wifiname));
                } else {
                    wf=new UiObject(new UiSelector().descriptionContains(wifiname));
                }
            } else {

                if (isHuawei) {
                    wf=new UiObject(new UiSelector().text("WLAN"));
                } else if (isOppo) {
                    wf=new UiObject(new UiSelector().descriptionContains(wifiname));
                } else {
                    wf=new UiObject(new UiSelector().description("WLAN"));
                }
            }
            if(wf!= null)
                break;
        }

        try {
            wf.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void unLock(){
        try {
            if (!mDevice.isScreenOn()){
                mDevice.wakeUp();
            }
            //向上滑动，调出解锁面板
            mDevice.swipe(width/2, height-200, width/2, height/2,10 );
            //获取边框
            String resourceId = "com.android.keyguard:id/lockPattern";
            if(isHuawei){
                resourceId = "com.android.systemui:id/lockPatternView";
            }
            if(isOppo){
                resourceId = "com.android.keyguard:id/lockPatternView";
            }
            UiObject graphicLock = new UiObject(new UiSelector().resourceId(resourceId));
            Rect bound = graphicLock.getBounds();
            int centerX = bound.centerX();
            int centerY = bound.centerY();
            int left = bound.left;
            int right = bound.right;
            int top = (bound.bottom - bound.top)/6 + bound.top;
            int bottom = bound.bottom - (bound.bottom - bound.top)/6;

            Point p1 = new Point();
            Point p2 = new Point();
            Point p3 = new Point();
            Point p4 = new Point();
            Point p5 = new Point();
            Point p6 = new Point();
            Point p7 = new Point();
            p1.x = left; p1.y = top;
            p2.x = centerX; p2.y = top;
            p3.x = right; p3.y = top;
            p4.x = centerX; p4.y = centerY;
            p5.x = left; p5.y = bottom;
            p6.x = centerX; p6.y = bottom;
            p7.x = right; p7.y = bottom;
            Point[] p = {p1,p2,p3,p4,p5,p6,p7};
            //滑动解锁
            mDevice.swipe(p, 20);
            mm.sleep(2000);


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }


    @After
    public void clear() {
        mDevice.pressHome();
    }


}
