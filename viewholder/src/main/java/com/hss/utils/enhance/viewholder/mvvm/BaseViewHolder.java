package com.hss.utils.enhance.viewholder.mvvm;


import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.enhance.lifecycle.LifecycleObjectUtil;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;


/**
 * by hss
 * data:2020-04-10
 * desc: 界面模块化
 * K 初始化时用数据刷新界面的数据类型,比如从bundle等处传过来的
 *
 * 如果带loading,error等状态,那么xml使用statuview或其子类即可
 *
 * 通过lifecycle感知生命周期
 */
public abstract class BaseViewHolder<VB extends ViewBinding, InitInfo>
        implements DefaultLifecycleObserver {

    public View getRootView() {
        return rootView;
    }

    protected View rootView;

    protected LifecycleOwner lifecycleOwner;

    protected ViewGroup parent;

    protected InitInfo initInfo;

    protected Context context;

    public VB getBinding() {
        return binding;
    }

    protected VB binding;

    public void setContainerViewHolderWithTitleBar(ContainerViewHolderWithTitleBar viewHolderWithTitleBar) {
        this.containerViewHolderWithTitleBar = viewHolderWithTitleBar;
    }

    protected ContainerViewHolderWithTitleBar containerViewHolderWithTitleBar;

    public BaseViewHolder(Context context) {
        Activity activity = LifecycleObjectUtil.getActivityFromContext(context);
        if(activity ==null){
            LogUtils.w("");
            activity= ActivityUtils.getTopActivity();
        }
        doInit(null,LifecycleObjectUtil.getLifecycleOwnerFromObj(context),
                (ViewGroup) activity.findViewById(android.R.id.content));
    }

    /**
     * 在activity或者view中使用
     */
    public BaseViewHolder(@NonNull LifecycleOwner lifecycleOwner, ViewGroup parent) {
        this(null,lifecycleOwner,parent);
    }

    /**
     * 在fragment中使用
     */
    public BaseViewHolder(@Nullable LayoutInflater inflater,
                          @NonNull LifecycleOwner lifecycleOwner,
                          @Nullable ViewGroup parent) {
        doInit(inflater, lifecycleOwner,parent);

    }


    private void doInit(LayoutInflater inflater, LifecycleOwner lifecycleOwner, ViewGroup parent) {
        this.lifecycleOwner = lifecycleOwner;
        lifecycleOwner.getLifecycle().addObserver(this);
        //ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        this.parent = parent;
        context = getContext(lifecycleOwner, parent);
        if(inflater == null){
            inflater = LayoutInflater.from(context);
        }
        this.binding = initViewBinding(inflater,parent);

        rootView = binding.getRoot();

        visiableListenerFrameLayout = new VisiableListenerFrameLayout(context);
        visiableListenerFrameLayout.addView(binding.getRoot());
        rootView = visiableListenerFrameLayout;

        visiableListenerFrameLayout.registerListener(new VisiableListenerFrameLayout.MyListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
                onBackPressedCallback.setEnabled(visibility == View.VISIBLE
                        && shouldInterceptBackPressed());
            }
        });

        onBackPressedCallback = new OnBackPressedCallback(rootView.getVisibility() == View.VISIBLE
                && shouldInterceptBackPressed()) {
            @Override
            public void handleOnBackPressed() {
                onBackPressed2();
            }
        };


    }

    protected boolean shouldInterceptBackPressed() {
        return false;
    }

    protected void onBackPressed2(){

    }



    OnBackPressedCallback onBackPressedCallback;
    VisiableListenerFrameLayout visiableListenerFrameLayout;
    protected OnBackPressedDispatcher getBackPressedDispatcher(){
        Activity topActivity1 = ActivityUtils.getTopActivity();
        if(!(topActivity1 instanceof ComponentActivity)){
            return new OnBackPressedDispatcher();
        }
        ComponentActivity topActivity = (ComponentActivity) ActivityUtils.getTopActivity();
       return topActivity.getOnBackPressedDispatcher();
    }



    /**
     * 依赖聚焦状态,偶尔有效,经常会无效
     */
    @Deprecated
    protected void rootViewBackPressedHanlde() {
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //LogUtils.w(v,keyCode,event);
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                  /*  boolean intercept = onBackPressed();
                    if(intercept){
                        return true;
                    }*/
                }
                return false;
            }
        });
    }



    protected  VB initViewBinding(LayoutInflater inflater, ViewGroup parent){
        try {
            Class<VB> clazz = ((Class<VB>) ((ParameterizedType) (this.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[0]);
            Method method = clazz.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            method.setAccessible(true);
            VB vb = (VB) method.invoke(clazz,inflater,parent,false);
            return vb;
        }catch (Throwable e){
            LogUtils.w(e);
            return null;
        }
        //HolderRelatedwordsStatus2Binding.inflate(inflater,parent,false);
    }

    public BaseViewHolder<VB,InitInfo> addToParentView(int index) {
        if (index < 0) {
            parent.addView(rootView);
            return this;
        }
        parent.addView(rootView, index);
        return this;
    }

    public void doUpdate(){
        init(initInfo);
    }

    /**
     * 初始化数据和事件.给外部调用
     */
    public final void init( @Nullable InitInfo bean) {
        initInfo = bean;
        initDataAndEventInternal(lifecycleOwner, bean);
        //rootViewBackPressedHanlde();
    }


    /**
     * 根据传入的数据初始化holder
     * @param lifecycleOwner
     * @param bean
     */
    protected abstract void initDataAndEventInternal(LifecycleOwner lifecycleOwner, InitInfo bean);



    static Context getContext(@NonNull LifecycleOwner lifecycleOwner, @Nullable ViewGroup parent) {
        Context context = null;
        if (parent != null) {
            context = parent.getContext();
        } else {
            if (lifecycleOwner instanceof Fragment) {
                context = ((Fragment) lifecycleOwner).getContext();
            } else if (lifecycleOwner instanceof FragmentActivity) {
                context = (Context) lifecycleOwner;
            }
        }
        return context;
    }


/*    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackground() {
        // 应用进入后台
        Log.w("test","LifecycleChecker onAppBackground ON_STOP: "+this);

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForeground() {
        // 应用进入前台
        Log.w("test","LifecycleChecker onAppForeground ON_START: "+this);
    }*/

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        //Log.w("test","onResume: "+this);
    }

}
