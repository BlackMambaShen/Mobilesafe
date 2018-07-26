package com.example.liang.mobilesafe74.global;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class MyApplication extends Application {
    private static final String tag = "MyApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        //捕获全局异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //在获取到了未捕获的异常后，处理的方法
                e.printStackTrace();
                Log.i(tag, "捕获到了一个程序的异常");
                //将捕获的异常存储到sd卡中
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "error74.log";
                File file = new File(path);
                try {
                    PrintWriter printWriter = new PrintWriter(file);
                    e.printStackTrace(printWriter);
                    printWriter.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                //上传公司的服务器
                //结束应用
                System.exit(0);
                //TODO 帮我在这里做一些初始化
            }
        });
    }
}
