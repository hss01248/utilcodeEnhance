package com.hss01248.refresh_loadmore;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss.utils.enhance.viewholder.MyViewHolder;
import com.hss01248.refresh_loadmore.databinding.CommonRefreshLoadmoreRecyclerviewBinding;
import com.hss01248.toast.MyToast;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Map;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/01/2023 17:50
 * @Version 1.0
 */
public class RefreshLoadMoreRecycleViewHolder<T> extends MyViewHolder<CommonRefreshLoadmoreRecyclerviewBinding, Map<String,Object>> {
    public RefreshLoadMoreRecycleViewHolder(Context context) {
        super(context);
        init2();
        //initRecyclerViewDefault();
    }

    public RefreshLoadMoreRecycleViewHolder(ViewGroup parent) {
        super(parent);
        init2();
        //initRecyclerViewDefault();
    }

    public void initRecyclerViewDefault() {
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

        BaseQuickAdapter.OnItemClickListener originalListener = adapter.getOnItemClickListener();
        BaseQuickAdapter.OnItemLongClickListener onItemLongClickListener = adapter.getOnItemLongClickListener();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(!loadDataImpl.onItemClicked(view, (T) adapter.getData().get(position),position)){
                    if(originalListener != null){
                        originalListener.onItemClick(adapter, view, position);
                    }

                }
            }
        });
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
               if(! loadDataImpl.onItemLongPressed(view, (T) adapter.getData().get(position),position)){
                   if(onItemLongClickListener != null){
                      return onItemLongClickListener.onItemLongClick(adapter, view, position);
                   }
                   return false;
               }else {
                   return true;
               }

            }
        });

        binding.recyclerView.setAdapter(adapter);
    }

    BaseQuickAdapter<T, BaseViewHolder> adapter;

    public PagerDto<T> getDto() {
        return dto;
    }

    PagerDto<T> dto = new PagerDto<T>().firstPage();

    public void setLoadDataImpl(ILoadData<T> loadDataImpl) {
        this.loadDataImpl = loadDataImpl;
    }

    public void loadByNewParams(String searchKey,Map<String,Object> params){

        loadFirstTime(searchKey,params);
    }

    ILoadData<T> loadDataImpl;
    boolean hasSucceed;

    private void init2() {
        RefreshLayout refreshLayout = binding.refreshLayout;
        //refreshLayout.setRefreshHeader(new MaterialHeader(parent.getContext()));
        //refreshLayout.setRefreshFooter(new ClassicsFooter(parent.getContext()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                PagerDto<T> firstPage = dto.firstPage();
                loadDataImpl.queryData(firstPage, new MyCommonCallback<PagerDto<T>>() {
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
                            //dto = tPagerDto;
                            RefreshLoadMoreRecycleViewHolder.this.dto = firstPage;
                            RefreshLoadMoreRecycleViewHolder.this.dto.isLast = tPagerDto.isLast;

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
                PagerDto<T> copy = dto.copy();
                copy.pageIndex++;
                copy.offset += copy.pageSize;
                loadDataImpl.queryData(copy, new MyCommonCallback<PagerDto<T>>() {
                    @Override
                    public void onSuccess(PagerDto<T> tPagerDto) {

                        if(tPagerDto.datas == null || tPagerDto.datas.isEmpty()){
                            refreshlayout.finishLoadMoreWithNoMoreData();
                        }else {
                            refreshlayout.finishLoadMore(true);
                            adapter.addData(tPagerDto.datas);

                            dto.pageIndex = copy.pageIndex;
                            dto.offset = copy.offset;
                            dto.isLast = tPagerDto.isLast;
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
    protected void assignDataAndEventReal(Map<String,Object> data) {


        loadFirstTime("",data);
    }

    private void loadFirstTime(String searchKey,Map<String,Object> data) {
        binding.getRoot().showLoading();
        PagerDto<T> firstPage = dto.firstPage();
        firstPage.searchText = searchKey;
        firstPage.searchParams = data;
        loadDataImpl.queryData(firstPage, new MyCommonCallback<PagerDto<T>>() {
            @Override
            public void onSuccess(PagerDto<T> tPagerDto) {
                hasSucceed = true;
                if(tPagerDto.datas == null || tPagerDto.datas.isEmpty()){
                    dto = firstPage;
                    dto.isLast = true;
                    binding.getRoot().showEmpty(emptyMsg,0,null,null);
                }else {
                    dto = firstPage;
                    dto.isLast = tPagerDto.isLast;
                    binding.getRoot().showContent();
                    adapter.replaceData(tPagerDto.datas);
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
