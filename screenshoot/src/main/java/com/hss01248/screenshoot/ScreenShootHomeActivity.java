package com.hss01248.screenshoot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hss01248.permission.ext.IExtPermissionCallback;
import com.hss01248.permission.ext.MyPermissionsExt;
import com.hss01248.permission.ext.permissions.SystemAlertPermissionImpl;
import com.hss01248.screenshoot.databinding.HomeScreenShootConfigBinding;
import com.hss01248.screenshoot.system.CaptureService;
import com.hss01248.screenshoot.system.SystemScreenShotUtil;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/10/24 9:51 AM
 * @Version 1.0
 */
public class ScreenShootHomeActivity extends AppCompatActivity {


    public  static void start(){
        ActivityUtils.getTopActivity().startActivityForResult(new Intent(ActivityUtils.getTopActivity(),ScreenShootHomeActivity.class),168);
    }

    HomeScreenShootConfigBinding configBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         configBinding = HomeScreenShootConfigBinding.inflate(getLayoutInflater());
        setContentView(configBinding.getRoot());

        configBinding.btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission2();
            }
        });
        updateConfigText();

        configBinding.btnLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropConfigActivity.start(true);
            }
        });
        configBinding.btnPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropConfigActivity.start(false);
            }
        });
    }

    private void updateConfigText() {
        boolean permission = new SystemAlertPermissionImpl().checkPermission(this);
        StringBuilder builder = new StringBuilder();
        builder.append("悬浮窗权限是否打开(用于一键截图按钮): ")
                .append(permission ? "已允许" : "未被允许")
                .append("\n\n");

        builder.append("悬浮窗是否正在显示: ")
                .append(SystemScreenShotUtil.textView==null ? "未显示" : "已显示(左上角)")
                .append("\n\n");

        builder.append("截图权限是否打开: ")
                .append(CaptureService.mediaProjection == null ? "未被允许" : "已允许")
                .append("\n\n");

        /*builder.append("辅助功能是否打开: ")
                .append(MyAccessibilityService.isAccessibilityServiceEnabled() ? "已打开" : "未打开")
                .append("\n\n");*/

        builder.append("截图后裁剪配置(横屏): ")
                .append(SystemScreenShotUtil.readRect(true))
                .append("\n\n");
        builder.append("截图后裁剪配置(竖屏): ")
                .append(SystemScreenShotUtil.readRect(false));

        configBinding.tvConfig.setText(builder.toString());

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateConfigText();
    }

    private void requestPermission2() {

        MyPermissionsExt.askPermission(this, new SystemAlertPermissionImpl(),
                new IExtPermissionCallback() {
                    @Override
                    public void onGranted(String name) {
                        updateConfigText();
                        SystemScreenShotUtil.createFloatView();
                        if (CaptureService.mediaProjection != null) {
                            showCropConfig();
                        }else{
                            SystemScreenShotUtil.startCapture(true);
                            delayShowConfigView();
                        }
                    }

                    @Override
                    public void onDenied(String name) {
                        ToastUtils.showShort("没有允许悬浮窗权限");
                    }
                });

    }

    private void delayShowConfigView() {
        ThreadUtils.getMainHandler().postDelayed(new Runnable(){

            @Override
            public void run() {
                updateConfigText();
                if (CaptureService.mediaProjection != null) {
                    showCropConfig();
                }else {
                    delayShowConfigView();
                }
            }
        },1000);
    }

    private void showCropConfig() {
        //CropViewActivity.start();
        if(MyAccessibilityService.isAccessibilityServiceEnabled()){
           // CropViewActivity.start();
        }else {
           // MyAccessibilityService.promptForAccessibilityServiceEnable();
        }

    }
}
