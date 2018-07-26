package com.example.liang.mobilesafe74.com.example.liang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.example.liang.mobilesafe74.com.example.liang.service.LocationService;
import com.example.liang.mobilesafe74.utils.ConstantValue;
import com.example.liang.mobilesafe74.utils.SpUtil;

import java.io.IOException;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //1.判断是否开启了防盗保护
        boolean open_security = SpUtil.getBoolean(context, ConstantValue.open_security, false);
        if (open_security){
            //2.获取短信内容
            Object []objects = (Object[]) intent.getExtras().get("pdus");
            //3.循环遍历短信过程
            for (Object object:objects){
                //4.获取短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                //5.获取短信对象的基本信息
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                //6.判断是否包含了播放音乐的关键字
                if (messageBody.contains("#*alarm*#")){
                    try {
                        //播放音乐(准备音乐,)
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource("C:\\Users\\liang\\AndroidStudioProjects\\Mobilesafe74\\app\\src\\main\\res\\raw\\liangliang.mp3");
                        mediaPlayer.setLooping(true);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (messageBody.contains("#*location*#")){
                    //开启获取位置服务
                    Intent intent1 = new Intent(context, LocationService.class);
                    context.startService(intent1);
                }
            }
        }
    }
}
