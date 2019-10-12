package com.example.nim_lib.uikit.contact.query;


import com.example.nim_lib.uikit.api.model.contact.AbsContactItem;

import java.util.List;

/**
 * 通讯录数据源提供者接口
 * Created by huangjun on 2015/4/2.
 */
public interface IContactDataProvider {
    public List<AbsContactItem> provide(TextQuery query);
}
