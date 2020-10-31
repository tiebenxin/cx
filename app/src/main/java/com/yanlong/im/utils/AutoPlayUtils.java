package com.yanlong.im.utils;

import android.graphics.Rect;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import cn.jzvd.JZUtils;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-10-30
 * @updateAuthor
 * @updateDate
 * @description 列表自动播放工具类
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class AutoPlayUtils {
    public static int positionInList = -1;//记录当前播放列表位置

    private AutoPlayUtils() {
    }

    /**
     * @param firstVisiblePosition 首个可见item位置
     * @param lastVisiblePosition  最后一个可见item位置
     */
    public static void onScrollPlayVideo(RecyclerView recyclerView, int jzvdId, int firstVisiblePosition, int lastVisiblePosition) {
        if (JZUtils.isWifiConnected(recyclerView.getContext())) {
            for (int i = 0; i <= lastVisiblePosition - firstVisiblePosition; i++) {
                View child = recyclerView.getChildAt(i);
                View view = child.findViewById(jzvdId);
                if (view != null && view instanceof Jzvd) {
                    JzvdStd player = (JzvdStd) view;
                    if (getViewVisiblePercent(player) == 1f) {
                        if (positionInList != i + firstVisiblePosition) {
                            if (player != null) {
                                try {
                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
                                    player.bottomProgressBar.setLayoutParams(layoutParams);
                                    // 播放地址不正确会报空指针异常
                                    if (player.mediaInterfaceClass != null) {
                                        player.startVideo();
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * @param firstVisiblePosition 首个可见item位置
     * @param lastVisiblePosition  最后一个可见item位置
     * @param percent              当item被遮挡percent/1时释放,percent取值0-1
     */
    public static void onScrollReleaseAllVideos(int firstVisiblePosition, int lastVisiblePosition, float percent) {
        if (Jzvd.CURRENT_JZVD == null) return;
        if (positionInList >= 0) {
            if ((positionInList <= firstVisiblePosition || positionInList >= lastVisiblePosition - 1)) {
                if (getViewVisiblePercent(Jzvd.CURRENT_JZVD) < percent) {
                    Jzvd.releaseAllVideos();
                }
            }
        }
    }

    /**
     * @param view
     * @return 当前视图可见比列
     */
    public static float getViewVisiblePercent(View view) {
        if (view == null) {
            return 0f;
        }
        float height = view.getHeight();
        Rect rect = new Rect();
        if (!view.getLocalVisibleRect(rect)) {
            return 0f;
        }
        float visibleHeight = rect.bottom - rect.top;
        return visibleHeight / height;
    }
}
