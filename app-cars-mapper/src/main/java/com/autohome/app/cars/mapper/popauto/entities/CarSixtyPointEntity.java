package com.autohome.app.cars.mapper.popauto.entities;

/**
 * @ Author     ：lvming
 * @ Date       ：Created in 13:47 2020/10/20
 * @ Description：产品库60图点位基本信息
 * @ Modified By：
 * @Version: $
 */
public class CarSixtyPointEntity {

    /**
     * 点位原始id跟VR智能后台对应的id
     */
    private Integer pointid;
    /**
     * 点位名称
     */
    private String pointname;
    /**
     * 排位，前台的显示顺序
     */
    private Integer ordercls;

    public Integer getOrdercls() {
        return ordercls;
    }

    public void setOrdercls(Integer ordercls) {
        this.ordercls = ordercls;
    }

    public Integer getPointid() {
        return pointid;
    }

    public void setPointid(Integer pointid) {
        this.pointid = pointid;
    }

    public String getPointname() {
        return pointname;
    }

    public void setPointname(String pointname) {
        this.pointname = pointname;
    }
}
