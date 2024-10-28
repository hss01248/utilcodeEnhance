package com.hss01248.viewholder_media;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.enhance.viewholder.ContainerActivity2;
import com.hss.utils.enhance.viewholder.mvvm.BaseViewHolder;
import com.hss.utils.enhance.viewholder.mvvm.ContainerViewHolderWithTitleBar;
import com.hss01248.iwidget.singlechoose.ISingleChooseItem;
import com.hss01248.viewholder_media.api.AbsFile;
import com.hss01248.viewholder_media.api.AbsFileFilter;
import com.hss01248.viewholder_media.api.JavaFile;
import com.hss01248.viewholder_media.databinding.LayoutFileTreeBinding;
import com.hss01248.viewstate.StatefulLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import io.reactivex.functions.Consumer;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/14/24 9:12 AM
 * @Version 1.0
 */
public class CommonFileTreeViewHolder extends BaseViewHolder<LayoutFileTreeBinding, AbsFile> {
    StatefulLayout stateManager;
    AbsFile currentPath;
    CommonMediaListViewHolder listViewHolder;
    DisplayAndFilterInfo filterInfo = new DisplayAndFilterInfo();
    Map<String,List<AbsFile>> cache = new TreeMap<>();


    public static void viewExternalStorage(){
        viewDirInActivity(new JavaFile(Environment.getExternalStorageDirectory()).setRoot(true));
    }

    public static void viewAppDir(){
        ContainerActivity2.start(new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                CommonMediaListViewHolder listViewHolder1 = new CommonMediaListViewHolder(pair.first);
                List<AbsFile> dirs = new ArrayList<>();
                dirs.add(new JavaFile(Utils.getApp().getFilesDir().getParentFile()).setRoot(true));
               // dirs.add(Utils.getApp().getCacheDir().getAbsolutePath());
                //dirs.add(Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_DCIM).getParentFile().getAbsolutePath());
                dirs.add(new JavaFile(Utils.getApp().getExternalCacheDir().getParentFile()).setRoot(true));
                listViewHolder1.init(dirs);
                pair.second.getBinding().realTitleBar.setTitle("应用私有目录,前1个为内部,后1个为外部");
                pair.second.getBinding().rlContainer.addView(listViewHolder1.getRootView());

                listViewHolder1.setOnItemClicked(new Consumer<AbsFile>() {
                    @Override
                    public void accept(AbsFile s) throws Exception {
                     viewDirInActivity(s);
                    }
                });
            }
        });
    }


    public static void viewDirInActivity(AbsFile dir){
        ContainerActivity2.start( new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                //File externalStorageDirectory = new File(dir);
                CommonFileTreeViewHolder viewHolder = new CommonFileTreeViewHolder(pair.first);
                viewHolder.init(dir);
                pair.second.getBinding().rlContainer.addView(viewHolder.getRootView());
                //pair.second.setTitleBarHidden(true);
                pair.second.setStatusBarFontBlack(true);

                pair.second.showRightMoreIcon(false);
                pair.second.getBinding().realTitleBar.getRightView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.initMenus(v);
                    }
                });
                //处理宽高:

                pair.first.setOnBackPressed(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        //ToastUtils.showShort("拦截后退键");
                        return viewHolder.onBackPressed();
                    }
                });
            }
        });
    }




    public CommonFileTreeViewHolder(Context context) {
        super(context);

       // rootView = stateManager;
        listViewHolder = new CommonMediaListViewHolder(context);
        stateManager = StatefulLayout.wrapWithStateOfPage(listViewHolder.getRootView(), new Runnable() {
            @Override
            public void run() {
                loadDir(currentPath);
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        stateManager.setLayoutParams(params);
        binding.llContainer.addView(stateManager);

        listViewHolder.setOnItemClicked(new Consumer<AbsFile>() {
            @Override
            public void accept(AbsFile s) throws Exception {

                if(s.isDirectory()){
                    loadDir(s);
                }else {

                    List<AbsFile> strings = cache.get(s.getAbsolutePath());
                    ////todo 罚款文件的实现
                   // FileOpenUtil.open(s.getFullPath(),strings);
                }
            }
        });
    }

    private void initMenus(View view) {
        List<ISingleChooseItem<AbsFile>> menus = new ArrayList<>();
        if(filterInfo.displayType == 0){
            //表格
            menus.add(new ISingleChooseItem<AbsFile>() {
                @Override
                public String text() {
                    return "文件名"+(filterInfo.showFileName?"隐藏":"显示");
                }

                @Override
                public void onItemClicked(int position, AbsFile bean) {

                    filterInfo.showFileName = !filterInfo.showFileName;
                    listViewHolder.setFilterInfo(filterInfo);
                }
            });
            menus.add(new ISingleChooseItem<AbsFile>() {
                @Override
                public String text() {
                    return "显示模式-列表";
                }

                @Override
                public void onItemClicked(int position, AbsFile bean) {
                    filterInfo.displayType = 1;
                    listViewHolder.setFilterInfo(filterInfo);
                }
            });
        }else {
            menus.add(new ISingleChooseItem<AbsFile>() {
                @Override
                public String text() {
                    return "显示模式-表格";
                }

                @Override
                public void onItemClicked(int position, AbsFile bean) {

                    filterInfo.displayType = 0;
                    listViewHolder.setFilterInfo(filterInfo);
                }
            });
        }



        menus.add(new ISingleChooseItem<AbsFile>() {
            @Override
            public String text() {
                return "排序-文件名-顺序";
            }

            @Override
            public void onItemClicked(int position, AbsFile bean) {

            }
        });
        menus.add(new ISingleChooseItem<AbsFile>() {
            @Override
            public String text() {
                return "排序-文件名-倒序";
            }

            @Override
            public void onItemClicked(int position, AbsFile bean) {

            }
        });
        menus.add(new ISingleChooseItem<AbsFile>() {
            @Override
            public String text() {
                return "排序-文件大小-大到小";
            }

            @Override
            public void onItemClicked(int position, AbsFile bean) {

            }
        });
        menus.add(new ISingleChooseItem<AbsFile>() {
            @Override
            public String text() {
                return "排序-文件大小-小到大";
            }

            @Override
            public void onItemClicked(int position, AbsFile bean) {

            }
        });
        menus.add(new ISingleChooseItem<AbsFile>() {
            @Override
            public String text() {
                return "排序-时间-旧到新";
            }

            @Override
            public void onItemClicked(int position, AbsFile bean) {

            }
        });
        menus.add(new ISingleChooseItem<AbsFile>() {
            @Override
            public String text() {
                return "排序-时间-新到旧";
            }

            @Override
            public void onItemClicked(int position, AbsFile bean) {

            }
        });

        ISingleChooseItem.showAsMenu2(view,menus,currentPath);
    }


    public  boolean onBackPressed(){
        if(currentPath.isRoot()){
            return false;
        }
        AbsFile dir = currentPath.getParent();
        if(dir !=null && dir.exists()){
            loadDir(dir);
            return true;
        }else {
            return false;
        }
    }


    private void loadDir(AbsFile path) {
        currentPath = path;
        binding.tvPath.setText(path.getAbsolutePath());
        stateManager.showLoading();
        AbsFile file = path;
        if(!file.exists()){
            stateManager.showError("文件路径不存在:\n"+path);
            return;
        }
        if(!file.isDirectory()){
            stateManager.showError("需要文件夹,但传入的是文件:\n"+path);
            return;
        }
        if(cache.containsKey(path.getAbsolutePath())){
            if(cache.get(path.getAbsolutePath()).isEmpty()){
                stateManager.showEmpty();
            }else{
                stateManager.showContent();
            }
            listViewHolder.init(cache.get(path.getAbsolutePath()));
        }

        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Pair<String,List<AbsFile>>>() {
            @Override
            public Pair<String,List<AbsFile>> doInBackground() throws Throwable {
                AbsFile[] dirs = file.listFiles(new AbsFileFilter() {
                    @Override
                    public boolean accept(AbsFile file) {
                        return file.isDirectory();
                    }
                });

                AbsFile[] files2 = file.listFiles(new AbsFileFilter() {
                    @Override
                    public boolean accept(AbsFile file) {
                        return !file.isDirectory();
                    }
                });
                //只列出文件名,不列出上级路径,蛋疼的api
                LogUtils.d(dirs,files2);
                List<AbsFile> list = new ArrayList<>();

                //排序

                if(dirs !=null && dirs.length > 0){
                    List<AbsFile> list1 = new ArrayList<>();
                    list1.addAll(Arrays.asList(dirs));
                    Collections.sort(list1, new Comparator<AbsFile>() {
                        @Override
                        public int compare(AbsFile file, AbsFile t1) {
                            return file.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                        }
                    });

                    list.addAll(list1);
                }
                if(files2 !=null && files2.length > 0){
                    List<AbsFile> list1 = new ArrayList<>();
                    list1.addAll(Arrays.asList(files2));
                    Collections.sort(list1, new Comparator<AbsFile>() {
                        @Override
                        public int compare(AbsFile file, AbsFile t1) {
                            return file.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                        }
                    });

                    list.addAll(list1);
                }
                return new Pair<>(path.getAbsolutePath(),list);
            }

            @Override
            public void onSuccess(Pair<String,List<AbsFile>> result) {
                //更新数据
                cache.put(result.first,result.second);
                if(!currentPath.getAbsolutePath().equals(result.first)){

                    LogUtils.d("路径已经变化(现-原)",currentPath,result.first);
                    return;
                }
                if(result.second.isEmpty()){
                    stateManager.showEmpty();
                }else{
                    stateManager.showContent();
                }
                listViewHolder.init(cache.get(path.getAbsolutePath()));
            }

            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                if(currentPath.equals(path)){
                    stateManager.showError("加载路径失败:\n"+t.getMessage()+"\n"+path);
                }
            }
        });
    }

    @Override
    protected void initDataAndEventInternal(LifecycleOwner lifecycleOwner, AbsFile bean) {
        loadDir(bean);
    }
}
