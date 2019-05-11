package net.cb.cb.library.bean;

import java.util.HashMap;
import java.util.Map;

public class QRCodeBean {
    //二维码头部
    private String head;

    //功能选项
    private String function;

    //参数集合
    private Map<String,String> parameter = new HashMap<>();

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Map<String, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }


    /**
     * 获取map中参数值
     * @param key
     * */
    public String getParameterValue(String key){
        if(parameter != null){
            for(Map.Entry<String, String> value: parameter.entrySet()){
                if(value.getKey().equals(key)){
                    return value.getValue();
                }
            }
        }
       return "";
    }

}
