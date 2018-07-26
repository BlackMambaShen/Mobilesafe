package com.example.liang.mobilesafe74;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.liang.mobilesafe74.utils.ConstantValue;
import com.example.liang.mobilesafe74.utils.SpUtil;

public class Setup3Activity extends BaseSetupActivity {

    private EditText et_phone_number;
    private Button bt_select_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initUI();
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    @Override
    protected void showNextPage() {
        //点击按钮以后需要获取输入框中的联系人，再做下一页操作
        String phone = et_phone_number.getText().toString().trim();
        //在sp存储到联系人后才可以跳转到下一个界面
//        String contact_phone = SpUtil.getString(getApplicationContext(), ConstantValue.contact_phone, "");
        if (!TextUtils.isEmpty(phone)){
            Intent intent = new Intent(this, Setup4Activity.class);
            startActivity(intent);
            finish();
            //如果是输入的电话号码，则需要去保存
            SpUtil.putString(getApplicationContext(), ConstantValue.contact_phone,phone);
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else {
            Toast.makeText(getApplicationContext(),"请输入电话号码",Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        //显示电话号码的输入框
         et_phone_number =(EditText) findViewById(R.id.et_phone_number);
         //获取联系人电话号码的回显
        String phone = SpUtil.getString(getApplicationContext(), ConstantValue.contact_phone, "");
        et_phone_number.setText(phone);
        //点击选择联系人的对话框
         bt_select_number = (Button) findViewById(R.id.bt_select_number);
         bt_select_number.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getApplicationContext(),ContactListActivity.class);
                 startActivityForResult(intent,0);
             }
         });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null){
            //返回当前界面要去接收结果的方法
            String phone = data.getStringExtra("phone");
            //将字符过滤
            phone = phone.replace("-", "").replace(" ","").trim();
            et_phone_number.setText(phone);
            //存储联系人到sp中
            SpUtil.putString(getApplicationContext(), ConstantValue.contact_phone,phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
