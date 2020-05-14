package com.yanlong.im.utils.edit;

import android.text.Editable;
import android.text.NoCopySpan;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/14 0014
 * @description
 */
public class NoCopySpanEditableFactory extends Editable.Factory {
    private NoCopySpan[] spnas;

    public NoCopySpanEditableFactory(NoCopySpan... spans) {
        this.spnas = spnas;
    }

    @Override
    public Editable newEditable(CharSequence source) {
        SpannableStringBuilder sb = SpannableStringBuilder.valueOf(source);
        if(spnas!=null){
            for (NoCopySpan span : spnas) {
                sb.setSpan(span, 0, source.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        return sb;
    }
}
