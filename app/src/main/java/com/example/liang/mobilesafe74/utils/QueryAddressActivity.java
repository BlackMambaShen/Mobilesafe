package com.example.liang.mobilesafe74.utils;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.liang.mobilesafe74.R;

public class QueryAddressActivity extends AppCompatActivity {

    private EditText et_phone;
    private Button bt_query;
    private TextView tv_query_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);
        initUI();
    }

    private void initUI() {
         et_phone = (EditText) findViewById(R.id.et_phone);
         bt_query = (Button) findViewById(R.id.bt_query);
         tv_query_result = (TextView) findViewById(R.id.tv_query_result);

         bt_query.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String phone = et_phone.getText().toString().trim();
                 if (!TextUtils.isEmpty(phone)){

                 }else {
//                     AnimationUtils shake=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
//                     et_phone.startAnimation(shake);
                     Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                     //震动的毫秒值
                     vibrator.vibrate(2000);
                     //规律的震动(震动规则(不震动时间，震动时间....)，重复次数)
                     vibrator.vibrate(new long[]{2000,5000,2000,5000},-1);
                 }
             }
         });
         et_phone.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {

             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });
    }
}
