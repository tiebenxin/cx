package com.yanlong.im.utils.edit.watcher;

import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;

import static java.lang.Math.abs;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/14 0014
 * @description 光标是一种Span
 * 通过SpanWatcher侦听光标活动，通过Selection实现当光标移动到Span内部时，让它重新移动到Span最近的边缘位置，Span内部永远无法插入光标。这样便能够实现把标签文本（spanned text）看作一个整体的思路
 *
 * 只需要在setText()之前把这个Span添加到文本上就可以了。
 */
public class SelectionSpanWatcher<T> implements SpanWatcher {
    private Class<T> mClass;
    private int selStart=0;
    private int selEnd=0;
    public SelectionSpanWatcher(Class<T> mClass){
        this.mClass=mClass;
    }
    @Override
    public void onSpanAdded(Spannable text, Object what, int start, int end) {

    }

    @Override
    public void onSpanRemoved(Spannable text, Object what, int start, int end) {

    }

    @Override
    public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
        if (what == Selection.SELECTION_END && selEnd != nstart) {
            selEnd = nstart;
            T[] classes = text.getSpans(nstart, nend, mClass);
           if(classes.length>0){
               T mclass = classes[0];
               int spanStart = text.getSpanStart(this);
               int spanEnd = text.getSpanEnd(this);
               int index = abs(selEnd - spanEnd) > abs(selEnd - spanStart)? spanStart : spanEnd ;
               Selection.setSelection(text, Selection.getSelectionStart(text), index);
           }
        }

        if (what == Selection.SELECTION_START && selStart != nstart) {
            selStart = nstart;
            T[] classes = text.getSpans(nstart, nend, mClass);
            if(classes.length>0){
                T mclass = classes[0];
                int spanStart = text.getSpanStart(this);
                int spanEnd = text.getSpanEnd(this);
                int index = abs(selStart - spanEnd) > abs(selStart - spanStart)? spanStart : spanEnd;
                Selection.setSelection(text, index, Selection.getSelectionEnd(text));
            }
        }
    }
}
