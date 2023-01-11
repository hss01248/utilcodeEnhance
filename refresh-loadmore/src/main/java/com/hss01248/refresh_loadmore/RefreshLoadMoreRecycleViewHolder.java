package com.hss01248.refresh_loadmore;

import android.graphics.Color;
import android.view.ViewGroup;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fondesa.recyclerviewdivider.DividerDecoration;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss.utils.enhance.viewholder.MyViewHolder;
import com.hss01248.refresh_loadmore.databinding.CommonRefreshLoadmoreRecyclerviewBinding;
import com.hss01248.toast.MyToast;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/01/2023 17:50
 * @Version 1.0
 */
public class RefreshLoadMoreRecycleViewHolder<T> extends MyViewHolder<CommonRefreshLoadmoreRecyclerviewBinding, Map> {
    public RefreshLoadMoreRecycleViewHolder(ViewGroup parent) {
        super(parent);
        init2(parent);
        defaultRecyclerView();
    }

    private void defaultRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
       /* RecyclerView.ItemDecoration itemDecoration = (RecyclerView.ItemDecoration) DividerDecoration.builder(binding.getRoot().getContext())
                .color(Color.parseColor("#eeeeee"))
                .size(SizeUtils.dp2px(1))
                .build();*/
        binding.recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(binding.getRoot().getContext())
                        .color(Color.parseColor("#eeeeee"))
                .size(SizeUtils.dp2px(1))
                .build());
    }

    /**
     * 可以拿到后修改配置
     * @return
     */
    public void setPageSize(int pageSize) {
         dto.pageSize = pageSize;
    }

    public void setEmptyMsg(String emptyMsg) {
        this.emptyMsg = emptyMsg;
    }

    String emptyMsg;

    public void setAdapter(BaseQuickAdapter<T, BaseViewHolder> adapter) {
        this.adapter = adapter;
        binding.recyclerView.setAdapter(adapter);
    }

    BaseQuickAdapter<T, BaseViewHolder> adapter;

    PagerDto<T> dto = new PagerDto<>();

    public void setLoadDataImpl(ILoadData<T> loadDataImpl) {
        this.loadDataImpl = loadDataImpl;
    }

    ILoadData<T> loadDataImpl;
    boolean hasSucceed;

    private void init2(ViewGroup parent) {
        RefreshLayout refreshLayout = binding.refreshLayout;
        //refreshLayout.setRefreshHeader(new MaterialHeader(parent.getContext()));
        //refreshLayout.setRefreshFooter(new ClassicsFooter(parent.getContext()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                PagerDto<T> pagerDto1 = new PagerDto<>();
                loadDataImpl.loadData(pagerDto1, new MyCommonCallback<PagerDto<T>>() {
                    @Override
                    public void onSuccess(PagerDto<T> tPagerDto) {
                        binding.getRoot().showContent();
                        refreshlayout.finishRefresh();
                        hasSucceed = true;
                        if(tPagerDto.datas == null || tPagerDto.datas.isEmpty()){
                            adapter.replaceData(new ArrayList<>());
                            binding.getRoot().showEmpty(emptyMsg,0,null,null);
                        }else {
                            binding.getRoot().showContent();
                            adapter.replaceData(tPagerDto.datas);
                            dto = tPagerDto;
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        MyCommonCallback.super.onError(msg);
                        refreshlayout.finishRefresh();
                        if(hasSucceed){
                            MyToast.error(msg);
                        }else {
                            binding.getRoot().showError(msg);
                        }
                    }
                });
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                //refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                loadDataImpl.loadData(dto, new MyCommonCallback<PagerDto<T>>() {
                    @Override
                    public void onSuccess(PagerDto<T> tPagerDto) {

                        if(tPagerDto.datas == null || tPagerDto.datas.isEmpty()){
                            refreshlayout.finishLoadMoreWithNoMoreData();
                        }else {
                            refreshlayout.finishLoadMore(true);
                            adapter.addData(tPagerDto.datas);
                            dto = tPagerDto;
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        MyCommonCallback.super.onError(msg);
                       refreshlayout.finishLoadMore(false);
                    }
                });
            }
        });
    }



    @Override
    protected void assignDataAndEventReal(Map data) {
        binding.getRoot().showLoading();

        dto.offset = 0;
       loadDataImpl.loadData(dto, new MyCommonCallback<PagerDto<T>>() {
           @Override
           public void onSuccess(PagerDto<T> tPagerDto) {
               hasSucceed = true;
               if(tPagerDto.datas == null || tPagerDto.datas.isEmpty()){
                   binding.getRoot().showEmpty(emptyMsg,0,null,null);
               }else {
                   binding.getRoot().showContent();
                   adapter.replaceData(tPagerDto.datas);
                   dto = tPagerDto;
               }
           }

           @Override
           public void onError(String msg) {
               MyCommonCallback.super.onError(msg);
               binding.getRoot().showError(msg);
           }
       });
    }
}
