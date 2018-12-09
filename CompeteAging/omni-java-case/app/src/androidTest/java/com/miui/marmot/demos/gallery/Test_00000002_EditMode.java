package com.miui.marmot.demos.gallery;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.miui.marmot.lib.Checker;
import com.miui.marmot.lib.Logger;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.Parameterized;

/**
 * 相册-大图页各个菜单操作（视频和图片编辑等功能）
 *
 * @author 田争曦 tianzhengxi@xiaomi.com
 * @since 2017年5月16日 上午9:00:43
 */
@RunWith(JUnit4.class)
public class Test_00000002_EditMode {
    private Marmot mm = null;
    private Checker cc = null;
    private String productName = null;
    private String orgPicDate = null;
    private String newPicDate = null;
    private String newPicInfoCheck = null;
    private String newVdInfo = null;
    private String orgVdInfo = null;
    private String newPicInfo = null;
    private String newVdInfoCheck = null;

    @Before
    public void initEnvironment(){
        mm = new Marmot();
        cc = new Checker();
        productName = mm.getUiDevice().getProductName();

        Logger.i("Prepare testing environment.");
        mm.pressHome(2);
    }

    public void meriClickCenter(){
        try {
            Rect frame = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.FrameLayout")).getBounds();
            mm.click(frame.centerX(), frame.centerY());
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Parameterized.Parameters
    public void add_to_new_album(String albumName){
        //PRECONDITIONS: 图片界面，且下面菜单一栏显示
        try {
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").text("更多")).click();
            mm.getUiDevice().findObject(new UiSelector().resourceId("miui:id/title").text("加到相册")).click();
            mm.getUiDevice().findObject(new UiSelector().resourceId("com.miui.gallery:id/title").text("新建相册")).click();
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.EditText")).setText(albumName);
            mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.Button").resourceId("android:id/button1").text("确定")).click();
            mm.pressBack();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Parameterized.Parameters
    public void add_to_new_album_5c(String albumName){
        try {
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").text("加到相册")).clickAndWaitForNewWindow();
            mm.getUiDevice().findObject(new UiSelector().resourceId("com.miui.gallery:id/title").text("新建相册")).click();
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.EditText")).setText(albumName);
            mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.Button").resourceId("android:id/button1").text("确定")).click();
            mm.pressBack();

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void view_more_info(){
        try {
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").text("更多")).click();
            mm.getUiDevice().findObject(new UiSelector().resourceId("miui:id/title").text("详情")).click();
            mm.sleep(2000);
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Parameterized.Parameters
    public void del_album(String albumName){
        try {
            Rect toLongClick = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/album_name").text(albumName)).getBounds();
            mm.longClick(toLongClick.centerX(), toLongClick.centerY());
            mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("miui:id/title").text("删除")).click();
            mm.click(By.res("android:id/button1"));
            cc.assertTrue("The video and the album has been deleted.", !mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/album_name").text(albumName)).exists());
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test_00000002_EditModeVideo() throws UiObjectNotFoundException {
        Logger.i("Prepare a video.");
        mm.launchApp("com.android.camera");
        mm.getUiDevice().waitForWindowUpdate("com.miui.camera", 2000);
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.ImageView").resourceId("com.android.camera:id/v6_module_picker").index(2)).click();
        mm.click(By.res("com.android.camera:id/v6_shutter_button_internal"));
        mm.sleep(5000);
        mm.pressHome(2);

        Logger.i("Step1: Enter into the gallery.");
        mm.launchApp("com.miui.gallery");
        mm.getUiDevice().waitForWindowUpdate("com.miui.gallery", 2000);

        Logger.i("Step2: Check the edit mode of a video.");
        if(productName.equals("meri")){
            Logger.i("Xiaomi 5c doesn't have edit video function.");
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(4)).click();
            if(productName.equals("meri")){
                mm.sleep(1000);
                meriClickCenter();
            }
            view_more_info();
            newVdInfo = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/file_info_title").index(1)).getText();
            mm.pressBack();
        }
        else{
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(4)).click();
            view_more_info();
            orgVdInfo = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/file_info_title").index(1)).getText();
            String[] tmp = orgVdInfo.split("\\.");
            mm.pressBack();
            //开始编辑视频
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").index(3)).click(); //小米和红米的编辑功能名称不一致，不能通过text来点击
            mm.sleep(2000);
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.TextView").text("滤镜")).click();
            mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/name").text("动感")).click();
            mm.click(By.res("com.miui.gallery:id/video_editor_btn_ok"));
            mm.click(By.res("com.miui.gallery:id/export"));
            mm.sleep(15000);    //等待生成视频
            mm.pressBack();
            //查看新编辑好的video
            mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(4)).click();
            view_more_info();
            newVdInfo = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/file_info_title").index(1)).getText();
            String[] tmp1 = newVdInfo.split("\\.");
            boolean flag;
            if(tmp1[0].equals(tmp[0]+"(0)")){
                flag = true;
            }
            else {
                flag = false;
            }
            cc.assertTrue("The video has been successfully edited.", flag);
            mm.pressBack();
        }

        Logger.i("Step3: Add the video to a new album.");
        if(productName.equals("meri")){
            add_to_new_album_5c("测试视频");
        }
        else {
            add_to_new_album("测试视频");
        }

        Logger.i("Step4: Check the video if exists in a new album.");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.TextView").text("相册")).click();
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/album_name").text("测试视频")).click();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(4)).click();
        //判断小米5c
        if(productName.equals("meri")){
            mm.sleep(1000);
            meriClickCenter();
        }
        view_more_info();
        newVdInfoCheck = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/file_info_title").index(1)).getText();
        cc.assertTrue("The video has been added to a new album.", newVdInfoCheck.endsWith(newVdInfo));
        mm.pressBack(3);

        Logger.i("Step5: Delete the video and the album.");
        del_album("测试视频");
    }

    @Test
    public void test_00000002_EditModePicture() throws UiObjectNotFoundException{
        Logger.i("Prepare a picture.");
        mm.launchApp("com.android.camera");
        mm.getUiDevice().waitForWindowUpdate("com.miui.camera", 2000);
        if(mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.ImageView").resourceId("com.android.camera:id/v6_module_picker"))
                .getContentDescription().equals("拍照切换")){
            mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.ImageView").resourceId("com.android.camera:id/v6_module_picker")).click();
        }
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.ImageView").resourceId("com.android.camera:id/v6_shutter_button_internal")).clickAndWaitForNewWindow();
        mm.pressHome(2);

        Logger.i("Step1: Enter into the gallery.");
        mm.launchApp("com.miui.gallery");
        mm.getUiDevice().waitForWindowUpdate("com.miui.gallery", 2000);

        Logger.i("Step2: Check the edit mode of a picture.");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(4)).click();
        //判断小米5c
        if(productName.equals("meri")){
            mm.sleep(1000);
            meriClickCenter();
        }
        view_more_info();
        //照片的命名方式和视频不同，但是新编辑好的照片和原始照片的拍摄时间是相同的
        orgPicDate = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/time_subtitle").index(2)).getText();
        mm.pressBack();
        //开始编辑照片
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").index(3)).click(); //小米和红米的编辑功能名称不一致，不能通过text来点击
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/title").text("花蔓")).click();
        mm.click(By.res("com.miui.gallery:id/save"));
        mm.click(By.res("com.miui.gallery:id/save"));
        mm.sleep(8000);
        //查看编辑好的照片
        if(!productName.equals("meri")) {
            view_more_info();
            newPicDate = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/time_subtitle").index(2)).getText();
            newPicInfo = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/file_info_title").index(1)).getText();
            cc.assertTrue("The picture has been successfully edited.", newPicDate.equals(orgPicDate));
            mm.pressBack();
        }
        //判断小米5c
        else{
            view_more_info();
            newPicDate = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/time_subtitle").index(2)).getText();
            newPicInfo = mm.getUiDevice().findObject(new UiSelector()
                    .className("android.widget.TextView").resourceId("com.miui.gallery:id/file_info_title").index(1)).getText();
            mm.pressBack();
        }

        Logger.i("Step3: Add the picture to a new album.");
        add_to_new_album("测试照片");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.TextView").text("相册")).click();
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/album_name").text("测试照片")).click();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.RelativeLayout").index(4)).click();
        //判断小米5c
        if(productName.equals("meri")){
            mm.sleep(1000);
            meriClickCenter();
        }
        view_more_info();
        newPicInfoCheck = mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.miui.gallery:id/file_info_title").index(1)).getText();
        cc.assertTrue("The picture has been successfully edited.", newPicInfoCheck.equals(newPicInfo));
        mm.pressBack();

        Logger.i("Set as Wallpaper.");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").text("更多")).click();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.TextView").resourceId("miui:id/title").text("设置为壁纸")).clickAndWaitForNewWindow();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button")).click(); //小米max和红米这里应用为壁纸的按钮ID不一致
        mm.getUiDevice().findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("android:id/text1").text("应用锁屏")).click();

        Logger.i("Start a slide show.");
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").text("更多")).click();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.TextView").resourceId("miui:id/title").text("开始幻灯片播放")).click();
        if(productName.equals("meri")){
            mm.pressBack();
        }
        else {
            mm.sleep(3000);
        }

        Logger.i("Set as private.");
        //判断小米5c
        if(productName.equals("meri")){
            mm.sleep(1000);
            meriClickCenter();
        }
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.Button").text("更多")).click();
        mm.getUiDevice().findObject(new UiSelector().className("android.widget.TextView").resourceId("miui:id/title").text("设为私密")).click();
        mm.sleep(2000);
        mm.pressBack();

        Logger.i("Detele the picture and album.");
        del_album("测试照片");
    }

    @After
    public void clearEnvironment() {
        Logger.i("clear testing environment.");
        mm.pressBack(3);
        mm.pressHome(2);
    }
}
