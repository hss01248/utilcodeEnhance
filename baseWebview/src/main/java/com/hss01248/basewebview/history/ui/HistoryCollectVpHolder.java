package com.hss01248.basewebview.history.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.hss.utils.enhance.viewholder.MyViewHolder;
import com.hss.utils.enhance.viewholder.viewpager.BasePagerAdapter;
import com.hss.utils.enhance.viewholder.viewpager.IViewPagerInstantiateItem;
import com.hss01248.basewebview.BaseQuickWebview;
import com.hss01248.basewebview.databinding.VpHistoryCollectBinding;

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
    public HistoryCollectVpHolder(Context parent) {
        super(parent);
        init2(parent);
    }

    private void init2(Context parent) {
        String[] titles = {"收藏","历史"};
        MagicIndicator magicIndicator = binding.magicIndicator;
        CommonNavigator commonNavigator = new CommonNavigator(binding.getRoot().getContext());
        commonNavigator.setAdjustMode(true);
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
                indicator.setMode(LinePagerIndicator.MODE_MATCH_EDGE);
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
                viewHolder.assignDataAndEvent(data);
                return viewHolder.binding.getRoot();
            }
        }));
    }

    @Override
    public Dialog showInFullScreenDialog() {
        Dialog  dialog0 =  super.showInFullScreenDialog();
        dialog = dialog0;
        dialog0.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog1) {
                dialog = null;
            }
        });
        return dialog0;
    }

    public static Dialog dialog;

}
