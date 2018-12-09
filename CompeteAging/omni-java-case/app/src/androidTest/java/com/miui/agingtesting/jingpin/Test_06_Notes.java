package com.miui.agingtesting.jingpin;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiSelector;

import com.miui.agingtesting.common.ATConfig;
import com.miui.agingtesting.common.Method;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.miui.marmot.lib.Config.*;

/**
 * Aging Test 05 : notes.
 *
 * @author 闫东 yandong@xiaomi.com
 * @since 2017年7月19日 下午 1:55:38
 */

@RunWith(AndroidJUnit4.class)
public class Test_06_Notes {

    private Marmot mm;
    private UiDevice mDevice;
    private Method md;
    private String PhoneName;
    private String notesActivity;

    @Before
    public void init() throws Exception{
        mm = new Marmot();
        md = new Method();
        mDevice = mm.getUiDevice();
        PhoneName=mDevice.getProductName();
        if (isOPPO(PhoneName))
            notesActivity = "com.nearme.note/.view.AllNoteActivity";
        else if(isHUAWEI(PhoneName) &&PhoneName!="SLA-AL00")
            notesActivity = "com.example.android.notepad/.NotePadActivity";
        else if(isXIAOMI(PhoneName) && !PhoneName.equals("SKR-A0"))
            notesActivity = "com.miui.notes/.ui.NotesListActivity";
        else if(PhoneName.equals("SKR-A0")){ //E30
            notesActivity = "com.blackshark.note/.view.NoteListActivity";
        }
        else if(isVIVO(PhoneName)){
            notesActivity = "com.android.notes/.Notes";
        }
        else{
            throw new Exception("Phone:"+ PhoneName+" "+ "isn't in the device list.");
        }
    }


    @Test
    public void notes() {
        for(int i = 0; i < ATConfig.NOTES_LOOP; i++) {
            launchnotes();
        }
    }

    public void launchnotes() {
        try {
            mDevice.pressHome();
            mDevice.executeShellCommand("am start -n  " + notesActivity);//执行shell命令开启便签程序
            mm.sleep(ATConfig.LAUNCH_TIME);//登陆时间
            md.closeAgreeAndGoOnDialog();//第一次打开去除是否同意对话框

            if(notesActivity =="com.nearme.note/.view.AllNoteActivity")      //OPPO
            {
                mDevice.findObject(new UiSelector().resourceId("com.nearme.note:id/menu_new_note")).clickAndWaitForNewWindow(2000);
                mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                mDevice.findObject(new UiSelector().className("android.widget.EditText")).setText("hello");
            }
            else if(notesActivity =="com.example.android.notepad/.NotePadActivity")      //华为
            {
                mDevice.findObject(new UiSelector().resourceId("com.example.android.notepad:id/app_bar_text_add")).clickAndWaitForNewWindow(2000);
                md.closeAgreeAndGoOnDialog();
                mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                mDevice.findObject(new UiSelector().className("android.widget.EditText")).setText("hello");
            }

            else if(notesActivity =="com.miui.notes/.ui.NotesListActivity")     //小米
            {
                //新建便签
                mDevice.findObject(new UiSelector().resourceId("com.miui.notes:id/menu_add")).clickAndWaitForNewWindow(2000);
                md.closemiSync();//关闭账号同步
                md.closesogou();//关闭搜狗弹窗
                mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                mDevice.findObject(new UiSelector().className("android.widget.EditText")).setText("hello");
            }

            else if(notesActivity =="com.blackshark.note/.view.NoteListActivity")  //E30
            {
                //新建便签
                mDevice.findObject(new UiSelector().resourceId("com.blackshark.note:id/fab_new_note")).clickAndWaitForNewWindow(2000);
                md.closemiSync();//关闭账号同步
                md.closesogou();//关闭搜狗弹窗
                mm.sleep(ATConfig.SIMPLE_OPERATION_TIME);
                mDevice.findObject(new UiSelector().resourceId("com.blackshark.note:id/image_template_icon")).click();
                mDevice.findObject(new UiSelector().className("android.widget.EditText")).setText("hello");
            }

            else if(notesActivity =="com.android.notes/.Notes")  //vivo
            {
                //新建便签
                mDevice.findObject(new UiSelector().text("新建便签")).clickAndWaitForNewWindow();
                mDevice.findObject(new UiSelector().resourceId("com.android.notes:id/line_edit_text")).setText("hello");
                mDevice.findObject(new UiSelector().text("完成")).click();

            }
            mm.pressBack(3);

        } catch (Exception e) {
            e.printStackTrace();
            mm.pressBack(5);
        }
    }

    @After
    public void clear() {
        mDevice.pressHome();
    }


}
