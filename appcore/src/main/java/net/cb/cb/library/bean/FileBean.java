package net.cb.cb.library.bean;

import net.cb.cb.library.base.BaseBean;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/8/29 0029
 * @updateAuthor
 * @updateDate
 * @description 文件上传实体类
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class FileBean extends BaseBean {
    private String md5;// 文件md5值
    private String url;// 文件地址
    private String locationPath;// 本地路径

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocationPath() {
        return locationPath;
    }

    public void setLocationPath(String locationPath) {
        this.locationPath = locationPath;
    }
}
