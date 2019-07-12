package com.yanlong.im.utils;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import com.yanlong.im.chat.bean.UserSeting;
import com.yanlong.im.chat.dao.MsgDao;

/***
 * 媒体反馈
 */
public class MediaBackUtil {

    public static Vibrator playVibration(Context context,long time) {
   UserSeting seting=   new MsgDao().userSetingGet();

        if(!seting.isShake()){//读配置,来控制是否振动
            return null;
        }

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(time);
        }
        return vibrator;
    }


    public static void palydingdong(Context context) {
        UserSeting seting=   new MsgDao().userSetingGet();
        if(!seting.isVoice()){//读配置,来控制是否声音
            return;
        }

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

    }


}
