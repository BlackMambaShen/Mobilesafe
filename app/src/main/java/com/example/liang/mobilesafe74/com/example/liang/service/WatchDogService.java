package com.example.liang.mobilesafe74.com.example.liang.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.example.liang.mobilesafe74.EnterPsdActivity;
import com.example.liang.mobilesafe74.db.dao.AppLockDao;

import java.util.List;

public class WatchDogService extends Service {
    private AppLockDao mDao;
    private List<String> packnameList;
    private InnerReceiver innerReceiver;
    private String skipPackageName;
    private boolean isWatch;
    private MycontentObserver mycontentObserver;

    public WatchDogService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //维护一个看门狗的死循环，让其时刻监测现在开启的应用，是否为程序所中要去拦截的应用
        isWatch=true;
         mDao = AppLockDao.getInstance(this);
        watch();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SKIP");
         innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver,intentFilter);
         mycontentObserver = new MycontentObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"),
                true,mycontentObserver);
        super.onCreate();
    }


    private class MycontentObserver extends ContentObserver{

        public MycontentObserver(Handler handler) {
            super(handler);
        }

        //数据库发生改变的时候 重新获取包名所在集合的数据
        public void onChange(boolean selfChange) {
            new Thread(){
                @Override
                public void run() {
                    packnameList = mDao.findAll();
                }
            }.start();
            super.onChange(selfChange);
        }
    }

    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取发送广播过程中传递过来的包名，跳过包名检测过程
             skipPackageName = intent.getStringExtra("packageName");

        }
    }

    private void watch() {
        //1.子线程中，开启一个可控的死循环
        new Thread(){
            @Override
            public void run() {
                packnameList = mDao.findAll();
                while (isWatch){
                    //2.监测现在正在开启的应用,任务栈
                    //3.获取activity的管理者对象
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    //获取正在开启应用的任务栈
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    //5.获取栈顶的activity,然后获取此activity所在应用的包名
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    //6.此包名在已加锁的包名集合中中去做比对,如果包含此包名，则需要弹出拦截界面
                    if (packnameList.contains(packageName)){
                        //已经解锁了就跳过逻辑了
                        if (!packageName.equals(skipPackageName)){
                            //7.弹出拦截界面
                            Intent intent=new Intent(getApplicationContext(),EnterPsdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packageName",packageName);
                            startActivity(intent);
                        }
                    }
                    //睡眠一下 每500毫秒检测一次
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        //停止循环
        isWatch=false;
        //注销广播接收者
        if (innerReceiver!=null){
            unregisterReceiver(innerReceiver);
        }
        //注销内容观察者
        if (mycontentObserver!=null){
            getContentResolver().unregisterContentObserver(mycontentObserver);
        }
        super.onDestroy();
    }
}
