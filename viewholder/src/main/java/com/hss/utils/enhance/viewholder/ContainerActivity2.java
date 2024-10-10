package com.hss.utils.enhance.viewholder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.enhance.viewholder.mvvm.ContainerViewHolderWithTitleBar;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.functions.Consumer;

public class ContainerActivity2 extends AppCompatActivity {

    Callable<Boolean> onBackPressed;
    static volatile Map<String,Consumer<Pair<ContainerActivity2,
            ContainerViewHolderWithTitleBar>>> onActivityCreateMap= new HashMap<>();

    public void setOnBackPressed(Callable<Boolean> onBackPressed) {
        this.onBackPressed = onBackPressed;
    }


    public static void start(Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>> onActivityCreate){
        Intent intent = new Intent(ActivityUtils.getTopActivity(),ContainerActivity2.class);
        String hashCode = onActivityCreate.toString();
        LogUtils.i("onActivityCreate.toString(): "+hashCode);
        intent.putExtra("onActivityCreateHashCode",hashCode);
        ContainerActivity2.onActivityCreateMap.put(hashCode,onActivityCreate);
        ActivityUtils.getTopActivity().startActivity(intent);
    }

    ContainerViewHolderWithTitleBar viewHolderWithTitleBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UltimateBarX.statusBarOnly(this)
                .fitWindow(false)
                .color(Color.TRANSPARENT)
                .light(true)//状态栏字体为白色
                .apply();

        //标题栏这一个层级的view
        viewHolderWithTitleBar = new ContainerViewHolderWithTitleBar(this);
        setContentView(viewHolderWithTitleBar.getRootView());


        viewHolderWithTitleBar.getBinding().realTitleBar.getLeftView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        try {
            onActivityCreateMap.remove(getIntent().getStringExtra("onActivityCreateHashCode"))
                    .accept(new Pair<>(this,viewHolderWithTitleBar));

            //透明状态栏
            /*ImmersionBar.with(this)
                    .transparentStatusBar()
                    .fitsSystemWindows(false)
                    .statusBarColorInt(Color.TRANSPARENT)
                    .init();*/
        } catch (Exception e) {
            LogUtils.w(e);
        }


    }
    @Override
    public void onBackPressed() {
        if(onBackPressed !=null){
            try {
                if(onBackPressed.call()){

                }else{
                    super.onBackPressed();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            super.onBackPressed();
        }
    }
}
