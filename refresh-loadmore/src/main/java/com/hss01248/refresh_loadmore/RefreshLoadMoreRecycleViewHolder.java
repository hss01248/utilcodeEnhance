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
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
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
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(binding.statefulLayout.getContext()));
       /* RecyclerView.ItemDecoration itemDecoration = (RecyclerView.ItemDecoration) DividerDecoration.builder(binding.statefulLayout.getContext())
                .color(Color.parseColor("#eeeeee"))
                .size(SizeUtils.dp2px(1))
                .build();*/
        binding.recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(binding.statefulLayout.getContext())
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
                if(! loadDataImpl.onItemLongPressed(view, adapter,(T) adapter.getData().get(position),position)){
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

    public ILoadData<T> getLoadDataImpl() {
        return loadDataImpl;
    }

    ILoadData<T> loadDataImpl;
    boolean hasSucceed;

    public void setAsPageLoad(boolean asPageLoad) {
        this.asPageLoad = asPageLoad;
    }

    boolean asPageLoad = true;
    private void init2() {
        RefreshLayout refreshLayout = binding.refreshLayout;
        //refreshLayout.setRefreshHeader(new MaterialHeader(parent.getContext()));
        //refreshLayout.setRefreshFooter(new ClassicsFooter(parent.getContext()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                if(asPageLoad && dto.pageIndex ==0){
                    refreshLayout.finishRefreshWithNoMoreData();
                    return;
                }
                onPre(refreshlayout);
            }


        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                //refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                if(asPageLoad &&  dto.pageIndex == dto.totalPage){
                    refreshlayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                onNext(refreshlayout,dto.pageIndex+1);
            }
        });

        if(asPageLoad){
            binding.llPager.setVisibility(View.VISIBLE);
            binding.tvPre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPre(binding.refreshLayout);

                }
            });
            binding.tvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNext(binding.refreshLayout,dto.pageIndex+1);
                }
            });

            binding.sbPager.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {

                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                    onNext(binding.refreshLayout,seekBar.getProgress()-1);
                }
            });
        }else {
            binding.llPager.setVisibility(View.GONE);
        }


    }

    private void onPre(RefreshLayout refreshlayout) {
        PagerDto<T> firstPage = null;
        if(asPageLoad){
            firstPage = dto.prePage();
        }else {
            firstPage = dto.firstPage();
        }
        PagerDto<T> finalFirstPage = firstPage;
        if(asPageLoad){
            binding.statefulLayout.showLoading();
        }
        loadDataImpl.queryData(firstPage, new MyCommonCallback<PagerDto<T>>() {
            @Override
            public void onSuccess(PagerDto<T> tPagerDto) {
                binding.statefulLayout.showContent();
                refreshlayout.finishRefresh();
                hasSucceed = true;
                if(tPagerDto.datas == null || tPagerDto.datas.isEmpty()){
                    adapter.replaceData(new ArrayList<>());
                    binding.statefulLayout.showEmpty(emptyMsg,0,null,null);
                }else {
                    binding.statefulLayout.showContent();
                    adapter.replaceData(tPagerDto.datas);
                    //dto = tPagerDto;
                }
                RefreshLoadMoreRecycleViewHolder.this.dto = finalFirstPage;
                RefreshLoadMoreRecycleViewHolder.this.dto.isLast = tPagerDto.isLast;
                RefreshLoadMoreRecycleViewHolder.this.dto.totalPage = tPagerDto.totalPage;
                if(asPageLoad){
                    binding.sbPager.setProgress(dto.pageIndex+1);
                    binding.sbPager.setMax(dto.totalPage+1);
                    binding.tvPageIndex.setText((dto.pageIndex+1)+"/"+(dto.totalPage+1));
                }
            }

            @Override
            public void onError(String msg) {
                MyCommonCallback.super.onError(msg);
                refreshlayout.finishRefresh();
                if(hasSucceed && !asPageLoad){
                    MyToast.error(msg);
                }else {
                    binding.statefulLayout.showError(msg);
                }
            }
        });
    }

    private void onNext(RefreshLayout refreshlayout,long pageIndex) {
        PagerDto<T> copy = dto.copy();
        copy.pageIndex =  pageIndex;
        copy.offset =  copy.pageSize * pageIndex;

        if(asPageLoad){
            binding.statefulLayout.showLoading();
        }
        loadDataImpl.queryData(copy, new MyCommonCallback<PagerDto<T>>() {
            @Override
            public void onSuccess(PagerDto<T> tPagerDto) {

                if(tPagerDto.datas == null || tPagerDto.datas.isEmpty()){
                    if(asPageLoad){
                        binding.statefulLayout.showEmpty();
                    }
                    refreshlayout.finishLoadMoreWithNoMoreData();

                }else {
                    if(asPageLoad){
                        binding.statefulLayout.showContent();
                    }
                    refreshlayout.finishLoadMore(true);
                    if(asPageLoad){
                        adapter.replaceData(tPagerDto.datas);
                    }else {
                        adapter.addData(tPagerDto.datas);
                    }
                }
                dto.pageIndex = copy.pageIndex;
                dto.offset = copy.offset;
                dto.isLast = tPagerDto.isLast;
                dto.totalPage = tPagerDto.totalPage;
                if(asPageLoad){
                    binding.sbPager.setProgress(dto.pageIndex+1);
                    binding.sbPager.setMax(dto.totalPage+1);
                    binding.tvPageIndex.setText((dto.pageIndex+1)+"/"+(dto.totalPage+1));
                }
            }

            @Override
            public void onError(String msg) {
                MyCommonCallback.super.onError(msg);
                dto.pageIndex = copy.pageIndex;
                dto.offset = copy.offset;
                if(asPageLoad){
                    binding.statefulLayout.showError(msg);
                }else {
                    refreshlayout.finishLoadMore(false);
                }

            }
        });
    }

    @Override
    protected void assignDataAndEventReal(Map<String,Object> data) {


        loadFirstTime("",data);
    }

    private void loadFirstTime(String searchKey,Map<String,Object> data) {
        binding.statefulLayout.showLoading();
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
                    binding.statefulLayout.showEmpty(emptyMsg,0,null,null);
                }else {
                    dto = firstPage;
                    dto.isLast = tPagerDto.isLast;
                    dto.totalPage = tPagerDto.totalPage;
                    binding.statefulLayout.showContent();
                    adapter.replaceData(tPagerDto.datas);
                    if(asPageLoad){
                        binding.sbPager.setMax(dto.totalPage+1);
                        binding.sbPager.setProgress(dto.pageIndex+1);
                        binding.tvPageIndex.setText((dto.pageIndex+1)+"/"+(dto.totalPage+1));
                    }
                }

            }

            @Override
            public void onError(String msg) {
                MyCommonCallback.super.onError(msg);
                binding.statefulLayout.showError(msg);
            }
        });
    }
}
