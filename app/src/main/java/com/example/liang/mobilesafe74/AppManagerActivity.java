package com.example.liang.mobilesafe74;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liang.mobilesafe74.db.domain.AppInfoBean;
import com.example.liang.mobilesafe74.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener{

    private List<AppInfoBean> mAppInfoList;
    private MyAdapter myAdapter;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //4.使用数据适配器
             myAdapter = new MyAdapter();
            lv_app_list.setAdapter(myAdapter);
        }
    };
    private ListView lv_app_list;
    //系统应用所在的集合
    private List<AppInfoBean> systemList;
    //用户应用所在的集合
    private List<AppInfoBean> customerList;
    private TextView tv_title_type;
    private AppInfoBean appInfoBean;
    private PopupWindow popupWindow;


    class MyAdapter extends BaseAdapter{
        //在lv中多添加一种类型条目,条目总数2种
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount()+1;
        }

        //区分索引指向的条目类型
        @Override
        public int getItemViewType(int position) {
            if (position==0||position==customerList.size()+1){
                //纯文本(状态码)
                return 0;
            }else {
                //图文(状态码)
                return 1;
            }
        }

        @Override
        public int getCount() {
            //系统应用+用户应用+2个灰色条目
            return customerList.size()+systemList.size()+2;
        }

        @Override
        public AppInfoBean getItem(int position) {
//            return mAppInfoList.get(position);
            if (position==0||position==customerList.size()+1){
                //纯文本条目不能去使用集合中的数据，所以此处返回null,后续手动维护
                return null;
            }else {
                //图文条目
                if (position<customerList.size()+1){
                    return customerList.get(position-1);
                }else {
                    return systemList.get(position-customerList.size()-2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //判断当前索引指向的条目类型状态码
            int itemViewType = getItemViewType(position);
            if (itemViewType==0){
                //纯文本
                ViewTitleHolder holder=null;
                if (convertView==null){
                    convertView = View.inflate(getApplicationContext(), R.layout.list_app_title_item, null);
                    holder=new ViewTitleHolder();
                    holder.tv_title_des=(TextView) convertView.findViewById(R.id.tv_title_des);
                    convertView.setTag(holder);
                }else {
                    holder= (ViewTitleHolder) convertView.getTag();
                }
                if (position==0){
                    holder.tv_title_des.setText("用户应用("+customerList.size()+")");
                }else {
                    holder.tv_title_des.setText("系统应用("+systemList.size()+")");
                }
                return convertView;
            }else {
                //图文条目
                ViewHolder holder=null;
                //1.判断convertView是否为空
                if (convertView==null){
                    convertView=View.inflate(getApplicationContext(),R.layout.list_app_item,null);
                    //2.获取控件,赋值给ViewHolder
                    holder=new ViewHolder();
                    holder.iv_icon=(ImageView) convertView.findViewById(R.id.iv_icon);
                    holder.tv_app_name=(TextView) convertView.findViewById(R.id.tv_app_name);
                    holder.tv_app_location = (TextView) convertView.findViewById(R.id.tv_app_location);
                    //3.holder放置在converView上
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                //4.获取holder中的字段(控件),赋值
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_app_name.setText(getItem(position).name);
                //5.显示安装位置
                if (getItem(position).isSdcard){
                    holder.tv_app_location.setText("sd卡应用");
                }else {
                    holder.tv_app_location.setText("内存应用");
                }
                //6.返回现有条目填充上数据
                return convertView;
            }
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_app_name;
        TextView tv_app_location;
    }

    static class ViewTitleHolder{

        TextView tv_title_des;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initTitleData();
        initListView();
    }

    private void initListView() {
         tv_title_type = (TextView) findViewById(R.id.tv_title_type);
        //1.找到控件
         lv_app_list = (ListView)findViewById(R.id.lv_app_list);
        //2.准备填充listView数据适配器的数值
        new Thread(){
            @Override
            public void run() {
                //3.发送消息告知主线程Listview可以使用数据适配器
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                systemList = new ArrayList<AppInfoBean>();
                customerList=new ArrayList<AppInfoBean>();
                //分割系统应用和用户应用
                for (AppInfoBean appInfoBean:mAppInfoList){
                    if (appInfoBean.isSystem){
                        systemList.add(appInfoBean);
                    }else {
                        customerList.add(appInfoBean);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (systemList!=null&&customerList!=null){
                    //滚动过程中调用方法
                    if (firstVisibleItem>=customerList.size()+1){
                        //滚动到了系统应用
                        tv_title_type.setText("系统应用("+systemList.size()+")");
                    }else {
                        //滚动到了用户应用
                        tv_title_type.setText("用户应用("+customerList.size()+")");
                    }
                }
            }
        });

        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //view点中条目指定的view对象
                       @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0||position==customerList.size()+1){
                    //纯文本条目不能去使用集合中的数据，所以此处返回null,后续手动维护
                    return;
                }else {
                    //图文条目
                    if (position<customerList.size()+1){
                         appInfoBean = customerList.get(position - 1);
                    }else {
                         appInfoBean = systemList.get(position - customerList.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    private void showPopupWindow(View view) {
        View popupview = View.inflate(this, R.layout.popupwindow_layout, null);
        TextView tv_uninstall = (TextView)popupview.findViewById(R.id.tv_uninstall);
        TextView tv_start = (TextView)popupview.findViewById(R.id.tv_start);
        TextView tv_share = (TextView)popupview.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        tv_start.setOnClickListener(this);

        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);

        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        //动画集合set
        AnimationSet set=new AnimationSet(true);
        //添加两个动画
        set.addAnimation(alphaAnimation);
        set.addAnimation(scaleAnimation);

        //创建窗体对象，指定宽高
         popupWindow = new PopupWindow(popupview,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,true);
        //2.设置一个透明的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        //3.制定窗体位置
        popupWindow.showAsDropDown(view,200,-view.getHeight());
        popupview.startAnimation(set);
    }

    @Override
    protected void onResume() {
        //重新获取数据
        new Thread(){
            @Override
            public void run() {
                //3.发送消息告知主线程Listview可以使用数据适配器
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                systemList = new ArrayList<AppInfoBean>();
                customerList=new ArrayList<AppInfoBean>();
                //分割系统应用和用户应用
                for (AppInfoBean appInfoBean:mAppInfoList){
                    if (appInfoBean.isSystem){
                        systemList.add(appInfoBean);
                    }else {
                        customerList.add(appInfoBean);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_uninstall:
                if (appInfoBean.isSystem){
                    Toast.makeText(getApplicationContext(),"此应用不能卸载",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent=new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:"+appInfoBean.packagename));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:
                //通过桌面去启动指定包名应用
                PackageManager pm=getPackageManager();
                //通过launch开启指定包名的意图，去开启应用
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(appInfoBean.packagename);
                if (launchIntentForPackage!=null){
                    startActivity(launchIntentForPackage);
                }else {
                    Toast.makeText(getApplicationContext(),"此应用不能被开启",Toast.LENGTH_SHORT).show();
                }
                break;
                //分享(微信，新浪，腾讯)平台
            case R.id.tv_share:
                //通过短信应用，向外发动短信
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,"分享一个应用，应用名称为"+appInfoBean.packagename);
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }

        if (popupWindow!=null){
            popupWindow.dismiss();
        }

    }

    private void initTitleData() {

        TextView tv_memory =(TextView) findViewById(R.id.tv_memory);

        TextView tv_sd_memory =(TextView) findViewById(R.id.tv_sd_memory);

        //1.获取磁盘(内存)可用大小,磁盘路径
        String path = Environment.getDataDirectory().getAbsolutePath();
        //2.获取sd卡可用大小
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //3.获取以上两个路径瞎文件夹的可用大小
        long space = getAvailSpace(path);
        long sdSpace = getAvailSpace(sdPath);
        //对bytes为单位的数值格式化
        String strSdSpace = Formatter.formatFileSize(this, sdSpace);
        String strSpace = Formatter.formatFileSize(this, space);
        tv_memory.setText("磁盘可用:"+strSpace);
        tv_sd_memory.setText("sd卡可用:"+strSdSpace);

    }

    //int代表多少个G
    private long getAvailSpace(String path) {
        //获取可用磁盘大小的类
        StatFs statFs = new StatFs(path);
        //获取可用区块的个数
        long count = statFs.getAvailableBlocks();
        //获取区块的大小
        long size = statFs.getBlockSize();
        //区块大小*可用区块个数==可用空间大小
        return count*size;
    }
}
