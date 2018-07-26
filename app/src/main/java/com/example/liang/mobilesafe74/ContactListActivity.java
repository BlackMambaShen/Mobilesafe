package com.example.liang.mobilesafe74;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    private ListView lv_contact;
    private static final String tag="ContactListActivity";
    private List<HashMap<String,String>>contactList=new ArrayList<HashMap<String,String>>();
    private MyAdapter myAdapter;
    private Handler mHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //填充数据适配器
            myAdapter = new MyAdapter();
            lv_contact.setAdapter(myAdapter);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
        ininData();
    }

    //数据适配器需要数据
    private void ininData() {
        new Thread(){
            @Override
            public void run() {
                //获取内容解析器对象
                ContentResolver contentResolver = getContentResolver();
                //查询系统联系人数据库过程
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"}, null, null, null);
                //先把集合清空，防止有数据
                contactList.clear();
                //循环游标，直到没有数据为止
                while (cursor.moveToNext()){
                    String id = cursor.getString(0);
//                    Log.i(tag,"id="+id);
                    //根据用户唯一性id，查询data表和mimetype表生成的视图，获取data以及mimetype字段
                    Cursor indexCursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{id}, null);
                    //循环获取每一个联系人的电话号码以及姓名
                    HashMap<String, String> hashMap = new HashMap<>();

                    while (indexCursor.moveToNext()){
                        String data = indexCursor.getString(0);
                        String type = indexCursor.getString(1);

                        //区分类型去给hashmap填充数据
                        if (type.equals("vnd.android.cursor.item/phone_v2")){
                            //数据非空判断
                            if (!TextUtils.isEmpty(data)){
                                hashMap.put("phone",data);
                            }
                        }else if(type.equals("vnd.android.cursor.item/name")){
                            if (!TextUtils.isEmpty(data)){
                                hashMap.put("name",data);
                            }
                        }
                    }
                    indexCursor.close();
                    //每一次内部循环的hashMap加入进去
                    contactList.add(hashMap);
                }
                cursor.close();
                //7.消息机制,发空消息，告知主线程可以使用子线程已经填充好的数据集合
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1.获取点中条目的索引指向集合中的对象
                if (myAdapter!=null){
                    HashMap<String, String> hashMap = myAdapter.getItem(position);
                    //2.获取当前条目指向集合对应的电话号码
                    String phone = hashMap.get("phone");
                    //3.此电话号码需要给第三个导航界面使用
                    //4.在结束此界面回到前一个导航界面的时候，需要将数据返回过去
                    Intent intent = new Intent();
                    intent.putExtra("phone",phone);
                    setResult(0,intent);
                    finish();
                }

            }
        });
    }

    //数据适配器
    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
            TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
            TextView tv_phone =(TextView) view.findViewById(R.id.tv_phone);
            tv_name.setText(getItem(position).get("name"));
            tv_phone.setText(getItem(position).get("phone"));
            return view;
        }
    }
}
