package com.autohome.app.cars.service.components.recrank.dtos;


import autohome.rpc.car.app_cars.v1.carext.RankResultRequest;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@Slf4j
public class RankParam implements Serializable {

    private int pm;
    private String levelid = "";

    private int typeid;
    private String price = "";
    private String date = "";
    private int pageindex = 1;
    private int pagesize = 20;

    private String pluginversion = "11.48.0";
    private int rolling = 0;
    private int model = 1;
    private String fcttypeid = "";
    private int isnewenergy;
    private String brandid = "";
    /**
     * 要排除掉的品牌id
     */
    private List<Integer>removebrandid=new ArrayList<>();
    private int fueltype;
    // 0不限， 1燃油车，4纯电, 5插电, 6增程, 456新能源
    private int energytype;
    private int cityid;
    private int minprice;
    private int maxprice;
    private int provinceid = -1;
    private int koubeitypeid;
    private int issale;
    private int subrank;
    private int subtype;
    private String puserid = "";
    private String deviceid = "";
    //榜单请求来源 rn会带参数 pf=rn
    private String pf = "";
    /**
     * 0：总榜  1：场景榜
     */
    private int channel;

    /**
     * 安全性数据来源
     */
    private int datatype = 1;
    /**
     * 安全分类
     */
    private Integer safetypeid = 2;

    /**
     * 子榜  1:车系月销量 2:车系周销量 3:品牌月销量 4:城市销量
     */
    private Integer subranktypeid = 0;

    private String salecounttype = "";
    private String beginMonth = "";
    private String endMonth = "";
    private String week = "";
    private String beginWeek = "";
    private String endWeek = "";
    private String penetrate_version = "";
    /**
     * 是否展示 询价按钮 a/b test
     */
    private String pricebtnab;
    /**
     * 当前是否可以展示询价按钮
     * 值从Apollo获取, 区分新登录用户和老用户
     */
    private boolean showAskPriceBtn;

    /**
     * 程序化询价按钮实验
     */
    private String askpricecxhab;
    private int showrankchange = 1;
    private int selectcityid;
    private int from;
    /**
     * 只查询某些车系的
     */
    private List<Integer>selectseriesids=new ArrayList<>();
    private int seriesid;
    private String monthrank;
    private String weekrank;
    private String yearrank;
    private String seasonid = "";
    private String auth;


    public static RankParam getInstance(RankResultRequest request) {
        RankParam param = new RankParam();
        param.setPm(request.getPm());
        param.setPluginversion(request.getPluginversion());
        param.setPenetrate_version(request.getPenetrateVersion());
        param.setChannel(request.getChannel());
        param.setModel(request.getModel());
        param.setFrom(request.getFrom());
        param.setPageindex(request.getPageindex());
        param.setPagesize(request.getPagesize());
        param.setTypeid(request.getTypeid());
        param.setSubranktypeid(request.getSubranktypeid());
        param.setBrandid(request.getBrandid());
        param.setLevelid(request.getLevelid());
        param.setPrice(request.getPrice());
        param.setEnergytype(request.getEnergytype());
        param.setFcttypeid(request.getFcttypeid());
        param.setBrandid(request.getBrandid());
        param.setIssale(request.getIssale());
        param.setDate(request.getDate());
        param.setWeek(request.getWeek());
        param.setPf(request.getPf());
        param.setDeviceid(request.getDeviceid());
        param.setKoubeitypeid(request.getKoubeitypeid());
        transPrice(param);
        transLevel(param);
        transMonth(param);
        transWeek(param);
        processEnergyType(request.getLevelid(), request.getEnergytype(), param);
        // transFctTypeId(param);
        param.setCityid(request.getCityid());
        //PC来源将fcttypeid转换成中文
        if (request.getFrom() == 28) {
            param.setFcttypeid(fcttypeConvertToChinese(param.getFcttypeid()));
        }
        param.setSalecounttype(request.getSalecounttype());
        param.setProvinceid(request.getProvinceid() != 0 ? request.getProvinceid() : -1);
        return param;
    }


    public static RankParam getInstance(RankParam param) {
        transPrice(param);
        transLevel(param);
        transMonth(param);
        transWeek(param);
        if (param.getBrandid().equals("0")) {
            param.setBrandid("");
        }
        processEnergyType(param.getLevelid(), param.getEnergytype(), param);
        // transFctTypeId(param);
        param.setCityid(param.getCityid());
        return param;
    }

    public static void transPrice(RankParam param) {
        if (param != null && param.getPrice() != null) {
            String[] pair = param.getPrice().split("-");
            if (pair.length == 2) {
                try {
                    param.setMinprice(Integer.parseInt(pair[0]) * 10000);
                    param.setMaxprice(Integer.parseInt(pair[1]) * 10000);
                } catch (Exception ex) {
                    ex.getStackTrace();
                    log.info("无效的价格数据,值为:{},Exception:{}", param.getPrice(), ex.getMessage());
                }
            } else {
                param.setMinprice(0);
                param.setMaxprice(9000 * 10000);
            }
        }
    }


    public static void transLevel(RankParam param) {
        if (param != null && param.getLevelid() != null) {
            String levelid = param.getLevelid();
            //全部
            switch (levelid) {
                case "0" -> param.setLevelid("");
                //轿车
                case "201905" -> param.setLevelid("1,2,3,4,5,6");

                //suv
                case "201906" -> param.setLevelid("16,17,18,19,20");
            }
        }
        if (param != null && param.getFcttypeid() != null) {
            if (param.getFcttypeid().equals("0")) {
                param.setFcttypeid("");
            }
        }
    }

    public static void transMonth(RankParam param) {
        if (param != null && param.getDate() != null) {
            String[] pair = param.getDate().split("_");
            if (pair.length == 2) {
                param.setBeginMonth(pair[0]);
                param.setEndMonth(pair[1]);
            } else if (pair.length == 1) {
                param.setBeginMonth(pair[0]);
                param.setEndMonth(pair[0]);
            }
        }
    }


    public static void transWeek(RankParam param) {
        if (param != null && param.getWeek() != null) {
            String[] pair = param.getWeek().split("_");
            if (pair.length == 2) {
                param.setBeginWeek(pair[0]);
                param.setEndWeek(pair[1]);
            } else if (pair.length == 1) {
                param.setBeginWeek(pair[0]);
                param.setEndWeek(pair[0]);
            }
        }
    }

    /**
     * 通过参数设置厂商类型
     * 注: 口碑榜不一样, 自主为 2 合资为 1
     *
     * @param param 参数
     */
    public static void transFctTypeId(RankParam param) {
        String fctTypeId = "";
        switch (param.getFcttypeid()) {
            case "自主":
                fctTypeId = "1";
                if (param.getTypeid() == 4) {
                    fctTypeId = "2";
                }
                break;
            case "合资":
                fctTypeId = "2";
                if (param.getTypeid() == 4) {
                    fctTypeId = "1";
                }
                break;
            case "进口":
                fctTypeId = "3";
                break;
            default:
                break;
        }
        param.setFcttypeid(fctTypeId);
    }

    private static void processEnergyType(String levelId, int energyType, RankParam param) {
        if (param.getTypeid() == 9 && Arrays.asList(2305, 2306).contains(param.getSubranktypeid())) {
            if (param.getEnergytype() <= 1) {
                param.setEnergytype(456);
            }
            param.setIsnewenergy(1);
        }
        switch (levelId) {
            case "201908" -> {
                if (energyType >= 4) {
                    param.setEnergytype(Math.min(456, param.getEnergytype()));
                } else if (energyType == 1) {
                    param.setEnergytype(-1);
                }
                param.setFueltype(456);
                param.setLevelid("");
            }
            case "202104" -> {
                if (energyType == 456 || energyType == 0) {
                    param.setEnergytype(4);
                } else if (energyType != 4) {
                    param.setEnergytype(-1);
                }
                param.setFueltype(4);
                param.setLevelid("");
            }
            case "202105" -> {
                if (energyType == 456 || energyType == 0) {
                    param.setEnergytype(5);
                } else if (energyType != 5) {
                    param.setEnergytype(-1);
                }
                param.setFueltype(5);
                param.setLevelid("");
            }
            case "202106" -> {
                if (energyType == 456 || energyType == 0) {
                    param.setEnergytype(6);
                } else if (energyType != 6) {
                    param.setEnergytype(-1);
                }
                param.setFueltype(6);
                param.setLevelid("");
            }

        }
        List<String> energyTypeList = Arrays.asList(String.valueOf(param.getEnergytype()).split(StrPool.EMPTY));
        if (CollectionUtils.containsAny(RankConstant.NEW_ENERGY_TYPE_LIST, energyTypeList)) {
            param.setIsnewenergy(1);
        }
    }

    /**
     * 将厂商属性由英文转换成中文
     *
     * @param fcttypeid 厂商属性参数值
     * @return
     */
    private static String fcttypeConvertToChinese(String fcttypeid) {
        String resultName = fcttypeid;
        switch (fcttypeid) {
            case "hezi":
                resultName = "合资";
                break;
            case "guochan":
                resultName = "自主";
                break;
            case "jinkou":
                resultName = "进口";
                break;
        }
        return resultName;
    }
}
