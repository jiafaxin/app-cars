package com.autohome.app.cars.service.components.recrank.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.apiclient.abtest.AbApiClient;
import com.autohome.app.cars.apiclient.abtest.dtos.ABTestDto;
import com.autohome.app.cars.apiclient.rank.ClueRepeatClient;
import com.autohome.app.cars.apiclient.rank.dtos.ClueCheckAllResultDto;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.common.utils.StrUtil;
import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.car.common.RankUtil;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.koubei.SeriesKouBeiComponent;
import com.autohome.app.cars.service.components.koubei.dtos.SeriesKouBeiDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultShareParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankShareParamInfo;
import com.autohome.app.cars.service.components.recrank.dtos.common.ClueRepeatResult;
import com.autohome.app.cars.service.components.recrank.dtos.configdtos.AskBtnExcludeConfig;
import com.autohome.app.cars.service.components.recrank.dtos.configdtos.BaseEnergyCountDataDto;
import com.autohome.app.cars.service.components.recrank.enums.RankEnergyType;
import com.autohome.app.cars.service.components.recrank.enums.RankLevelIdEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 排行榜公共Component
 *
 * @author zhangchengtao
 * @date 2024/5/6 19:21
 */
@Component
@Slf4j
public class RankCommonComponent {


    @Resource
    private SeriesKouBeiComponent kouBeiComponent;

    @Resource
    private SeriesDetailComponent seriesDetailComponent;

    @Resource
    private AbApiClient abApiClient;
    @Resource
    private ClueRepeatClient clueRepeatClient;
    @Value("${loginoptimize:0}")
    private int loginOptimizeFlag;

    /**
     * 线索重复询价配置
     */
    @Value("${clue_repeat_config:}")
    private String clue_repeat_config;

    @Value("#{T(com.autohome.app.cars.service.components.recrank.dtos.configdtos.AskBtnExcludeConfig).createFromJson('${askbtn_exclude_config:}')}")
    private AskBtnExcludeConfig askBtnExcludeConfig;


    /**
     * 获取口碑分
     *
     * @param seriesIdList 车系ID
     * @return 车系口碑信息
     */
    public Map<String, String> getKouBeiScoreMap(List<Integer> seriesIdList) {
        Map<String, String> kouBeiScoreMap = new HashMap<>(seriesIdList.size());
        CompletableFuture<List<SeriesKouBeiDto>> listCompletableFuture = kouBeiComponent.getList(seriesIdList);
        if (Objects.nonNull(listCompletableFuture)) {
            List<SeriesKouBeiDto> kouBeiInfoList = listCompletableFuture.join();
            kouBeiInfoList.forEach(kouBeiInfo -> {
                if (Objects.nonNull(kouBeiInfo) && Objects.nonNull(kouBeiInfo.getScoreInfo())) {
                    kouBeiScoreMap.put(String.valueOf(kouBeiInfo.getSeriesId()), BigDecimal.valueOf(kouBeiInfo.getScoreInfo().getAverage()).setScale(2, RoundingMode.HALF_UP).toString());
                }
            });

        }
        return kouBeiScoreMap;
    }

    /**
     * 通过车系IDList 获取车系详细信息
     *
     * @param seriesIdList 车系IDList
     * @return 车系信息Map
     */
    public Map<String, SeriesDetailDto> getSeriesDetailMap(List<Integer> seriesIdList) {
        Map<String, SeriesDetailDto> detailDtoMap = new HashMap<>(seriesIdList.size());
        Lists.partition(seriesIdList, 50).forEach(list -> {
                    List<SeriesDetailDto> seriesDetailList = seriesDetailComponent.getListSync(list);
                    if (Objects.nonNull(seriesDetailList) && !seriesDetailList.isEmpty()) {
                        detailDtoMap.putAll(seriesDetailList.stream().filter(Objects::nonNull).collect(Collectors.toMap(x -> String.valueOf(x.getId()), x -> x)));
                    }
                }
        );
        return detailDtoMap;
    }

    public String getPriceDesc(RankParam param) {
        int min = param.getMinprice() / 10000;
        int max = param.getMaxprice() / 10000;
        if (min == 0 && max >= 9000) {
            return "全";
        }
        if (max >= 9000) {
            return min + "万以上";
        }
        if (min == max && min != 0) {
            return min + "万";
        } else {
            if (min == 0) {
                return max + "万以内";
            } else {
                return min + StrPool.DASHED + max + "万";
            }
        }
    }

    public String getPriceDesc(int minPrice, int maxPrice) {
        int min = minPrice / 10000;
        int max = maxPrice / 10000;
        if (min == 0 && max >= 9000) {
            return "全";
        }
        if (max >= 9000) {
            return min + "万以上";
        }
        if (min == max && min != 0) {
            return min + "万";
        } else {
            if (min == 0) {
                return max + "万以内";
            } else {
                return min + StrPool.DASHED + max + "万";
            }
        }
    }

    /**
     * 计算能源类型最小限制
     *
     * @param rankParam 参数
     * @return 能源类型最小限制
     */
    public Map<Boolean, List<String>> getEnergyLimit(RankParam rankParam) {
        // 级别筛选能源类型
        String energyTypeTop = String.valueOf(rankParam.getFueltype());
        // 底部能源类型筛选项
        String energyTypeBottom = String.valueOf(rankParam.getEnergytype());
        List<String> energyLimitList;
        // 是否
        boolean engFilterFlag = true;
        if (energyTypeTop.equals("0") && energyTypeBottom.equals("0")) {
            energyLimitList = Collections.emptyList();
            engFilterFlag = false;
        } else if (energyTypeTop.equals("0") || energyTypeBottom.equals("0")) {
            energyLimitList = Arrays.asList((energyTypeBottom.equals("0") ? energyTypeTop : energyTypeBottom).split(""));
        } else {
            List<String> topList = new ArrayList<>(Arrays.asList(energyTypeTop.split("")));
            List<String> bottomList = new ArrayList<>(Arrays.asList(energyTypeBottom.split("")));
            topList.retainAll(bottomList);
            if (!topList.isEmpty()) {
                energyLimitList = topList;
            } else {
                return Collections.emptyMap();
            }
        }
        return Collections.singletonMap(engFilterFlag, energyLimitList);
    }


    /**
     * 过滤能源类型
     *
     * @param rankParam       请求参数
     * @param dto             车系详情
     * @param engFilterFlag   是否需要过滤燃油类型
     * @param energyLimitList 能源类型筛选限制List
     * @return 是否符合条件
     */
    public boolean filterByEnergy(RankParam rankParam, RankResultDto.ListDTO dto, boolean engFilterFlag, List<String> energyLimitList) {

        if (Objects.isNull(dto)) {
            return false;
        }
        // 燃油车时过滤掉所有新能源
        if (rankParam.getEnergytype() == 1 && rankParam.getIsnewenergy() == 0 && dto.getEnergytype() == 1) {
            return false;
        }
        if (rankParam.getIsnewenergy() == 1 && dto.getEnergytype() != 1) {
            return false;
        }
        // 过滤能源类型
        if (engFilterFlag) {
            if (Objects.isNull(dto.getFuelTypes())) {
                return false;
            }
            if (!dto.getEnergyTypes().isEmpty()) {
                List<String> energyTypeList = Arrays.asList(dto.getEnergyTypes().split(""));
                return CollectionUtils.containsAny(energyTypeList, energyLimitList);
            } else {
                List<String> fuelTypeDetailIdList = Arrays.asList(dto.getFuelTypes().split(StrPool.COMMA));
                return CollectionUtils.containsAny(fuelTypeDetailIdList, energyLimitList);

            }
            // 能源类型有交集就返回true
        }
        return true;
    }

    /**
     * 计算当前日期偏移 amount * field 后的日期
     *
     * @param current    当前日期字符串
     * @param dateFormat 日期格式化
     * @param field      偏移单位 {@link Calendar#MONTH} / {@link Calendar#WEEK_OF_YEAR}
     * @param amount     偏移数量
     * @return 偏移后的日期 异常时直接返回 current
     */
    public String getDateOffset(String current, SimpleDateFormat dateFormat, int field, int amount) {
        if (StringUtils.hasLength(current)) {
            try {
                Date date = dateFormat.parse(current);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(field, amount);
                return dateFormat.format(calendar.getTime());
            } catch (ParseException e) {
                log.error("排行榜日期格式化错误:current{}, dateFormat:{}, field:{}, amount:{}", current, dateFormat, field, amount, e);
            }
        }
        log.warn("排行榜日期为空");
        return current;
    }


    /**
     * 计算上月销量排名
     *
     * @param resultDto    当前查询销量数据
     * @param preResultDto 上个时间段销量数据
     */
    public void calcPreRankNum(RankResultDto resultDto, RankResultDto preResultDto) {
//        if (Objects.nonNull(resultDto) && Objects.nonNull(resultDto.getResult()) && Objects.nonNull(resultDto.getResult().getList())
//                && Objects.nonNull(preResultDto) && Objects.nonNull(preResultDto.getResult()) && Objects.nonNull(preResultDto.getResult().getList())) {
        List<RankResultDto.ListDTO> curList = resultDto.getResult().getList();
        List<RankResultDto.ListDTO> preList = preResultDto.getResult().getList();
        // 将上月销量转为Map
        Map<String, RankResultDto.ListDTO> preMonthMap = preList.stream().collect(Collectors.toMap(RankResultDto.ListDTO::getSeriesid, x -> x));
        // 设置上月销量排名
        for (RankResultDto.ListDTO cur : curList) {
            if (preMonthMap.containsKey(cur.getSeriesid())) {
                // 获取上月销量排名
                RankResultDto.ListDTO preSaleData = preMonthMap.get(cur.getSeriesid());
                cur.setPreRankNum(preSaleData.getRn());
                // 计算排名变化
                cur.setRankchange(preSaleData.getRn() - cur.getRn());
            }
        }
//        }
    }


    /**
     * 获取榜单迁移后的EID
     *
     * @param channel 场景榜/总榜
     * @param pm      平台 安卓/iOS
     * @return Eid
     */
    public String getNewEnergyMonthAndWeekRankEid(int channel, int pm) {
        String eid;
        if (channel == 0) {
            eid = pm == 1 ? "3|1411002|572|25528|205924|305225" : "3|1412002|572|25528|205924|305226";
        } else {
            eid = pm == 1 ? "3|1411002|572|25528|205923|305224" : "3|1412002|572|25528|205923|305223";
        }
        return eid;
    }

    /**
     * 生成程序化询价协议
     *
     * @param param     接口参数
     * @param seriesId  车系ID
     * @param hotSpecId 热门车型ID
     */
    public void setProgrammaticPriceInfo(RankResultDto.RightinfoDTO rightInfo, RankParam param, int seriesId, int hotSpecId, int priceshow, String btnTitle) {
        // 子榜单类型, 1: 月榜; 2: 周榜
        Integer typeId = param.getSubranktypeid();
        String eid = "";
        // pm: android: 2 ios: 1
        int pm = param.getPm();
        switch (typeId) {
            case 1:
                if (param.getChannel() == 0) {
                    eid = pm == 1 ? "3|1411002|572|25528|205415|304431" : "3|1412002|572|25528|205415|304430";
                } else {
                    eid = pm == 1 ? "3|1411002|572|25529|205414|304429" : "3|1412002|572|25529|205414|304428";
                }
                break;
            case 2:
                if (param.getChannel() == 0) {
                    eid = pm == 1 ? "3|1411002|572|25528|205883|305153" : "3|1412002|572|25528|205883|305153";
                } else {
                    eid = pm == 1 ? "3|1411002|572|25529|205884|305153" : "3|1412002|572|25529|205884|305153";
                }
                break;
            default:
                return;
        }
        // 程序化询价按钮协议模板
        String loginOptimize = "&loginoptimize=1";
        if (CommonHelper.isTakeEffectVersion(param.getPluginversion(), "11.57.5") && loginOptimizeFlag == 0) {
            loginOptimize = "";
        }
        String programmaticAskPriceUrlTemplate =
                "autohome://car/asklowprice?customshowanimationtype=2&eid=%s&seriesid=%s&specid=%s&inquirytype=2&ordertype=1&price_show=%s&title=%s%s&ext=%s";
        // url 中的eid需要编码两次
        String encodeEid = CommonHelper.encodeUrl(CommonHelper.encodeUrl(eid));
        String rightPriceUrl = String.format(programmaticAskPriceUrlTemplate, encodeEid, seriesId, hotSpecId, priceshow, CommonHelper.encodeUrl(btnTitle), loginOptimize, "");
        if (StringUtils.hasLength(rightPriceUrl)) {
            rightInfo.setRightpriceeid(eid);
            rightInfo.setRightpriceurl(rightPriceUrl);
        }
    }


    /**
     * 按条件筛选
     *
     * @param resultDto 结果集
     * @param param     查询条件
     */
    public void filterByParam(RankResultDto resultDto, RankParam param) {
        if (Objects.nonNull(resultDto) && Objects.nonNull(resultDto.getResult()) && !resultDto.getResult().getList().isEmpty()) {
            // 通过过滤条件刷新数据
            List<String> levelIdList;
            List<RankResultDto.ListDTO> list = resultDto.getResult().getList();
            if (StringUtils.hasLength(param.getLevelid())) {
                String[] levelIdArr = param.getLevelid().split(StrPool.COMMA);
                levelIdList = Arrays.asList(levelIdArr);
            } else {
                levelIdList = Collections.emptyList();
            }
            Map<Boolean, List<String>> energyLimitMap = getEnergyLimit(param);
            // 级别中的能源类型和能源类型没有交集 直接设置空返回
            if (Objects.nonNull(energyLimitMap) && energyLimitMap.isEmpty()) {
                resultDto.getResult().setList(Collections.emptyList());
                return;
            }
            Map.Entry<Boolean, List<String>> next = energyLimitMap.entrySet().iterator().next();
            boolean engFilterFlag = next.getKey();
            List<String> energyLimitList = next.getValue();

            // 品牌过滤
            if (!CollectionUtils.isEmpty(param.getRemovebrandid())) {
                list.removeIf(x -> param.getRemovebrandid().contains(x.getBrandid()));
            }

            list = list.stream().filter(x -> {
                if (StringUtils.hasLength(param.getLevelid()) && !levelIdList.contains(x.getLevelId())) {
                    return false;
                }
                //车系过滤
                if (!CollectionUtils.isEmpty(param.getSelectseriesids())&&!param.getSelectseriesids().contains(NumberUtils.toInt(x.getSeriesid()))) {
                    return false;
                }
                // 过滤品牌
                if (StringUtils.hasLength(param.getBrandid()) && !param.getBrandid().equals(x.getBrandid().toString())) {
                    return false;
                }
                // 过滤价格
                if (param.getMaxprice() > 0 && param.getMaxprice() >= param.getMinprice()) {
                    if (!(x.getMinPrice() <= param.getMaxprice() && x.getMaxPrice() >= param.getMinprice())) {
                        return false;
                    }
                }
                // 厂商类型不选择时为空 选中全部时为 "0"
                if (StringUtils.hasLength(param.getFcttypeid()) && !"0".equals(param.getFcttypeid()) && !x.getManuType().equals(param.getFcttypeid())) {
                    return false;
                }
                // 筛选在售状态
                if (param.getIssale() == 1 && !RankConstant.ON_SALE_STATE_LIST.contains(x.getState())) {
                    return false;
                }
                // 筛选能源类型
                return filterByEnergy(param, x, engFilterFlag, energyLimitList);

            }).toList();
            resultDto.getResult().setList(list);
        }
    }

    /**
     * 处理排名, 如果销量相同则排名相同, 下一名名次为 上一名名次 + 相同名次的条数 - 1
     * 如 销量为 A: 100 B: 100 C: 100 D: 99
     * 则排名为: A: 1 B: 1 C: 1  D: 4
     *
     * @param resultDto 车系销量Result
     */
    public void processRankNum(RankResultDto resultDto) {
//        int currentRankNum = 1;
        int rankNum = 1;
//        int passNum = 0;
        List<RankResultDto.ListDTO> list = resultDto.getResult().getList();
        if (!list.isEmpty()) {
            for (RankResultDto.ListDTO item : list) {
                item.setRn(rankNum);
                item.setRnNum(rankNum);
                rankNum++;
            }
//            Long lastSaleCount = list.get(0).getSalecount();
//            for (RankResultDto.ListDTO item : list) {
//                if (item.getSalecount() < lastSaleCount) {
//                    currentRankNum += passNum == 0 ? 1 : passNum;
//                    passNum = 0;
//                } else {
//                    passNum++;
//                }
//                lastSaleCount = item.getSalecount();
//                item.setRn(currentRankNum);
//                item.setRnNum(rankNum++);
//            }

        }
    }

    /**
     * 添加排行榜单询价按钮
     *
     * @param result 结果集
     * @param param  参数
     */
    public void addAskPriceBtn(RankResultDto result, RankParam param) {
        // 检查若不需要添加询价按钮则直接返回
        if (!checkIsNeedAddAskPriceBtn(param)) {
            return;
        }
        result.getResult().getList().forEach(listDto -> {
            String seriesId = listDto.getSeriesid();
            RankResultDto.RightinfoDTO rightInfo = listDto.getRightinfo();
            rightInfo.setRightpricetitle("查成交价");
            StringBuilder rightPriceUrl = new StringBuilder("autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=rn%3A%2F%2FMallService%2FAskPrice%3FpanValid%3D0%26pvareaid%3D6849804%26seriesid%3D").append(seriesId).append("%26eid%3D");
            // channel: 总榜: 0; 场景榜: 1
            // pm: iOS 为 1; android 为 2
            String eid = "";
            if (param.getPm() == 1) {
                if (param.getChannel() == 0) {
                    //场景榜
                    if (param.getTypeid() == 17) {
                        eid = "3|1411002|572|25528|206025|305352";
                    } else {
                        eid = "3|1411002|572|25528|205924|305225";
                    }
                } else if (param.getChannel() == 1) {
                    eid = "3|1411002|572|25528|205923|305224";
                }
            } else {
                // android
                if (param.getChannel() == 0) {
                    if (param.getTypeid() == 17) {
                        eid = "3|1412002|572|25528|206025|305351";
                    } else {
                        eid = "3|1412002|572|25528|205924|305226";
                    }
                } else if (param.getChannel() == 1) {
                    eid = "3|1412002|572|25528|205923|305223";
                }
            }
            rightInfo.setRightpriceeid(eid);
            rightPriceUrl.append(UrlUtil.encode(UrlUtil.encode(eid)));
            rightInfo.setRightpriceurl(rightPriceUrl.toString());
            if (askBtnExcludeConfig != null
                    && askBtnExcludeConfig.getIsopen() == 1
                    && askBtnExcludeConfig.getList() != null
                    && askBtnExcludeConfig.getList().contains(Integer.parseInt(seriesId))) {
                String defurl = "autohome://car/seriesmain?seriesid=" + seriesId;
                rightInfo.setRightpriceurl(defurl);
                rightInfo.setRightpricetitle("查看详情");
                listDto.setRnbtn(1);

            }
        });

    }

    private boolean checkIsNeedAddAskPriceBtn(RankParam param) {
        // 销量榜-城市榜、新能源榜、关注榜、保值榜、降价榜、口碑榜、安全榜、实测榜、玩车榜
        //
        // 需要添加询价按钮的榜单类型
        // 销量榜 / 新能源榜 / 口碑榜 / 关注榜 / 保值榜 / 降价榜  / 安全榜 / 实测榜 / 玩车榜 / 摩托车榜
        // 榜单ID对照:
        // 销量榜 case 1:
        // 关注榜 case 2:
        // 降价榜 case 3:
        // 口碑榜 case 4:
        // 安全榜 case 5:
        // 保值榜 case 6:
        // 实测榜 case 8:
        // 新能源榜 case 9:
        // 摩托车榜 case 10:
        // 车家号榜 case 12:
        // 内容榜 case 13:
        // 玩车榜 case 14:
        // 自驾游榜 case 15:
        // 智能科技榜 case 16:
        if (param.getTypeid() == 17) {
            return true;
        }
        if ("rn".equals(param.getPf())) {
            return true;
        }
        List<Integer> hasAskPriceBtnTypeList = Arrays.asList(1, 9, 2, 6, 3, 4, 5, 8, 14, 18);
        // 降价榜固定返回询价按钮
        if (CommonHelper.isTakeEffectVersion(param.getPluginversion(), "11.65.5")) {
            if (param.getTypeid() == 3) {
                return true;
            }

        }
        if (param.isShowAskPriceBtn() && hasAskPriceBtnTypeList.contains(param.getTypeid())) {
            // 销量榜只有城市榜需要添加, 其余榜单都需要
            return param.getTypeid() != 1 || param.getSubranktypeid() == 4;
        }
        return false;
    }

    /**
     * 排行榜数据分页
     *
     * @param result 结果
     * @param param  参数
     */
    public void pagination(RankResultDto result, RankParam param) {
        List<RankResultDto.ListDTO> list = result.getResult().getList();
        if (Objects.isNull(list) || list.isEmpty()) {
            return;
        }
        int skipCount = (param.getPageindex() - 1) * param.getPagesize();
        int maxCount = Math.min(skipCount + param.getPagesize(), list.size());
        if (skipCount >= list.size()) {
            result.setReturncode(101);
            result.setMessage("超出最大页数");
            result.getResult().setList(Collections.emptyList());
            return;
        }
        List<RankResultDto.ListDTO> currentPageList = list.subList(skipCount, maxCount);
        // 补齐新的销量排名
        currentPageList.forEach(cur -> {
            String rankStr = String.format("%02d", cur.getRn());
            cur.setRank(rankStr);
            cur.getPvitem().getArgvs().put("rank", rankStr);
            if (Objects.nonNull(cur.getRightinfo()) && Objects.nonNull(cur.getRightinfo().getPvitem())) {
                cur.getRightinfo().getPvitem().getArgvs().put("rank", rankStr);
            }
        });
        result.getResult().setList(currentPageList);
        result.getResult().setPagecount(RankUtil.calcPageCount(list.size(), param.getPagesize()));
    }

    /**
     * 分页 + 处理询价按钮 + 重复询价协议 + 分享按钮
     *
     * @param result 结果集
     * @param param  参数
     */
    public void processOtherInfo(RankResultDto result, RankParam param) {
        // 分页
        pagination(result, param);
        // 询价按钮
        addAskPriceBtn(result, param);
        // 重复询价链接 不在List接口处理, 已迁移到 /carext/recrank/all/getrankbuttoninfo
        // addClueRepeatLink(result, param);
        // 添加是否可分享信息
        if (CommonHelper.isTakeEffectVersion(param.getPluginversion(), "11.56.0")) {
            // 分享按钮
            addShareBtn(param, result);
        }
        if (CommonHelper.isTakeEffectVersion(param.getPluginversion(), "11.56.5")) {
            // 在rightInfo中添加priceInfo
            setPriceInfo(result);
        }
        fillInfo(param, result);
    }


    public void fillInfo(RankParam param, RankResultDto result) {
        // 设置场景榜信息
        RankLevelIdEnum rankLevelIdEnum = RankLevelIdEnum.getInstance(!StringUtils.hasLength(param.getLevelid()) ? "0" : param.getLevelid());
        if (Objects.nonNull(rankLevelIdEnum)) {
            result.getResult().setScenetitle(getPriceDesc(param) + String.format(rankLevelIdEnum.getRankNameScheme(), "销量"));
        }
        result.getResult().setScenesubtitle(genSceneSubTitle(param));
    }

    /**
     * 返回结果统一业务逻辑处理
     *
     * @param result 返回结果
     * @param param  请求参数
     */
    public void resultCommonDeal(RankResultDto result, RankParam param) {
        // 询价按钮
        addAskPriceBtn(result, param);
        // 重复询价链接 /carext/recrank/all/getrankbuttoninfo
        // addClueRepeatLink(result, param);
        if (result == null || result.getResult() == null || result.getResult().getList() == null || result.getResult().getList().size() == 0) {
            if (result != null) {
                //TODO
                result.setCacheable(0);
            }
        }
        //处理list中seriesimage车系图片地址、showenergyicon值 各榜单组件内部结果单独处理

        // 添加是否可分享信息
        //TODO
        if (CommonHelper.isTakeEffectVersion(param.getPluginversion(), "11.56.0")) {
            // 分享按钮
            addShareBtn(param, result);
        }
        if (CommonHelper.isTakeEffectVersion(param.getPluginversion(), "11.56.5")) {
            // 在rightInfo中添加priceInfo
            setPriceInfo(result);
        }
    }


    /**
     * 添加排行榜单询价重复协议
     *
     * @param result 结果集
     * @param param  参数
     */
    private void addClueRepeatLink(RankResultDto result, RankParam param) {

        Map<Integer, ClueRepeatResult> spec_clue_map = new HashMap<>();
        if (StringUtils.hasLength(param.getDeviceid()) && CommonHelper.isTakeEffectVersion(param.getPluginversion(), "11.56.0")) {
            try {
                CompletableFuture<ABTestDto> abTestFuture = abApiClient.getABTest("100921", param.getDeviceid());
                ABTestDto abTest = abTestFuture.join();
                CompletableFuture<ClueCheckAllResultDto> checkAllResultFuture = clueRepeatClient.getCheckAllResult(param.getSelectcityid(), param.getDeviceid());
                ClueCheckAllResultDto checkAllResult = checkAllResultFuture.join();
                List<ClueRepeatResult> clueList = getClueRepeatAllResult(abTest, checkAllResult, param.getSelectcityid(), "ranklist");
                // 通过SeriesId去重
                List<ClueRepeatResult> distinctList = clueList.stream()
                        .collect(Collectors.toMap(ClueRepeatResult::getSeriesid, Function.identity(), (oldValue, newValue) -> oldValue))
                        .values().stream().toList();
                spec_clue_map.putAll(distinctList.stream().collect(Collectors.toMap(ClueRepeatResult::getSeriesid, p -> p)));
                result.getResult().getList().forEach(listDto -> {
                    String seriesId = listDto.getSeriesid();
                    RankResultDto.RightinfoDTO rightInfo = listDto.getRightinfo();
                    int seriesid = Integer.parseInt(seriesId);
                    if ("查成交价".equals(rightInfo.getRightpricetitle()) && spec_clue_map.containsKey(seriesid)) {
                        ClueRepeatResult clueItem = spec_clue_map.get(seriesid);
                        rightInfo.setRightpriceurl(clueItem.getLinurl());
                        rightInfo.setExt(clueItem.getExt());
                    }
                });
            } catch (Exception e) {
                log.error("排行榜重复线索入口实验异常", e);
            }
        }

    }

    /**
     * 获取重复询价信息
     *
     * @param abTest
     * @param clueCheckresultDto
     * @param cityid
     * @param position
     * @return
     */
    public List<ClueRepeatResult> getClueRepeatAllResult(ABTestDto abTest, ClueCheckAllResultDto clueCheckresultDto, int cityid, String position) {
        List<ClueRepeatResult> resultList = new ArrayList<>();
        try {
            Integer isopen = JSONObject.parse(clue_repeat_config).getInteger(position);
            if (isopen != null && isopen == 1 && abTest != null && abTest.getResult() != null && clueCheckresultDto != null && clueCheckresultDto.getResult() != null) {
                List<ABTestDto.ResultDTO.ListDTO> list = abTest.getResult().getList();
                List<ClueCheckAllResultDto.ResultDTO> clueList = clueCheckresultDto.getResult();
                Optional<ABTestDto.ResultDTO.ListDTO> first = list.stream().filter(i -> "100921".equals(i.getVariable())).findFirst();
                if (first.isPresent() && !clueList.isEmpty()) {
                    String abVersion = first.get().getVersion();
                    if ("chongfu_leads_A".equals(abVersion) || "chongfu_leads_B".equals(abVersion)) {
                        String resubmitStr = StrUtil.subAfter(abVersion, StrPool.UNDERLINE, true);
                        int resubmit = 0;
                        if ("A".equals(resubmitStr)) {
                            resubmit = 1;
                        } else if ("B".equals(resubmitStr)) {
                            resubmit = 2;
                        }
                        for (ClueCheckAllResultDto.ResultDTO item : clueList) {
                            ClueRepeatResult result = new ClueRepeatResult();
                            result.setSeriesid(item.getSeriesid());
                            result.setSpecid(item.getSpecid());
                            Integer specid = item.getSpecid();
                            Integer seriesid = item.getSeriesid();
                            String rn_prefix = "autohome://rninsidebrowser?url=";
                            String rn_url = String.format("rn://Car_SeriesSummary/AskPriceSuccess?seriesid=%s&specid=%s&dealercityid=%s&resubmit=%s", seriesid, specid, cityid, resubmit);
                            String linkurl = rn_prefix + UrlUtil.encode(rn_url);
                            JSONObject ext = new JSONObject();
                            ext.put("seriesid", seriesid);
                            ext.put("specid", specid);
                            ext.put("cityid", cityid);
                            ext.put("resubmit", resubmit);
                            ext.put("chongfu_leads", resubmit > 0 ? 1 : 0);
                            ext.put("link", linkurl);
                            result.setLinurl(linkurl);
                            result.setExt(ext.toJSONString());
                            resultList.add(result);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("getClueRepeatAllResult-error", e);
        }
        return resultList;
    }

    /**
     * 添加分享按钮
     */
    public void addShareBtn(RankParam param, RankResultDto result) {
        if (Objects.isNull(param) || Objects.isNull(result) || Objects.isNull(result.getResult()) || result.getResult().getList().isEmpty()) {
            log.warn("参数为空或榜单数据为空");
            return;
        }
        String rankType = String.valueOf(param.getTypeid());
        String subRankType;
        if ("9".equals(rankType)) {
            subRankType = String.valueOf(param.getSubrank());
            if (StringUtils.hasLength(subRankType)) {
                subRankType = param.getSubranktypeid().toString();
            }
        } else {
            subRankType = String.valueOf(param.getSubranktypeid());
        }
        // 可以分享的榜单类型Map key 总榜单TypeId  values 子榜单ID List, 子榜单类型为空List时,不校验子榜单类型
        Map<String, List<String>> canShareMap = new HashMap<>(5);
        canShareMap.put("1", Arrays.asList("1", "2"));
        // 关注榜
        canShareMap.put("2", Collections.emptyList());
        // 口碑榜 本期不上
        // canShareMap.put("4", Collections.emptyList());
        // 保值榜
        canShareMap.put("6", Collections.emptyList());
        // 新能源榜  [1210]: 电耗榜 [1209]: 续航榜
        canShareMap.put("9", Arrays.asList("1210", "1209", "2305", "2306"));
        List<String> subRankTypeList = canShareMap.get(rankType);
        boolean specialDisableFlag = 6 != param.getTypeid() || (!Arrays.asList("201908", "202104", "202105", "202106").contains(param.getLevelid()) && param.getEnergytype() <= 1);
        // 保值榜新能源不允许长按分享
        // 新能源榜中的电耗榜和续航榜不支持分享
        if (9 == param.getTypeid() && (1209 == param.getSubrank() || 1210 == param.getSubrank()) && param.getIssale() == 0) {
            specialDisableFlag = false;
        }
        if (specialDisableFlag && canShareMap.containsKey(rankType) && (subRankTypeList.isEmpty() || subRankTypeList.contains(subRankType))) {

            result.getResult().getList().forEach(item -> {
                RankShareParamInfo shareParamInfo = new RankShareParamInfo();
                shareParamInfo.setCanlongshare(1);
                RankResultShareParam params = new RankResultShareParam();
                // 销量榜
                params.setTypeid(param.getTypeid());
                params.setSeriesid(Integer.parseInt(item.getSeriesid()));
                switch (param.getTypeid()) {
                    case 1:
                        params.setSubranktypeid(param.getSubranktypeid());
                        // 月榜时间参数
                        if (1 == param.getSubranktypeid() && StringUtils.hasLength(param.getDate())) {
                            String date = param.getDate();
                            if (param.getDate().contains(StrPool.UNDERLINE)) {
                                date = param.getDate().split(StrPool.UNDERLINE)[1];
                            }
                            params.setDate(date);
                        }
                        // 周榜时间参数
                        if (2 == param.getSubranktypeid() && StringUtils.hasLength(param.getWeek())) {
                            params.setDate(param.getWeek());
                        }
                        break;
                    case 2:
                        params.setCityid(param.getProvinceid());
                        if (2002 == param.getSubranktypeid()) {
                            // 关注榜下的新车榜关闭长按分享功能
                            shareParamInfo.setCanlongshare(0);
                        }
                        break;
//                    case 4:
//                        break;
//                    case 6:
//                        break;
                    case 9:
                        params.setSubranktypeid(param.getSubranktypeid());
                        if (2305 == param.getSubranktypeid()) {
                            String date = param.getDate();
                            if (param.getDate().contains(StrPool.UNDERLINE)) {
                                date = param.getDate().split(StrPool.UNDERLINE)[1];
                            }
                            params.setDate(date);
                        } else if (2306 == param.getSubranktypeid()) {
                            params.setDate(param.getWeek());
                        } else if (param.getSubrank() != 0) {
                            params.setSubranktypeid(param.getSubrank());
                        }
                        break;
                    default:
                        break;
                }
                RankShareParamInfo.ShareBtn carShareBtn = RankShareParamInfo.ShareBtn.getInstance("1", "http://nfiles3.autohome.com.cn/zrjcpk10/rank_share_cxfx_20231106.webp", "车系分享", 168, item, params);
                RankShareParamInfo.ShareBtn listShareBtn = RankShareParamInfo.ShareBtn.getInstance("2", "http://nfiles3.autohome.com.cn/zrjcpk10/rank_share_phfx_20231106.webp", "排行分享", 169, item, params);
                List<RankShareParamInfo.ShareBtn> shareBtnList = new ArrayList<>();
                shareBtnList.add(carShareBtn);
                shareBtnList.add(listShareBtn);
                shareParamInfo.setSharelist(shareBtnList);
                shareParamInfo.setShareext(JSON.toJSONString(params));
                item.setShareinfo(shareParamInfo);
            });

        }
    }


    private void setPriceInfo(RankResultDto result) {
        if (result != null && result.getResult() != null && result.getResult().getList() != null) {
            result.getResult().getList().forEach(listDto -> {
                RankResultDto.RightinfoDTO rightInfo = listDto.getRightinfo();
                if (rightInfo != null && rightInfo.getPriceinfo() != null) {

                    if (askBtnExcludeConfig != null
                            && StringUtils.hasLength(rightInfo.getRightpriceurl())
                            && askBtnExcludeConfig.getIsopen() == 1
                            && askBtnExcludeConfig.getList() != null
                            && askBtnExcludeConfig.getList().contains(Integer.parseInt(listDto.getSeriesid()))) {
                        String defurl = "autohome://car/seriesmain?seriesid=" + listDto.getSeriesid();
                        rightInfo.setRightpriceurl(defurl);
                        rightInfo.setRightpricetitle("查看详情");
                        listDto.setRnbtn(1);

                    }
                    rightInfo.getPriceinfo().setTitle(rightInfo.getRightpricetitle());
                    rightInfo.getPriceinfo().setEid(rightInfo.getRightpriceeid());
                    rightInfo.getPriceinfo().setLinkurl(rightInfo.getRightpriceurl());
                    rightInfo.getPriceinfo().setExt(rightInfo.getExt());

                }
                if (StringUtils.hasLength(listDto.getRank())) {
                    String rankNum = "0";
                    if (listDto.getRank().startsWith("0")) {
                        rankNum = StrUtil.subAfter(listDto.getRank(), "0", true);
                    } else {
                        rankNum = listDto.getRank();
                    }
                    if (NumberUtils.isCreatable(rankNum)) {
                        listDto.setRankNum(Integer.parseInt(rankNum));
                    }
                }
            });
        }
    }

    /**
     * 获取ShareInfo
     *
     * @return RecRankListPageResult2.ResultDTO.ShareinfoDTO
     */
    public RankResultDto.ResultDTO.ShareinfoDTO getShareInfo() {
        RankResultDto.ResultDTO.ShareinfoDTO shareInfo = new RankResultDto.ResultDTO.ShareinfoDTO();
        shareInfo.setUrl("https://athm.cn/x/9Cvfb5c");
        shareInfo.setLogo(StrPool.EMPTY);
        shareInfo.setSubtitle(StrPool.EMPTY);
        shareInfo.setTitle(StrPool.EMPTY);
        return shareInfo;
    }


    /**
     * 生成场景榜副标题
     *
     * @param param 请求参数
     * @return 场景榜副标题
     */
    public String genSceneSubTitle(RankParam param) {
        String sceneSubTitle = StrPool.EMPTY;
        if (StringUtils.hasLength(param.getBeginMonth())) {

            LocalDate beginMonth = LocalDate.parse(param.getBeginMonth(), RankConstant.LOCAL_MONTH_FORMATTER);
            LocalDate endMonth = LocalDate.parse(param.getEndMonth(), RankConstant.LOCAL_MONTH_FORMATTER);
            Period between = Period.between(beginMonth, endMonth);
            int months = between.getMonths();
            if (Objects.equals(param.getBeginMonth(), param.getEndMonth())) {
                sceneSubTitle = beginMonth.getMonth().getValue() + "月";
            } else {
                sceneSubTitle = months > 3 ? "近半年" : "近三个月";
            }
        }
        return sceneSubTitle;
    }


    public String getMonthDisplay(String dateParam) {
        if (dateParam.contains("_")) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
        DateTime dateTime = formatter.parseDateTime(dateParam);
        return dateTime.getMonthOfYear() + "月";
    }


    /**
     * 填充各个能源类型的销量
     *
     * @param dataDto         dto
     * @param energySaleCount 销量分类字段
     */
    public void fillEnergyTypeSaleCount(BaseEnergyCountDataDto dataDto, String energySaleCount) {
        boolean contains = energySaleCount.contains("|");
        dataDto.setHasManyEnergy(contains);
        String[] saleCounts = energySaleCount.split("\\|");
        // 按能源类型分类的销量
        Arrays.stream(saleCounts).sorted().forEach(saleCount -> {

            String[] split = saleCount.split("_");
            if (split.length == 2) {
                int energyType = Integer.parseInt(split[0]);
                long curEnergySaleCount = Long.parseLong(split[1]);
                switch (energyType) {
                    // 燃油车销量
                    case 1 -> dataDto.setOfv(curEnergySaleCount);
                    // 纯电动销量
                    case 4 -> dataDto.setEv(curEnergySaleCount);
                    // 插电式混动销量
                    case 5 -> dataDto.setPhev(curEnergySaleCount);
                    // 增程式销量
                    case 6 -> dataDto.setReev(curEnergySaleCount);
                }
            }
        });
        // 新能源总销量
        dataDto.setNewEnergy(dataDto.getEv() + dataDto.getPhev() + dataDto.getReev());
    }


    /**
     * 设置能源类型销量
     *
     * @param energyType 能源类型
     * @param item       数据
     * @param dto        DTO
     */
    public void setEnergyTypeSaleDetail(int energyType, BaseEnergyCountDataDto item, RankResultDto.ListDTO dto) {
        // 销量能源类型标识
        String energyTypes = (item.getOfv() == 0 ? "" : "1") + (item.getEv() == 0 ? "" : "4") + (item.getPhev() == 0 ? "" : "5") + (item.getReev() == 0 ? "" : "6");
        dto.setEnergyTypes(energyTypes);
        // 是否有多种能源类型
        if (item.isHasManyEnergy()) {
            List<String> energySaleInfoList = new ArrayList<>(2);
            // 按能源类型分类的销量
            if (energyType == 0 || energyType == 456) {
                long totalSaleCount = 0;
                if (energyType != 456) {
                    if (item.getOfv() != 0) {
                        totalSaleCount += item.getOfv();
                        energySaleInfoList.add(RankEnergyType.OFV.getName() + StrPool.C_COLON + StrPool.C_SPACE + item.getOfv() + "辆");
                    }
                }
                if (item.getEv() > 0) {
                    totalSaleCount += item.getEv();
                    energySaleInfoList.add(RankEnergyType.EV.getName() + StrPool.C_COLON + StrPool.C_SPACE + item.getEv() + "辆");
                }
                if (item.getPhev() > 0) {
                    totalSaleCount += item.getPhev();
                    energySaleInfoList.add(RankEnergyType.PHEV.getName() + StrPool.C_COLON + StrPool.C_SPACE + item.getPhev() + "辆");
                }
                if (item.getReev() > 0) {
                    totalSaleCount += item.getReev();
                    energySaleInfoList.add(RankEnergyType.REEV.getName() + StrPool.C_COLON + StrPool.C_SPACE + item.getReev() + "辆");
                }
                dto.setSalecount(totalSaleCount);
            } else {
                dto.setSalecount(getSaleCountByEnergyType(item, energyType));
            }
            item.setSaleCount(dto.getSalecount());
            if (!energySaleInfoList.isEmpty() && energySaleInfoList.size() > 1) {
                dto.setRcmdesc(String.join("  |  ", energySaleInfoList));
            }
        } else {
            dto.setSalecount(item.getSaleCount());
        }
    }

    public long getSaleCountByEnergyType(BaseEnergyCountDataDto item, int energyType) {
        long saleCount = 0;
        switch (energyType) {
            case 0 -> saleCount = item.getSaleCount();
            case 1 -> saleCount = item.getOfv();
            case 4 -> saleCount = item.getEv();
            case 5 -> saleCount = item.getPhev();
            case 6 -> saleCount = item.getReev();
            case 456 -> saleCount = item.getNewEnergy();
        }
        return saleCount;
    }

    /**
     * 根据当前能源类型重新排序
     *
     * @param energyType 能源类型
     * @param dataList   数据List
     */
    public void reSort(int energyType, List<? extends BaseEnergyCountDataDto> dataList) {
        if (energyType != 0) {
            switch (energyType) {
                case 1 -> dataList.sort(Comparator.comparingLong(BaseEnergyCountDataDto::getOfv).reversed());
                case 4 -> dataList.sort(Comparator.comparingLong(BaseEnergyCountDataDto::getEv).reversed());
                case 5 -> dataList.sort(Comparator.comparingLong(BaseEnergyCountDataDto::getPhev).reversed());
                case 6 -> dataList.sort(Comparator.comparingLong(BaseEnergyCountDataDto::getReev).reversed());
                case 456 -> dataList.sort(Comparator.comparingLong(BaseEnergyCountDataDto::getNewEnergy).reversed());
            }
        }
    }

    /**
     * 计算销量历史图表最大值
     *
     * @param saleCount 销量历史
     * @return 最大值
     */
    public int getSaleHistoryMax(int saleCount) {
        if (saleCount < 50) {
            return 50;
        } else if (saleCount < 100) {
            return 100;
        } else {
            int pow = (int) Math.pow(10, (int) (Math.log10(saleCount)));
            return (saleCount / pow + 1) * pow;
        }
    }

    /**
     * 对结果进行分页处理
     *
     * @param result    结果
     * @param rankParam 请求入参
     */
    public <T> List<T> pageHandle(RankResultDto result, RankParam rankParam, List<T> dtoList) {
        result.getResult().setPageindex(rankParam.getPageindex());
        result.getResult().setPagesize(rankParam.getPagesize());
        int skipCount = (rankParam.getPageindex() - 1) * rankParam.getPagesize();
        if (Objects.nonNull(result.getResult())
                && !CollectionUtils.isEmpty(dtoList)) {
            try {
                result.getResult().setPagecount(RankUtil.calcPageCount(dtoList.size(), rankParam.getPagesize()));
                int maxCount = Math.min(dtoList.size(), skipCount + rankParam.getPagesize());
                if (!CollectionUtils.isEmpty(dtoList) && skipCount < maxCount) {
                    return dtoList.subList(skipCount, maxCount);
                } else {
                    return Collections.emptyList();
                }
            } catch (Exception e) {
                log.error("分页处理异常, result:{}, rankParam:{}", result, rankParam, e);
            }
        }
        return dtoList;
    }

    public String getRankStr(RankParam rankParam, int index) {
        return org.apache.commons.lang3.StringUtils.leftPad(
                String.valueOf((rankParam.getPageindex() - 1) * rankParam.getPagesize() + index + 1),
                2, "0");
    }
}
