package com.example.liang.mobilesafe74.utils;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.liang.mobilesafe74.R;
import com.example.liang.mobilesafe74.ToastLocationActivity;
import com.example.liang.mobilesafe74.com.example.liang.service.AddressService;
import com.example.liang.mobilesafe74.com.example.liang.service.BlackNumberService;
import com.example.liang.mobilesafe74.com.example.liang.service.WatchDogService;

import view.SettingClickView;
import view.SettingItemView;

public class SettingActivity extends AppCompatActivity {

    private SettingClickView scv_toast_style;
    private String [] toastStyleDes;
    private int toast_style;
    private SettingClickView scv_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUpdate();
        initAddress();
        initToastStyle();
        initLocation();
        initBlacknumber();
        initAppLock();
    }

    //初始化程序锁
    private void initAppLock() {
        final SettingItemView siv_app_lock = (SettingItemView) findViewById(R.id.siv_app_lock);
        boolean isRunning = ServiceUtils.isRunning(this, "com.example.liang.mobilesafe74.com.example.liang.service.WatchDogService");
        siv_app_lock.setCheck(isRunning);
        siv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取反
                boolean checked = siv_app_lock.isChecked();
                siv_app_lock.setCheck(!checked);
                if (!checked){
                    //开启服务
                    startService(new Intent(getApplicationContext(),WatchDogService.class));
                }else {
                    //关闭服务
                    stopService(new Intent(getApplicationContext(),WatchDogService.class));
                }
            }
        });
    }

    //拦截黑名单短信和电话
    private void initBlacknumber() {
        final SettingItemView siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);
        boolean isRunning = ServiceUtils.isRunning(this, "com.example.liang.mobilesafe74.com.example.liang.service.BlackNumberService");
        siv_blacknumber.setCheck(isRunning);
        siv_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取反
                boolean checked = siv_blacknumber.isChecked();
                siv_blacknumber.setCheck(!checked);
                if (!checked){
                    //开启服务
                    startService(new Intent(getApplicationContext(),BlackNumberService.class));
                }else {
                    //关闭服务
                    stopService(new Intent(getApplicationContext(),BlackNumberService.class));
                }
            }
        });
    }

    //双击居中view的处理
    private void initLocation() {
         scv_location = (SettingClickView)findViewById(R.id.scv_location);
         scv_location.setTitle("归属地提示框的位置");
         scv_location.setDes("设置归属地提示框的位置");
         scv_location.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
             }
         });
    }

    //自定义土司组合控件
    private void initToastStyle() {
         scv_toast_style =(SettingClickView) findViewById(R.id.scv_toast_style);
         scv_toast_style.setTitle("设置归属地显示风格");
         //1.创建描述文字所在类型的string数组
        toastStyleDes = new String[]{"透明", "橙色", "蓝色", "灰色", "橙色"};
        //2.SP获取土司显示样式的索引值(int),用于获取描述的文字
         toast_style = SpUtil.getInt(this, ConstantValue.toast_style, 0);
        //3.通过索引，获取字符串数组中的文字，显示给描述内容控件
        scv_toast_style.setDes(toastStyleDes[toast_style]);
        //4.监听点击事件，弹出对话框
        scv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //5.创建土司显示样式的对话框
                showDialog();
            }
        });
    }

    //5.创建土司显示样式的对话框
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("请选择归属地样式");
        //选择单个条目的事件监听(1.string类型数组,描述颜色文字数组；2.弹出对话框的时候选中条目的索引值;3.点击条目后触发的点击事件 )
        builder.setSingleChoiceItems(toastStyleDes, toast_style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //1.记录选中条目的索引值；2.关闭对话框；3.显示选中色值文字
                SpUtil.putInt(getApplicationContext(),ConstantValue.toast_style,which);
                dialog.dismiss();
                scv_toast_style.setDes(toastStyleDes[which]);
            }
        });
        //消极按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    //是否显示电话号码归属地的方法
    private void initAddress() {
        final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);
        //对服务是否开启的状态显示
        boolean isRunning = ServiceUtils.isRunning(this, "com.example.liang.mobilesafe74.com.example.liang.service.AddressService");
        siv_address.setCheck(isRunning);
        //点击过程中状态的切换
        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回点击前的状态
                boolean isChecked = siv_address.isChecked();
                siv_address.setCheck(!isChecked);
                if (!isChecked){
                    //开启服务 管理土司
                    startService(new Intent(getApplicationContext(),AddressService.class));
                }else {
                    //关闭服务 不显示土司
                    stopService(new Intent(getApplicationContext(),AddressService.class));
                }
            }
        });
    }

    private void initUpdate() {
        final SettingItemView siv_update =(SettingItemView) findViewById(R.id.siv_update);
        //获取已有的开关状态，用作显示
        boolean open_update = SpUtil.getBoolean(this, ConstantValue.open_update, false);
        //是否选中根据上次存储的结果去做决定
        siv_update.setCheck(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果是选中的，点击过后，变成未选中

                //如果之前是未选中的，点击过后，变成选中的
                //获取之前的选中状态
                boolean ischeck = siv_update.isChecked();
                siv_update.setCheck(!ischeck);
                //将取反后的状态存到sp中
                SpUtil.putBoolean(getApplicationContext(),ConstantValue.open_update,!ischeck);
            }
        });
    }

}
