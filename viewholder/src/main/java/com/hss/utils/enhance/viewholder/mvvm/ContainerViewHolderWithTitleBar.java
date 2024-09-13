package com.hss.utils.enhance.viewholder.mvvm;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.BarUtils;
import com.hss.utils.enhance.lifecycle.LifecycleObjectUtil;
import com.hss01248.viewholder.databinding.ActivityCommonContainerBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

public class ContainerViewHolderWithTitleBar extends BaseViewHolder<ActivityCommonContainerBinding, Object>{
    public ContainerViewHolderWithTitleBar(Context context) {
        super(context);
        //嵌入到状态栏
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.vStatus.getLayoutParams();
        layoutParams.height = BarUtils.getStatusBarHeight();
        binding.vStatus.setLayoutParams(layoutParams);
        binding.realTitleBar.getLeftView().setText("");
    }

    public void useInDialog(boolean statusBarTransplant) {
        this.useInDialog = true;
        this.statusBarTransplant = statusBarTransplant;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.vStatus.getLayoutParams();
        layoutParams.height = statusBarTransplant ? BarUtils.getStatusBarHeight() :0;
        binding.vStatus.setLayoutParams(layoutParams);

    }

    public boolean useInDialog;
    public boolean statusBarTransplant;

    public  void setTitleBarWhite(){
        binding.llTitlebar.setVisibility(View.VISIBLE);
        binding.llTitlebar.setBackgroundColor(Color.WHITE);
        setStatusBarFontBlack(true);
    }

    public  void setStatusBarFontBlack(boolean black){
        UltimateBarX.statusBarOnly((FragmentActivity) LifecycleObjectUtil.getActivityFromContext(context))
                .fitWindow(false)
                .color(Color.TRANSPARENT)
                //.colorRes(R.color.deepSkyBlue)
                .light(black)
                //.lvlColorRes(R.color.cyan)
                .apply();
    }

    /**
     * 右边显示三个...,自行设置onclicklistener
     * @param whiteIcon
     */
    public void showRightMoreIcon(boolean whiteIcon){
        binding.realTitleBar.setRightTitle("● ● ●");
        binding.realTitleBar.setRightTitleColor(whiteIcon?Color.WHITE:Color.parseColor("#333333"));
        binding.realTitleBar.getRightView().setTextSize(8);
    }


    public  void setTitleBarHidden(boolean keepStatusBarSub){
        if(keepStatusBarSub){
            binding.realTitleBar.setVisibility(View.GONE);
        }else {
            binding.llTitlebar.setVisibility(View.GONE);
        }
    }
    public  void setTitleBarTransplantAndRelative(boolean fontWhite){
        binding.llTitlebar.setVisibility(View.VISIBLE);
        binding.llTitlebar.setBackgroundColor(Color.TRANSPARENT);
        binding.realTitleBar.setBackgroundColor(Color.TRANSPARENT);
        binding.realTitleBar.setLineDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(fontWhite){
            binding.realTitleBar.setTitleColor(Color.WHITE);
            binding.realTitleBar.setLeftIconTint(Color.WHITE);
        }else{
            binding.realTitleBar.setTitleColor(Color.parseColor("#333333"));
            binding.realTitleBar.setLeftIconTint(Color.parseColor("#333333"));
        }
        setStatusBarFontBlack(!fontWhite);
        binding.llRoot.removeView(binding.llTitlebar);
        binding.rlContainer.addView(binding.llTitlebar);

    }

    @Override
    protected void initDataAndEventInternal(LifecycleOwner lifecycleOwner, Object bean) {

    }
}
