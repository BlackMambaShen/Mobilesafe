package com.example.liang.mobilesafe74;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class Setup1Activity extends BaseSetupActivity {

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
        //创建手势管理对象，用作管理在onTouchEvent传递过来的手势动作
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //监听手势的移动
                if (e1.getX() - e2.getX() > 0) {
                    //由右向左,移动到下一页

                    Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
                    startActivity(intent);
                    finish();
                    //开启平移动画
                    overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
                }
                if (e1.getX() - e2.getX() < 0) {
                    //由左向右，移动到上一页
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    protected void showPrePage() {
        //空实现
    }

    @Override
    protected void showNextPage() {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        finish();
        //开启平移动画
        overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
    }

//    public void nextPage(View v){
//    }
//
//
//    //监听屏幕上相应的事件类型(按下，移动，抬起)
//    public boolean onTouchEvent(MotionEvent event) {
//        //通过手势的处理类，接收多种类型的事件，用作处理的方法
//        gestureDetector.onTouchEvent(event);
//
//        return super.onTouchEvent(event);
//    }
}
