package com.yanlong.im.utils.edit.span;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/14 0014
 * @description
 * 让用户提供一个CharSequence对象作为标签，它决定了标签文本的样式和内容
 * 提供一个方法返回DataBindingSpan<T>对象所绑定的业务数据。
 */
public interface DataBindingSpan<T> {
    CharSequence getSpannedText();
    T bindingData();
    String getText();
}
