package net.cb.cb.library.utils;

import net.cb.cb.library.bean.HuaweiObsConfigBean;
import net.cb.cb.library.bean.ReturnBean;

/***
 * @author jyj
 * @date 2016/12/20
 */
public class UpFileAction {
    private UpFileServer server;

    public UpFileAction() {
        server = NetUtil.getNet().create(UpFileServer.class);
    }


    public void haweiObs(CallBack<ReturnBean<HuaweiObsConfigBean>> callback) {
        NetUtil.getNet().exec(
                server.haweiObs()
                , callback);
    }

}

