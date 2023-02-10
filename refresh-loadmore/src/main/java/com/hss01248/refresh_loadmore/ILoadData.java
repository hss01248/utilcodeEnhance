package com.hss01248.refresh_loadmore;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.widget.PopupWindowCompat;

import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.iwidget.BaseDialogListener;
import com.hss01248.iwidget.msg.AlertDialogImplByDialogUtil;
import com.hss01248.iwidget.singlechoose.ISingleChooseItem;

import java.util.ArrayList;
import java.util.List;

public interface ILoadData<T> {

    void queryData(PagerDto<T> pager, MyCommonCallback<PagerDto<T>> callback);

    default boolean onItemLongPressed(View view, T data, int position) {
        List<ISingleChooseItem<T>> chooseItems = new ArrayList<>();
        chooseItems.add(new ISingleChooseItem<T>() {
            @Override
            public String text() {
                return "删除";
            }

            @Override
            public void onItemClicked(int position, T bean) {
                new AlertDialogImplByDialogUtil()
                        .showMsg("删除确认", "是否删除当前条目?", "删除", "取消",
                                new BaseDialogListener() {
                                    @Override
                                    public void onConfirm() {
                                        boolean delete = deleteData(data, position);
                                    }
                                });
            }
        });
       // ISingleChooseItem.showAsMenu(view, chooseItems, data);

        PopupWindow pop = new PopupWindow(view.getContext());
        TextView textView = new TextView(view.getContext());
        textView.setText("xxxxxxxxxxxxxxxxxxxxxxxx");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //textView.setClipToOutline(true);
        }
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundColor(Color.GREEN);
        //textView.setPadding(20,20,20,20);
        //textView.setBackgroundColor(Color.GREEN);
        pop.setContentView(textView);
        pop.setOutsideTouchable(true);
       //pop.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pop.setElevation(10);
        }

        PopupWindowCompat.showAsDropDown(pop,view,0,0, Gravity.LEFT);

        return true;
    }

    boolean deleteData(T data, int position);

    default boolean insertData(T data) {
        return false;
    }

    default boolean updateData(T data) {
        return false;
    }


    /**
     * false表示不覆写
     * @param view
     * @param data
     * @param position
     * @return
     */
    default boolean onItemClicked(View view, T data, int position) {
        //显示详情
        return false;
    }

}
