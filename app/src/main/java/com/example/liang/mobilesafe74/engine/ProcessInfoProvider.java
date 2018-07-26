package com.example.liang.mobilesafe74.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;

import com.example.liang.mobilesafe74.R;
import com.example.liang.mobilesafe74.db.domain.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessInfoProvider {
    //获取进程总数的方法
    public static int getProcessCount(Context context){
        //1.获取activityManager
        ActivityManager am =(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                am.getRunningAppProcesses();
        //3.返回集合的总数
        Log.i("haha",""+runningAppProcesses.size());
        return runningAppProcesses.size();
    }

   public static long getAvailSpace(Context context){
       //1.获取activityManager
       ActivityManager am =(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
       //2.构建存储可用内存的对象
       ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //3.给memoryInfo对象赋值
       am.getMemoryInfo(memoryInfo);
       //4.获取memoryInfo中相应可用内存大小
       return memoryInfo.availMem;
   }

    public static long getTotalSpace(Context context){
//        //1.获取activityManager
//        ActivityManager am =(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        //2.构建存储可用内存的对象
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        //3.给memoryInfo对象赋值
//        am.getMemoryInfo(memoryInfo);
//        //4.获取memoryInfo中相应可用内存大小
//        return memoryInfo.totalMem;

        //内存大小写入文件中，读取proc/meminfo文件，读取第一行，获取数字字符，转换成bytes返回
        FileReader fileReader=null;
        BufferedReader bufferedReader=null;
        try {
             fileReader=new FileReader("proc/meminfo");
             bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            //将字符串转换成字符的数组
            char[] charArray = line.toCharArray();

            StringBuffer stringBuffer = new StringBuffer();
            //循环遍历每一个字符
            for (char c:charArray){
                if (c>='0'&&c<='9'){
                    stringBuffer.append(c);
                }
            }
           return Long.parseLong(stringBuffer.toString())*1024;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (fileReader!=null&&bufferedReader!=null){
                    fileReader.close();
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    //返回当前手机正在运行的进程的相关信息
    public static List<ProcessInfo> getProcessInfo(Context context){
        //获取进程相关信息
        List<ProcessInfo>processInfoList=new ArrayList<ProcessInfo>();
            //1.获取activityManager
            ActivityManager am =(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            PackageManager pm=context.getPackageManager();
            //2.获取正在运行进程的集合
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                    am.getRunningAppProcesses();
            //3.循环遍历上述集合，获取进程相关信息
            for (ActivityManager.RunningAppProcessInfo info:runningAppProcesses){
                ProcessInfo processInfo=new ProcessInfo();
                //4.获取进程的名称==应用的包名
                processInfo.packageName=info.processName;
                //5.获取进程占用的内存大小（传递一个进程对应的pid数组）
                Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
                //6.返回数组中索引位置为0的对象，为当前进程内存信息的对象
                Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
                //7.获取已使用的大小
                processInfo.memSize = memoryInfo.getTotalPrivateDirty()*1024;
                try {
                    ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                    //8.获取应用的名称
                    processInfo.name = applicationInfo.loadLabel(pm).toString();
                    //9.获取应用的图标
                    processInfo.icon = applicationInfo.loadIcon(pm);
                    //10.判断是否为系统进程
                    if ((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                        processInfo.isSystem=true;
                    }else {
                        processInfo.isSystem=false;
                    }

                } catch (Exception e) {
                    //需要处理
                    processInfo.name=info.processName;
                    processInfo.icon=context.getResources().getDrawable(R.mipmap.ic_launcher);
                    processInfo.isSystem=true;
                    e.printStackTrace();
                }
                //将对象添加到集合中
                processInfoList.add(processInfo);
            }
            return processInfoList;
    }

    public static void killProcess(Context context,ProcessInfo processInfo) {
        //1.获取activityManager
        ActivityManager am =(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.杀死指定包名的进程
        am.killBackgroundProcesses(processInfo.packageName);
    }

    public static void killAllProcess(Context context) {
        //1.获取activityManager
        ActivityManager am =(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                am.getRunningAppProcesses();
        //3.循环遍历所有的进程,并且杀死
        for (ActivityManager.RunningAppProcessInfo info:runningAppProcesses){
            //4.除了手机卫士以外，其他的进程都需要杀死
            if (info.processName.equals(context.getPackageName())){
                //如果匹配上了手机卫士，则需要跳出本次循环,进行下一次，继续杀死进程
                continue;
            }
            am.killBackgroundProcesses(info.processName);
        }
    }
}
