package com.example.liang.mobilesafe74.com.example.liang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.liang.mobilesafe74.engine.ProcessInfoProvider;

public class KillProcessReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //杀死进程
        ProcessInfoProvider.killAllProcess(context);
    }
}
