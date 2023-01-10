package com.hss01248.basewebview.history.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ActivityUtils;
import com.hss.utils.enhance.viewholder.MyViewHolder;
import com.hss.utils.enhance.viewholder.viewpager.BasePagerAdapter;
import com.hss.utils.enhance.viewholder.viewpager.IViewPagerInstantiateItem;
import com.hss01248.basewebview.BaseQuickWebview;
import com.hss01248.basewebview.R;
import com.hss01248.basewebview.databinding.VpHistoryCollectBinding;
import com.hss01248.dialog.fullscreen.FullScreenDialog;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/01/2023 20:13
 * @Version 1.0
 */
public class HistoryCollectVpHolder extends MyViewHolder<VpHistoryCollectBinding,BaseQuickWebview> {
    public HistoryCollectVpHolder(ViewGroup parent) {
        super(parent);
        init2(parent);
    }

    private void init2(ViewGroup parent) {
        String[] titles = {"收藏","历史"};
        MagicIndicator magicIndicator = binding.magicIndicator;
        CommonNavigator commonNavigator = new CommonNavigator(binding.getRoot().getContext());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(Color.GRAY);
                colorTransitionPagerTitleView.setSelectedColor(Color.BLACK);
                colorTransitionPagerTitleView.setText(titles[index]);
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.viewPager.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);

        ViewPagerHelper.bind(magicIndicator, binding.viewPager);




    }



    @Override
    protected void assignDataAndEventReal(BaseQuickWebview data) {
        List<Boolean> datas = new ArrayList<>();
        datas.add(true);
        datas.add(false);
        binding.viewPager.setAdapter(new BasePagerAdapter<Boolean>(datas, new IViewPagerInstantiateItem<Boolean>() {
            @Override
            public View initView(ViewGroup container, List<Boolean> datas, int position) {
                HistoryOrCollectViewHolder viewHolder = new HistoryOrCollectViewHolder(container);
                viewHolder.isCollect = datas.get(position);
                viewHolder.assignDataAndEventReal(data);
                return viewHolder.binding.getRoot();
            }
        }));
    }

    public static Dialog dialog;
    public static Dialog showInDialog(BaseQuickWebview bean){
        HistoryCollectVpHolder holder = new HistoryCollectVpHolder(ActivityUtils.getTopActivity().findViewById(android.R.id.content));
        holder.assignDataAndEvent(bean);
        FullScreenDialog dialog = new FullScreenDialog(ActivityUtils.getTopActivity());
        dialog.setContentView(holder.getRootView());
        dialog.show();
        HistoryCollectVpHolder.dialog = dialog;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                HistoryCollectVpHolder.dialog = null;
            }
        });
        return dialog;
    }
}
