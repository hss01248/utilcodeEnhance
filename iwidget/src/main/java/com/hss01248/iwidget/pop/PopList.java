package com.hss01248.iwidget.pop;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.ListPopupWindow;

import java.util.List;

public class PopList {

    public static ListPopupWindow showPop(Context context,int width,View anchorView, List<String> list,OnItemClickListener listener){
        ListPopupWindow pop = new ListPopupWindow(context);
        ListPopupWindowAdapter adapter = new ListPopupWindowAdapter(list,context);
        pop.setAdapter(adapter);

        pop.setWidth(width);
        pop.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String chosen = list.get(position);
                if(listener != null){
                    listener.onClick(position,chosen);
                }
                pop.dismiss();
            }
        });
        pop.setAnchorView(anchorView);
        pop.setDropDownGravity(Gravity.BOTTOM);
        pop.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        pop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //设置高度自适应！


        pop.show();
        if(pop.getListView() != null){
            //pop.getListView().setBackgroundResource(R.drawable.pop_bg_stoke);
            pop.getListView().setVerticalScrollBarEnabled(false);
        }
        Activity activity = getActivityFromContext(context);
        if(activity == null || activity.getWindow() ==null){

        }else {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
        return pop;

    }

    static Activity getActivityFromContext(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public interface OnItemClickListener{
        void onClick(int position,String str);
    }
}
