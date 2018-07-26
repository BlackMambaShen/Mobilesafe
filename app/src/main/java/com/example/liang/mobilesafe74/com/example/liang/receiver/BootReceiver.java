package com.example.liang.mobilesafe74.com.example.liang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.liang.mobilesafe74.utils.ConstantValue;
import com.example.liang.mobilesafe74.utils.SpUtil;

public class BootReceiver extends BroadcastReceiver {

    private static final String tag="BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(tag,"重启手机成功了，并且监听到了相应的广播");
        //获取开机后手机的sim卡的序列号
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();
        //2.sp存储的序列卡号
        String sim_number = SpUtil.getString(context, ConstantValue.sim_number, "");
        if (!simSerialNumber.equals(sim_number)){
            //发送短信给报警号码
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("5556",null,"Sim change!!!",null,null);
        }
    }
}
