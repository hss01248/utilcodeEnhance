package com.hss01248.media.contact;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;


import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.permission.MyPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 14/12/2021 14:23
 * @Version 1.0
 */
public class ContactListUtil {



    public static void getAllContacts(boolean silent, MyCommonCallback<List<ContactInfo>> callback){
        if(silent){
            getAsync(callback);
        }else {
            MyPermissions.requestByMostEffort(false,true,new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(@NonNull List<String> granted) {
                    getAsync(callback);
                }

                @Override
                public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                    callback.onError("no permission","need read_contacts permission to get contacts",null);
                }
            },Manifest.permission.READ_CONTACTS);
        }
    }

    private static void getAsync(MyCommonCallback<List<ContactInfo>> callback) {
        ThreadUtils.executeBySingle(new ThreadUtils.Task<List<ContactInfo>>() {
            @Override
            public List<ContactInfo> doInBackground() throws Throwable {
                return getAllContacts();
            }

            @Override
            public void onSuccess(List<ContactInfo> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {
                callback.onError("exception",t.getMessage(),t);
            }
        });
    }


    public static List<ContactInfo> getAllContacts() {
        try {
            if(PermissionUtils.isGranted(Manifest.permission.READ_CONTACTS)){
                return getPhoneNumberSDK11();
            }else {
                LogUtils.w("no read contacts permission");
                return new ArrayList<>();
            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static ArrayList<ContactInfo> getPhoneNumberSDK11( ) {
        final String SORT_ORDER_SDK_11 =
                ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY
                        + " COLLATE LOCALIZED asc";

        ArrayList<ContactInfo> contacts = new ArrayList<>();
        ContentResolver resolver = Utils.getApp().getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, SORT_ORDER_SDK_11);

        if (cursor == null) {
            LogUtils.w("have permission but no contact data 1");
            return contacts;
        }
        int  count = cursor.getCount();
        if(count > 16){
            contacts = new ArrayList<>(count);
        }

        /*if(onlyForCount){
           int  count = cursor.getCount();
            if(count ==0){
                LogUtils.w("have permission but no contact data 2");
                return contacts;
            }
            return new ArrayList<>(count);
        }*/


        while (cursor.moveToNext()) {
            ContactInfo contact = new ContactInfo();
            contact.name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DATA1));
            if (phoneNumber == null) {
                continue;
            }
            contact.phoneNumber = phoneNumber;
//            contact.sortKey = cursor.getString(cursor.getColumnIndex(
//                    ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY));
            contact.sysId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID));

//            contact.face = cursor.getString(cursor.getColumnIndex(
//                    ContactsContract.CommonDataKinds.Phone.PHOTO_ID));

            contacts.add(contact);
        }
        cursor.close();
        if(contacts.isEmpty()){
            LogUtils.w("have permission but no contact data 2");
        }
        count = contacts.size();
        return contacts;
    }


}
