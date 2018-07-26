package com.example.liang.mobilesafe74;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class CacheClearActivity extends AppCompatActivity {

    private Button bt_clear;
    private ProgressBar pb_bar;
    private TextView tv_name;
    private LinearLayout ll_add_text;
    private static  int index=0;
    private static final int CHECK_CACHE_APP=101;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
        switch (msg.what){
            case CHECK_CACHE_APP:
                tv_name.setText((String)msg.obj);
                break;
            case CHECK_FINISH:
                tv_name.setText("扫描完成");
                break;
        }
        }
    };
    private static final int CHECK_FINISH=102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        initUI();
        initData();
    }

    //遍历手机所有的应用，获取有缓存的应用，用作显示
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                //1.获取包的管理者
                PackageManager pm = getPackageManager();
                //2.获取安装在手机上的所有的应用
                List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
                //3.给进度条设置最大值(手机中所有应用的总数)
                pb_bar.setMax(installedPackages.size());
                //4.遍历每一个应用，获取有缓存的应用信息（应用名称，图标，缓存大小。包名）
                for (PackageInfo packageInfo:installedPackages){
                    //包名作为获取缓存信息的条件
                    String packageName = packageInfo.packageName;
                    getPackageCache(packageName);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    index++;
                    pb_bar.setProgress(index);
                    //每循环一次就将检测应用的名称发送给主线程显示
                    Message msg=Message.obtain();
                    msg.what=CHECK_CACHE_APP;
                    String name = null;
                    try {
                        name = pm.getApplicationInfo(packageName, 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    msg.obj=name;
                    handler.sendMessage(msg);
                }
                Message msg=Message.obtain();
                msg.what=CHECK_FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    //通过包名获取此包名指向应用的缓存信息
    private void getPackageCache(String packageName) {

    }

    private void initUI() {
        bt_clear = (Button)findViewById(R.id.bt_clear);
        pb_bar = (ProgressBar)findViewById(R.id.pb_bar);
        tv_name = (TextView)findViewById(R.id.tv_name);
        ll_add_text = (LinearLayout)findViewById(R.id.ll_add_text);

//        bt_clear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    Class<?>clazz=Class.forName("android.content.pm.PackageManager");
//                    clazz.getMethod("freeStorageAndNotify",long.class,)
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }
}
