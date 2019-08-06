package com.yanlong.im.chat.ui.cell;

import com.yanlong.im.chat.ChatEnum;

public interface ICellEventListener {
    void onEvent(ChatEnum.ECellEventType type, Object o1, Object o2);
}
