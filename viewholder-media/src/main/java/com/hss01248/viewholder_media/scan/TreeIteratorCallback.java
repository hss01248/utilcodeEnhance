package com.hss01248.viewholder_media.scan;


import java.util.List;

public interface TreeIteratorCallback<T> {

   static boolean shouldCancel = false;

    boolean onFile(T node);

   default void onDirStart(T dir){

   }

   default void onDirCanceled(T dir){

   }

   default void onDirFinished(T dir){

   }

   default void onCalTotalCountStart(){

   }

   default boolean onCalTotalCountEnd(long count){
      return true;
   }

   default void onStart(){

   }

   default boolean doCancel(T container){
      return shouldCancel;
   }

   default boolean skipDir(T container){
      return false;
   }

   void onProgress(long total,long current,T node,String desc);

   void onFinished(long total, long timeCostMills, List<T> failedList);

   default void onFail(List<T> DocumentFiles,String desc,Throwable throwable){

   }


}
