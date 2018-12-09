package com.miui.marmot.lib;

import android.os.Bundle;
import android.support.test.runner.AndroidJUnitRunner;
import android.test.InstrumentationTestRunner;

public class Runner extends AndroidJUnitRunner {
    private static Bundle commandLineParameters = null;

    public static Bundle getParameters() {
        return commandLineParameters;
    }

    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);

        if(arguments != null) {
            Runner.commandLineParameters = arguments;
        }
    }

}
