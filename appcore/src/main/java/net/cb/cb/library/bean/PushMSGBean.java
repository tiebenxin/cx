package net.cb.cb.library.bean;

/***
 * @author jyj
 * @date 2017/5/27
 */
public class PushMSGBean {
    private String cmd;
    private String message;
    private String op;
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
    public String getCmd() {
        return cmd;
    }

    public void setOp(String op) {
        this.op = op;
    }
    public String getOp() {
        return op;
    }

    
}
