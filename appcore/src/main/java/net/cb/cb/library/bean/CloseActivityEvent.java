package net.cb.cb.library.bean;

import net.cb.cb.library.utils.StringUtil;

/***
 * 关闭界面 事件
 */
public class CloseActivityEvent {
    public String type;

    public CloseActivityEvent(String type){
        if(StringUtil.isNotNull(type)){
            this.type=type;
        }else {
            this.type="";
        }
    }
}
