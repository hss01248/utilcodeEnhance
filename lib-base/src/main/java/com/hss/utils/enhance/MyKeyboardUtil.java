package com.hss.utils.enhance;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.hss.utils.enhance.lifecycle.LifecycleObjectUtil;

/**
 * @Despciption activity xml设置:   android:windowSoftInputMode="adjustPan"
 *             android:configChanges="keyboard|keyboardHidden|screenSize"
 *
 *
 * @Author hss
 * @Date 11/01/2023 19:30
 * @Version 1.0
 */
public class MyKeyboardUtil implements DefaultLifecycleObserver {

    /**
     * 软键盘监听
     */
    private OnKeyBoardStateListener mListener;
    /**
     * 当前android.R.id.content View下的第一个子view
     */
    private View mRootLayout;
    private int mVisibleHeight;
    private int mFirstVisibleHeight;
    /**
     * 当前键盘是否处于显示状态
     */
    private boolean mIsKeyboardShow;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = this::calKeyBordState;


    public MyKeyboardUtil(AppCompatActivity appCompatActivity) {
        mRootLayout = ((ViewGroup) appCompatActivity.findViewById(android.R.id.content)).getChildAt(0);
        mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        appCompatActivity.getLifecycle().addObserver(this);
    }

    public boolean keyboardShow() {
        return mIsKeyboardShow;
    }

    private void calKeyBordState() {
        if (mRootLayout == null) {
            return;
        }
        Rect r = new Rect();
        mRootLayout.getWindowVisibleDisplayFrame(r);
        int visibleHeight = r.height();
        if (mVisibleHeight == 0) {
            mVisibleHeight = visibleHeight;
            mFirstVisibleHeight = visibleHeight;
            return;
        }
        if (mVisibleHeight == visibleHeight) {
            return;
        }
        mVisibleHeight = visibleHeight;
        mIsKeyboardShow = mVisibleHeight < mFirstVisibleHeight;
        if (mIsKeyboardShow) {
            int keyboardHeight = Math.abs(mVisibleHeight - mFirstVisibleHeight);//键盘高度
            if (mListener != null) {
                mListener.onSoftKeyBoardShow(keyboardHeight);
            }
        } else {
            if (mListener != null) {
                mListener.onSoftKeyBoardHide();
            }
        }
    }

    /**
     * 添加软键盘的监听
     * @param listener listener
     */
    public MyKeyboardUtil addOnKeyBoardStateListener(OnKeyBoardStateListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 移除当前软键盘的监听
     */
     void removeOnKeyBoardStateListener() {
        if (mRootLayout != null && mOnGlobalLayoutListener != null) {
            mRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
        if (mListener != null) {
            mListener = null;
        }
    }


    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        removeOnKeyBoardStateListener();
    }


    public static void adaptView(View innerScrollableView){
        final int[] totalHeight = {0};
        innerScrollableView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if(totalHeight[0] ==0){
                    totalHeight[0] = innerScrollableView.getMeasuredHeight();
                    if(totalHeight[0] >0){
                        innerScrollableView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                }
            }
        });

        //内部自动在ondestory时取消
        new MyKeyboardUtil((AppCompatActivity) LifecycleObjectUtil.getActivityFromContext(innerScrollableView.getContext()))
                .addOnKeyBoardStateListener(new MyKeyboardUtil.OnKeyBoardStateListener() {
                    @Override
                    public void onSoftKeyBoardShow(int keyboardHeight) {
                        ViewGroup.LayoutParams layoutParams = innerScrollableView.getLayoutParams();
                        layoutParams.height = totalHeight[0] - keyboardHeight;
                        innerScrollableView.setLayoutParams(layoutParams);
                    }

                    @Override
                    public void onSoftKeyBoardHide() {
                        ViewGroup.LayoutParams layoutParams = innerScrollableView.getLayoutParams();
                        layoutParams.height = totalHeight[0] ;
                        innerScrollableView.setLayoutParams(layoutParams);
                    }
                });
    }




    /**
     * 软键盘相关监听
     */
    public interface OnKeyBoardStateListener {
        /**
         * 软键盘显示
         * @param keyboardHeight 软键盘高度
         */
        void onSoftKeyBoardShow(int keyboardHeight);

        /**
         * 软键盘隐藏
         */
        void onSoftKeyBoardHide();
    }
}
