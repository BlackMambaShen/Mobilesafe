package com.example.liang.mobilesafe74;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liang.mobilesafe74.db.dao.BlackNumberDao;
import com.example.liang.mobilesafe74.db.domain.BlackNumberInfo;

import java.util.List;
import java.util.Random;

public class BlackNumberActivity extends AppCompatActivity {

    private Button bt_add;
    private ListView lv_blacknumber;
    private BlackNumberDao mDao;
    private List<BlackNumberInfo> blackNumberInfoList;
    private MyAdapter myAdapter;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //4.告知lv可以去设置数据适配器
            if (myAdapter==null){
                myAdapter = new MyAdapter();
            }
             lv_blacknumber.setAdapter(myAdapter);
        }
    };
    private int mode=1;
    private boolean isLoad=false;
    private int count;

    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return blackNumberInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return blackNumberInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
//            View view=null;
//            if (convertView==null){
//                view = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
//            }else {
//                view=convertView;
//            }
            //复用ViewHolder步骤1
            ViewHolder holder=null;
            if (convertView==null){
                convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
                //2.减少findViewById次数
                //复用ViewHolder步骤3
                holder=new ViewHolder();
                //复用ViewHolder步骤4
                holder.tv_mode = (TextView)convertView.findViewById(R.id.tv_mode);
                holder.tv_phone =(TextView) convertView.findViewById(R.id.tv_phone);
                holder.iv_delete = (ImageView)convertView.findViewById(R.id.iv_delete);
                //复用ViewHolder步骤5
                convertView.setTag(holder);
            }else {
                holder= (ViewHolder) convertView.getTag();
            }

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //1.数据库删除
                    mDao.delete(blackNumberInfoList.get(position).phone);
                    //2.集合中的删除，通知数据适配器刷新
                    blackNumberInfoList.remove(position);
                    //3.通知适配器刷新
                    if (myAdapter!=null){
                        myAdapter.notifyDataSetChanged();
                    }
                }
            });
            holder.tv_phone.setText(blackNumberInfoList.get(position).phone);
            int mode = Integer.parseInt(blackNumberInfoList.get(position).mode);
            switch (mode){
                case 1:
                    holder.tv_mode.setText("拦截短信");
                    break;
                case 2:
                    holder.tv_mode.setText("拦截电话");
                    break;
                case 3:
                    holder.tv_mode.setText("拦截所有");
                    break;
            }
            return convertView;
        }
    }


    //复用ViewHolder步骤2
     static class ViewHolder {
         TextView tv_phone;
         TextView tv_mode;
         ImageView iv_delete;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);
        initUI();
        initData();
    }

    private void initData() {
        //获取数据库中所有的电话号码
        new Thread(){
            public void run() {
                //1.获取操作黑名单数据库的对象
                mDao = BlackNumberDao.getInstance(getApplicationContext());
//                 //查询所有数据
//                blackNumberInfoList = mDao.findAll();
                //查询部分数据
                blackNumberInfoList=mDao.find(0);
                 count = mDao.getCount();
                //通过消息机制 可以使用包含数据的集合
                handler.sendEmptyMessage(0);

            }
        }.start();
    }


    private void initUI() {
         bt_add = (Button) findViewById(R.id.bt_add);
         lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
         bt_add.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                showDialog();
             }
         });
         //监听lv滚动状态
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            //滚动过程中，状态发生改变调用方法()
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //AbsListView.OnScrollListener.SCROLL_STATE_FLING飞速滚动
               //AbsListView.OnScrollListener.SCROLL_STATE_IDLE 空闲状态
               // AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 拿手触摸的滚动状态
                if (blackNumberInfoList!=null){
                    //条件一：滚动到停止状态
                    //条件二：最后一个条目可见（）
                    if (scrollState==AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            &&lv_blacknumber.getLastVisiblePosition()>=blackNumberInfoList.size()-1
                            &&!isLoad){
                        //isLoad防止重复加载的变量
                        //如果当前正在加载isLoad就会为TRUE,加载完毕后，再将isLoad改为false
                        //如果下一次加载需要去做执行的时候，会判断上述isLoad变量，是否为false
                        //如果条目总数大于集合大小的时候，才可以去继续加载更多
                        if (count>blackNumberInfoList.size()){
                            //加载下一页数据
                            new Thread(){
                                public void run() {
                                    //1.获取操作黑名单数据库的对象
                                    mDao = BlackNumberDao.getInstance(getApplicationContext());
                                    //查询部分数据
                                    List<BlackNumberInfo> moreData = mDao.find(blackNumberInfoList.size());
                                    //添加下一页数据的过程
                                    blackNumberInfoList.addAll(moreData);
                                    //通知适配器刷新
                                    handler.sendEmptyMessage(0);
                                }
                            }.start();
                        }
                    }
                }
            }
            //滚动过程中调用方法
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
        dialog.setView(view,0,0,0,0);
        final EditText et_phone = (EditText)view.findViewById(R.id.et_phone);
        RadioGroup rg_group = (RadioGroup)view.findViewById(R.id.rg_group);
        Button bt_submit = (Button)view.findViewById(R.id.bt_submit);
        Button bt_cancel =(Button)view.findViewById(R.id.bt_cancel);
        //监听其选中条目的切换
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_sms:
                        mode=1;
                        break;
                    case R.id.rb_phone:
                        mode=2;
                        break;
                    case R.id.rb_all:
                        mode=3;
                        break;
                }
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.获取输入框的电话号码
                String phone = et_phone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)){
                    //2.数据库插入当前输入的拦截电话号码
                    mDao.insert(phone,mode+"");
                    //3.让数据库和集合保持同步(1.数据库重新读一遍，2.手动向集合添加对象)
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.phone=phone;
                    blackNumberInfo.mode=mode+"";
                    //4.将对象插入集合最顶部
                    blackNumberInfoList.add(0,blackNumberInfo);
                    //5.通知数据适配器刷新
                    if (myAdapter!=null){
                        myAdapter.notifyDataSetChanged();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"请输入拦截号码",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
