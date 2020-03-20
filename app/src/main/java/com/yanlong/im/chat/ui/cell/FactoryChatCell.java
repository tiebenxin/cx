package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.view.View;

import com.yanlong.im.chat.ChatEnum;

/*
 * @author Liszt
 * Description  ChatCell 工厂类
 * */
public class FactoryChatCell {

    private final Context mContext;
    private final ICellEventListener mListener;
    private final MessageAdapter mAdapter;
    private int position;

    public FactoryChatCell(Context context, MessageAdapter adapter, ICellEventListener listener) {
        mContext = context;
        mListener = listener;
        mAdapter = adapter;
    }

    public void setPosition(int p) {
        position = p;
    }


    public ChatCellBase createCell(ChatEnum.EChatCellLayout layout, View view) {
        ChatCellBase cell = null;
        switch (layout) {
            case TEXT_RECEIVED:
            case TEXT_SEND:

            case ASSISTANT:

            case AT_RECEIVED:
            case AT_SEND:
                cell = new ChatCellText(mContext, view, mListener, mAdapter);
                break;
            case IMAGE_RECEIVED:
            case IMAGE_SEND:
                cell = new ChatCellImage(mContext, view, mListener, mAdapter);
                break;
            case VIDEO_RECEIVED:
            case VIDEO_SEND:
                cell = new ChatCellVideo(mContext, view, mListener, mAdapter);
                break;
            case RED_ENVELOPE_RECEIVED:
            case RED_ENVELOPE_SEND:
                cell = new ChatCellRedEnvelope(mContext, view, mListener, mAdapter);
                break;
            case TRANSFER_RECEIVED:
            case TRANSFER_SEND:
                cell = new ChatCellTransfer(mContext, view, mListener, mAdapter);
                break;
            case NOTICE:
                cell = new ChatCellNotice(mContext, view, mListener, mAdapter);
                break;
            case CARD_RECEIVED:
            case CARD_SEND:
                cell = new ChatCellBusinessCard(mContext, view, mListener, mAdapter);
                break;
            case VOICE_RECEIVED:
            case VOICE_SEND:
                cell = new ChatCellVoice(mContext, view, mListener, mAdapter);
                break;
            case STAMP_RECEIVED:
            case STAMP_SEND:
                cell = new ChatCellStamp(mContext, view, mListener, mAdapter);
                break;
            case MAP_RECEIVED:
            case MAP_SEND:
                cell = new ChatCellMap(mContext, view, mListener, mAdapter);
                break;
            case FILE_RECEIVED:
            case FILE_SEND:
                cell = new ChatCellFile(mContext, view, mListener, mAdapter);
                break;
            case WEB_RECEIVED:
            case WEB_SEND:
                cell = new ChatCellWeb(mContext, view, mListener, mAdapter);
                break;
            case MULTI_RECEIVED:
            case MULTI_SEND:
                cell = new ChatCellMulti(mContext, view, mListener, mAdapter);
                break;
            case LOCK:
                cell = new ChatCellLock(mContext, view, mListener, mAdapter);
                break;
            case BALANCE_ASSISTANT:
                cell = new ChatCellBalanceAssitant(mContext, view, mListener, mAdapter);
                break;
            default:
                cell = new ChatCellUnrecognized(mContext, view, mListener, mAdapter);
                break;
        }
        return cell;

    }
}
