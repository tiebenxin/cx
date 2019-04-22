package com.yanlong.im.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import net.cb.cb.library.utils.CheckPermissionUtils;
import net.cb.cb.library.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class PhoneListUtil {
    private String[] permission = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};

    private Activity activity;
    private Event event;

    public void getPhones(Activity act, Event event) {
        activity = act;
        this.event = event;

       if( CheckPermissionUtils.requestPermissions(act, permission)){
           getAllContacts(activity.getApplicationContext());
       }


    }

    public void getAllContacts(Context context) {
        ArrayList<PhoneBean> contacts = new ArrayList<PhoneBean>();

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //新建一个联系人实例
            PhoneBean temp = new PhoneBean();
            String contactId = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人姓名
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            temp.name = name;

            //获取联系人电话号码
            Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            while (phoneCursor.moveToNext()) {
                String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone = phone.replace("-", "");
                phone = phone.replace(" ", "");
                temp.phone = phone;
            }

            //获取联系人备注信息
            Cursor noteCursor = context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Nickname.NAME},
                    ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "'",
                    new String[]{contactId}, null);
            if (noteCursor.moveToFirst()) {
                do {
                    String note = noteCursor.getString(noteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                    //   temp.note = note;
                    // Log.i("note:", note);
                } while (noteCursor.moveToNext());
            }
            contacts.add(temp);
            //记得要把cursor给close掉
            phoneCursor.close();
            noteCursor.close();
        }
        cursor.close();
        event.onList(contacts);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CheckPermissionUtils.checkPermissionResult(new CheckPermissionUtils.OnHasGetPermissionListener() {
            @Override
            public void onSuccess() {

                getAllContacts(activity.getApplicationContext());
            }

            @Override
            public void onFail() {
                Log.d("xxx", "onFail: ");

            }
        }, activity.getApplicationContext(), permission, grantResults);
    }

    public class PhoneBean {
        private String name;
        private String phone;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public interface Event {
        void onList(List<PhoneBean> list);
    }

}
