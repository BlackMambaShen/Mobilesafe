package com.example.liang.mobilesafe74;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.liang.mobilesafe74.com.example.liang.service.UpdateWidgetService;

public class MyAppWidgetProvider extends AppWidgetProvider {
    private static final String tag="MyAppWidgetProvider";
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        //开启服务
        context.startService(new Intent(context,UpdateWidgetService.class));
        //创建第一个窗体小部件的方法
        Log.i(tag,"onEnabled 创建第一个窗体小部件调用方法");
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //开启服务
        context.startService(new Intent(context,UpdateWidgetService.class));
        Log.i(tag,"onUpdate 创建多一个窗体小部件调用方法");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        //开启服务
        context.startService(new Intent(context,UpdateWidgetService.class));
        //当窗体小部件宽高发生改变的时候调用方法，创建多一个窗体小部件的时候调用此方法
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }


    @Override
    public void onDisabled(Context context) {
        Log.i(tag,"onDisabled 删除最后一个窗体小部件调用方法");
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        //开启服务
        context.stopService(new Intent(context,UpdateWidgetService.class));
        Log.i(tag,"onDeleted 删除一个窗体小部件调用方法");
        super.onDeleted(context, appWidgetIds);
    }

}
