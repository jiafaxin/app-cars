package com.autohome.app.cars.service.services;

import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.cars.apiclient.clubcard.dtos.SBI_RcmDataResult;
import com.autohome.app.cars.service.components.cms.dtos.MediaTypeEnums;
import com.autohome.app.cars.service.components.cms.dtos.biadapter.*;
import org.springframework.stereotype.Component;

@Component
public class BI_AdapterFactorys {
    public BICardItemModel buildCardItemModelForSeriesSummaryRcmFeeds(SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem) {
        BICardItemModel biCardItemModel = null;
        MediaTypeEnums mediaTypeEnum = MediaTypeEnums.getByValue(rcmDataItem.getResourceobj().getBiz_type());
        switch (mediaTypeEnum) {
            case PuTongNew:
                biCardItemModel = new BI_NewsAdapter(rcmDataItem).initCardItemModel_SeriesSummary_Rcm_Feed();
                break;
            case Video:
                biCardItemModel = new BI_VideoAdapter(rcmDataItem).initCardItemModel_SeriesSummary_Rcm_Feed();
                break;
            case YCChangWen:
                biCardItemModel = new BI_YCChangWenAdapter(rcmDataItem).initCardItemModel_SeriesSummary_Rcm_Feed();
                break;
            case YCVideo:
                biCardItemModel = new BI_YCVideoAdapter(rcmDataItem).initCardItemModel_SeriesSummary_Rcm_Feed();
                break;
            case FAST_NEWS:
            	biCardItemModel = new BI_FastNewsAdapter(rcmDataItem).initCardItemModel_SeriesSummary_Rcm_Feed();
            	break;
            default:
                break;
        }
        return biCardItemModel;
    }
}
