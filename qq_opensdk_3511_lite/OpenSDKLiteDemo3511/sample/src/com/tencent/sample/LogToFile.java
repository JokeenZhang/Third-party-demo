package com.tencent.sample;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.tencent.tauth.Tencent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.MEDIA_MOUNTED;

public class LogToFile {
    private static String TAG = "LogToFile";
    private static String sLogPath = null;//log日志存放路径
    
    private static Date date = new Date();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH", Locale.US);
    
    private static final char VERBOSE = 'v';
    private static final char DEBUG = 'd';
    private static final char INFO = 'i';
    private static final char WARN = 'w';
    private static final char ERROR = 'e';
    
    public static void init(Context context) {
        sLogPath = getFilePath(context) + "/Logs";
        Tencent.setCustomLogger(new SdkTracer());
    }
    
    private static String getFilePath(Context context) {
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File fileDir = context.getExternalFilesDir(null);
            if (fileDir != null) {
                return fileDir.getPath();
            }
        }
        return context.getFilesDir().getPath();
    }
    
    public static void v(String tag, String msg) {
        writeToFile(VERBOSE, tag, msg);
    }
    
    public static void d(String tag, String msg) {
        writeToFile(DEBUG, tag, msg);
    }
    
    public static void i(String tag, String msg) {
        writeToFile(INFO, tag, msg);
    }
    
    public static void w(String tag, String msg) {
        writeToFile(WARN, tag, msg);
    }
    
    public static void e(String tag, String msg) {
        writeToFile(ERROR, tag, msg);
    }
    
    // 此处仅仅是演示把日志写到文件, 效率比较低，最好在子线程使用缓存减少IO
    private static void writeToFile(char type, String tag, String msg) {
        if (null == sLogPath) {
            Log.e(TAG, "sLogPath == null!!!");
            return;
        }
        
        String filePath = sLogPath + "/log_" + dateFormat.format(new Date()) + ".log";
        String log = dateFormat.format(date) + " " + type + " " + tag + " " + msg + "\n";
        
        File file = new File(sLogPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        
        FileOutputStream fos;
        BufferedWriter bw = null;
        try {
            
            fos = new FileOutputStream(filePath, true);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(log);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
            }
        }
    }
}