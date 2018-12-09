package com.miui.marmot.suite;

import com.miui.marmot.demos.Test_00000001_BasicSample;
import com.miui.marmot.demos.Test_00000002_BasicSample;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Using Suite as a runner allows you to manually build a suite containing tests from many classes.
 * When you run this class, it will run all the tests in all the suite classes.
 * @author yumiao
 * @since API Level 18
 * @version 3.0.0
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        Test_00000001_BasicSample.class,
        Test_00000002_BasicSample.class
})
public class SuiteSample {

}
