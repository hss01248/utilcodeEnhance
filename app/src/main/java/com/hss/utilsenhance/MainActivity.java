package com.hss.utilsenhance;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.GsonBuilder;
import com.hss.utils.enhance.HomeMaintaner;
import com.hss.utils.enhance.intent.ShareUtils;
import com.hss.utils.enhance.UrlEncodeUtil;
import com.hss.utils.enhance.intent.SysIntentUtil;
import com.hss01248.media.pick.MediaPickOrCaptureUtil;
import com.hss01248.media.pick.MediaPickUtil;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.media.pick.SafUtil;
import com.hss01248.media.pick.TakePictureUtil;
import com.hss01248.media.pick.VideoCaptureUtil;
import com.hss01248.media.metadata.MetaDataUtil;
import com.hss01248.media.uri.ContentUriUtil;
import com.hss01248.openuri.OpenUri;
import com.hss01248.permission.MyPermissions;
import com.hss01248.toast.MyToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;

import org.devio.takephoto.wrap.TakeOnePhotoListener;
import org.devio.takephoto.wrap.TakePhotoUtil;

import java.io.File;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        MediaPickUtil.pickVideo(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri s) {
                showMata(s);
            }

            @Override
            public void onError(String code, String msg, Throwable throwable) {

            }
        });
    }

    public void pickImage(View view) {
        MediaPickUtil.pickImage(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri s) {
                showMata(s);
            }

            @Override
            public void onError(String code, String msg, Throwable throwable) {

            }
        });
    }

    public void pickVideo(View view) {
        MediaPickUtil.pickVideo(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri s) {
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
                showMata(OpenUri.fromFile(Utils.getApp(),new File(path)));
                LogUtils.d(path);
            }

            @Override
            public void onError(String code, String msg,Throwable e) {
                LogUtils.d(msg);
            }
        });
    }

    private void showMata(Uri path0) {
        String path = path0.toString();
        LogUtils.d(path);
        String desc = path+"\n";

        Map map0 = new LinkedHashMap();
        Map map = ContentUriUtil.getInfos(path0);
        map0.put("uriInfo",map);

        if(!path.startsWith("/")){
            desc = URLDecoder.decode(path);
        }else {
            try {
                //todo MetaDataUtil兼容fileprovider的uri,此时至少有读的权限
                Map map1 = MetaDataUtil.getMetaData(path);
                map0.put("meta",map1);
            }catch (Throwable throwable){
                throwable.printStackTrace();
                map0.put("meta",throwable.getMessage());
                MyPermissions.request(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {

                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {

                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        desc  = path0.toString()+"\n"+  new GsonBuilder().setPrettyPrinting().create().toJson(map);
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
                showMata(OpenUri.fromFile(Utils.getApp(),new File(s)));
                LogUtils.d(s);
            }

            @Override
            public void onError(String code, String msg, @Nullable Throwable throwable) {

            }
        });
    }

    public void pickAudio(View view) {
        MediaPickUtil.pickAudio(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri s) {
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

    public void pickPdf(View view) {

       /* FilePickUtil.pickPdf(this, new MyCommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                showMata(OpenUri.fromFile(getApplicationContext(),new File(s)));
            }
        });*/
        MediaPickUtil.pickPdf(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                showMata(uri);
            }
        });
    }

    public void externalPrint(View view) {
        Uri uriQuery =  MediaStore.Files.getContentUri("external");
        print(uriQuery);
        //除了图片,视频,音频,还是100个其他类型的文件
        //group by : https://blog.csdn.net/weixin_30755709/article/details/94945476
        //"0=0) group by (mime_type"  已经不行了  Invalid token group
    }

    public void externalImage(View view) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }

            //打开文件:
            try (ParcelFileDescriptor pfd =
                    resolver.openFileDescriptor(content-uri, readOnlyMode)) {
                // Perform operations on "pfd".
            } catch (IOException e) {
                e.printStackTrace();
            }
            //其他存储卷:
            Set<String> volumeNames = MediaStore.getExternalVolumeNames(context);
            String firstVolumeName = volumeNames.iterator().next();
*/
        Uri uriQuery =  MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        print(uriQuery);

    }
    void print(Uri uriQuery){
        ContentResolver cr = Utils.getApp().getContentResolver();
        Cursor query = cr.query(uriQuery, null, null, null, MediaStore.Files.FileColumns.DISPLAY_NAME + " DESC");
        ContentUriUtil.doQuery(query,10);
    }

    public void externalAudio(View view) {
        print(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
    }

    public void externalVideo(View view) {
        print(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
    }

    public void groupby(View view) {
        Uri uriQuery =  MediaStore.Files.getContentUri("external");
        Cursor query = Utils.getApp().getContentResolver().query(uriQuery, null,
                null, null, MediaStore.Files.FileColumns.DISPLAY_NAME + " DESC");
        ContentUriUtil.groupBy(query,"mime_type");
    }

    public void groupbyNone(View view) {
       /* String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;

        Uri uriQuery =  MediaStore.Files.getContentUri("external");
        Cursor query = Utils.getApp().getContentResolver().query(uriQuery, null,
                selection, null, MediaStore.Files.FileColumns.DISPLAY_NAME + " DESC");
        MediaPickUtil.groupBy(query,"mime_type");*/

        String[] columns = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.DATA};
        String select = "(_data LIKE '%.pdf')";

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), columns, select, null, null);

        LogUtils.d( " pdf count " + cursor.getCount());
        int columnIndexOrThrow_DATA = 0;
        if (cursor != null) {
            columnIndexOrThrow_DATA = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(columnIndexOrThrow_DATA);

                //PDFFileInfo document = PDFUtil.getFileInfoFromFile(new File(path));

               // pdfData.add(document);
                LogUtils.d( " pdf " + path);
            }
        }
        cursor.close();


        //原文链接：https://blog.csdn.net/u012556114/article/details/101217053
    }

    public void pickPdf2(View view) {

    }

    public void saf(View view) {
        SafUtil.askAndroidDataDir();
    }

    public void pageStateXml(View view) {
        startActivity(new Intent(this,StateActivity1.class));
    }

    public void pageStateCode(View view) {
        startActivity(new Intent(this,StateActivityHasParent.class));
    }

    public void smallView(View view) {
        startActivity(new Intent(this,StateActivitySmallView.class));
    }

    public void takeOrSelect(View view) {
        MediaPickOrCaptureUtil.pickImageOrTakePhoto(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                showMata(uri);
            }
        });
    }
}