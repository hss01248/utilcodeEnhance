package com.hss01248.bitmap_saver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hss01248.bitmap_saver.databinding.ActivityDirConfigBinding;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/11/24 3:18 PM
 * @Version 1.0
 */
public class DirConfigActivity extends AppCompatActivity {


    public  static void start(){
        Intent intent = new Intent(ActivityUtils.getTopActivity(), DirConfigActivity.class);
        ActivityUtils.getTopActivity().startActivity(intent);
    }
    ActivityDirConfigBinding binding;
    DirConfigInfo info;
    static File parentDir;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDirConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        info = DirConfigInfo.loadConfigInfo();
        initParentDir(info.hiddenType);
        initData();
        initEvent();

    }

    private File  initParentDir(int hiddenType) {
        if(hiddenType ==0){
            parentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"quick_screen_shot");
        }else{
            parentDir = new File(Environment.getExternalStorageDirectory(),".tyuio");
        }
        return parentDir;
    }

    private void initData() {
        binding.radioGroup.check(checkId());
        if(!TextUtils.isEmpty(info.subDir)){
            binding.btnDir.setText(info.subDir);
        }
        binding.sAsPrefix.setChecked(info.subDirAsPrefix);
        binding.sAsSubDir.setChecked(info.prefixAsSubDir);
        if(!TextUtils.isEmpty(info.prefix)){
            binding.etPrefix.setText(info.prefix);
        }
        updateFileName();


    }

    private void updateFileName() {
        binding.tvNameSample.setText(DirConfigInfo.filePath());
    }

    private int checkId() {
        if(info.hiddenType ==0){
            return  R.id.option1;
        }if(info.hiddenType ==1){
            return  R.id.option2;
        }if(info.hiddenType ==2){
            return  R.id.option3;
        }
        return R.id.option1;
    }

    private void initEvent() {

        binding.sAsPrefix.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                info.subDirAsPrefix = b;
                updateFileName();
            }
        });
        binding.sAsSubDir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                info.prefixAsSubDir = b;
                updateFileName();
            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DirConfigInfo.save(info);
                if(info.hiddenType !=0){
                    File file = new File(DirConfigInfo.filePath()).getParentFile();
                    if(!file.exists()){
                        file.mkdirs();
                    }
                }
                ToastUtils.showShort("保存成功");
                finish();
            }
        });
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(i == R.id.option1){
                    info.hiddenType = 0;
                    //不需要权限
                } else if (i == R.id.option2) {
                    //需要管理存储的超级权限
                    info.hiddenType = 1;
                    requestStoragePermission();
                } else if (i == R.id.option3) {
                    info.hiddenType = 2;
                    requestStoragePermission();
                }
                updateFileName();
            }
        });
        binding.etPrefix.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                info.prefix = charSequence.toString();
                updateFileName();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.btnDir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                info.subDir = charSequence.toString();
                updateFileName();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void requestStoragePermission() {

        BitmapSaveUtil.askWritePermission(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if(!aBoolean ){
                    ToastUtils.showShort("需要管理存储的权限才能隐藏文件");
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }
}
