package com.hss.utils.enhance.viewholder;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/13/24 10:14 AM
 * @Version 1.0
 */
public abstract class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewHolder> {

    private List datas;
    boolean isListViewFling;

    public boolean isListViewFling() {
        return isListViewFling;
    }

    public void setListViewFling(boolean listViewFling) {
        isListViewFling = listViewFling;
    }
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return generateCoustomViewHolder(viewType);
    }

    protected abstract MyRecyclerViewHolder generateCoustomViewHolder(int viewType);


    @Override
    public void onBindViewHolder(MyRecyclerViewHolder holder, int position) {
        holder.assignDatasAndEvents(datas.get(position),position,
                position == getItemCount() -1,isListViewFling,datas,this);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    public void refresh(List newData) {
        if (newData == null){
            datas.clear();
            notifyDataSetChanged();
            return;
        }
        if (datas == null){
            datas = newData;
            notifyDataSetChanged();
        }else {
            datas.clear();
            datas.addAll(newData);
            notifyDataSetChanged();
        }
    }


    public void addAll(List newData) {
        if (newData == null){
            return;
        }
        if (datas == null){
            datas = newData;
            notifyDataSetChanged();
        }else {
            datas.addAll(newData);
            notifyDataSetChanged();
        }
    }


    public void clear() {
        if (datas != null){
            datas.clear();
            notifyDataSetChanged();
        }
    }


    public void delete(int position) {
        if (datas != null && position < getItemCount()){
            datas.remove(position);
            notifyItemRemoved(position);
        }
    }


    public void add(Object object) {
        if (object != null){
            datas.add(object);
            notifyItemInserted(datas.size() -1);
        }

    }

    public List getListData(){
        return datas;
    }
}
