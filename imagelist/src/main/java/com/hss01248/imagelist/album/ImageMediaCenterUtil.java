package com.hss01248.imagelist.album;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.core.util.Pair;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.hss.utils.enhance.viewholder.ContainerActivity2;
import com.hss.utils.enhance.viewholder.mvvm.ContainerViewHolderWithTitleBar;
import com.hss01248.bigimageviewpager.LargeImageViewer;
import com.hss01248.fullscreendialog.FullScreenDialogUtil;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * time:2019/11/30
 * author:hss
 * desription:
 */
public class ImageMediaCenterUtil {


    public static void showAlbums(boolean isVideo){
        ContainerActivity2.start(new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                ImageListView view1 =  new ImageListView(pair.first);
                pair.second.getBinding().rlContainer.addView(view1);
                pair.second.getBinding().realTitleBar.setVisibility(ScreenUtils.isLandscape() ? View.GONE:View.VISIBLE);
                view1.showAllAlbums(isVideo);
            }
        });
    }

    public static void showPureImageOrVideos(boolean isVideo){
        ContainerActivity2.start(new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                ImageListView view1 =  new ImageListView(pair.first);
                pair.second.getBinding().rlContainer.addView(view1);
                pair.second.getBinding().realTitleBar.setVisibility(ScreenUtils.isLandscape() ? View.GONE:View.VISIBLE);
                view1.showImagesInAlbum(null,isVideo);
            }
        });
    }








    public static void showBigImag(Context context, List<String> urlsOrPaths, int position) {
        LargeImageViewer.showInBatch(urlsOrPaths,position);
    }


    public static void showViewAsActivityOrDialog(Context context,boolean asDialog, IViewInit init){
        if(asDialog){
            showViewAsDialog(context, init);
        }else {
            showViewAsActivity( context, init);
        }
    }


    public static void showImagesInDir(Context context,String dir){
        ImageMediaCenterUtil.showViewAsActivity(context, new IViewInit() {
            @Override
            public View init(Activity activity) {
                ImageListView listView = new ImageListView(activity);
                listView.showImagesInDir(dir);
                return listView;
            }
        });
    }

    public static void showAllImages(){

    }



    public static void showViewAsActivity(Context context, IViewInit init){

        ContainerActivity2.start(new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                View view =  init.init(pair.first);
                pair.second.getBinding().rlContainer.addView(view);
                pair.second.getBinding().realTitleBar.setVisibility(View.GONE);

            }
        });
    }



    public static void showViewAsDialog(Context context, IViewInit init) {
        FullScreenDialogUtil.showFullScreen(init.init(ActivityUtils.getTopActivity()));
    }





}
