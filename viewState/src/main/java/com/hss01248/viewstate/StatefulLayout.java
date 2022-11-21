package com.hss01248.viewstate;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.blankj.utilcode.util.LogUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/11/2022 10:41
 * @Version 1.0
 */
public class StatefulLayout extends FrameLayout implements IViewState{

    public static final int LOADING = 0;
    public static final int EMPTY = 1;
    public static final int ERROR = 2;
    public static final int CONTENT = 3;

    @IntDef({LOADING,EMPTY,ERROR,CONTENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewState{}

    public static StatefulLayout wrapWithState(@NonNull View view, @Nullable ViewStateConfig config){
        if(view == null){
            return  null;
        }
        StatefulLayout layout = new StatefulLayout(view.getContext());
        if(config != null){
            layout.setConfig(config);
        }
        layout.setContentView(view);
        return layout;
    }

    public static StatefulLayout wrapWithState(@NonNull View view, @NonNull Runnable errorClick){
        if(view == null){
            return  null;
        }
        StatefulLayout layout = new StatefulLayout(view.getContext());
        layout.setConfig(ViewStateConfig.Builder
                .newBuilder()
                .errorClick(new Runnable() {
                    @Override
                    public void run() {
                        errorClick.run();
                    }
                }).build());

        layout.setContentView(view);
        return layout;
    }


    public StatefulLayout setErrorClick(@NonNull Runnable errorClick){
        return setConfig(ViewStateConfig.Builder
                .newBuilder()
                .errorClick(new Runnable() {
                    @Override
                    public void run() {
                        errorClick.run();
                    }
                }).build());
    }

    public StatefulLayout setConfig(ViewStateConfig config) {
        this.config = config;
        return this;
    }

    ViewStateConfig config =  ViewStateConfig.globalConfig;

    public int getCurrentState() {
        return currentState;
    }

    int currentState;
    Map<Integer, View> subViews = new HashMap<>();

    public StatefulLayout setContentView(View contentView) {
        this.contentView = contentView;
        if(contentView.getParent()  == null){
            return this;
        }
        ViewGroup parent = (ViewGroup) contentView.getParent();
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();

        //todo margin处理
        parent.removeView(contentView);
        this.setLayoutParams(layoutParams);
        parent.addView(this);
        return this;
    }

    View contentView;


    public StatefulLayout(Context context) {
        super(context);
        init2(context);
    }

    public StatefulLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init2(context);
    }

    public StatefulLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init2(context);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        init2(null);
        return super.generateLayoutParams(lp);
    }

    boolean firstIn = true;
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(firstIn && contentView == null){
            //&& contentView == null
            firstIn = false;
            init2(null);
        }
    }

    /**
     * In state of constructing your FrameLayout, children are still not fully attached to the view. So, calling getChildCount() will return you 0.
     * If you want to iterate over child views and update them, do it inside onLayout() or onMeasure().
     * @param context
     */
    private void init2(Context context) {
        int childCount = getChildCount();
        /*if(childCount >1){
            throw new IllegalArgumentException("only 1 child view accepted");
        }*/
        if(childCount == 1){
            contentView = getChildAt(0);
        }else if(childCount >1){
            List<View> views = new ArrayList<>();
            for (int i = 0; i < childCount; i++) {
                View view = getChildAt(i);
                if(!subViews.containsValue(view)){
                    views.add(view);
                }
            }
            if(views.size() >1){
                throw new IllegalArgumentException("only 1 child view accepted");
            }
            contentView = views.get(0);
        }


        LogUtils.d("content view : ",contentView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatefulLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init2(context);
    }

    View pickView(@ViewState int state){
        View viewGroup = null;
        currentState = state;
        if(subViews.containsKey(state)){
            viewGroup =  subViews.get(state);
        }else {
            viewGroup = createView(state);

            subViews.put(state,viewGroup);
            if(viewGroup.getParent() == this){
                //写在xml里的contentview
            }else {
                addView(viewGroup);
            }

        }
        LogUtils.d("state:"+state+",vg:"+viewGroup);
        for (Integer integer : subViews.keySet()) {
            if(integer == state){
                subViews.get(integer).setVisibility(VISIBLE);
            }else {
                subViews.get(integer).setVisibility(GONE);
            }
        }


        return viewGroup;

    }

    private View createView(int state) {
        if(state == LOADING){
            return  LayoutInflater.from(getContext()).inflate(config.loadingLayout,this,false);
        }
        if(state == EMPTY){
            return  LayoutInflater.from(getContext()).inflate(config.emptyLayout,this,false);
        }
        if(state == ERROR){
            return  LayoutInflater.from(getContext()).inflate(config.errorLayout,this,false);
        }
        if(state == CONTENT){
            return  contentView;
        }
        return null;
    }

    @Override
    public void showLoading(@Nullable CharSequence msg) {
        if(TextUtils.isEmpty(msg)){
            msg = config.loadingMsg;
        }
        View view = pickView(LOADING);
        if(!(view instanceof ViewGroup)){
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if(childAt instanceof TextView){
                TextView textView = (TextView) childAt;
                if(!TextUtils.isEmpty(msg)){
                    textView.setText(msg);
                }

            }
        }
    }

    @Override
    public void showEmpty(@Nullable CharSequence msg, @Nullable int icon, @Nullable  CharSequence btnText,@Nullable Runnable emptyClick) {
        if(TextUtils.isEmpty(msg)){
            msg = config.emptyMsg;
        }
        if(icon == 0){
            icon = config.emptyIcon;
        }
        if(TextUtils.isEmpty(btnText)){
            btnText = config.emptyBtnText;
        }
        if(emptyClick == null){
            emptyClick = config.emptyClick;
        }
        View view = pickView(EMPTY);
        if(!(view instanceof ViewGroup)){
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if(childAt instanceof Button){
                Button textView = (Button) childAt;
                if(!TextUtils.isEmpty(btnText)){
                    textView.setText(btnText);
                }
                if(emptyClick == null){
                    textView.setVisibility(GONE);
                }else {
                    textView.setVisibility(VISIBLE);
                    Runnable finalEmptyClick = emptyClick;
                    textView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finalEmptyClick.run();
                        }
                    });
                }
            }else if(childAt instanceof TextView){
                TextView textView = (TextView) childAt;
                if(!TextUtils.isEmpty(msg)){
                    textView.setText(msg);
                }

            }else if(childAt instanceof ImageView){
                ImageView textView = (ImageView) childAt;
                if(icon != 0){
                    try {
                        textView.setImageResource(icon);
                    }catch (Throwable throwable){
                        throwable.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void showError(@Nullable CharSequence msg, @Nullable int icon, @Nullable  CharSequence btnText,@Nullable Runnable errorClick) {
        if(icon == 0){
            icon = config.errorIcon;
        }
        if(TextUtils.isEmpty(btnText)){
            btnText = config.errorBtnText;
        }
        if(errorClick == null){
            errorClick = config.errorClick;
        }
        View view = pickView(ERROR);
        if(!(view instanceof ViewGroup)){
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if(childAt instanceof Button){
                Button textView = (Button) childAt;
                if(!TextUtils.isEmpty(btnText)){
                    textView.setText(btnText);
                }
                if(errorClick == null){
                    textView.setVisibility(GONE);
                }else {
                    textView.setVisibility(VISIBLE);
                    Runnable finalEmptyClick = errorClick;
                    textView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finalEmptyClick.run();
                        }
                    });
                }
            }else if(childAt instanceof TextView){
                TextView textView = (TextView) childAt;
                if(!TextUtils.isEmpty(msg)){
                    textView.setText(msg);
                }

            }else if(childAt instanceof ImageView){
                ImageView textView = (ImageView) childAt;
                if(icon != 0){
                    try {
                        textView.setImageResource(icon);
                    }catch (Throwable throwable){
                        throwable.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void showContent() {
        pickView(CONTENT);
    }
}
