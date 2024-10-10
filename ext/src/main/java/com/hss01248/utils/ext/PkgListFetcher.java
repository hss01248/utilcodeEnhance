package com.hss01248.utils.ext;

import android.app.Activity;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.UtilsTransActivity;
import com.google.gson.reflect.TypeToken;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.activityresult.GoOutOfAppForResultFragment;
import com.hss01248.activityresult.InAppResultFragment;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Despciption todo
 * @Author hss
 * @Date 6/11/24 11:28 AM
 * @Version 1.0
 */
public class PkgListFetcher {

    public static void fetch(String spKey,String url, MyCommonCallback<Boolean> pkgHit){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(5,TimeUnit.SECONDS)
                .build();
        client.newCall(new Request.Builder()
                .url(url)
                .get().build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.w(e);
                readCache(spKey, pkgHit);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    readCache(spKey, pkgHit);
                    return;
                }
                String string = response.body().string();
                parseStr(string,true,spKey,pkgHit);
            }
        });
    }


    public static  boolean isForResult(Activity activity) {
        if (activity instanceof UtilsTransActivity) {
            return true;
        }
        if(activity instanceof FragmentActivity){
            FragmentActivity activity1 = (FragmentActivity) activity;
            List<Fragment> fragments = activity1.getSupportFragmentManager().getFragments();
            //LogUtils.i(fragments);
            for (Fragment fragment : fragments) {
                if(fragment.isAdded()){
                    if(fragment instanceof InAppResultFragment){
                        LogUtils.i("have a InAppResultFragment",fragment);
                        return true;
                    }else if(fragment instanceof GoOutOfAppForResultFragment){
                        LogUtils.i("have a GoOutOfAppForResultFragment",fragment);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void readCache(String spKey, MyCommonCallback<Boolean> pkgHit) {
        String str =  SPStaticUtils.getString(spKey);
        if(TextUtils.isEmpty(str) || !str.startsWith("[")){
            pkgHit.onSuccess(false);
            return;
        }
        try {
            parseStr(str,false, spKey, pkgHit);
        } catch (IOException ex) {
            LogUtils.w(ex);
        }
    }


    private static void parseStr(String string, boolean fromNet,String spKey,
                          MyCommonCallback<Boolean> pkgHit) throws IOException{
        boolean should = false;
        try {
            List<String> list = GsonUtils.fromJson(string, new TypeToken<List<String>>() {
            }.getType());
            if(fromNet){
                SPStaticUtils.put(spKey,string);
            }
            if(list ==null || list.isEmpty()){

            }else{
                for (String s : list) {
                    if (s.equals(AppUtils.getAppPackageName())) {
                        should = true;
                        break;
                    }
                }
            }
            pkgHit.onSuccess(should);
        } catch (Throwable throwable) {
            if(fromNet){
                throw new IOException(throwable);
            }
        }
    }
}
