package com.example.liang.mobilesafe74.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.liang.mobilesafe74.db.BlackNumberOpenHelper;
import com.example.liang.mobilesafe74.db.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

public class BlackNumberDao {
    private final BlackNumberOpenHelper blackNumberOpenHelper;

    //BlackNumberDao单例模式
    //1.私有化构造方法
    private BlackNumberDao(Context context){
        //创建数据库以及表结构
         blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }
    //2.声明一个当前类的对象
    private static BlackNumberDao blackNumberDao=null;
    //3.提供一个静态方法，如果当前类的对象为空，创建一个新的
    public static BlackNumberDao getInstance(Context context){
        if (blackNumberDao==null){
            blackNumberDao=new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    //增加一个条目
    /*
    phone 拦截的电话
    mode 拦截类型（1.短信  2.电话  3.拦截所有（短信+电话））
     */
    public void insert(String phone,String mode){
        //开启数据库，做写入操作
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        db.insert("blacknumber",null,values);
        db.close();
    }

    //删除电话号码
    public void delete(String phone){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        db.delete("blacknumber","phone=?",new String[]{phone});
        db.close();
    }

    //更新电话
    /*
    phone 更新拦截模式的电话号码
    mode 要更新为的模式（1.短信  2.电话  3.拦截所有（短信+电话））
     */
    public void update(String phone,String mode){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("mode",mode);
        db.update("blacknumber",contentValues,"phone=?",new String[]{phone});
        db.close();
    }

    //返回的是查询到数据库中所有的号码以及拦截类型所在的集合
    public List<BlackNumberInfo> findAll(){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        //"_id desc" 倒叙排
        Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone= cursor.getString(0);
            blackNumberInfo.mode=cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberList;
    }

    //查询索引值 每次查询20条数据
    public List<BlackNumberInfo> find(int index){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        //"_id desc" 倒叙排
        Cursor cursor=db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;",new String[]{index+""});
        List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone= cursor.getString(0);
            blackNumberInfo.mode=cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberList;
    }

    //查询总数
    public int getCount(){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int count=0;
        Cursor cursor=db.rawQuery("select count(*) from blacknumber;",null);
        if (cursor.moveToNext()){
             count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    //查询条件的电话号码
    //传入电话号码的拦截模式  1:短信  2：电话  3：所有
    public int getMode(String phone){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int mode=0;
        Cursor cursor=db.query("blacknumber",new String[]{"mode"},"phone=?",new String[]{phone},null,null,null);
        if (cursor.moveToNext()){
            mode = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return mode;
    }
}
