package com.yanlong.im.utils.edit;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/14 0014
 * @description
 */
public class SpannableData implements DataBindingSpan<String>, RemoveOnDirtySpan  {
    private String spanned = null;

    public SpannableData(String spanned) {
        this.spanned = spanned;
    }

    @Override
    public CharSequence spannedText() {
        SpannableString sb = new SpannableString(spanned);
        sb.setSpan(new ForegroundColorSpan(Color.RED),0,sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Override
    public String bindingData() {
        return spanned;
    }

    @Override
    public boolean isDirty(Spannable text) {
        int spanStart = text.getSpanStart(this);
        int spanEnd = text.getSpanEnd(this);
        return spanStart >= 0 && spanEnd >= 0 && text.subSequence(spanStart, spanEnd) != spanned;
    }
}
