package com.yanlong.im.repository;

import com.yanlong.im.data.local.MsgSearchLocalDataSource;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/22 0022
 * @description
 */
public class MsgSearchRepository {
    private MsgSearchLocalDataSource localDataSource;
    public MsgSearchRepository(){
        localDataSource = new MsgSearchLocalDataSource();
    }
    public void onDestory() {
        localDataSource.onDestory();
    }
}
