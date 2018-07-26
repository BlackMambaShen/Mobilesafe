package com.example.liang.mobilesafe74.com.example.liang.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Process;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.liang.mobilesafe74.MyAppWidgetProvider;
import com.example.liang.mobilesafe74.R;
import com.example.liang.mobilesafe74.engine.ProcessInfoProvider;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {
    private Timer timer;
    private InnerReceiver innerReceiver;
    private static final String tag="UpdateWidgetService";

    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //管理进程总数和可用内存数更新(定时器)
        startTimer();
        //注册开锁，解锁广播接收者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

         innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver,intentFilter);
        super.onCreate();
    }


    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                //开启定时更新任务
                startTimer();
            }else {
                //关闭定时更新任务
                cancelTimerTask();
            }
        }
    }

    private void cancelTimerTask() {
        //取消定时任务的方法
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    private void startTimer() {
         timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //UI定时刷新
                updateAppWidget();
                Log.i(tag,"5秒一次的定时任务现在正在运行");
            }
        },0,5000);
    }

    private void updateAppWidget() {
        //1.获取AppWidget对象
        AppWidgetManager aWM = AppWidgetManager.getInstance(this);
        //2.获取窗体小部件布局转换成的view对象
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.example_appwidget);
        //3.给窗体小部件view对象，内部控件赋值
        remoteViews.setTextViewText(R.id.tv_process_content,"进程总数："+ ProcessInfoProvider.getProcessCount(this));
        //4.显示可用内存大小
        String strAvailSpace = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
        remoteViews.setTextViewText(R.id.tv_process_count1,"可用内存："+strAvailSpace);

        //点击窗体小部件，进入应用
        //在哪个控件上相应点击事件:延期意图
        //隐式意图开启活动
        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root,pendingIntent);

        //通过延期意图发送广播，在广播接收者中杀死进程
        Intent broadCastIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadCastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear,broadcast);
        //窗体小部件对应广播接收者的字节码文件
        ComponentName componentName = new ComponentName(getApplicationContext(), MyAppWidgetProvider.class);
        aWM.updateAppWidget(componentName,remoteViews);
    }

    @Override
    public void onDestroy() {
        if (innerReceiver!=null){
            unregisterReceiver(innerReceiver);
        }
        //调用onDestroy即关闭服务,关闭服务的方法在移除最后一个窗体小部件的时候调用
        cancelTimerTask();
        super.onDestroy();
    }
}
