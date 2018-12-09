package com.miui.marmot.demos;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;

import com.miui.marmot.lib.Checker;
import com.miui.marmot.lib.Logger;
import com.miui.marmot.lib.Marmot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 * Marmot测试用例展示
 *
 * @author 于淼 yumiao@xiaomi.com
 * @since 2017年3月27日 下午4:15:24
 */

@RunWith(AndroidJUnit4.class)
public class Test_00000001_BasicSample {
    private Marmot mm = null;
    private Checker cc = null;

    @Before
    public void initEnvironment() {
        mm = new Marmot();
        cc = new Checker();

        Logger.i("Prepare testing environment.");
        mm.pressHome(2);
    }

    @Test
    public void test_00000001_BasicSample() {
        Logger.i("Step1: Launch calculator app.");
        mm.launchApp("com.miui.calculator");

        Logger.i("Step2: Enter settings UI.");
        mm.click(By.res("com.miui.calculator:id/imv_home_menu"));

        Logger.i("Step3: Click 'financial character'.");
        mm.click(By.text("大写数字"));

        Logger.i("Step4: Input number.");
        mm.click(By.res("com.miui.calculator:id/btn_1"));
        mm.click(By.res("com.miui.calculator:id/btn_2"));
        mm.click(By.res("com.miui.calculator:id/btn_3"));
        mm.click(By.res("com.miui.calculator:id/btn_4"));

        Logger.i("Step5: Check result.");
        String text = mm.getUiObject(By.res("com.miui.calculator:id/txv_word_figure")).getText();
        cc.assertThat("financial character match.", text, is(equalTo("壹仟贰佰叁拾肆元整")));
    }

    @After
    public void clearEnvironment() {
        Logger.i("clear testing environment.");
        mm.pressBack(3);
        mm.pressHome(2);
    }

}
