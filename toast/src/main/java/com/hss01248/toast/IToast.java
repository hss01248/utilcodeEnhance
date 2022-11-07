package com.hss01248.toast;

import android.app.Dialog;

import androidx.annotation.Nullable;

public interface IToast {

    void success(CharSequence text);

    void error(CharSequence text);

    void show(CharSequence text);

    void debug(CharSequence text);

    Dialog showLoadingDialog(@Nullable String loadingText);

    void dismissLoadingDialog(Dialog dialog);

}
