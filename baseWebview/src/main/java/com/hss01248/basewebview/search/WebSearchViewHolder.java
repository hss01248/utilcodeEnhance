package com.hss01248.basewebview.search;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss.utils.enhance.viewholder.MyViewHolder;
import com.hss01248.basewebview.BaseQuickWebview;
import com.hss01248.basewebview.R;
import com.hss01248.basewebview.databinding.WebSearchHolderBinding;
import com.hss01248.dialog.fullscreen.FullScreenDialog;
import com.hss01248.history.api.OnHistoryItemClickListener;
import com.hss01248.history.api.SearchHistoryViewHolder;
import com.hss01248.history.api.db.SearchDbUtil;
import com.hss01248.iwidget.singlechoose.ISingleChooseItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/01/2023 15:25
 * @Version 1.0
 */
public class WebSearchViewHolder extends MyViewHolder<WebSearchHolderBinding, BaseQuickWebview> {


    SearchHistoryViewHolder historyViewHolder;
   public static final String KEY_ENGIN = "search_engine";
    static  int searchEngin = 0;

    public WebSearchViewHolder(Context context) {
        super(context);
        searchEngin = SPStaticUtils.getInt(KEY_ENGIN);
    }


    @Override
    protected void assignDataAndEventReal(BaseQuickWebview quickWebview) {
        ThreadUtils.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.etInput.requestFocus();
                KeyboardUtils.showSoftInput(binding.etInput);
            }
        },500);
        binding.etInput.setText(quickWebview.getWebView().getUrl());
        binding.etInput.setSelection(binding.etInput.getText().length());
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ISingleChooseItem<Integer>> items = new ArrayList<>();
                ISingleChooseItem<Integer> item0 = new ISingleChooseItem() {
                    @Override
                    public String text() {
                        return "百度"+(searchEngin ==0?"(✓)":"");
                    }

                    @Override
                    public void onItemClicked(int position, Object bean) {
                        SPStaticUtils.put(KEY_ENGIN,0);
                        changeEnginIcon();
                    }
                };
                ISingleChooseItem<Integer> item1 = new ISingleChooseItem() {
                    @Override
                    public String text() {
                        return "谷歌"+(searchEngin ==1?"(✓)":"");
                    }

                    @Override
                    public void onItemClicked(int position, Object bean) {
                        SPStaticUtils.put(KEY_ENGIN,1);
                        changeEnginIcon();
                    }
                };
                ISingleChooseItem<Integer> item2 = new ISingleChooseItem() {
                    @Override
                    public String text() {
                        return "必应"+(searchEngin ==2?"(✓)":"");
                    }

                    @Override
                    public void onItemClicked(int position, Object bean) {
                        SPStaticUtils.put(KEY_ENGIN,2);
                        changeEnginIcon();
                    }
                };
                items.add(item0);
                items.add(item1);
                items.add(item2);
                ISingleChooseItem.showAsMenu2(binding.ivClose,items,1);
            }
        });

        changeEnginIcon();


        if(historyViewHolder == null){
            historyViewHolder = new SearchHistoryViewHolder((ViewGroup) getRootView());
            historyViewHolder.assignDataAndEvent("");
            binding.rlContent.addView(historyViewHolder.getRootView());
        }else {
            historyViewHolder.refreshData();
        }

        historyViewHolder.setItemClickListener(new OnHistoryItemClickListener() {
            @Override
            public void onHistoryItemClick(String text) {
                quickWebview.loadUrl(text);
                if(dialog != null){
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });


        binding.ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etInput.setText("");
            }
        });

        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog != null){
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });

        binding.etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    String str = binding.etInput.getText().toString().trim();
                    if(TextUtils.isEmpty(str)){
                        return false;
                    }
                    // String url2 = "https://www.baidu.com/s?wd="+url;
                    //https://www.google.com/search?q=
                    //https://www.bing.com/search?q=%E9%87%91%E6%9D%A1
                    quickWebview.loadUrl(str);
                    KeyboardUtils.hideSoftInput(binding.etInput);
                    if(dialog != null){
                        dialog.dismiss();
                        dialog = null;
                    }
                    if(!str.startsWith("http")){
                        SearchDbUtil.addOrUpdate(str);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void changeEnginIcon() {

        Integer[] res = {R.drawable.icon_baidu,R.drawable.icon_google,R.drawable.icon_bing};
        int index = SPStaticUtils.getInt(KEY_ENGIN);
        binding.ivClose.setImageResource(res[index]);
    }

    public static Dialog showHistory(Context context,BaseQuickWebview quickWebview){
        return new WebSearchViewHolder(context).assignDataAndEvent(quickWebview).showInFullScreenDialog();
    }
}
