package com.example.liang.mobilesafe74;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.liang.mobilesafe74.com.example.liang.service.LockScreenService;
import com.example.liang.mobilesafe74.utils.ConstantValue;
import com.example.liang.mobilesafe74.utils.ServiceUtils;
import com.example.liang.mobilesafe74.utils.SpUtil;

public class ProcessSettingActivity extends AppCompatActivity {

    private CheckBox cb_lock_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);

        initSystemShow();
        initLockScreenClear();
    }

    //锁屏清理
    private void initLockScreenClear() {
        cb_lock_clear =(CheckBox) findViewById(R.id.cb_lock_clear);
        //根据锁屏清理服务是否开启去，决定是否单选框选中
        boolean isRunning = ServiceUtils.isRunning(this, "com.example.liang.mobilesafe74.com.example.liang.service.LockScreenService");
        if (isRunning){
            cb_lock_clear.setText("锁屏清理已开启");
        }else {
            cb_lock_clear.setText("锁屏清理已关闭");
        }
        //cb_lock_clear,选中状态维护
        cb_lock_clear.setChecked(isRunning);
        //对选中状态进行监听
        cb_lock_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //isChecked就作为是否选中的状态
                if (isChecked){
                    cb_lock_clear.setText("锁屏清理已开启");
                    //开启服务
                    startService(new Intent(getApplicationContext(),LockScreenService.class));
                }else {
                    cb_lock_clear.setText("锁屏清理已关闭");
                    //关闭服务
                    stopService(new Intent(getApplicationContext(),LockScreenService.class));
                }
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.show_system,isChecked);
            }
        });
    }

    private void initSystemShow() {
        final CheckBox cb_show_system =(CheckBox) findViewById(R.id.cb_show_system);
        //对之前存储锅的状态进行回显
        boolean showSystem = SpUtil.getBoolean(this, ConstantValue.show_system, false);
        //单选框的选中状态
        cb_show_system.setChecked(showSystem);
        if (showSystem){
            cb_show_system.setText("显示系统进程");
        }else {
            cb_show_system.setText("隐藏系统进程");
        }
        //对选中状态进行监听
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //isChecked就作为是否选中的状态
                if (isChecked){
                    cb_show_system.setText("显示系统进程");
                }else {
                    cb_show_system.setText("隐藏系统进程");
                }
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.show_system,isChecked);
            }
        });
    }
}
