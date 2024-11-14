package com.autohome.app.cars.job.jobs.brand;

import com.autohome.app.cars.service.components.car.BrandDetailAllComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander("BrandInfoAllJob")
@Service
public class BrandInfoAllJob extends IJobHandler {

    @Autowired
    private BrandDetailAllComponent brandDetailAllComponent;

    @Override
    public ReturnT<String> execute(String... strings) {
        brandDetailAllComponent.refreshAll();
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
