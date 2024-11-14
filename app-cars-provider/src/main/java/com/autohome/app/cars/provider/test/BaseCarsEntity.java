package com.autohome.app.cars.provider.test;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class BaseCarsEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private int returncode = 0;
    private int cacheable = 1;
    /**
     * 根据返回值，设置CDN缓存头。默认值0：不操作；-1：不缓存；大于0；增加缓存头；
     * 缓存单位：秒；
     * 增加字段时间：2023-09-20
     */
    private int cdncachesecond = 0;
    private String message = "";

    public BaseCarsEntity() {
    }

    public int getReturncode() {
        return this.returncode;
    }

    public void setReturncode(int returncode) {
        this.returncode = returncode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCacheable() {
        return cacheable;
    }

    public void setCacheable(int cacheable) {
        this.cacheable = cacheable;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public int getCdncachesecond() {
        return cdncachesecond;
    }

    public void setCdncachesecond(int cdncachesecond) {
        this.cdncachesecond = cdncachesecond;
    }
}
