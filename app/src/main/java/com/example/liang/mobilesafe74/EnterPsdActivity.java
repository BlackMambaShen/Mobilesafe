package com.example.liang.mobilesafe74;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPsdActivity extends AppCompatActivity {

    private String packageName;
    private TextView tv_app_name;
    private ImageView iv_app_icon;
    private EditText et_psd;
    private Button bt_commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取包名
         packageName = getIntent().getStringExtra("packageName");
         initUI();
         initData();
        setContentView(R.layout.activity_enter_psd);
    }

    private void initData() {
        //通过传递过来的包名获取拦截应用的图标以及名称
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            Drawable icon = applicationInfo.loadIcon(pm);
            iv_app_icon.setBackgroundDrawable(icon);
            tv_app_name.setText(applicationInfo.loadLabel(pm).toString());
        } catch (Exception e) {

        }
        bt_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = et_psd.getText().toString();
                if (!TextUtils.isEmpty(psd)){
                    if (psd.equals("123")){
                        //解锁,进入应用,告知看门狗不要去监听已经解锁的应用,发送广播
                        finish();
                        Intent intent=new Intent("android.intent.action.SKIP");
                        intent.putExtra("packageName",packageName);
                        sendBroadcast(intent);

                    }else {
                        Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initUI() {
         tv_app_name = (TextView)findViewById(R.id.tv_app_name);
         iv_app_icon = (ImageView)findViewById(R.id.iv_app_icon);
         et_psd = (EditText)findViewById(R.id.et_psd);
         bt_commit = (Button)findViewById(R.id.bt_commit);
    }

    @Override
    public void onBackPressed() {
        //跳转到桌面
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        super.onBackPressed();
    }
}
