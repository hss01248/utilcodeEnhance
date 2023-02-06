package com.hss01248.refresh_loadmore;

import android.view.View;

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
        ISingleChooseItem.showAsMenu(view, chooseItems, data);
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
