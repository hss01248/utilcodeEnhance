package com.hss01248.media.contact;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.activityresult.ActivityResultListener;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.permission.MyPermissions;

import java.util.List;

public class ContactPickUtil {

    public static void pickOneContact(MyCommonCallback<ContactInfo> callback){
        Intent intent = new Intent(Intent.ACTION_PICK);
        //new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        StartActivityUtil.goOutAppForResult(ActivityUtils.getTopActivity(), intent, new ActivityResultListener() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                if(resultCode == Activity.RESULT_CANCELED){
                    callback.onError("canceled");
                    return;
                }
                if(resultCode != Activity.RESULT_OK){
                    callback.onError("resultCode: "+ resultCode);
                    return;
                }
                requestPermission2(data,callback);
               /* ContactInfo info = parseContacts(data);
                if(info )*/

            }

            @Override
            public void onActivityNotFound(Throwable e) {

            }
        });
    }

    private static void requestPermission2(Intent data, MyCommonCallback<ContactInfo> callback) {
        MyPermissions.requestByMostEffort(false, true,
                new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        ContactInfo info = parseContacts(data);
                        if(info == null){
                            callback.onError("data is null");
                            return;
                        }
                        callback.onSuccess(info);
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        callback.onError("not read contact permission");
                    }
                }, Manifest.permission.READ_CONTACTS);
    }

    private static ContactInfo parseContacts(Intent data) {
        if (data == null) {
            return null;
        }

        Uri contactData = data.getData();
        if (contactData == null) {
            return null;
        }
        String name = "";
        String phoneNumber = "";
        ContactInfo info = new ContactInfo();

        Uri contactUri = data.getData();
        Cursor cursor = Utils.getApp().getContentResolver().query(contactUri, null, null, null, null);
        if (cursor.moveToFirst()) {
            name = cursor
                    .getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            info.name = name;
            String hasPhone = cursor
                    .getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            info.sysId = id;
            if (hasPhone.equalsIgnoreCase("1")) {
                hasPhone = "true";
            } else {
                hasPhone = "false";
            }
            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = Utils.getApp().getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = " + id, null, null);
                while (phones.moveToNext()) {
                    phoneNumber = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                if (phoneNumber != null) {
                    phoneNumber = phoneNumber.replaceAll(" ", "");
                }
                info.phoneNumber = phoneNumber;
                phones.close();
            }
            cursor.close();
        }
        return info;
    }

}
