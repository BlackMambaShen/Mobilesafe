package com.example.liang.mobilesafe74.com.example.liang.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
//import android.os.ServiceManager;


import com.example.liang.mobilesafe74.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class BlackNumberService extends Service {
    private InnerSmsReceiver innerSmsReceiver;
    private BlackNumberDao dao;
    private TelephonyManager telephonyManager;
    private MyphoneStateListener myphoneStateListener;
    private MyContentObserver myContentObserver;

    public BlackNumberService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        dao = BlackNumberDao.getInstance(getApplicationContext());
        //拦截短信
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provier.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);
         innerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(innerSmsReceiver,intentFilter);
        super.onCreate();
        //监听电话的状态
        //电话管理者对象
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //监听电话状态
        myphoneStateListener = new MyphoneStateListener();
        telephonyManager.listen(myphoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    class MyphoneStateListener extends PhoneStateListener{
        //电话状态发生改变会触发的方法

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态 没有活动
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃状态 挂断电话
                    endCall(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void endCall(String phone) {
        int mode = dao.getMode(phone);
        if (mode==2||mode==3){
            //拦截电话
            //ServiceManager此类安卓对开发者隐藏，所以不能去直接调用其方法，需要反射调用
            try {
                //1.获取serviceManager字节码文件
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                //2.获取方法
                Method method = clazz.getMethod("getService", String.class);
                //3.反射调用此方法(静态方法不需要实例，所以传null)
                IBinder iBinder =(IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                //4.调用aidl文件对象方法
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Uri uri= Uri.parse("content://call_log/calls");
        //6.在内容解析器上，去注册内容观察者，通过内容观察者观察数据库的变化
         myContentObserver = new MyContentObserver(new Handler(),phone);
        getContentResolver().registerContentObserver(uri,true, myContentObserver);
    }


    class MyContentObserver extends ContentObserver{
        private final String phone;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler,String phone) {
            super(handler);
            this.phone=phone;
        }

        //数据库指定calls表发生改变的时候会去调用的方法
        public void onChange(boolean selfChange,String phone) {
            super.onChange(selfChange);
            getContentResolver().delete(Uri.parse("content://call_log/calls"),"number=?",new String[]{phone});
        }
    }
    @Override
    public void onDestroy() {
        //注销广播
        if (innerSmsReceiver!=null){
            unregisterReceiver(innerSmsReceiver);
        }
        //注销内容观察者
        if (myContentObserver!=null){
            getContentResolver().unregisterContentObserver(myContentObserver);
        }
        super.onDestroy();
        //取消对电话状态的监听
        telephonyManager.listen(myphoneStateListener,PhoneStateListener.LISTEN_NONE);
    }

    private class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信内容,获取发动短信的电话号码，如果是黑名单，模式为1和3，需要拦截
            //2.获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //3.循环遍历短信过程
            for (Object object : objects) {
                //4.获取短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                //5.获取短信对象的基本信息
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                int mode = dao.getMode(originatingAddress);
                if (mode==1||mode==3){
                    //拦截短信
                    abortBroadcast();
                }
            }
        }
    }
}
