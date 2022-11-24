package com.hss01248.media.uri;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Despciption todo
 * @Author hss
 * @Date 24/11/2022 15:42
 * @Version 1.0
 */
public class ContentUriUtil {

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

    public static String  getRealPath(Uri uri){

        Map<String, Object> infos = getInfos(uri);
        if(infos ==null){
            return null;
        }
        Object data1 = infos.get("_data");
        if(data1 instanceof String){
            return (String) data1;
        }
        return null;
    }

    public static Map<String,Object>  getInfos(Uri uri){
        ContentResolver cr = Utils.getApp().getContentResolver();
        /** 数据库查询操作。
         * 第一个参数 uri：为要查询的数据库+表的名称。
         * 第二个参数 projection ： 要查询的列。
         * 第三个参数 selection ： 查询的条件，相当于SQL where。
         * 第三个参数 selectionArgs ： 查询条件的参数，相当于 ？。
         * 第四个参数 sortOrder ： 结果排序。
         */
        Cursor cursor = cr.query(uri, null, null, null, null);
        List<Map<String, Object>> maps = ContentUriUtil.doQuery(cursor);
        if (maps.isEmpty()) {
            return null;
        }
        Map<String, Object> map1 = maps.get(0);
        return map1;

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
