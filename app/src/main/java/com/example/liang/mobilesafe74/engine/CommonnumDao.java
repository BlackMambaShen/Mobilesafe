package com.example.liang.mobilesafe74.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class CommonnumDao {
    //1.指定访问数据库的路径
    public static String path="data/data/com.example.liang.mobilesafe74/files/commonnum.db";
    //2.开启数据库
    public List<Group>  getGroup(){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        List<Group> groupsList=new ArrayList<Group>();
        while (cursor.moveToNext()){
            Group group = new Group();
            group.name = cursor.getString(0);
            group.idx = cursor.getString(1);
            group.childList=getChild(group.idx);
            groupsList.add(group);
        }
        cursor.close();
        db.close();
        return groupsList;
    }
    //获取每一个组中孩子节点的数据
    public List<Child> getChild(String idx){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);
        List<Child> childsList=new ArrayList<Child>();
        while (cursor.moveToNext()){
            Child child = new Child();
            child._id=cursor.getString(0);
            child.number=cursor.getString(1);
            child.name=cursor.getString(2);
            childsList.add(child);
        }
        cursor.close();
        db.close();
        return childsList;
    }


    public class Group{
        public String name;
        public String idx;
        public List<Child> childList;
    }

    public class Child{
        public String _id;
        public String number;
        public String name;
    }
}
