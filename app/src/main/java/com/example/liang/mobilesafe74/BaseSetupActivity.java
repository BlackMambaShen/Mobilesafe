package com.example.liang.mobilesafe74;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public abstract class BaseSetupActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //监听手势的移动
                if (e1.getX() - e2.getX() > 0) {
                    //调用子类的下一页方法,抽象方法
                    //在第一个界面上的时候，跳转到第二个界面
                    showNextPage();
                }
                if (e1.getX() - e2.getX() < 0) {
                    //调用子类的上一页方法,抽象方法
                    //在第二个界面上的时候，跳转到第一个界面
                    showPrePage();
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    //上一页的抽象方法
    protected abstract void showPrePage();

    //下一页的抽象方法

    protected abstract void showNextPage();

    //点击下一页按钮的时候，根据子类的showNextPage方法相应跳转
    public void nextPage(View v){
       showNextPage();
    }

    //点击上一页按钮的时候，根据子类的showPrePage方法相应跳转
    public void prePage(View v){
        showPrePage();
    }

    //监听屏幕上相应的事件类型(按下，移动，抬起)
    public boolean onTouchEvent(MotionEvent event) {
        //通过手势的处理类，接收多种类型的事件，用作处理的方法
        gestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }
}
