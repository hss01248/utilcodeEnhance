package com.hss01248.lock_app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.startup.Initializer;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.toast.MyToast;
import com.hss01248.utils.ext.PkgListFetcher;
import com.hss01248.utils.ext.lifecycle.AppFirstActivityOnCreateListener;
import com.hss01248.utils.ext.lifecycle.BackgroundAndFirstActivityCreatedCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 09/05/2022 14:41
 * @Version 1.0
 */
public class LockInit implements Initializer<String> {
    @Override
    public String create(Context context) {
        if (context instanceof Application) {
            Application application = (Application) context;
            init2(application);
        }
        return "LockInit";
    }

    public static String pp = "7235";
    public static boolean lock = true;

    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return new ArrayList<>();
    }


    private void init2(Application application) {
        BackgroundAndFirstActivityCreatedCallback
                .addAppFirstActivityOnCreateListener(new AppFirstActivityOnCreateListener() {

                    @Override
                    public void onFirtActivityCreated(Activity activity, @Nullable Bundle savedInstanceState) {
                        AppFirstActivityOnCreateListener.super.onFirtActivityCreated(activity, savedInstanceState);
                        //fetchConfig(activity);
                    }

                    @Override
                    public void onForegroundBackgroundChanged(Activity activity, boolean changeToBackground) {
                        AppFirstActivityOnCreateListener.super.onForegroundBackgroundChanged(activity, changeToBackground);

                        if (changeToBackground) {
                            return;
                        }
                        if(!lock){
                            return;
                        }
                        boolean isForResult = PkgListFetcher.isForResult(activity);
                        if(isForResult){
                            return;
                        }


                        showLockDialog(activity);
                    }
                });

    }



    private static void showLockDialog(Activity activity) {
        EditText editText = new EditText(activity);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        int padding = SizeUtils.dp2px(10);
        editText.setPadding(padding, padding, padding, padding);
        editText.setHint("输入密码");
        //startActivity
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("验证")
                .setView(editText)
                .setPositiveButton("确认", null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setDimAmount(1.0f);


        // Keyboard
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        // Auto show keyboard
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean isFocused) {

                if (isFocused) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                ThreadUtils.getMainHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editText.requestFocus();
                    }
                },500);


                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (editText.getText().toString().trim().equals(pp+"")) {
                            // Hide keyboard
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            dialogInterface.dismiss();
                            //下面这个也可以
                            //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        } else {
                            MyToast.error("密码错误");
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void fetchConfig(Activity activity) {
        PkgListFetcher.fetch("lockxx",
                "https://oss-kodo.hss01248.tech/2024/locklist.json",
                new MyCommonCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        LogUtils.w("result:"+aBoolean);
                        lock = aBoolean;
                    }
                });
    }
}
