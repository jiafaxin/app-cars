package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carcfg.*;
import com.autohome.app.cars.provider.config.SimplifyJson;
import com.autohome.app.cars.service.services.ParamConfigService;
import com.autohome.app.cars.service.services.SpecParamConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@DubboService
@RestController
@Slf4j
public class NewSpecCompareServiceGrpcImpl extends DubboNewSpecCompareServiceTriple.NewSpecCompareServiceImplBase {

    @Autowired
    private SpecParamConfigService specParamConfigService;
    @Autowired
    ParamConfigService paramConfigService;

    @Override
    @SimplifyJson(value = "11.67.8")
    @GetMapping(value = "/carcfg/config/newspeccompare", produces = "application/json;charset=utf-8")
    public NewSpecCompareResponse newSpecCompare(NewSpecCompareRequest request) {
        return paramConfigService.getSpecCompare(request).join();
    }

    @Override
    @GetMapping(value = "/carcfg/config/getbaikeinfo", produces = "application/json;charset=utf-8")
    public GetBaiKeInfoResponse getBaikeInfo(GetBaiKeInfoRequest request) {
        return specParamConfigService.getConfigBaikeInfo(request);
    }

    private NewSpecCompareResponse convertResponse(GetSpecParamConfigInfoResponse newResponse) {
        try {
            GetSpecParamConfigInfoResponse.Result newResult = newResponse.getResult();
            NewSpecCompareResponse.Result.Builder oldResult = NewSpecCompareResponse.Result.newBuilder();

            oldResult.setAttentionspecinfo(newResult.getAttentionspecinfo());
            oldResult.addAllConditionlist(newResult.getConditionlistList());
            oldResult.setCpsinfo(newResult.getCpsinfo());
            oldResult.addAllMustseelist(newResult.getMustseelistList());
            oldResult.setSeriesids(newResult.getSeriesids());
            oldResult.setToolboxentry(newResult.getToolboxentry());
            oldResult.setFootaskpriceinfo(newResult.getFootaskpriceinfo());

            if(newResult.getDatalistList() != null && newResult.getDatalistCount() > 0){
                NewSpecCompareResponse.Result.Specinfo.Builder specinfo = NewSpecCompareResponse.Result.Specinfo.newBuilder();
                List<Specitem> specitems = newResult.getDatalistList().stream().map(GetSpecParamConfigInfoResponse.Result.Datalist::getSpecinfo).collect(Collectors.toList());
                specinfo.addAllSpecitems(specitems);
                oldResult.setSpecinfo(specinfo);

                List<Paramitem> newaramitems = newResult.getDatalistList().get(0).getParamitemsList();
                newaramitems.forEach(newparamitem -> {
                    Paramitem.Builder oldparamitem = Paramitem.newBuilder();
                    oldparamitem.setItemtype(newparamitem.getItemtype());
                    oldparamitem.setShowtips(newparamitem.getShowtips());
                    oldparamitem.setGroupname(newparamitem.getGroupname());
                    newparamitem.getItemsList().forEach(newitem->{
                        Item.Builder olditem = Item.newBuilder();
                        olditem.setSubid(newitem.getSubid());
                        olditem.setDatatype(newitem.getDatatype());
                        olditem.setContentid(newitem.getContentid());
                        olditem.setName(newitem.getName());
                        olditem.setParamitemid(newitem.getParamitemid());
                        olditem.setVideoid(newitem.getVideoid());
                        olditem.setId(newitem.getId());
                        olditem.setLinkurl(newitem.getLinkurl());
                        olditem.setPlaystarttime(newitem.getPlaystarttime());
                        newResult.getDatalistList().forEach(data -> {
                            data.getParamitemsList().stream().filter(x->x.getGroupname().equals(newparamitem.getGroupname()) && x.getItemtype().equals(newparamitem.getItemtype())).findFirst().ifPresent(x->{
                                x.getItemsList().stream().filter(y->y.getParamitemid() == newitem.getParamitemid() && y.getName().equals(newitem.getName())).findFirst().ifPresent(y->{
                                    if(y.getModelexcessidsCount() > 0){
                                        olditem.addModelexcessids(y.getModelexcessids(0));
                                    }
                                });
                            });
                        });
                        oldparamitem.addItems(olditem);
                    });
                    oldResult.addParamitems(oldparamitem);
                });

                List<Configitem> newconfigitems = newResult.getDatalistList().get(0).getConfigitemsList();
                newconfigitems.forEach(newconfigitem -> {
                    Configitem.Builder oldconfigitem = Configitem.newBuilder();
                    oldconfigitem.setItemtype(newconfigitem.getItemtype());
                    oldconfigitem.setShowtips(newconfigitem.getShowtips());
                    oldconfigitem.setGroupname(newconfigitem.getGroupname());
                    newconfigitem.getItemsList().forEach(newitem->{
                        Item.Builder olditem = Item.newBuilder();
                        olditem.setSubid(newitem.getSubid());
                        olditem.setDatatype(newitem.getDatatype());
                        olditem.setContentid(newitem.getContentid());
                        olditem.setName(newitem.getName());
                        olditem.setParamitemid(newitem.getParamitemid());
                        olditem.setVideoid(newitem.getVideoid());
                        olditem.setId(newitem.getId());
                        olditem.setLinkurl(newitem.getLinkurl());
                        olditem.setPlaystarttime(newitem.getPlaystarttime());
                        newResult.getDatalistList().forEach(data -> {
                            data.getConfigitemsList().stream().filter(x->x.getGroupname().equals(newconfigitem.getGroupname()) && x.getItemtype().equals(newconfigitem.getItemtype())).findFirst().ifPresent(x->{
                                x.getItemsList().stream().filter(y->y.getParamitemid() == newitem.getParamitemid() && y.getName().equals(newitem.getName())).findFirst().ifPresent(y->{
                                    if(y.getModelexcessidsCount() > 0){
                                        olditem.addModelexcessids(y.getModelexcessids(0));
                                    }
                                });
                            });
                        });
                        oldconfigitem.addItems(olditem);
                    });
                    oldResult.addConfigitems(oldconfigitem);
                });
            }

            NewSpecCompareResponse.Builder oldResponse = NewSpecCompareResponse.newBuilder();
            oldResponse.setReturnCode(newResponse.getReturnCode());
            oldResponse.setReturnMsg(newResponse.getReturnMsg());
            oldResponse.setResult(oldResult);
            return oldResponse.build();
        } catch (Exception e) {
            log.error("convert new response to old response error", e);
            NewSpecCompareResponse.Builder oldResponse = NewSpecCompareResponse.newBuilder();
            oldResponse.setReturnCode(0);
            oldResponse.setReturnMsg("服务端错误");
            return oldResponse.build();
        }
    }


    @Override
    @GetMapping(value = "/carcfg/config/getConfigShare", produces = "application/json;charset=utf-8")
    public GetConfigShareResponse getConfigShare(GetConfigShareRequest request) {
        try {
            GetConfigShareResponse newResponse = paramConfigService.getConfigShare(request).join();
            return newResponse;
        }catch (Exception e) {
                return null;
            }

    }
}
