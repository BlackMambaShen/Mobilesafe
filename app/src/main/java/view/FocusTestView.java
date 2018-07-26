package view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class FocusTestView extends AppCompatTextView {

    //使用通过java代码创建控件
    public FocusTestView(Context context) {
        super(context);
    }

    //由系统调用(带属性+上下文构造方法)
    public FocusTestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //由系统调用(带属性+上下文构造方法+布局文件中定义样式文件构造方法)
    public FocusTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //重写获取焦点的方法即可,由系统调用，默认获取焦点
    public boolean isFocused() {
        return true;
    }
}
