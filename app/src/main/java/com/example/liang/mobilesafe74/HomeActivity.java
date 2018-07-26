package com.example.liang.mobilesafe74;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liang.mobilesafe74.utils.AToolActivity;
import com.example.liang.mobilesafe74.utils.ConstantValue;
import com.example.liang.mobilesafe74.utils.Md5Util;
import com.example.liang.mobilesafe74.utils.SettingActivity;
import com.example.liang.mobilesafe74.utils.SpUtil;

public class HomeActivity extends AppCompatActivity {

    private GridView gv_home;
    private String[] mTitleStr;
    private int[] mDrawableIds;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        initData();
    }


    private void initData() {
        //准备数据(文字（9组）,图片（9张）)
        mTitleStr =new String[] {"手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计",
                "手机杀毒", "缓存清理", "高级工具", "设置中心"};
        mDrawableIds = new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
                R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
                R.mipmap.ic_launcher};

        //九宫格控件设置数据适配器
        gv_home.setAdapter(new MyAdapter());
        //注册九宫格单个条目点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //position点中条目的索引
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //开启对话框
                        showDialog();
                        break;
                    case 1:
                        //跳转到通信卫士
                        startActivity(  new Intent(getApplicationContext(), BlackNumberActivity.class));
                        break;
                    case 2:
                        //跳转到通信卫士
                        startActivity(  new Intent(getApplicationContext(),AppManagerActivity.class));
                        break;
                    case 3:
                        //高级工具
                        startActivity(  new Intent(getApplicationContext(), ProcessManagerActivity.class));
                        break;
                    case 4:
                        //流量统计
                        startActivity(  new Intent(getApplicationContext(), TrafficActivity.class));
                        break;
                    case 5:
                        //杀死病毒
                        startActivity(  new Intent(getApplicationContext(), AnitVirusActivity.class));
                        break;
                    case 6:
                        //缓存清理
                        startActivity(  new Intent(getApplicationContext(), BaseCacheClearActivity.class));
                        break;
                    case 7:
                        //高级工具
                        startActivity(  new Intent(getApplicationContext(), AToolActivity.class));
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    //创建对话框
    private void showDialog() {
        //判断本地是否有存储密码(sp)
        String psd = SpUtil.getString(this, ConstantValue.mobileSafe_psd, "");
        if (TextUtils.isEmpty(psd)){
            //1.初始设置对话框
            showSetPsdDialog();
        }else {
            //2.确认密码对话框
            showConfirmPsdDialog();
        }
    }

    //确认密码的对话框
    private void showConfirmPsdDialog() {
        //自己定义对话框的展示样式 调用dialog.setView(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view=View.inflate(this,R.layout.dialog_confirm_psd,null);
        //让对话框显示一个自己定义的对话框
        dialog.setView(view,0,0,0,0);
        dialog.show();
        Button bt_submit =(Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel =(Button) view.findViewById(R.id.bt_cancel);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String confirmPsd = et_confirm_psd.getText().toString().trim();
                if (!TextUtils.isEmpty(confirmPsd)){
                    //将存储在sp中32位的密码获取出来，然后输入的密码同样进行md5，然后比对
                    String psd = SpUtil.getString(getApplicationContext(), ConstantValue.mobileSafe_psd, "");
                    if (psd.equals(Md5Util.encoder(confirmPsd))){
                        //进入手机防盗模块
                        Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
                        startActivity(intent);
                        //到新界面以后要去隐藏对话框
                        dialog.dismiss();
                    }else {
                        Toast.makeText(getApplicationContext(),"确认密码错误",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    //提示用户密码输入有为空的情况
                    Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //设置密码的对话框
    private void showSetPsdDialog() {
        //自己定义对话框的展示样式 调用dialog.setView(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view=View.inflate(this,R.layout.dialog_set_psd,null);
        //让对话框显示一个自己定义的对话框
        dialog.setView(view,0,0,0,0);
        dialog.show();
        Button bt_submit =(Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel =(Button) view.findViewById(R.id.bt_cancel);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String psd = et_set_psd.getText().toString().trim();
                String confirmPsd = et_confirm_psd.getText().toString().trim();
                if (!TextUtils.isEmpty(psd)&&!TextUtils.isEmpty(confirmPsd)){
                    if (psd.equals(confirmPsd)){
                        //进入手机防盗模块
                        Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
                        startActivity(intent);
                        //到新界面以后要去隐藏对话框
                        dialog.dismiss();
                        SpUtil.putString(getApplicationContext(),
                                ConstantValue.mobileSafe_psd, Md5Util.encoder(confirmPsd));
                    }else {
                        Toast.makeText(getApplicationContext(),"确认密码错误",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    //提示用户密码输入有为空的情况
                    Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //获取控件
    private void initUI() {
        gv_home = (GridView)findViewById(R.id.gv_home);
    }


    //适配器
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            //条目的总数
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView==null){
                view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            }else {
                view=convertView;
            }
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            ImageView iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStr[position]);
            iv_icon.setBackgroundResource(mDrawableIds[position]);
            return view;
        }
    }
}
