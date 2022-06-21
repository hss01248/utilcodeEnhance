package com.hss.utilsenhance;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hss.utils.enhance.ShareUtils;
import com.hss.utils.enhance.UrlEncodeUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import org.devio.takephoto.wrap.TakeOnePhotoListener;
import org.devio.takephoto.wrap.TakePhotoUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



    public void urlEncode(View view) {
        String str = "1 2  34+7+8+ 9";
        String encode = UrlEncodeUtil.encode(str);
        UrlEncodeUtil.decode(encode);



      /*  String str2 = "12 34+789";
        String encode1 = UrlEncodeUtil.encode(str2);
        UrlEncodeUtil.decode(encode1);*/
    }

    public void getDecodedPath(View view) {
        //秒 +开+45 a p+i
        String url = "https://www.baidu.com/1234%2B789/%E7%A7%92%E5%BC%80%2B45%20api";
        UrlEncodeUtil.decode(url);
        UrlEncodeUtil.getDecodedPath(url);

        String url2 = "https://www.baidu.com/1234%2B789/%E7%A7%92%20%2B%E5%BC%80%2B45%20a%20p%2Bi";
        UrlEncodeUtil.decode(url2);
        UrlEncodeUtil.getDecodedPath(url2);

    }

    public void encodeFilePathToUrlUsage(View view) {
        String path = "F:/img cache/秒  +开+45 a p+i/45.jpg";
        String s = UrlEncodeUtil.encodeFilePathToUrlUsage(path);
        //看能否还原
        String url2 = "https://www.baidu.com"+s;
        UrlEncodeUtil.getDecodedPath(url2);

    }

    public void share(View view) {
        TakePhotoUtil.startPickOneWitchDialog(this, new TakeOnePhotoListener() {
            @Override
            public void onSuccess(String path) {
                ShareUtils.shareFile(new File(path));
            }

            @Override
            public void onFail(String path, String msg) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * 状态栏: 透明+沉浸
     * @param view
     */
    public void statusbarTrans(View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 导航栏和状态栏均透明+沉浸
     * @param view
     */
    public void navibarTrans(View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void hideStatusbar(View view) {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
    }

    public void hideNaviBar(View view) {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
    }

    /**
     * https://blog.csdn.net/guolin_blog/article/details/51763825
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow().setNavigationBarColor(Color.TRANSPARENT);
            }

        }
    }


}