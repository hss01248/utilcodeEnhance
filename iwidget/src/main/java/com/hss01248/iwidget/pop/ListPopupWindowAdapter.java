package com.hss01248.iwidget.pop;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class ListPopupWindowAdapter extends BaseAdapter {
    private List<String> mArrayList;
    private Context mContext;
    public ListPopupWindowAdapter(List<String> list, Context context) {
        super();
        this.mArrayList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mArrayList == null) {
            return 0;
        } else {
            return this.mArrayList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mArrayList == null) {
            return null;
        } else {
            return this.mArrayList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = new TextView(parent.getContext());
            holder.itemTextView = (TextView) convertView;
            convertView.setTag(holder);
            holder.itemTextView.setTextColor(Color.parseColor("#222222"));
            holder.itemTextView.setTextSize(16);
            int top = dp2px(9);
            int left = dp2px(16);
            holder.itemTextView.setPadding(left,top,left,top);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (this.mArrayList != null) {
            final String itemName = this.mArrayList.get(position);
            if (holder.itemTextView != null) {
                holder.itemTextView.setText(itemName);
            }
        }

        return convertView;

    }

    private class ViewHolder {
        TextView itemTextView;
    }

    static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
