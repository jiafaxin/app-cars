package com.autohome.app.cars.service.components.recrank.attention;

import com.autohome.app.cars.common.enums.ZiXunNewCarTagEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.appcars.AttRankNewCarMapper;
import com.autohome.app.cars.mapper.appcars.entities.AttRankNewCarEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.AttentionNewCarListDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.enums.RankLevelIdEnum;
import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.autohome.app.cars.service.components.car.common.RankUtil.calcPageCount;
import static com.autohome.app.cars.service.components.car.common.RankUtil.genPriceLinkUrl;

/**
 * @author chengjincheng
 * @date 2024/3/28
 */
@Deprecated // 关注榜新车榜 使用DtNewCarAttentionComponent取数据
@Slf4j
@Component
public class AttentionNewCarListComponent extends BaseComponent<AttentionNewCarListDto> {

    final static String pageIndexParamName = "pageIndex";

    final static String pageSizeParamName = "pageSize";

    final static String levelIdParamName = "levelIds";

    final static String dataUpdateFlag = ":updateFlag:";

    @Autowired
    private AttRankNewCarMapper attRankNewCarMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private RankCommonComponent rankCommonComponent;

    TreeMap<String, Object> makeParam(int pageIndex, int pageSize, String levelIds) {
        return ParamBuilder.create(pageIndexParamName, pageIndex)
                .add(pageSizeParamName, pageSize)
                .add(levelIdParamName, levelIds)
                .build();
    }

    public String get(TreeMap<String, Object> params) {
        String levelIds = Objects.isNull(params.get("levelIds")) ? null : String.valueOf(params.get("levelIds"));
        AttentionNewCarListDto dto = get((int) params.get("pageIndex"), (int) params.get("pageSize"), levelIds)
                .join();
        return JsonUtil.toString(dto);
    }

    /**
     * 数据组同步的新车关注度数据每小时更新一次，因此本组件取值时也需要取最新的数据
     *
     * @param pageIndex
     * @param pageSize
     * @param levelIds
     * @return
     */
    public CompletableFuture<AttentionNewCarListDto> get(int pageIndex, int pageSize, String levelIds) {
        // 获取更新标识
        String updateFlag = stringRedisTemplate.opsForValue().get(getUpdateFlagKey());
        // 从redis缓存中获取新车关注度数据
        AttentionNewCarListDto listDto = baseGetAsync(makeParam(pageIndex, pageSize, levelIds)).join();
        if (Objects.isNull(listDto)) {
            // 没有缓存就查库，并将更新标识补充到dto之后反写缓存
            AttentionNewCarListDto dtoFromDb = getFromDb(updateFlag, pageIndex, pageSize, levelIds);
            if (Objects.nonNull(dtoFromDb)) {
                update(makeParam(pageIndex, pageSize, levelIds), dtoFromDb);
            }
            return CompletableFuture.completedFuture(dtoFromDb);
        } else {
            // 有缓存的话，先取出缓存并比较更新标识与回调接口写入的更新标识是否一致
            if (Objects.equals(updateFlag, listDto.getUpdateFlag())) {
                // 若一致则表示数据没有更新，直接返回
                return CompletableFuture.completedFuture(listDto);
            } else {
                // 若不一致则表示数据库数据有更新，查库并将更新标识补充到dto之后反写缓存，再返回
                AttentionNewCarListDto dtoFromDb = getFromDb(updateFlag, pageIndex, pageSize, levelIds);
                if (Objects.nonNull(dtoFromDb)) {
                    update(makeParam(pageIndex, pageSize, levelIds), dtoFromDb);
                }
                return CompletableFuture.completedFuture(dtoFromDb);
            }
        }
    }

    private AttentionNewCarListDto getFromDb(String updateFlag, int pageIndex, int pageSize, String levelIds) {
        // 分页查询db
        String dt = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        int start = (pageIndex - 1) * pageSize;
        List<AttRankNewCarEntity> entityList = attRankNewCarMapper.pageGetByLevelAndDt(levelIds, dt, start, pageSize);
        Integer count = attRankNewCarMapper.countByLevelAndDt(levelIds, dt);
        if (CollectionUtils.isEmpty(entityList)) {
            // 兜底逻辑，防止过0点后，数据组的数据还没有生产出来，此时取前一天的数据进行兜底
            dt = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
            entityList = attRankNewCarMapper.pageGetByLevelAndDt(levelIds, dt, start, pageSize);
            count = attRankNewCarMapper.countByLevelAndDt(levelIds, dt);
        }

        // TODO chengjincheng 2024/10/14 昨天没数据，热修代码
        if (CollectionUtils.isEmpty(entityList)) {
            // 兜底逻辑，防止过0点后，数据组的数据还没有生产出来，此时取前2天的数据进行兜底
            dt = DateFormatUtils.format(DateUtils.addDays(new Date(), -2), "yyyy-MM-dd");
            entityList = attRankNewCarMapper.pageGetByLevelAndDt(levelIds, dt, start, pageSize);
            count = attRankNewCarMapper.countByLevelAndDt(levelIds, dt);
        }

        if (CollectionUtils.isEmpty(entityList)) {
            // 连续两天都查询不到新车关注度数据，属于异常情况，要么分页参数传错，要么db中数据有误
            log.error("获取不到新车关注度数据，updateFlag={}, pageIndex={}, pageSize={}, levelIds={}",
                    updateFlag, pageIndex, pageSize, levelIds);
            return null;
        } else {
            // 查询出来的车系id集合
            List<String> seriesIdStrList = entityList.stream()
                    .map(AttRankNewCarEntity::getSeriesId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            // 前一天的新车关注度信息，用于计算排名变化
            String yesterdayDt = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
            List<AttRankNewCarEntity> yesterdayEntityList =
                    attRankNewCarMapper.getBySeriesIdsAndDt(String.join(",", seriesIdStrList), yesterdayDt);

            if(CollectionUtils.isEmpty(yesterdayEntityList)){
                String yesterdayDt2 = DateFormatUtils.format(DateUtils.addDays(new Date(), -2), "yyyy-MM-dd");
                yesterdayEntityList =
                        attRankNewCarMapper.getBySeriesIdsAndDt(String.join(",", seriesIdStrList), yesterdayDt2);
            }
            Map<Integer, AttRankNewCarEntity> yesterdayEntityMap = yesterdayEntityList.stream()
                    .collect(Collectors.toMap(AttRankNewCarEntity::getSeriesId, Function.identity(), (v1, v2) -> v2));

            // 组装AttentionNewCarListDto
            AttentionNewCarListDto attentionNewCarListDto = new AttentionNewCarListDto();
            attentionNewCarListDto.setDt(dt);
            attentionNewCarListDto.setUpdateFlag(updateFlag);
            attentionNewCarListDto.setPageIndex(pageIndex);
            attentionNewCarListDto.setPageSize(pageSize);
            attentionNewCarListDto.setCount(count);
            entityList.forEach(e -> {
                AttentionNewCarListDto.ResultDto resultDto = new AttentionNewCarListDto.ResultDto();
                resultDto.setSeriesId(e.getSeriesId());
                resultDto.setSeriesName(e.getSeriesName());
                resultDto.setLevelId(e.getLevelId());
                resultDto.setAtt(e.getAtt());
                resultDto.setOnTime(e.getOnTime());
                resultDto.setSeriesTagId(e.getSeriesTagId());
                resultDto.setSeriesTag(ZiXunNewCarTagEnum.getTypeByValue(e.getSeriesTagId()));
                resultDto.setArticleId(e.getArticleId());
                resultDto.setRankNum(e.getRankNum());
                // 排名变化
                AttRankNewCarEntity yesterdayEntity = yesterdayEntityMap.get(e.getSeriesId());
                resultDto.setRanChange(Objects.nonNull(yesterdayEntity)
                        ? yesterdayEntity.getRankNum() - e.getRankNum()
                        : 0);
                attentionNewCarListDto.getSeriesList().add(resultDto);
            });

            return attentionNewCarListDto;
        }
    }

    public String getUpdateFlagKey() {
        return super.getBaseKey() + dataUpdateFlag;
    }

    public void updateAttentionNewCarDataFlag() {
        stringRedisTemplate.opsForValue().set(getUpdateFlagKey(), String.valueOf(new Date().getTime()),
                24, TimeUnit.HOURS);
    }




}
