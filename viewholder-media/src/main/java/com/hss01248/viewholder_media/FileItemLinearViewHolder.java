package com.hss01248.viewholder_media;

import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ConvertUtils;
import com.hss.utils.enhance.viewholder.MyRecyclerViewHolder;
import com.hss01248.viewholder_media.api.AbsFile;
import com.hss01248.viewholder_media.databinding.LayoutFileItemLinearBinding;

import org.apache.commons.lang3.time.DateFormatUtils;

import io.reactivex.functions.Consumer;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/20/24 4:03 PM
 * @Version 1.0
 */
public class FileItemLinearViewHolder extends MyRecyclerViewHolder<LayoutFileItemLinearBinding, AbsFile> {
    public FileItemLinearViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public FileItemLinearViewHolder setOnItemClicked(Consumer<AbsFile> onItemClicked) {
        this.onItemClicked = onItemClicked;
        return this;
    }

    Consumer<AbsFile> onItemClicked;

    @Override
    public void assignDatasAndEvents(AbsFile data) {
        FileItemGridViewHolder.showInfo(data, itemView.getContext(),
                itemView,binding.tv,
                binding.iv,onItemClicked,
                true,binding.ivType);
        AbsFile file = data;
        String time = DateFormatUtils.format(file.lastModified(),"yyyy-MM-dd HH:mm:ss");
        String size = ConvertUtils.byte2FitMemorySize(file.getLength());
        if(file.isDirectory()){
            AbsFile[] list = file.listFiles();
            size = (list==null ? 0 : list.length)+"个文件";
        }else {
            size = ConvertUtils.byte2FitMemorySize(file.getLength(),1);
        }
        binding.info.setText(time+"    "+ size);
    }
}
