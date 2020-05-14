package com.yanlong.im.utils.edit;

import android.text.Spannable;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/14 0014
 * @description 判断Span是否失效。
 */
public interface RemoveOnDirtySpan {
    boolean isDirty(Spannable text);
}
