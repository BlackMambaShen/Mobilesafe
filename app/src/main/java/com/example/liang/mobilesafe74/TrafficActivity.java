package com.example.liang.mobilesafe74;

import android.net.TrafficStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TrafficActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        //获取流量(R 手机（3，4G）下栽流量)
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        //(T total(手机总流量 上传+下载))
        long mobileTxBytes = TrafficStats.getMobileTxBytes();
        //（手机+wifi下栽流量的总和）
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        //手机+WiFi 上传+下载
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        //流量获取模块（发送短信）

    }
}
