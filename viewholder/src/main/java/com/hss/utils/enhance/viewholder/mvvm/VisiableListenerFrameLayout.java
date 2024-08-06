package com.hss.utils.enhance.viewholder.mvvm;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @Despciption todo
 * @Author hss
 * @Date 8/6/24 6:19 PM
 * @Version 1.0
 */
public class VisiableListenerFrameLayout extends FrameLayout {
    public VisiableListenerFrameLayout(@NonNull Context context) {
        super(context);
    }

    public VisiableListenerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VisiableListenerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VisiableListenerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public interface MyListener {
        public void onVisibilityChanged(int visibility);
    }
    protected MyListener mListener;
    public void registerListener(MyListener myListener) {
        this.mListener = myListener;
    }



    public void setVisibility (int visibility) {
        super.setVisibility(visibility);
        if (mListener != null)
            mListener.onVisibilityChanged(visibility);
    }
}
