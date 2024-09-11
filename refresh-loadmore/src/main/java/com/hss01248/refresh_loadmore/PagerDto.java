package com.hss01248.refresh_loadmore;

import androidx.annotation.Keep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/01/2023 17:24
 * @Version 1.0
 */
@Keep
public class PagerDto<T> {

   public List<T> datas;
    public boolean isLast;
    public int pageSize = 20;
    public long offset = 0;
    public long pageIndex = 0;
    public long totalPage = 0;
    public Map<String,Object> extras = new HashMap<>();
    public Map<String,Object> searchParams = new HashMap<>();
    public String searchText;


    public PagerDto<T> copy(){
     PagerDto<T> dto = new PagerDto<>();
     dto.offset =offset;
     dto.pageSize = pageSize;
     dto.pageIndex = pageIndex;
     dto.searchParams = searchParams;
     dto.isLast = isLast;
     dto.searchText = searchText;
     dto.extras = extras;
     dto.datas = datas;
     dto.totalPage = totalPage;
     return dto;
    }

    public PagerDto<T> firstPage(){
     PagerDto<T> copy = copy();
     copy.pageIndex = 0;
     copy.offset = 0;
     copy.isLast = false;
     return copy;
    }

 public PagerDto<T> prePage(){
  PagerDto<T> copy = copy();
  copy.pageIndex --;
  if(copy.pageIndex <0){
   copy.pageIndex = 0;
  }
  copy.offset = copy.pageIndex * copy.pageSize;
  copy.isLast = false;
  return copy;
 }

 public PagerDto<T> nextPage(){
  PagerDto<T> copy = copy();
  copy.pageIndex ++;
  copy.offset = copy.pageIndex * copy.pageSize;
  copy.isLast = false;
  return copy;
 }
}
