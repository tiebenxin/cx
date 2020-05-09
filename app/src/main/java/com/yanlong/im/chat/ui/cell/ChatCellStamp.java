package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MsgAllBean;

import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;

/*
 * @author Liszt
 * Description  戳一下消息
 * */
public class ChatCellStamp extends ChatCellBase {

    private TextView tv_content;

    protected ChatCellStamp(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_content = getView().findViewById(R.id.tv_content);
        //设置自定义文字大小
        Integer fontSize=new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        if(fontSize!=null){
            tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        setText(message.getStamp().getComment());
    }

    private void setText(String msg) {
        if (!StringUtil.isNotNull(msg)) {
            return;
        }
        String textSource = "<font color='#079892'>戳一下　</font>" + msg;
        tv_content.setText(Html.fromHtml(textSource));
    }


}
