package com.example.liang.mobilesafe74.com.example.liang.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liang.mobilesafe74.R;
import com.example.liang.mobilesafe74.engine.AddressDao;
import com.example.liang.mobilesafe74.utils.ConstantValue;
import com.example.liang.mobilesafe74.utils.SpUtil;

public class AddressService extends Service {
    private TelephonyManager telephonyManager;
    private MyphoneStateListener myphoneStateListener;
    private static final String tag="AddressService";
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View ViewToast;
    private WindowManager windowManager;
    private TextView tv_toast;
    private int screenWidth;
    private int screenHeight;
    private InnerOutCallReceiver innerOutCallReceiver;

    public AddressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //第一次开启 就需要管理土司的显示
        //电话状态的监听
        //电话管理者对象
         telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
         //监听电话状态
         myphoneStateListener = new MyphoneStateListener();
        telephonyManager.listen(myphoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
        //获取窗体对象
         windowManager =(WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeight = windowManager.getDefaultDisplay().getHeight();
        //监听播出电话的广播接收者
        //过滤条件
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        //创建广播接收者
         innerOutCallReceiver = new InnerOutCallReceiver();
        registerReceiver(innerOutCallReceiver,intentFilter);
    }


    class InnerOutCallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到此广播后，需要显示自定义的土司，显示播出归属地号码
            //获取播出电话号码的字符串
            String phone = getResultData();
            showToast(phone);
        }
    }
    class MyphoneStateListener extends PhoneStateListener{
        //电话状态发生改变会触发的方法

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态 没有活动
                    Log.i(tag,"挂断电话···");
                    //挂断电话，需要移除土司
                    if (windowManager!=null&&ViewToast!=null){
                        windowManager.removeView(ViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃状态 展示土司
                    Log.i(tag,"响铃了···");
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void showToast(String incomingNumber) {
//        Toast.makeText(getApplicationContext(),"incomingNumber:"+incomingNumber,Toast.LENGTH_SHORT).show();
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        //在响铃时显示土司 和电话类型一致
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //指定所在位置(土司制定在左上角)
        params.gravity= Gravity.LEFT+Gravity.TOP;
        //土司显示效果(土司布局文件)
        //xml--->view,将土司挂在到windowManager窗体上才能显示
        ViewToast = View.inflate(this, R.layout.toast_view, null);
        tv_toast = (TextView)ViewToast.findViewById(R.id.tv_toast);
        tv_toast.setText(incomingNumber);
        ViewToast.setOnTouchListener(new View.OnTouchListener() {
            public int startY;
            public int startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        int disX = moveX - startX;
                        int disY = moveY - startY;
                        params.x = params.x + disX;
                        params.y = params.y + disY;
                        //容错处理
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.x > screenWidth - ViewToast.getWidth()) {
                            params.x = screenWidth - ViewToast.getWidth();
                        }
                        if (params.y > screenHeight - ViewToast.getHeight() - 22) {
                            params.y = screenHeight - ViewToast.getHeight() - 22;
                        }
                        //告知土司按照手势的移动，去做位置的更新
                        windowManager.updateViewLayout(ViewToast, params);
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //4.存储移动到的位置
                        SpUtil.putInt(getApplicationContext(), ConstantValue.location_x, params.x);
                        SpUtil.putInt(getApplicationContext(), ConstantValue.location_y, params.y);
                        break;
                }
                return true;
            }});
        //读取sp中存储土司位置的x,y坐标值
        //params.x为土司左上角的X的坐标
        params.x = SpUtil.getInt(getApplicationContext(), ConstantValue.location_x,0);
        params.y = SpUtil.getInt(getApplicationContext(), ConstantValue.location_y, 0);
        //从sp中获取色值文字的索引,匹配图片,用作展示
        int[]drawableIds=new int[]{R.drawable.ic_launcher_background,R.drawable.reset_setup_bg_press,
        R.drawable.selector_atool_item_bg,R.drawable.selector_next_btn_bg,R.drawable.selector_number_btn_bg};

        int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.toast_style, 0);
        tv_toast.setBackgroundResource(drawableIds[toastStyleIndex]);
        //在窗体上挂在一个view
        windowManager.addView(ViewToast,params);
        //获取到了来电号码后，需要做来电号码查询
//        query(incomingNumber);
    }

    private void query(final String incomingNumber) {
        new Thread(){
            @Override
            public void run() {
                AddressDao.getAddress(incomingNumber);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        //销毁土司 取消对电话状态的监听
        if (telephonyManager!=null&&myphoneStateListener!=null){
            telephonyManager.listen(myphoneStateListener,PhoneStateListener.LISTEN_NONE);
        }
        if (innerOutCallReceiver!=null){
            //去电广播接收者的注销过程
            unregisterReceiver(innerOutCallReceiver);
        }
        super.onDestroy();
    }

}
