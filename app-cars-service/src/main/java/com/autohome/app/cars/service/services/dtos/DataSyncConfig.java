package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 机房数据同步配置
 */
@Data
public class DataSyncConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncConfig.class);

    //是否同步，1同步，0不同步
    private int syncPeer;

    //是否增量更新redis和db
    private int compareJsonValue;

    //主机房
    String jobCluster;

    public static DataSyncConfig createFromJson(String json) {
        try {
            if (StringUtils.isBlank(json))
                return null;
            return JsonUtil.toObject(json, DataSyncConfig.class);
        } catch (Exception e) {
            logger.error("DataSyncConfig 序列化失败");
            return null;
        }

    }
}
