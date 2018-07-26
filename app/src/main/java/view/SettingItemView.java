package view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liang.mobilesafe74.R;


public class SettingItemView extends RelativeLayout {
    public static final String namespace="http://schemas.android.com/apk/res/com.example.liang.mobilesafe74";
    private static final String tag="SettingItemView";
    private CheckBox cb_box;
    private TextView tv_des;
    private String destitle;
    private String desoff;
    private String deson;

    public SettingItemView(Context context) {
        this(context,null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //xml转换成view 将条目转换成view对象,直接添加到了当前对应的view中
        View.inflate(context, R.layout.setting_item_view, this);
        //自定义组合控件标题描述
        TextView tv_title = (TextView)findViewById(R.id.tv_title);
        tv_des =(TextView) findViewById(R.id.tv_des);
        cb_box = (CheckBox) findViewById(R.id.cb_box);

        //获取自定义以及原生属性的操作，写在这里
        initAttrs(attrs);
        //获取布局w文件中的字符串，
        tv_title.setText(destitle);
    }

    //构造方法中维护好的属性集合
    //返回属性集合中自定义属性属性值
    private void initAttrs(AttributeSet attrs) {
        //获取属性的总个数
//        Log.i(tag,""+attrs.getAttributeCount());
//        //获取属性名称以及属性值
//        for (int i = 0; i <attrs.getAttributeCount() ; i++) {
//            Log.i(tag,"name="+ attrs.getAttributeName(i));
//            Log.i(tag,"value="+ attrs.getAttributeValue(i));
//            Log.i(tag,"========================");
//        }

        destitle = attrs.getAttributeValue(namespace, "destitle");
        desoff = attrs.getAttributeValue(namespace, "desoff");
        deson = attrs.getAttributeValue(namespace, "deson");
//        Log.i(tag,destitle);
//        Log.i(tag,desoff);
//        Log.i(tag,deson);

    }

    //返回当前settingItemView是否选中状态,true开启,false关闭
    public boolean isChecked(){
        //由cb选中结果决定当前条目是否开启
        return cb_box.isChecked();
    }

    //是否作为开启的变量，由点击过程中去做传递
    public void  setCheck(boolean isCheck){
        //当前条目选择过程中,cb选中状态也随着变化
        cb_box.setChecked(isCheck);
        if (isCheck){
            tv_des.setText(deson);
        }else {
            tv_des.setText(desoff);
        }

    }

}
