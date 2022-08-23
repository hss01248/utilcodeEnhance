package com.hss.utils.enhance.lifecycle;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class LifecledCallbackHelper implements DefaultLifecycleObserver {

    /**
     * activity,context,fragment,LifecycleOwner
     */
    public LifecledCallbackHelper(Object lifeCycledObj) {
        if(lifeCycledObj == null){
            return;
        }
        LifecycleOwner lifecycleOwner = LifecycleObjectUtil.getLifecycleOwnerFromObj(lifeCycledObj);
        if(lifecycleOwner != null){
            lifecycleOwner.getLifecycle().addObserver(this);
        }else {
            this.lifeCycledObj =  LifecycleObjectUtil.getLifecycledObjectFromObj(lifeCycledObj);
        }
    }

    /**
     * activity,context,fragment,LifecycleOwner
     */
    protected Object lifeCycledObj;
    protected boolean hasDestoryed;



    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        hasDestoryed = true;
        lifeCycledObj = null;
    }

    public boolean hasDestoryed(){
        if(hasDestoryed){
            return true;
        }
        if(lifeCycledObj == null){
            return false;
        }
        if(lifeCycledObj instanceof Activity){
            Activity activity = (Activity) lifeCycledObj;
            if(activity.isFinishing() || activity.isDestroyed()){
                return true;
            }
        }else if(lifeCycledObj instanceof Fragment){
            Fragment fragment = (Fragment) lifeCycledObj;
            if(fragment.isDetached() || fragment.isRemoving()){
                return true;
            }
        }
        return false;
    }
}
