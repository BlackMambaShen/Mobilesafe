package com.example.liang.mobilesafe74.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceUtils {
    private static ActivityManager activityManager;

    //判断是否正在运行服务
    public static boolean isRunning(Context context,String serviceName){
        //1.获取activity管理者对象,可以获取当前手机正在运行的所有服务
         activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
         //2.获取手机中正在运行的服务集合(多少个服务)
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(1000);
        //遍历获取的所有服务的集合 ,拿到每一个服务的类的名称，和传递进来的类做对比，如果一直，说明正在运行
        for(ActivityManager.RunningServiceInfo runningServiceInfo:runningServices){
            //4.获取每一个真正运行服务的名称
            if (serviceName.equals(runningServiceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
