package com.yanlong.im.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;

import com.example.nim_lib.controll.PlayerManager;
import com.yanlong.im.chat.bean.UserSeting;
import com.yanlong.im.chat.dao.MsgDao;

/***
 * 媒体反馈
 */
public class MediaBackUtil {

    public static Vibrator playVibration(Context context, long time) {
        UserSeting setting = new MsgDao().userSetingGet();

        if (!setting.isShake()) {//读配置,来控制是否振动
            return null;
        }

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(time);
        }
        return vibrator;
    }


    public static void playDingDong(Context context) {
        UserSeting setting = new MsgDao().userSetingGet();
        if (!setting.isVoice()) {//读配置,来控制是否声音
            return;
        }
        try {
            // 播放自定义铃声
            PlayerManager.getManager().init(context, PlayerManager.MESSAGE_TYPE);
            // 铃声为静音或振動时不播放
            if (PlayerManager.getManager().getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                PlayerManager.getManager().play(PlayerManager.MODE_SPEAKER);
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(200);
                }
            }else if (PlayerManager.getManager().getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(200);
                }
            }
        }catch (RuntimeException e){

        }
    }
}
