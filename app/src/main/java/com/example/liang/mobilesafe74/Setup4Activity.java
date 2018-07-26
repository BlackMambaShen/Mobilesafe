package com.example.liang.mobilesafe74;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.liang.mobilesafe74.utils.ConstantValue;
import com.example.liang.mobilesafe74.utils.SpUtil;

public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cb_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        initUI();
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    @Override
    protected void showNextPage() {
        boolean open_security = SpUtil.getBoolean(this, ConstantValue.open_security, false);
        if (open_security){
            Intent intent = new Intent(this, SetupOverActivity.class);
            startActivity(intent);
            finish();
            SpUtil.putBoolean(this, ConstantValue.setup_over,true);
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else {
            Toast.makeText(this,"请开启防盗保护设置!",Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        cb_box = (CheckBox)findViewById(R.id.cb_box);
        //1.是否选中状态的回显过程
        boolean open_security = SpUtil.getBoolean(getApplicationContext(), ConstantValue.open_security, false);
        cb_box.setChecked(open_security);
        //2.根据状态修改cb后续的文字显示
        if (open_security){
            cb_box.setText("安全设置已开启");
        }else {
            cb_box.setText("安全设置已关闭");
        }
        //3.点击过程中，cb选中状态发生改变的过程
        cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //isChecked点击后的状态
                //4.存储点击后的状态
                SpUtil.putBoolean(getApplicationContext(),ConstantValue.open_security,isChecked);
                //根据开启还是关闭的状态显示文字
                if (isChecked){
                    cb_box.setText("安全设置已开启");
                }else {
                    cb_box.setText("安全设置已关闭");
                }
            }
        });
    }
}
