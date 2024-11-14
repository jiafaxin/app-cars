package com.autohome.app.cars.job.kafka_listeners;

import com.autohome.app.cars.common.utils.CarSettings;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.job.basic.car.KafkaEntity;
import com.autohome.app.cars.mapper.popauto.BrandMapper;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.BrandEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.components.car.BrandDetailAllComponent;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.*;
import com.autohome.app.cars.service.components.car.dtos.BrandDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.hqpic.HqPicDataComponent;
import com.autohome.app.cars.service.components.video.SpecConfigSmallVideoComponent;
import com.autohome.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class BaseCarListener {

    @Autowired
    BrandMapper brandMapper;

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SpecMapper specMapper;

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    SpecDetailComponent specDetailComponent;

    @Autowired
    ProtobufSeriesDetailComponent protobufSeriesDetailComponent;

    @Autowired
    SpecConfigInfoComponent specConfigInfoComponent;

    @Autowired
    SpecParamInfoComponent specParamInfoComponent;

    @Autowired
    SpecConfigInfoNewComponent specConfigInfoNewComponent;

    @Autowired
    SpecParamInfoNewComponent specParamInfoNewComponent;

    @Autowired
    SpecOutInnerColorComponent specOutInnerColorComponent;

    @Autowired
    SpecConfigBagComponent specConfigBagComponent;

    @Autowired
    SpecSpecialConfigComponent specSpecialConfigComponent;

    @Autowired
    SpecParamConfigPicInfoComponent specParamConfigPicInfoComponent;

    @Autowired
    SpecConfigSmallVideoComponent smallVideoComponent;

    @Autowired
    SpecYearNewComponent specYearNewComponent;

    @Autowired
    HqPicDataComponent hqPicDataComponent;

    @Autowired
    HqPhotoComponent hqPhotoComponent;

    @Autowired
    private BrandDetailAllComponent brandDetailAllComponent;

    @KafkaListener(id = "base_car_listener", idIsGroup = false ,topics = "${kafkatopics}", containerFactory = "kafkaDefaultContainerFactory")
    public void listen(String message) {
        log.warn("kafka refresh, message:{}", JsonUtil.toObject(message, KafkaEntity.class));
        try {
            KafkaEntity kafkaEntity = JsonUtil.toObject(message, KafkaEntity.class);
            if (!"car".equals(kafkaEntity.getBusiness())) {
                return;
            }
            switch (kafkaEntity.getType()) {
                case "brand": {
                    processBrand(kafkaEntity);
                    break;
                }
                case "series": {
                    processSeries(kafkaEntity);
                    break;
                }
                case "spec":
                case "cvspec": {
                    processSpec(kafkaEntity);
                    processSeries(kafkaEntity);
                    break;
                }
                case "highQuality": {
                    updateHqPic(kafkaEntity);
                    break;
                }
                default:
            }
        } catch (Exception ex) {
            log.error("kafka refresh error, message:{}", JsonUtil.toObject(message, KafkaEntity.class), ex);
        }
    }

    private void processBrand(KafkaEntity kafkaEntity) {
        int brandId = kafkaEntity.getData().getId();
        if (brandId <= 0) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            log.info("refresh brand detail, brandId: {}", brandId);
            BrandEntity entity = brandMapper.getBrand(brandId);
            BrandDetailDto dto = new BrandDetailDto();
            dto.setId(entity.getId());
            dto.setName(entity.getName());
            dto.setLogo(CarSettings.getInstance().GetFullImagePath(entity.getImg()));
            brandDetailComponent.refresh(dto);
            //刷新全量的
            brandDetailAllComponent.refreshAll();
        }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
            log.error("refresh brand detail error, brandId: {}", brandId, e);
            return null;
        });
    }

    private void processSeries(KafkaEntity kafkaEntity) {
        int seriesId;
        if (kafkaEntity.getType().equals("spec")) {
            SpecEntity spec = specMapper.getSpec(kafkaEntity.getData().getSpecId());
            seriesId = spec.getSeriesId();
        } else if (kafkaEntity.getType().equals("cvspec")) {
            SpecEntity spec = specMapper.getCvSpec(kafkaEntity.getData().getSpecId());
            seriesId = spec.getSeriesId();
        } else {
            seriesId = kafkaEntity.getData().getId();
        }
        if (seriesId <= 0) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            XxlJobLogger.log("xxlJob refresh series detail, seriesId: " + seriesId);
            seriesDetailComponent.refresh(seriesId);
            protobufSeriesDetailComponent.refresh(seriesId);
            specYearNewComponent.refresh(seriesId);
        }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
            XxlJobLogger.log("refresh series detail error, seriesId: " + seriesId);
            return null;
        });
    }

    private void processSpec(KafkaEntity kafkaEntity) {
        try {
            int specId = kafkaEntity.getData().getSpecId();
            if (specId <= 0) {
                return;
            }
            CompletableFuture.runAsync(() -> {
                log.warn("kafka refresh spec detail, specId: " + specId);
                SpecDetailDto specOld = specDetailComponent.getSync(specId);
                SpecDetailDto specNew = specDetailComponent.refresh(specId);
                String s = JsonUtil.toString(specOld);
                String s1 = JsonUtil.toString(specNew);
                if (StringUtils.equals(s,s1)) {
                    log.warn("kafka refresh a=b, specId:{} ; {} ; {} ",specId,s,s1);
                }
                specConfigInfoComponent.refresh(specId);
                specParamInfoComponent.refresh(specId);
                specConfigInfoNewComponent.refresh(specId);
                specParamInfoNewComponent.refresh(specId);
                specOutInnerColorComponent.refresh(specId);
                specConfigBagComponent.refresh(specId);
                specSpecialConfigComponent.refresh(specId);
                specParamConfigPicInfoComponent.refresh(specId);
            }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
                log.error("kafka refresh spec detail error, specId: " + specId,e);
                return null;
            });
        } catch (Exception e) {
            log.error("kafka refresh spec detail error, kafkaEntity: " + JsonUtil.toString(kafkaEntity),e);
        }
    }

    private void updateHqPic(KafkaEntity kafkaEntity) {
        try {
            int seriesId = kafkaEntity.getData().getSeriesId();
            if (seriesId <= 0) {
                return;
            }
            CompletableFuture.runAsync(() -> {
                log.warn("kafka refresh series hq pic, seriesId: " + seriesId);
                hqPicDataComponent.refresh(seriesId, null);
                hqPhotoComponent.refreshOne(seriesId);
            }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
                log.error("kafka refresh series hq pic error, seriesId: " + seriesId, e);
                return null;
            });
        } catch (Exception e) {
            log.error("kafka refresh series hq pic error, kafkaEntity: " + JsonUtil.toString(kafkaEntity), e);
        }
    }
}
