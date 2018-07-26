package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liang.mobilesafe74.R;


public class SettingClickView extends RelativeLayout {
    private final TextView tv_title;
    private TextView tv_des;


    public SettingClickView(Context context) {
        this(context,null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //xml转换成view 将条目转换成view对象,直接添加到了当前对应的view中
        View.inflate(context, R.layout.setting_click_view, this);
        //自定义组合控件标题描述
         tv_title = (TextView)findViewById(R.id.tv_title);
        tv_des =(TextView) findViewById(R.id.tv_des);
    }

    //设置标题内容
    public void setTitle(String title){
        tv_title.setText(title);
    }

    //设置描述内容
    public void setDes(String des){
        tv_des.setText(des);
    }



}
