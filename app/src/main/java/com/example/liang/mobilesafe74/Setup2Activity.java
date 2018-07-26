package com.example.liang.mobilesafe74;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.liang.mobilesafe74.utils.ConstantValue;
import com.example.liang.mobilesafe74.utils.SpUtil;

import view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView siv_sim_bound;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        initUI();
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    @Override
    protected void showNextPage() {
        String serialNumber = SpUtil.getString(this, ConstantValue.sim_number, "");
        if (!TextUtils.isEmpty(serialNumber)){
            Intent intent = new Intent(this, Setup3Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else {
            Toast.makeText(this,"请绑定SIM卡！",Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        siv_sim_bound =(SettingItemView) findViewById(R.id.siv_sim_bound);
        //回显的过程(读取已有的绑定状态,用作显示,sp中是否存储了sim卡的序列号)
        String sim_number = SpUtil.getString(this, ConstantValue.sim_number, "");
        //判断是否序列卡号为空
        if (TextUtils.isEmpty(sim_number)){
            siv_sim_bound.setCheck(false);
        }else {
            siv_sim_bound.setCheck(true);
        }
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取原有的状态
                boolean ischecked = siv_sim_bound.isChecked();
                //将原有状态取反,设置给当前条目
                siv_sim_bound.setCheck(!ischecked);
                if (!ischecked){
                    //存储（序列卡号）
                    //获取sim卡序列号 telephoneManager
                    TelephonyManager manager =(TelephonyManager)
                            getSystemService(Context.TELEPHONY_SERVICE);
                    //获取序列卡号
                    if (ContextCompat.checkSelfPermission(Setup2Activity.this,
                            Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(Setup2Activity.this,new String[]
                                {Manifest.permission.READ_PHONE_STATE},1);
                    }else {
                        String simSerialNumber = manager.getSimSerialNumber();
                        //存储
                        SpUtil.putString(getApplicationContext(),ConstantValue.sim_number,simSerialNumber);
                    }
                }else {
                    //将节点从sp中删除
                    SpUtil.remove(getApplicationContext(),ConstantValue.sim_number);
                }
            }
        });
    }
}
