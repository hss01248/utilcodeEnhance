package com.hss.utils.enhance.viewstate;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/11/2022 10:41
 * @Version 1.0
 */
public class StatefulLayout extends RelativeLayout {
    public StatefulLayout(Context context) {
        super(context);
    }

    public StatefulLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatefulLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatefulLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
