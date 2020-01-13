package com.yanlong.im.view.function;

import androidx.annotation.Nullable;

import com.yanlong.im.chat.ChatEnum;

/**
 * @author Liszt
 * @date 2020/1/11
 * Description 聊天拓展功能
 */
public class FunctionItemModel {
    String name;
    int drawableId;
    @ChatEnum.EFunctionId
    int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof FunctionItemModel) {
            return this.id == ((FunctionItemModel) obj).id;
        }
        return false;
    }
}
