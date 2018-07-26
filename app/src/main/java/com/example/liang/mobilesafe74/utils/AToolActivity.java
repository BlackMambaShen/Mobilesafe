package com.example.liang.mobilesafe74.utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liang.mobilesafe74.AppLockActivity;
import com.example.liang.mobilesafe74.CommonNumberActivity;
import com.example.liang.mobilesafe74.R;
import com.example.liang.mobilesafe74.engine.SmsBackUp;

import java.io.File;

public class AToolActivity extends AppCompatActivity {

    private ProgressBar pb_bar;
    private TextView tv_commonnumber_query;
    private TextView tv_app_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
        //电话归属地查询的方法
        initPhoneAddress();
        //短信备份
        initSmsBackUp();
        //常用号码查询
        initCommonNumberQuery();
        initAppLock();
    }

    private void initAppLock() {
        tv_app_lock = (TextView)findViewById(R.id.tv_app_lock);
        tv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
            }
        });
    }

    private void initCommonNumberQuery() {
        tv_commonnumber_query = (TextView)findViewById(R.id.tv_commonnumber_query);
        tv_commonnumber_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CommonNumberActivity.class));
            }
        });
    }

    private void initSmsBackUp() {
        TextView tv_sms_backup =(TextView) findViewById(R.id.tv_sms_backup);
         pb_bar =(ProgressBar) findViewById(R.id.pb_bar);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackUpDialog();
            }
        });
    }

    private void showSmsBackUpDialog() {
        //1.创建一个带进度条的对话框
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("短信备份");
        //指定进度条的样式
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //展示短信进度条
        dialog.show();
        //直接调用备份短信方法即可
        new Thread(){
            @Override
            public void run() {
//                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sms74.xml";
                String path = getCacheDir() + File.separator + "sms74.xml";
                SmsBackUp.backup(AToolActivity.this, getApplicationContext(), path, new SmsBackUp.CallBack() {
                    @Override
                    public void setMax(int max) {
                        //由开发者自己决定,使用对话框还是进度条
                        dialog.setMax(max);
                        pb_bar.setMax(max);

                    }

                    @Override
                    public void setProgress(int index) {
                        //由开发者自己决定,使用对话框还是进度条
                        dialog.setProgress(index);
                        pb_bar.setProgress(index);
                    }
                });
                dialog.dismiss();
            }
        }.start();
    }

    private void initPhoneAddress() {
        TextView tv_query_address =(TextView) findViewById(R.id.tv_query_address);
        tv_query_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QueryAddressActivity.class));
            }
        });
    }
}
