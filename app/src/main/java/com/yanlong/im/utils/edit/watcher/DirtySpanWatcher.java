package com.yanlong.im.utils.edit.watcher;

import android.text.SpanWatcher;
import android.text.Spannable;
import android.util.Log;

import com.yanlong.im.utils.edit.IRemovePredicate;
import com.yanlong.im.utils.edit.span.DataBindingSpan;
import com.yanlong.im.utils.edit.span.RemoveOnDirtySpan;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/14 0014
 * @description
 */
public class DirtySpanWatcher implements SpanWatcher {
    private IRemovePredicate removePredicate;

    public DirtySpanWatcher(IRemovePredicate removePredicate) {
        this.removePredicate = removePredicate;
    }

    @Override
    public void onSpanAdded(Spannable text, Object what, int start, int end) {
    }

    @Override
    public void onSpanRemoved(Spannable text, Object what, int start, int end) {
    }

    @Override
    public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
        if (what instanceof RemoveOnDirtySpan && ((RemoveOnDirtySpan) what).isDirty(text)) {
            int spanStart = text.getSpanStart(what);
            int spanEnd = text.getSpanEnd(what);
            Object[] objects = text.getSpans(spanStart, spanEnd, Object.class);
            Log.e("raleigh_test", "ostart=" + ostart + ",oend=" + oend + ",nstart=" + nstart + ",nend=" + nend);
            Log.e("raleigh_test", "text=" + text + ",spanStart=" + spanStart + ",spanEnd=" + spanEnd);
            for (Object object : objects) {
                if (removePredicate.isToRemove(object)) {
                    Log.e("raleigh_test", "object=" + object);
                    if (object instanceof DataBindingSpan) {
                        if (spanEnd - spanStart != ((DataBindingSpan) object).getSpannedText().length())
                            text.removeSpan(object);
                    } else {
                        text.removeSpan(object);
                    }

                }
            }
        }
    }
}