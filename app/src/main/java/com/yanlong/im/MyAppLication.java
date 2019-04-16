package com.yanlong.im;


import net.cb.cb.library.MainApplication;
import com.yanlong.im.utils.DaoUtil;

public class MyAppLication extends MainApplication {




    @Override
    public void onCreate() {
        super.onCreate();

       /* EmojiCompat.Config config = new BundledEmojiCompatConfig(this)
                .setReplaceAll(true);
        EmojiCompat.init(config);*/
      //初始化
        DaoUtil.get();


    }

}
