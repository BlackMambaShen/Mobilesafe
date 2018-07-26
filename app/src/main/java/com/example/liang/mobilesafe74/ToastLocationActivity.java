package com.example.liang.mobilesafe74;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.liang.mobilesafe74.utils.ConstantValue;
import com.example.liang.mobilesafe74.utils.SpUtil;

public class ToastLocationActivity extends AppCompatActivity {

    private ImageView iv_drag;
    private Button bt_top;
    private Button bt_bottom;
    private WindowManager wm;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);
        initUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        //可拖拽双击剧中的图片控件
         iv_drag = (ImageView)findViewById(R.id.iv_drag);
         bt_top = (Button)findViewById(R.id.bt_top);
         bt_bottom = (Button)findViewById(R.id.bt_bottom);

         wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final int screenWidth = wm.getDefaultDisplay().getWidth();
        final int screenHeight = wm.getDefaultDisplay().getHeight();
        int locationX = SpUtil.getInt(getApplicationContext(), ConstantValue.location_x, 0);
        int locationY = SpUtil.getInt(getApplicationContext(), ConstantValue.location_y, 0);
        //左上角坐标作用在iv_drag上（）
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //将左上角的坐标作用在iv_drag对应规则参数上
        layoutParams.leftMargin=locationX;
        layoutParams.topMargin=locationY;
        //将规则作用在iv_drag上
        iv_drag.setLayoutParams(layoutParams);
        if (locationY>screenHeight/2){
            bt_bottom.setVisibility(View.INVISIBLE);
            bt_top.setVisibility(View.VISIBLE);
        }else {
            bt_bottom.setVisibility(View.VISIBLE);
            bt_top.setVisibility(View.INVISIBLE);
        }

        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTime!=0){
                    long endTime = System.currentTimeMillis();
                    if (endTime-startTime<2000){
                        int left = screenWidth / 2 - iv_drag.getWidth() / 2;
                        int top = screenHeight / 2 - iv_drag.getHeight() / 2;
                        int right = screenWidth / 2 + iv_drag.getWidth() / 2;
                        int bottom=screenHeight / 2 +iv_drag.getHeight() / 2;
                        //控件按以上规则显示
                        iv_drag.layout(left,top,right,bottom);
                        //存储最终位置
                        SpUtil.putInt(getApplicationContext(), ConstantValue.location_x,iv_drag.getLeft());
                        SpUtil.putInt(getApplicationContext(), ConstantValue.location_y,iv_drag.getTop());
                    }
                }
                 startTime = System.currentTimeMillis();
            }
        });
        //监听某一个控件的拖拽过程(按下，移动，抬起)
        iv_drag.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            //对不同的事件做不同的逻辑处理
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                         startX = (int) event.getRawX();
                         startY=(int)event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX=(int) event.getRawX();
                        int moveY=(int) event.getRawY();
                        int disX = moveX - startX;
                        int disY = moveY - startY;
                        //1.当前控件屏幕左上角的位置
                        //左边缘和屏幕边缘的间距
                        int left = iv_drag.getLeft()+disX;
                        //顶端坐标
                        int top = iv_drag.getTop()+disY;
                        //右端
                        int right = iv_drag.getRight() + disX;
                        //底端
                        int bottom = iv_drag.getBottom() + disY;
                        //容错处理(不能出界面)
                        if (left<0){
                            return true;
                        }
                        if (right>screenWidth){
                            return true;
                        }
                        if (top<0){
                            return true;
                        }
                        if (bottom>screenHeight-60){
                            return true;
                        }
                        if (top>screenHeight/2){
                            bt_bottom.setVisibility(View.INVISIBLE);
                            bt_top.setVisibility(View.VISIBLE);
                        }else {
                            bt_bottom.setVisibility(View.VISIBLE);
                            bt_top.setVisibility(View.INVISIBLE);
                        }
                        //告知移动的控件，按计算的坐标进行展示
                        iv_drag.layout(left,top,right,bottom);
                        //3.每触发一次重置起始坐标
                        startX = (int)event.getRawX();
                        startY=(int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //4.存储移动到的位置
                        SpUtil.putInt(getApplicationContext(), ConstantValue.location_x,iv_drag.getLeft());
                        SpUtil.putInt(getApplicationContext(), ConstantValue.location_y,iv_drag.getTop());
                        break;
                }
                //在当前的情况下返回事件的相应状态
                return false;
            }
        });
    }
}
