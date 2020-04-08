package com.yanlong.im;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.protobuf.InvalidProtocolBufferException;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.manager.FileManager;

import java.io.File;

/**
 * @author Liszt
 * @date 2020/4/3
 * Description
 */
public class MainTest {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
        testParseMessage();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void testParseMessage() {
        MsgBean.UniversalMessage message = createMessage();
        if (message != null) {
            byte[] bytes = message.toByteArray();
            if (bytes != null) {
                System.out.println("PC同步--1--" + bytes.length);
                File file = FileManager.getInstance().saveMsgFile(bytes);
                if (file != null) {
                    parseFile(file);
                }
            }
        }
    }

    public static MsgBean.UniversalMessage createMessage() {
        MsgBean.UniversalMessage.Builder builder = MsgBean.UniversalMessage.newBuilder();
        builder.setRequestId(SocketData.getUUID());
//        builder.setToUid(100105);
        for (int i = 0; i < 4000; i++) {
            MsgBean.UniversalMessage.WrapMessage.Builder wrapMsg = MsgBean.UniversalMessage.WrapMessage.newBuilder();
            wrapMsg.setMsgId(SocketData.getUUID());
            wrapMsg.setFromUid(100804);
            wrapMsg.setTimestamp(SocketData.getSysTime());
            wrapMsg.setNickname("Liszt");
            wrapMsg.setAvatar("http://zx-im-img.zhixun6.com/product-environment/avatar/99a5614b-6648-4f45-a512-5e03e0d0dd6e.jpg");
            wrapMsg.setToUid(100105);
            MsgBean.ChatMessage.Builder msg = MsgBean.ChatMessage.newBuilder();
            msg.setMsg("测试第" + i + "条数据");
            wrapMsg.setChat(msg.build());
            builder.addWrapMsg(wrapMsg.build());
        }
        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void parseFile(File file) {
        byte[] bytes = FileManager.getInstance().readFileBytes(file);
        if (bytes != null) {
            System.out.println("PC同步--2--" + bytes.length);
            if (bytes != null) {
                try {
                    MsgBean.UniversalMessage message = MsgBean.UniversalMessage.parseFrom(bytes);
                    if (message != null) {
                        System.out.println("PC同步--3--" + message.getWrapMsgCount());
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
