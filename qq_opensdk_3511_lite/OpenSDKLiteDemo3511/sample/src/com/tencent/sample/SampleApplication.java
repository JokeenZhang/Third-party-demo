package com.tencent.sample;

import android.app.Application;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        LogToFile.init(this);
        LogToFile.v("SampleApplication", "-->onCreate");
    }
}
