package com.hss.utils.enhance.media;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContentResolverCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.activityresult.ActivityResultListener;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.permission.MyPermissions;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author: Administrator
 * @date: 2022/11/4
 * @desc: //todo
 */
public class MediaPickUtil {

    public static void pickImage(MyCommonCallback<String> callback) {
        pickOne("image/*", callback);
    }

    public static void pickVideo(MyCommonCallback<String> callback) {
        pickOne("video/*", callback);
    }

    public static void pickAudio(MyCommonCallback<String> callback) {
        pickOne("audio/*", callback);
    }

    public static void pickOne(String mimeType, MyCommonCallback<String> callback) {
        MyPermissions.requestByMostEffort(false, true,
                new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        startIntent(mimeType, callback);
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        callback.onError("permission", "[read external storage] permission denied", null);
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    /**
     * @param mimeType
     * @param callback string可能为文件路径,可能为fileprovider形式的uri,
     *                 如果是content://协议的uri,那么会去查询真正的path
     */
    private static void startIntent(String mimeType, MyCommonCallback<String> callback) {
        //https://www.cnblogs.com/widgetbox/p/7503894.html
        Intent intent = new Intent();
        //intent.setType("video/*;image/*");//同时选择视频和图片
        intent.setType(mimeType);//
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //打开方式有两种action，1.ACTION_PICK；2.ACTION_GET_CONTENT 区分大意为：
        // ACTION_PICK 为打开特定数据一个列表来供用户挑选，其中数据为现有的数据。而 ACTION_GET_CONTENT 区别在于它允许用户创建一个之前并不存在的数据。
        intent.setAction(Intent.ACTION_PICK);
        //startActivityForResult(Intent.createChooser(intent,"选择图像..."), PICK_IMAGE_REQUEST);
        //FragmentManager: Activity result delivered for unknown Fragment
        StartActivityUtil.goOutAppForResult(ActivityUtils.getTopActivity(), intent, new ActivityResultListener() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                if (resultCode != RESULT_OK) {
                    callback.onError("-1", "cancel", null);
                    return;
                }
                if (data == null || data.getData() == null) {
                    callback.onError("-2", "data is null", null);
                    return;
                }


                Uri uri = data.getData();
                //content://com.android.providers.media.documents/document/video%3A114026
                LogUtils.i(uri);
                if (!uri.toString().startsWith("content://")) {
                    callback.onSuccess(uri.toString());
                    return;
                }
                //content://media/external/video/media/103988
                // content://com.android.fileexplorer.myprovider/external_files/qqmusic/song/萧敬腾&张杰&袁娅维
                ContentResolver cr = Utils.getApp().getContentResolver();
                /** 数据库查询操作。
                 * 第一个参数 uri：为要查询的数据库+表的名称。
                 * 第二个参数 projection ： 要查询的列。
                 * 第三个参数 selection ： 查询的条件，相当于SQL where。
                 * 第三个参数 selectionArgs ： 查询条件的参数，相当于 ？。
                 * 第四个参数 sortOrder ： 结果排序。
                 */
                Cursor cursor = cr.query(uri, null, null, null, null);

                List<Map<String, Object>> maps = doQuery(cursor);
                if (maps.isEmpty()) {
                    callback.onError("", "not found", new Throwable("file not found"));
                    return;
                }
                Map<String, Object> map1 = maps.get(0);
                Object data1 = map1.get("_data");
                if (data1 != null && data1 instanceof String) {
                    callback.onSuccess((String) data1);
                    return;
                }
                callback.onError("-3","data query failed",null);
            }

            @Override
            public void onActivityNotFound(Throwable e) {
                callback.onError("", "ActivityNotFound", e);
                //todo 用另一个action
            }
        });
    }

    public static Map<String, Object> queryMediaStore(Uri uri) {
        if (uri == null) {
            LogUtils.d("uri == null");
            return new HashMap<>();
        }
        if (!uri.toString().startsWith("content://")) {
            //todo 根据文件路径反查
            return new HashMap<>();
        }
        ContentResolver cr = Utils.getApp().getContentResolver();
        /** 数据库查询操作。
         * 第一个参数 uri：为要查询的数据库+表的名称。
         * 第二个参数 projection ： 要查询的列。
         * 第三个参数 selection ： 查询的条件，相当于SQL where。
         * 第三个参数 selectionArgs ： 查询条件的参数，相当于 ？。
         * 第四个参数 sortOrder ： 结果排序。
         *
         * _display_name : 第三方uri exposed只能查到这个和_size. 可以再根据这个去查_data
         * _data
         */
        Cursor cursor = cr.query(uri, null, null, null, null);

        List<Map<String, Object>> maps = doQuery(cursor);
        if(maps.isEmpty()){
            LogUtils.w("maps.isEmpty()");
            return  new HashMap<>();
        }
        if(maps.get(0).containsKey("_data")){
            return maps.get(0);
        }

        /**
         * 第一个参数 uri：为要查询的数据库+表的名称。
         * 第二个参数 projection ： 要查询的列。
         * 第三个参数 selection ： 查询的条件，相当于SQL where。
         * 第三个参数 selectionArgs ： 查询条件的参数，相当于 ？。
         * 第四个参数 sortOrder ： 结果排序。
         * */
        LogUtils.w("没有查到真实的文件路径,根据已有信息继续查询: ");
        Uri uriQuery =  MediaStore.Files.getContentUri("external");
                //MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                //

        String selectionClause = MediaStore.Files.FileColumns.DISPLAY_NAME + " = ?";
        //"_display_name" + " = ?";
        //document_id=audio:33614

        // Moves the user's input string to the selection arguments.
        String[] selectionArgs = {""};
        selectionArgs[0] = maps.get(0).get("_display_name") + "";
        if(maps.get(0).containsKey("document_id")){
            String document_id = (String) maps.get(0).get("document_id");
            String[] split = document_id.split(":");
            String dbType = split[0];
            String id = split[1];
            String idColumnName = BaseColumns._ID;
           /* if("document".equals(dbType)){
                idColumnName = "document_id";
            }else if("audio".equals(dbType)){
                idColumnName = MediaStore.Audio.AudioColumns.ORIGINAL_DOCUMENT_ID;
            }*/
            selectionClause = idColumnName+" = ?";
            selectionArgs[0] = id;

        }


        LogUtils.i(uriQuery, selectionClause, selectionArgs[0]);
        //MediaStore.Files.getContentUri(“external”)|全部内容
        //|MediaStore.Video.Media.EXTERNAL_CONTENT_URI|视频内容
        //|MediaStore.Audio.Media.EXTERNAL_CONTENT_URI|音频内容
        //Uri uri1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI|图片内容
        //————————————————
        //版权声明：本文为CSDN博主「LoneySmoke」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
        //原文链接：https://blog.csdn.net/LoneySmoke/article/details/108944485
        Cursor cursor2 = cr.query(uriQuery, null, selectionClause, selectionArgs, null);
        List<Map<String, Object>> maps2 = doQuery(cursor2);
        if(maps2.isEmpty()){
            return maps.get(0);
        }
        maps.get(0).putAll(maps2.get(0));
        LogUtils.i("最终查询结果:",maps.get(0));
        return maps.get(0);
    }
    public static List<Map<String, Object>> doQuery(Cursor cursor){
        return doQuery(cursor,1);
    }

    public static List<Map<String, Object>> doQuery(Uri uri){
        ContentResolver resolverCompat = Utils.getApp().getContentResolver();
        //UnsupportedOperationException: Unsupported Uri
        try {
            Cursor query = resolverCompat.query(uri, null, null, null, null);
            return doQuery(query);
        }catch (Throwable throwable){
            LogUtils.w(throwable);
            return new ArrayList<>();
        }
    }

    public static Map<Object,Integer> groupBy(Cursor cursor,String groupBy){
        Map<Object,Integer> map0 = new TreeMap<>();
        if (cursor == null) {
            LogUtils.w("cursor == null");
            return map0;
        }
        LogUtils.i("查询到结果个数: " + cursor.getCount());

        while (cursor.moveToNext()) {
            //int columnCount = cursor.getColumnCount();
            int columnIndex = cursor.getColumnIndex(groupBy);
            if(columnIndex == -1){
                LogUtils.w("no such column: "+ groupBy);
                return map0;
            }
            Object val = null;
            int type = cursor.getType(columnIndex);
            if (type == Cursor.FIELD_TYPE_STRING) {
                val = cursor.getString(columnIndex);
            } else if (type == Cursor.FIELD_TYPE_INTEGER) {
                val = cursor.getInt(columnIndex);
            } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                val = cursor.getFloat(columnIndex);
            } else if (type == Cursor.FIELD_TYPE_BLOB) {
                //val = cursor.getBlob(columnIndex);
            } else if (type == Cursor.FIELD_TYPE_NULL) {
                //val = cursor.getLong(columnIndex);
            } else {
                LogUtils.w("unknown column type: " + type + ", name: " +groupBy);
            }
            if(val == null){
                val = "null";
            }
            if(map0.containsKey(val)){
                map0.put(val,map0.get(val)+1) ;
            }else {
                map0.put(val,1) ;
            }
        }
        cursor.close();
        LogUtils.d(map0);
        return map0;
    }

    public static List<Map<String, Object>> doQuery(Cursor cursor, int maxCount) {
        List<Map<String, Object>> list = new ArrayList<>();

        if (cursor == null) {
            LogUtils.w("cursor == null");
            return list;
        }
        LogUtils.i("查询到结果个数: " + cursor.getCount());

        int count = 1;
        while (cursor.moveToNext()) {
            Map<String, Object> map = new LinkedHashMap<>();
            //int columnCount = cursor.getColumnCount();
            String[] columnNames = cursor.getColumnNames();
            for (int i = 0; i < columnNames.length; i++) {
                int columnIndex = cursor.getColumnIndexOrThrow(columnNames[i]);
                Object val = null;
                int type = cursor.getType(columnIndex);
                if (type == Cursor.FIELD_TYPE_STRING) {
                    val = cursor.getString(columnIndex);
                } else if (type == Cursor.FIELD_TYPE_INTEGER) {
                    val = cursor.getInt(columnIndex);
                } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                    val = cursor.getFloat(columnIndex);
                } else if (type == Cursor.FIELD_TYPE_BLOB) {
                    val = cursor.getBlob(columnIndex);
                } else if (type == Cursor.FIELD_TYPE_NULL) {
                    //val = cursor.getLong(columnIndex);
                } else {
                    LogUtils.w("unknown column type: " + type + ", name: " + columnNames[i]);
                }
                if (val != null) {
                    map.put(columnNames[i], val);
                }
            }
            LogUtils.d(map);
            list.add(map);
            count++;
            if(count>= maxCount){
                break;
            }
        }
        cursor.close();
        return list;
    }
}
