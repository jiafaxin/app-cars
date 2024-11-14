package com.autohome.app.cars.job.jobs.spec;

import com.autohome.app.cars.service.components.car.SpecParamConfigTempComponent;
import com.autohome.app.cars.service.components.car.SpecParamInfoNewComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**

 * 车型参数模版项信息
 *
 *
 */
@JobHander("SpecParamConfigTempJob")
@Service
public class SpecParamConfigTempJob extends IJobHandler {

    @Autowired
    SpecParamConfigTempComponent component;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        component.refreshAll();
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
