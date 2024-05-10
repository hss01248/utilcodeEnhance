package com.hss01248.screenshoot;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/10/24 11:46 AM
 * @Version 1.0
 */
public class MyAccessibilityService extends AccessibilityService {

   public static  String topAppName = "";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d("Current Top App", event.toString());
            if(event.getText() !=null && !event.getText().isEmpty()){
                topAppName = event.getText().get(0).toString();
                Log.d("Current Top App", "top appName: "+topAppName);
            }
            if (event.getPackageName() != null) {
                String currentTopApp = event.getPackageName().toString();
                Log.d("Current Top App", currentTopApp);
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    public static boolean isAccessibilityServiceEnabled( ) {
        return  isAccessibilityServiceEnabled(MyAccessibilityService.class);
    }

    public static boolean isAccessibilityServiceEnabled( Class<? extends AccessibilityService> service) {
        String prefString = Settings.Secure.getString(Utils.getApp().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        ComponentName expectedComponentName = new ComponentName(Utils.getApp(), service);

        if (prefString != null && !prefString.isEmpty()) {
            String[] components = TextUtils.split(prefString, ":");
            for (String component : components) {
                ComponentName componentName = ComponentName.unflattenFromString(component);
                if (componentName != null && componentName.equals(expectedComponentName))
                    return true;
            }
        }
        return false;
    }

    public static void promptForAccessibilityServiceEnable() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityUtils.getTopActivity());
        builder.setTitle("Enable Accessibility Service");
        builder.setMessage("To use all features, please enable the Accessibility Service in Settings.");
        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                ActivityUtils.getTopActivity().startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
