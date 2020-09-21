package net.cb.cb.library.base.bind;

import androidx.annotation.NonNull;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public interface IPresenter<V extends IBaseView> {
    /**
     * 连接view
     *
     * @param view
     */
    void attachView(@NonNull V view);

    /**
     * 分离view
     */
    void detachView();

    /**
     * 解除绑定关系
     */
    void unbind();
}
