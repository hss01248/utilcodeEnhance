package com.hss.utilsenhance;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hss.utils.enhance.HomeMaintaner;
import com.hss.utils.enhance.ShareUtils;
import com.hss.utils.enhance.UrlEncodeUtil;
import com.hss.utils.enhance.intent.SysIntentUtil;
import com.hss.utils.enhance.media.MediaPickUtil;
import com.hss.utils.enhance.media.MyCommonCallback;
import com.hss.utils.enhance.media.TakePictureUtil;
import com.hss.utils.enhance.media.VideoCaptureUtil;
import com.hss01248.media.metadata.MetaDataUtil;
import com.hss01248.permission.MyPermissions;
import com.hss01248.toast.MyToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import org.devio.takephoto.wrap.TakeOnePhotoListener;
import org.devio.takephoto.wrap.TakePhotoUtil;

import java.io.File;
import java.util.List;

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
       /* if (hasFocus && Build.VERSION.SDK_INT >= 19) {
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

        }*/
    }


    public void clickHome(View view) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        //clickHome(null);
        HomeMaintaner.onBackPressed(this,true,null);
    }

    public void local2(View view) {

        MediaPickUtil.pickVideo(new MyCommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                showMata(s);
            }

            @Override
            public void onError(String code, String msg, Throwable throwable) {

            }
        });
    }

    public void pickImage(View view) {
        MediaPickUtil.pickImage(new MyCommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                showMata(s);
            }

            @Override
            public void onError(String code, String msg, Throwable throwable) {

            }
        });
    }

    public void pickVideo(View view) {
        MediaPickUtil.pickVideo(new MyCommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                showMata(s);
            }

            @Override
            public void onError(String code, String msg, Throwable throwable) {

            }
        });
    }

    public void takeVideo(View view) {
        VideoCaptureUtil.startVideoCapture(30, 1024 * 1024 * 1024, new MyCommonCallback<String>() {
            @Override
            public void onSuccess(String path) {
                showMata(path);
                LogUtils.d(path);
            }

            @Override
            public void onError(String code, String msg,Throwable e) {
                LogUtils.d(msg);
            }
        });
    }

    private void showMata(String path) {
        LogUtils.d(path);
        String desc = path+"";
        try {
            desc = MetaDataUtil.getDes(path);
        }catch (Throwable throwable){
            throwable.printStackTrace();
            MyPermissions.request(new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(@NonNull List<String> granted) {

                }

                @Override
                public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {

                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        new AlertDialog.Builder(this)
                .setTitle("mata data")
                .setMessage(desc)
                .setPositiveButton("预览", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SysIntentUtil.openFile(path);
                    }
                }).setNegativeButton("cancel",null)
                .create().show();
    }

    public void takePicture(View view) {
        TakePictureUtil.takePicture(new MyCommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                showMata(s);
                LogUtils.d(s);
            }

            @Override
            public void onError(String code, String msg, @Nullable Throwable throwable) {

            }
        });
    }

    public void pickAudio(View view) {
        MediaPickUtil.pickAudio(new MyCommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                showMata(s);
            }

            @Override
            public void onError(String code, String msg, @Nullable Throwable throwable) {

            }
        });
    }

    public void toastSuccess(View view) {
        MyToast.success("success---->");
    }

    public void toastError(View view) {
        MyToast.error("toastError---->");
    }

    public void toastNormal(View view) {
        MyToast.show("toastNormal---->");
    }

    public void toastDebug(View view) {
        MyToast.debug("toastDebug---->");
    }

    Dialog dialog;
    public void showLoading(View view) {
         dialog = MyToast.showLoadingDialog("");
        //dialog.setCancelable(true);
    }

    public void dismissLoading(View view) {
        MyToast.dismissLoadingDialog(dialog);
    }

    public void showLoadingInBack(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dialog = MyToast.showLoadingDialog("showLoadingInBackshowLoadingInBackshowLoading" +
                        "InBackshowLoadingInBackshowLoadingInBackshowLoadingInBackshowLoadingInBackshowLoadingInBackshowLoadingInBack");
            }
        }).start();
    }
}