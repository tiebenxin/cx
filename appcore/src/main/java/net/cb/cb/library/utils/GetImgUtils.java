package net.cb.cb.library.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @类名：最新一张截图或拍照相片
 * @Date：2020/6/8
 * @by zjy
 * @备注：
 */

public class GetImgUtils {

    public static class ImgBean{
        public long mTime;
        public String imgUrl;

        public ImgBean(long mTime, String imgUrl) {
            this.mTime = mTime;
            this.imgUrl = imgUrl;
        }
    }


    /**
     * 获取相册中最新一张图片
     * @param context
     * @return TODO #128104
     */
    public static ImgBean getLatestPhoto(Context context) {
        //检查所有文件夹
        List<ImgBean> imgBeans=new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Files.FileColumns.DATE_MODIFIED);
        //循环遍历找出所有图片
        while (cursor.moveToNext()) {
            long mtime = 0;
            String imgUrl = "";
            if(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))!=0L
                && cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))!=null){
                mtime=cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                imgUrl=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            imgBeans.add(new ImgBean(mtime, imgUrl));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        //按时间降序排序
        if(imgBeans.size()>0){
            //如果只有1个，则直接返回，否则超过2个需要排序
            if(imgBeans.size()>=2){
                Collections.sort(imgBeans, new Comparator<ImgBean>() {
                    @Override
                    public int compare(ImgBean imgBean, ImgBean t1) {
                        return (int) (t1.mTime-imgBean.mTime);
                    }
                });
            }
            //拿最近一张图片
            return imgBeans.get(0);
        }else {
            return null;
        }

    }

}
