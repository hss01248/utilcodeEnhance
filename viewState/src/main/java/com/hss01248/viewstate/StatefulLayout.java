package com.hss01248.viewstate;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import com.blankj.utilcode.util.ThreadUtils;

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



    public static StatefulLayout wrapWithStateOfPage(@NonNull View view, @NonNull Runnable errorClick){
        return wrapWithState(view,false,errorClick);
    }
    public static StatefulLayout wrapWithStateOfSmallView(@NonNull View view, @NonNull Runnable errorClick){
        return wrapWithState(view,true,errorClick);
    }

    public static StatefulLayout wrapWithState(@NonNull View view,boolean isForSmallView, @NonNull Runnable errorClick){
        if(view == null){
            return  null;
        }
        StatefulLayout layout = new StatefulLayout(view.getContext());
        layout.setConfig(ViewStateConfig.newBuilder(isForSmallView ? ViewStateConfig.globalSmallViewConfig : ViewStateConfig.globalConfig)
                .errorClick(new Runnable() {
                    @Override
                    public void run() {
                        errorClick.run();
                    }
                }).build());

        layout.setContentView(view);
        //主要是宽高
        if(view.getLayoutParams() !=null){
            layout.setLayoutParams(view.getLayoutParams());
        }
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


        parent.removeView(contentView);
        this.setLayoutParams(layoutParams);
        parent.addView(this);
        //todo 原始view 的margin处理: 会自动抹除 ,有点牛逼

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

            init2(null);
            if(contentView != null){
                firstIn = false;
            }
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
            if(views.size() == 1){
                contentView = views.get(0);
            }

        }


       // LogUtils.d("content view : ",contentView);
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
            if(viewGroup != null){
                subViews.put(state,viewGroup);
                //NullPointerException
                if(viewGroup.getParent() == this){

                }else {
                    addView(viewGroup);
                }
            }
        }
        if(viewGroup == null){
            //写在xml里的contentview
            viewGroup = getChildAt(0);
            subViews.put(state,viewGroup);
        }
       // LogUtils.d("state:"+state+",vg:"+viewGroup);
        for (Integer integer : subViews.keySet()) {
            if(integer == state){
                subViews.get(integer).setVisibility(VISIBLE);
            }else {
                subViews.get(integer).setVisibility(GONE);
            }
        }
        if(config.isDarkMode()){
            if(state != CONTENT){
                viewGroup.setBackgroundColor(Color.BLACK);
                findChildTextviewAndSetWhite(viewGroup);
            }

        }

        return viewGroup;

    }

    private void findChildTextviewAndSetWhite(View view) {
        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                findChildTextviewAndSetWhite(child);
            }
        }else if(view instanceof TextView  && !(view instanceof Button)){
            TextView textView = (TextView) view;
            textView.setTextColor(Color.WHITE);
            Drawable background = textView.getBackground();
            //Log.w("setDarkMode","background "+background);
            if(background != null){
                textView.setBackgroundColor(Color.parseColor("#333333"));
            }
        }
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
        final CharSequence[] msg1 = {msg};
        ThreadUtils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(msg1[0])){
                    msg1[0] = config.loadingMsg;
                }
                View view = pickView(LOADING);
                if(view instanceof ViewGroup){
                    ViewGroup viewGroup = (ViewGroup) view;
                    for (int i = 0; i < viewGroup.getChildCount(); i++) {
                        View childAt = viewGroup.getChildAt(i);
                        if(childAt instanceof TextView){
                            TextView textView = (TextView) childAt;
                            if(!TextUtils.isEmpty(msg1[0])){
                                textView.setText(msg1[0]);
                            }

                        }
                    }
                }
                if(config.listener != null){
                    config.listener.onStateChanged(view,LOADING);
                }
            }
        });
    }

    @Override
    public void showEmpty(@Nullable  CharSequence msg,
                          @Nullable  int icon,
                          @Nullable  CharSequence btnText,
                          @Nullable  Runnable emptyClick) {
         CharSequence[] msg0 = {msg};
         int[] icon0 = {icon};
         CharSequence[] btnText0 = {btnText};
         Runnable[] emptyClick0 = {emptyClick};
        ThreadUtils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(msg0[0])){
                    msg0[0] = config.emptyMsg;
                }
                if(icon0[0] == 0){
                    icon0[0] = config.emptyIcon;
                }
                if(TextUtils.isEmpty(btnText0[0])){
                    btnText0[0] = config.emptyBtnText;
                }
                if(emptyClick0[0] == null){
                    emptyClick0[0] = config.emptyClick;
                }
                View view = pickView(EMPTY);
                if(view instanceof ViewGroup){
                    ViewGroup viewGroup = (ViewGroup) view;
                    for (int i = 0; i < viewGroup.getChildCount(); i++) {
                        View childAt = viewGroup.getChildAt(i);
                        if(childAt instanceof Button){
                            Button textView = (Button) childAt;
                            if(!TextUtils.isEmpty(btnText0[0])){
                                textView.setText(btnText0[0]);
                            }
                            if(emptyClick0[0] == null){
                                textView.setVisibility(GONE);
                            }else {
                                textView.setVisibility(VISIBLE);
                                Runnable finalEmptyClick = emptyClick0[0];
                                textView.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finalEmptyClick.run();
                                    }
                                });
                            }
                        }else if(childAt instanceof TextView){
                            TextView textView = (TextView) childAt;
                            if(!TextUtils.isEmpty(msg0[0])){
                                textView.setText(msg0[0]);
                            }

                        }else if(childAt instanceof ImageView){
                            ImageView textView = (ImageView) childAt;
                            if(icon0[0] != 0){
                                try {
                                    textView.setImageResource(icon0[0]);
                                }catch (Throwable throwable){
                                    throwable.printStackTrace();
                                }
                            }
                        }
                    }
                }
                if(config.listener != null){
                    config.listener.onStateChanged(view,EMPTY);
                }
            }
        });

    }

    @Override
    public void showError(@Nullable CharSequence msg, @Nullable int icon, @Nullable  CharSequence btnText,@Nullable Runnable errorClick) {
        CharSequence[] msg0 = {msg};
        int[] icon0 = {icon};
        CharSequence[] btnText0 = {btnText};
        Runnable[] errorClick0 = {errorClick};
        ThreadUtils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if(icon0[0] == 0){
                    icon0[0] = config.errorIcon;
                }
                if(TextUtils.isEmpty(btnText0[0])){
                    btnText0[0] = config.errorBtnText;
                }
                if(errorClick0[0] == null){
                    errorClick0[0] = config.errorClick;
                }
                View view = pickView(ERROR);
                if(view instanceof ViewGroup){
                    ViewGroup viewGroup = (ViewGroup) view;
                    for (int i = 0; i < viewGroup.getChildCount(); i++) {
                        View childAt = viewGroup.getChildAt(i);
                        if(childAt instanceof Button){
                            Button textView = (Button) childAt;
                            if(!TextUtils.isEmpty(btnText0[0])){
                                textView.setText(btnText0[0]);
                            }
                            if(errorClick0[0] == null){
                                textView.setVisibility(GONE);
                            }else {
                                textView.setVisibility(VISIBLE);
                                Runnable finalEmptyClick = errorClick0[0];
                                textView.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!NoNetworkHelper2.isNetWorkAvailable(v.getContext())) {
                                            NoNetworkHelper2.showNoNetWorkDlg(v.getContext());
                                        } else {
                                            if (finalEmptyClick != null) {
                                                finalEmptyClick.run();
                                            }
                                        }
                                    }
                                });
                            }
                        }else if(childAt instanceof TextView){
                            TextView textView = (TextView) childAt;
                            if(!TextUtils.isEmpty(msg0[0])){
                                textView.setText(msg0[0]);
                            }

                        }else if(childAt instanceof ImageView){
                            ImageView textView = (ImageView) childAt;
                            if(icon0[0] != 0){
                                try {
                                    textView.setImageResource(icon0[0]);
                                }catch (Throwable throwable){
                                    throwable.printStackTrace();
                                }
                            }
                        }
                    }
                }
                Runnable finalErrorClick = errorClick0[0];
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NoNetworkHelper2.isNetWorkAvailable(v.getContext())) {
                            NoNetworkHelper2.showNoNetWorkDlg(v.getContext());
                        } else {
                            if (finalErrorClick != null) {
                                finalErrorClick.run();
                            }
                        }
                    }
                });
                if(config.listener != null){
                    config.listener.onStateChanged(view,ERROR);
                }
            }
        });

    }

    @Override
    public void showContent() {
        ThreadUtils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                View view = pickView(CONTENT);
                if(config.listener != null){
                    config.listener.onStateChanged(view,CONTENT);
                }
            }
        });

    }
}
