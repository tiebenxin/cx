package com.yanlong.im.utils.edit;

import android.text.Spannable;
import android.text.SpannableString;


/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/14 0014
 * @description
 */
public class SpanFactory {
    public static Spannable newSpannable(CharSequence source, Object...spans){

        SpannableString sb =  SpannableString.valueOf(source);
        for(Object span: spans){
            sb.setSpan(span, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sb;
    }
}
