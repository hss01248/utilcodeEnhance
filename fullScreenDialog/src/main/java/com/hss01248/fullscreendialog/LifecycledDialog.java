package com.hss01248.fullscreendialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 * todo 退到后台会导致dialog dimiss ,回到前后又重新show
 * 处理参考: com.kongzue.dialogx.interfaces.BaseDialog
 */
public class LifecycledDialog extends Dialog implements LifecycleOwner {

    protected LifecycleRegistry lifecycle = new LifecycleRegistry(this);

    public LifecycledDialog(@NonNull Context context) {
        super(context);
    }

    public LifecycledDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LifecycledDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        super.show();

    }

    @Override
    public void dismiss() {
        super.dismiss();
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }

    @Override
    public void cancel() {
        super.cancel();
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycle;
    }
}
