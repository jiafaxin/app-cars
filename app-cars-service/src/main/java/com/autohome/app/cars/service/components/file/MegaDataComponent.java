package com.autohome.app.cars.service.components.file;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.services.dtos.MegaDataDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author wbs
 * @date 2024/6/7
 */
@Slf4j
@Component
public class MegaDataComponent extends BaseComponent<MegaDataDto> {

    @Value("${spring.profiles.active}")
    private String env;

    TreeMap<String, Object> makeParam(Integer seriesId) {
        return ParamBuilder.create("seriesId", seriesId).build();
    }

    public MegaDataDto getMegaPicData(int seriesId) {
        try {
            MegaDataDto dto = baseGet(makeParam(seriesId));
            if (null != dto) {
                return dto;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setMegaPicData(Consumer<String> xxllog) {

        try {
            refresh("piclist_mega", 6939, xxllog);
            refresh("piclist_bao5", 7177, xxllog);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("缓存执行失败", e);
            xxllog.accept("缓存执行失败");
        }

    }

    private void refresh(String fileName, int seriesId, Consumer<String> xxllog) throws Exception {

        if (Arrays.asList(env.split(",")).contains("dev")) {
            fileName = fileName + "_dev";
        }
        if (Arrays.asList(env.split(",")).contains("fat")) {
            fileName = fileName + "_fat";
        }
        FileUtils.copyURLToFile(new URL("http://nfiles3.in.autohome.com.cn/zrjcpk10/" + fileName + ".txt?t=" + System.currentTimeMillis()), new File(fileName), 1000, 10000);

        String readJson = IOUtils.toString(new FileInputStream(new File(fileName)), Charset.forName("UTF-8"));

        MegaDataDto dto = JsonUtil.toObject(readJson, MegaDataDto.class);
        updateRedis(makeParam(seriesId), JsonUtil.toString(dto), true);
        xxllog.accept("缓存执行成功 megaData:" + seriesId);
    }


}
