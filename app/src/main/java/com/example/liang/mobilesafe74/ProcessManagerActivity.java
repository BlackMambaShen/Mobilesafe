package com.example.liang.mobilesafe74;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liang.mobilesafe74.db.domain.AppInfoBean;
import com.example.liang.mobilesafe74.db.domain.ProcessInfo;
import com.example.liang.mobilesafe74.engine.AppInfoProvider;
import com.example.liang.mobilesafe74.engine.ProcessInfoProvider;
import com.example.liang.mobilesafe74.utils.ConstantValue;
import com.example.liang.mobilesafe74.utils.SpUtil;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class ProcessManagerActivity extends AppCompatActivity implements View.OnClickListener{

    private Button bt_select_all;
    private Button bt_select_reverse;
    private Button bt_clear;
    private Button bt_setting;
    private TextView tv_process_count;
    private TextView tv_memory_info;
    private ListView lv_process_list;
    private int processCount;
    private List<ProcessInfo> processInfoList;
    private ArrayList<ProcessInfo> MsystemList;
    private ArrayList<ProcessInfo> McustomerList;

    private MyAdapter myAdapter;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //4.使用数据适配器
            myAdapter = new MyAdapter();
            lv_process_list.setAdapter(myAdapter);
            if (tv_des!=null&&McustomerList!=null){
                tv_des.setText("用户应用（"+McustomerList.size()+")");
            }
        }
    };
    private TextView tv_des;
    private ProcessInfo processInfo;
    private long availSpace;
    private long totalSpace;

    class MyAdapter extends BaseAdapter {
        //在lv中多添加一种类型条目,条目总数2种
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount()+1;
        }

        //区分索引指向的条目类型
        @Override
        public int getItemViewType(int position) {
            if (position==0||position==McustomerList.size()+1){
                //纯文本(状态码)
                return 0;
            }else {
                //图文(状态码)
                return 1;
            }
        }

        @Override
        public int getCount() {
            if (SpUtil.getBoolean(getApplicationContext(), ConstantValue.show_system,false)){
                //系统应用+用户应用+2个灰色条目
                return McustomerList.size()+MsystemList.size()+2;
            }else {
                return McustomerList.size()+1;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
//            return mAppInfoList.get(position);
            if (position==0||position==McustomerList.size()+1){
                //纯文本条目不能去使用集合中的数据，所以此处返回null,后续手动维护
                return null;
            }else {
                //图文条目
                if (position<McustomerList.size()+1){
                    return McustomerList.get(position-1);
                }else {
                    return MsystemList.get(position-McustomerList.size()-2);
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
                AppManagerActivity.ViewTitleHolder holder=null;
                if (convertView==null){
                    convertView = View.inflate(getApplicationContext(), R.layout.list_app_title_item, null);
                    holder=new AppManagerActivity.ViewTitleHolder();
                    holder.tv_title_des=(TextView) convertView.findViewById(R.id.tv_title_des);
                    convertView.setTag(holder);
                }else {
                    holder= (AppManagerActivity.ViewTitleHolder) convertView.getTag();
                }
                if (position==0){
                    holder.tv_title_des.setText("用户进程("+McustomerList.size()+")");
                }else {
                    holder.tv_title_des.setText("系统进程("+MsystemList.size()+")");
                }
                return convertView;
            }else {
                //图文条目
                ViewHolder holder=null;
                //1.判断convertView是否为空
                if (convertView==null){
                    convertView=View.inflate(getApplicationContext(),R.layout.list_process_item,null);
                    //2.获取控件,赋值给ViewHolder
                    holder=new ViewHolder();
                    holder.iv_icon=(ImageView) convertView.findViewById(R.id.iv_icon);
                    holder.tv_app_name=(TextView) convertView.findViewById(R.id.tv_app_name);
                    holder.tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
                    holder.cb_box  = (CheckBox)convertView.findViewById(R.id.cb_box);
                    //3.holder放置在converView上
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                //4.获取holder中的字段(控件),赋值
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_app_name.setText(getItem(position).name);
                String strMemSize = android.text.format.Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
                holder.tv_memory_info.setText(strMemSize);
                //本应用不能被选中,将CB隐藏掉
                if (getItem(position).packageName.equals(getPackageName())){
                    holder.cb_box.setVisibility(View.GONE);
                }else {
                    holder.cb_box.setVisibility(View.VISIBLE);
                }
                holder.cb_box.setChecked(getItem(position).isCheck);
                //6.返回现有条目填充上数据
                return convertView;
            }
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_app_name;
        TextView tv_memory_info;
        CheckBox cb_box;
    }

    static class ViewTitleHolder{

        TextView tv_title_des;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        initUI();
        initTitleData();
        initListData();
    }

    private void initListData() {
        //耗时
        getData();
    }

    private void initTitleData() {
         processCount = ProcessInfoProvider.getProcessCount(this);
        tv_process_count.setText("进程总数"+processCount);
        //获取可用内存大小，并且格式化
         availSpace = ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace = android.text.format.Formatter.formatFileSize(this, availSpace);

        //获取可用内存大小，并且格式化
         totalSpace = ProcessInfoProvider.getTotalSpace(this);
        String strTotalSpace = android.text.format.Formatter.formatFileSize(this, totalSpace);
        tv_memory_info.setText("剩余/总共:"+strAvailSpace+"/"+strTotalSpace);
    }

    private void initUI() {
         tv_process_count = (TextView) findViewById(R.id.tv_process_count);
         tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);

         lv_process_list = (ListView)findViewById(R.id.lv_process_list);

         bt_select_all = (Button)findViewById(R.id.bt_select_all);
         bt_select_reverse = (Button)findViewById(R.id.bt_select_reverse);
         bt_clear = (Button)findViewById(R.id.bt_clear);
         bt_setting = (Button)findViewById(R.id.bt_setting);

         bt_select_all.setOnClickListener(this);
        bt_select_reverse.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_setting.setOnClickListener(this);

        tv_des = (TextView) findViewById(R.id.tv_des);

        lv_process_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (MsystemList!=null&&McustomerList!=null){
                    //滚动过程中调用方法
                    if (firstVisibleItem>=McustomerList.size()+1){
                        //滚动到了系统应用
                        tv_des.setText("系统进程("+MsystemList.size()+")");
                    }else {
                        //滚动到了用户应用
                        tv_des.setText("用户进程("+McustomerList.size()+")");
                    }
                }
            }
        });

        lv_process_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //View是选中条目指向的view对象
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0||position==McustomerList.size()+1){
                    //纯文本条目不能去使用集合中的数据，所以此处返回null,后续手动维护
                    return;
                }else {
                    //图文条目
                    if (position<McustomerList.size()+1){
                        processInfo = McustomerList.get(position - 1);
                    }else {
                        processInfo = MsystemList.get(position - McustomerList.size() - 2);
                    }
                    if (processInfo!=null){
                        if (!processInfo.packageName.equals(getPackageName())){
                            //选中条目指定的对象和本应用的包名不一致，才需要去取反状态和设置单选框状态
                            processInfo.isCheck=!processInfo.isCheck;
                            //cb状态切换
                            //通过选中条目的view对象，findViewById找到此条目指向的cb，然后切换其状态码
                            CheckBox cb_box = (CheckBox)view.findViewById(R.id.cb_box);
                            cb_box.setChecked(processInfo.isCheck);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.bt_select_all:
            selectAll();
            break;

        case R.id.bt_select_reverse:
            selectReverse();
            break;

        case R.id.bt_clear:
            clearAll();
            break;

        case R.id.bt_setting:
            setting();
            break;
    }
    }

    private void setting() {
        Intent intent = new Intent(this, ProcessSettingActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //通知数据适配器刷新
        if (myAdapter!=null){
            myAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void clearAll() {
        List<ProcessInfo>killProcessList=new ArrayList<ProcessInfo>();
        //1.获取选中过程
        for (ProcessInfo processInfo:McustomerList){
            if (processInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            if (processInfo.isCheck){
                //不能再集合循环过程中去一处集合中的对象
                //记录需要杀死的用户进程
                killProcessList.add(processInfo);
            }
        }

        for (ProcessInfo processInfo:MsystemList){
            if (processInfo.isCheck){
                //不能再集合循环过程中去一处集合中的对象
                //记录需要杀死的用户进程
                killProcessList.add(processInfo);
            }
        }
        //5.循环遍历killProcessList,然后去移除McustomerList和MsystemList中的对象
        long totalReleaseSpace=0;
        for (ProcessInfo processInfo:killProcessList){
            //6.判断当前进程在那个集合中,从所在集合中移除
            if (McustomerList.contains(processInfo)){
                McustomerList.remove(processInfo);
            }

            if (MsystemList.contains(processInfo)){
                MsystemList.remove(processInfo);
            }
            //7.杀死记录在killProcessList中的进程
            ProcessInfoProvider.killProcess(this,processInfo);
            //记录释放空间的总大小
            totalReleaseSpace+=processInfo.memSize;
        }
        //8.在集合改变后通知数据适配器刷新
        if (myAdapter!=null){
            myAdapter.notifyDataSetChanged();
        }
        //9.进程总数的更新
        processCount-=killProcessList.size();
        //10.更新可用剩余空间(释放空间+原有剩余空间)
        availSpace+=totalReleaseSpace;
        //根据进程总数和剩余空间大小
        tv_process_count.setText("进程总数："+processCount);
        tv_memory_info.setText("剩余/总共："+ android.text.format.Formatter.formatFileSize(this,availSpace)+"/"+android.text.format.Formatter.formatFileSize(this,totalSpace));
        //12.通过toast,释放了多少空间，杀死了几个进程
        String totalRelease = android.text.format.Formatter.formatFileSize(this, totalReleaseSpace);
        Toast.makeText(getApplicationContext(),"杀死了"+killProcessList.size()+"个进程，释放了"+totalRelease+"空间",Toast.LENGTH_SHORT).show();
    }

    private void selectReverse() {
        //1.将所有的集合中的对象上ischeck字段设置为true,代表全选，派出当前应用
        for (ProcessInfo processInfo:McustomerList){
            if (processInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            processInfo.isCheck=!processInfo.isCheck;
        }
        for (ProcessInfo processInfo:MsystemList){
            processInfo.isCheck=!processInfo.isCheck;
        }
        //2.通知数据适配器刷新
        if (myAdapter!=null){
            myAdapter.notifyDataSetChanged();
        }
    }

    private void selectAll() {
        //1.将所有的集合中的对象上ischeck字段设置为true,代表全选，派出当前应用
        for (ProcessInfo processInfo:McustomerList){
            if (processInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            processInfo.isCheck=true;
        }
        for (ProcessInfo processInfo:MsystemList){
            processInfo.isCheck=true;
        }
        //2.通知数据适配器刷新
        if (myAdapter!=null){
            myAdapter.notifyDataSetChanged();
        }
    }

    private void getData(){
        //重新获取数据
        new Thread(){
            @Override
            public void run() {
                //3.发送消息告知主线程Listview可以使用数据适配器
                processInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
                MsystemList = new ArrayList<ProcessInfo>();
                McustomerList = new ArrayList<ProcessInfo>();
                //分割系统应用和用户应用
                for (ProcessInfo info:processInfoList){
                    if (info.isSystem){
                        MsystemList.add(info);
                    }else {
                        McustomerList.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }
}
