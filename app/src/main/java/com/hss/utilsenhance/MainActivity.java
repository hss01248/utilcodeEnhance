package com.hss.utilsenhance;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hss.utils.enhance.UrlEncodeUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

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
}