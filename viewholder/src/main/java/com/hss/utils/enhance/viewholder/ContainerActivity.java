package com.hss.utils.enhance.viewholder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hss01248.viewholder.databinding.ActivityCommonContainerBinding;

import java.util.concurrent.Callable;

import io.reactivex.functions.Consumer;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/20/24 2:44 PM
 * @Version 1.0
 */
public class ContainerActivity extends AppCompatActivity {

    ActivityCommonContainerBinding binding;
    static Consumer<Pair<ContainerActivity,ActivityCommonContainerBinding>> onActivityCreate;

    public void setOnBackPressed(Callable<Boolean> onBackPressed) {
        this.onBackPressed = onBackPressed;
    }

    Callable<Boolean> onBackPressed;

    public static void start(String title,
                             Consumer<Pair<ContainerActivity,ActivityCommonContainerBinding>> onActivityCreate){
        Intent intent = new Intent(ActivityUtils.getTopActivity(),ContainerActivity.class);
        intent.putExtra("title",title);
        ContainerActivity.onActivityCreate = onActivityCreate;
        ActivityUtils.getTopActivity().startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommonContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewGroup.LayoutParams layoutParams = binding.getRoot().getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        binding.getRoot().setLayoutParams(layoutParams);
        String title = getIntent().getStringExtra("title");
        if(TextUtils.isEmpty(title)){
            binding.qrTitlebar.setTitle("");
            binding.qrTitlebar.setVisibility(View.GONE);
        }else{
            binding.qrTitlebar.setTitle(title);
        }

        binding.qrTitlebar.getLeftView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        try {
            onActivityCreate.accept(new Pair<>(this,binding));
            onActivityCreate = null;
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
