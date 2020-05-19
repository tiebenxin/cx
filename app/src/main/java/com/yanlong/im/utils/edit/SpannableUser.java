package com.yanlong.im.utils.edit;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.yanlong.im.utils.edit.span.DataBindingSpan;
import com.yanlong.im.utils.edit.span.RemoveOnDirtySpan;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/14 0014
 * @description
 */
public class SpannableUser implements DataBindingSpan<Long>, RemoveOnDirtySpan {
    private String text = null;
    private long userId = -1L;

    public SpannableUser(String spanned, long userId) {
        this.text = spanned;
        this.userId = userId;
    }

    @Override
    public CharSequence getSpannedText() {
        SpannableString sb = new SpannableString("@" + text + " ");
        sb.setSpan(new ForegroundColorSpan(Color.BLACK),0,sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Override
    public Long bindingData() {
        return userId;
    }

    @Override
    public String getText() {
        return text;
    }


    /**
     * 和原文本不一致
     * @param text
     * @return
     */
    @Override
    public boolean isDirty(Spannable text) {
        int spanStart = text.getSpanStart(this);
        int spanEnd = text.getSpanEnd(this);
        return spanStart >= 0 && spanEnd >= 0 && text.subSequence(spanStart, spanEnd) != this.text;
    }
}
