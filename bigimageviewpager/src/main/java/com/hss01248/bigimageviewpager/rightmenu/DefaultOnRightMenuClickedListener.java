package com.hss01248.bigimageviewpager.rightmenu;

import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.hss01248.bigimageviewpager.OnRightMenuClickedListener;
import com.hss01248.bigimageviewpager.motion.MotionEditViewHolder;

import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/10/24 5:17 PM
 * @Version 1.0
 */
public class DefaultOnRightMenuClickedListener implements OnRightMenuClickedListener {
    @Override
    public void onClicked(View view, String path, List<String> paths, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("choose");
        CharSequence[] choices = new CharSequence[]{
                "分享",
                "元数据",
                "motion photo编辑"
        };
        builder.setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if(i ==2){
                    doEdit(path);
                }else if(i ==0){
                    doShare(path);
                }

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void doShare(String path) {

    }

    private void doEdit(String path) {
        MotionEditViewHolder.start(path);
    }
}
