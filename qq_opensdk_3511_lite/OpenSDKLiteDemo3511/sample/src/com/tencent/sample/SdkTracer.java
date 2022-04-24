package com.tencent.sample;

import android.util.Log;

import com.tencent.open.log.TraceLevel;
import com.tencent.open.log.Tracer;

public class SdkTracer extends Tracer {
    @Override
    protected void doTrace(int level, Thread thread, long time, String tag, String msg, Throwable tr) {
        switch (level) {
            case TraceLevel.VERBOSE:
                Log.v(tag, msg, tr);
                LogToFile.v(tag, msg);
                break;
            case TraceLevel.DEBUG:
                Log.d(tag, msg, tr);
                LogToFile.d(tag, msg);
                break;
            case TraceLevel.INFO:
                Log.i(tag, msg, tr);
                LogToFile.i(tag, msg);
                break;
            case TraceLevel.WARN:
                Log.w(tag, msg, tr);
                LogToFile.w(tag, msg);
                break;
            case TraceLevel.ERROR:
                Log.e(tag, msg, tr);
                LogToFile.e(tag, msg);
                break;
            default:
                Log.e(tag, msg, tr);
                LogToFile.e(tag, msg);
                break;
        }
    }
}
