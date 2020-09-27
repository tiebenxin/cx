package com.yanlong.im.user.bean;

import net.cb.cb.library.base.BaseBean;

/**
 * @author Liszt
 * @date 2020/9/27
 * Description 活跃日
 */
public class DailyReportBean extends BaseBean {
    int historyClean;//1表示可以开启，其他不可开启

    public int getHistoryClean() {
        return historyClean;
    }

    public void setHistoryClean(int historyClean) {
        this.historyClean = historyClean;
    }
}
