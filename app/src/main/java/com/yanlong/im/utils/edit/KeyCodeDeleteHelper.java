package com.yanlong.im.utils.edit;

import android.text.Selection;
import android.text.Spannable;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/14 0014
 * @description
 */
public  class KeyCodeDeleteHelper {
    public static boolean onDelDown(Spannable text){
        int selectionStart = Selection.getSelectionStart(text);
        int selectionEnd = Selection.getSelectionEnd(text);
        DataBindingSpan[] dataBindingSpans=text.getSpans(selectionStart, selectionEnd, DataBindingSpan.class);
        if(dataBindingSpans.length>0){
            DataBindingSpan firstSpan=dataBindingSpans[0];
            if(text.getSpanEnd(firstSpan) == selectionStart){
                if(selectionStart == selectionEnd){
                    int spanStart = text.getSpanStart(firstSpan);
                    int spanEnd = text.getSpanEnd(firstSpan);
                    Selection.setSelection(text, spanStart, spanEnd);
                    return true;
                }
            }

        }
        return false;
    }
}
