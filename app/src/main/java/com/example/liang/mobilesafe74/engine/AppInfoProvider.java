package com.example.liang.mobilesafe74.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.liang.mobilesafe74.db.domain.AppInfoBean;

import java.util.ArrayList;
import java.util.List;

public class AppInfoProvider {
    //获取安装在手机上的应用信息
    public static List<AppInfoBean> getAppInfoList(Context context){
        List<AppInfoBean> appInfoBeanList=new ArrayList<AppInfoBean>();
        //1.获取包管理者对象
        PackageManager pm = context.getPackageManager();
        //2.固定做法获取安装在手机上所有应用信息,则传递0
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        //3.遍历集合,获取每一个安装在手机上的应用相关信息(包名,名称,路径,系统,图标)
        for (PackageInfo packageInfo:installedPackages){
            AppInfoBean appInfoBean = new AppInfoBean();
            //4.包名
            appInfoBean.packagename=packageInfo.packageName;
            //5.获取应用名称,图标
            appInfoBean.name = packageInfo.applicationInfo.loadLabel(pm).toString();
            appInfoBean.icon=packageInfo.applicationInfo.loadIcon(pm);
            //6.是否为系统应用(状态机)
            if ((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                //系统应用
                appInfoBean.isSystem=true;
            }else {
                //非系统应用
                appInfoBean.isSystem=false;
            }
            //7.是否为sd卡应用
            if ((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
                //系统应用
                appInfoBean.isSdcard=true;
            }else {
                //非系统应用
                appInfoBean.isSdcard=false;
            }
            appInfoBeanList.add(appInfoBean);
        }
        return appInfoBeanList;
    }
}
