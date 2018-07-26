package com.example.liang.mobilesafe74;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.liang.mobilesafe74.db.dao.AppLockDao;
import com.example.liang.mobilesafe74.db.domain.AppInfoBean;
import com.example.liang.mobilesafe74.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends AppCompatActivity {

    private Button bt_unlock;
    private Button bt_lock;
    private LinearLayout ll_unlock;
    private TextView tv_unlock;
    private ListView lv_unlock;
    private LinearLayout ll_lock;
    private TextView tv_lock;
    private ListView lv_lock;
    private List<AppInfoBean> appInfoList;
    private ArrayList<AppInfoBean> Locklist;
    private ArrayList<AppInfoBean> UnLockList;
    private AppLockDao mDao;
    private MyAdapter mLockAdapter;
    private MyAdapter mUnlockAdapter;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           //接收到消息后，请填充已加锁和未加锁的数据适配器
             mLockAdapter = new MyAdapter(true);
             lv_lock.setAdapter(mLockAdapter);
             mUnlockAdapter = new MyAdapter(false);
             lv_unlock.setAdapter(mUnlockAdapter);
        }
    };
    private TranslateAnimation translateAnimation;

    class MyAdapter extends BaseAdapter{
        //true 已加锁的数据适配器   false未加锁的数据适配器
        //用于区分已加锁和未加锁应用的标示
        private boolean isLock;
        public MyAdapter(boolean isLock){
            this.isLock=isLock;
        }

        @Override
        public int getCount() {
            if (isLock){
                tv_lock.setText("已加锁应用："+Locklist.size());
                return Locklist.size();
            }else {
                tv_unlock.setText("未加锁应用："+UnLockList.size());
                return UnLockList.size();
            }
        }

        @Override
        public AppInfoBean getItem(int position) {
            if (isLock){
                return Locklist.get(position);
            }else {
                return UnLockList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if (convertView==null){
                convertView = View.inflate(getApplicationContext(), R.layout.listview_islock_item, null);
                holder=new ViewHolder();
                holder.iv_icon=(ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
                holder.iv_lock=(ImageView) convertView.findViewById(R.id.iv_lock);
                convertView.setTag(holder);
            }else {
                holder=(ViewHolder) convertView.getTag();
            }
            final AppInfoBean appInfoBean = getItem(position);
            holder.iv_icon.setBackgroundDrawable(appInfoBean.icon);
            holder.tv_name.setText(appInfoBean.name);
            if (isLock){
                holder.iv_lock.setBackgroundResource(R.drawable.ic_launcher_background);
            }else {
                holder.iv_lock.setBackgroundResource(R.mipmap.ic_launcher);
            }

            //执行动画的view 赋给临时变量
            final View AnimationView = convertView;
            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加动画效果
                    AnimationView.startAnimation(translateAnimation);
                    //对动画执行过程做事件监听，动画执行完成后，再去移除集合中的数据，操作数据库，刷新界面
                    translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //动画结束后执行的方法
                            if (isLock){
                                //已加锁切换到未加锁过程
                                //1.已加锁集合会删除一个，未加锁集合添加一个
                                Locklist.remove(appInfoBean);
                                UnLockList.add(appInfoBean);
                                //2.从已加锁的数据库中删除一条数据
                                mDao.delete(appInfoBean.packagename);
                                //3.刷新数据适配器
                                mLockAdapter.notifyDataSetChanged();
                                mUnlockAdapter.notifyDataSetChanged();
                            }else {
                                //未加锁切换到已加锁过程
                                //1.已加锁集合会添加一个，未加锁集合删除一个
                                Locklist.add(appInfoBean);
                                UnLockList.remove(appInfoBean);
                                //2.从已加锁的数据库中插入一条数据
                                mDao.insert(appInfoBean.packagename);
                                //3.刷新数据适配器
                                mLockAdapter.notifyDataSetChanged();
                                mUnlockAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });
            return convertView;
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initUI();
        initData();
        initAnimation();
    }

    //初始化平移动画的方法(平移自身的一个宽度大小)
    private void initAnimation() {
         translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        translateAnimation.setDuration(500);


    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                //1.获取所有手机中的应用
                appInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                //2.区分已加锁应用和未加锁应用
                Locklist = new ArrayList<AppInfoBean>();
                UnLockList = new ArrayList<AppInfoBean>();
                //3.获取数据库中已加锁应用包名的结合
                 mDao = AppLockDao.getInstance(getApplicationContext());
                List<String> lockPackageList = mDao.findAll();
                for (AppInfoBean appInfoBean:appInfoList){
                    //4.如果循环到的应用的包名，在数据库中，则说明是已加锁应用
                    if (lockPackageList.contains(appInfoBean.packagename)){
                        Locklist.add(appInfoBean);
                    }else {
                        UnLockList.add(appInfoBean);
                    }
                }
                //5.告知主线程可以使用数据
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
         bt_unlock = (Button)findViewById(R.id.bt_unlock);
         bt_lock = (Button)findViewById(R.id.bt_lock);
         ll_unlock = (LinearLayout)findViewById(R.id.ll_unlock);
         tv_unlock = (TextView)findViewById(R.id.tv_unlock);
         lv_unlock = (ListView)findViewById(R.id.lv_unlock);
         ll_lock = (LinearLayout)findViewById(R.id.ll_lock);
         tv_lock = (TextView)findViewById(R.id.tv_lock);
         lv_lock =(ListView) findViewById(R.id.lv_lock);

         bt_unlock.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //1.已加锁列表显示，未加锁列表隐藏
                 ll_lock.setVisibility(View.GONE);
                 ll_unlock.setVisibility(View.VISIBLE);
                 //2.未加锁变成浅色图片。已加锁变成深色图片
                 bt_unlock.setBackgroundColor(Color.BLUE);
                 bt_lock.setBackgroundColor(Color.RED);
             }
         });

         bt_lock.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //1.已加锁列表显示，未加锁列表隐藏
                 ll_lock.setVisibility(View.VISIBLE);
                 ll_unlock.setVisibility(View.GONE);
                 //2.未加锁变成浅色图片。已加锁变成深色图片
                 bt_unlock.setBackgroundColor(Color.RED);
                 bt_lock.setBackgroundColor(Color.BLUE);
             }
         });
    }
}
