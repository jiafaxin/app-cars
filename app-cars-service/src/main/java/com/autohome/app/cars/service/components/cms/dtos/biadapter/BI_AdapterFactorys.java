package com.autohome.app.cars.service.components.cms.dtos.biadapter;

import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_TagModel;
import com.autohome.app.bicard.enums.TagPositionEnums;
import com.autohome.app.cars.apiclient.cms.dtos.SeriesAllTabResult;
import com.autohome.app.cars.apiclient.maindata.dtos.MainDataSeriesSummaryFeedsResult;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.service.components.cms.dtos.MediaTypeEnums;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

public class BI_AdapterFactorys {
	public static BICardItemModel getCardItemModel_SeriesSummary_News_Feed(MediaTypeEnums mediaTypeEnums, MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed seriesSummaryFeed) {
		BICardItemModel biCardItemModel = null;
		if(mediaTypeEnums != null){
			switch (mediaTypeEnums) {
				case PuTongNew:
					biCardItemModel = new BI_NewsAdapter(seriesSummaryFeed).initCardItemModel_SeriesSummary_News_Feed();
					break;
				case TuWen:
					biCardItemModel = new BI_TuWenAdapter(seriesSummaryFeed).initCardItemModel_SeriesSummary_News_Feed();
					break;
				case AH_100:
					biCardItemModel = new BI_AH100Adapter(seriesSummaryFeed).initCardItemModel_SeriesSummary_News_Feed();
					break;
				case YCChangWen:
					biCardItemModel = new BI_YCChangWenAdapter(seriesSummaryFeed).initCardItemModel_SeriesSummary_News_Feed();
					break;
				case NewsTopic:
					biCardItemModel = new BI_NewsTopicAdapter(seriesSummaryFeed).initCardItemModel_SeriesSummary_News_Feed();
					break;
				case YC_CheDan:
					biCardItemModel = new BI_YCCheDanAdapter(seriesSummaryFeed).initCardItemModel_SeriesSummary_News_Feed();
					break;
				case Video:
					biCardItemModel = new BI_VideoAdapter(seriesSummaryFeed).initCardItemModel_SeriesSummary_News_Feed();
					break;
				case YCVideo:
					biCardItemModel = new BI_YCVideoAdapter(seriesSummaryFeed).initCardItemModel_SeriesSummary_News_Feed();
					break;
				case FAST_NEWS:
					biCardItemModel = new BI_FastNewsAdapter(seriesSummaryFeed).initCardItemModel_SeriesSummary_News_Feed();
					break;
				default:
					return null;
			}
			//处理原接口时间格式
			Date publishTime = StringUtils.isNotEmpty(seriesSummaryFeed.getPublish_time()) ? DateUtil.parse(seriesSummaryFeed.getPublish_time(),"yyyy/MM/dd") : new Date();
			if (biCardItemModel == null || biCardItemModel.getCarddata() == null){
				return biCardItemModel;
			}
			biCardItemModel.getCarddata().getCardinfo().getTaginfo().add(new Card_CardInfo_TagModel(DateUtil.format(publishTime,"yyyy-MM-dd"),  TagPositionEnums.Right));
			if(biCardItemModel.getCarddata().getCardtype()==10400){
				biCardItemModel.getExtension().setScheme("autohome://article/videodetail?newsid=" + biCardItemModel.getExtension().getObjinfo().getId() + "&mediatype=" + biCardItemModel.getCarddata().getMediatype());

			}
		}
		return biCardItemModel;
	}

	public static BICardItemModel getCardItemModel_SeriesSummary_News_FeedV2(MediaTypeEnums mediaTypeEnums, SeriesAllTabResult allTabResult) {
		BICardItemModel biCardItemModel = null;
		if(mediaTypeEnums != null){
			switch (mediaTypeEnums) {
				case PuTongNew:
					biCardItemModel = new BI_NewsAdapter(allTabResult).initCardItemModel_SeriesSummary_News_Feed_AllTab();
					break;
				case TuWen:
					biCardItemModel = new BI_TuWenAdapter(allTabResult).initCardItemModel_SeriesSummary_News_Feed_AllTab();
					break;
				case AH_100:
					biCardItemModel = new BI_AH100Adapter(allTabResult).initCardItemModel_SeriesSummary_News_Feed_AllTab();
					break;
				case YCChangWen:
					biCardItemModel = new BI_YCChangWenAdapter(allTabResult).initCardItemModel_SeriesSummary_News_Feed_AllTab();
					break;
				case NewsTopic:
					biCardItemModel = new BI_NewsTopicAdapter(allTabResult).initCardItemModel_SeriesSummary_News_Feed_AllTab();
					break;
				case YC_CheDan:
					biCardItemModel = new BI_YCCheDanAdapter(allTabResult).initCardItemModel_SeriesSummary_News_Feed_AllTab();
					break;
				case Video:
					biCardItemModel = new BI_VideoAdapter(allTabResult).initCardItemModel_SeriesSummary_News_Feed_AllTab();
					break;
				case YCVideo:
					biCardItemModel = new BI_YCVideoAdapter(allTabResult).initCardItemModel_SeriesSummary_News_Feed_AllTab();
					break;
				case FAST_NEWS:
					biCardItemModel = new BI_FastNewsAdapter(allTabResult).initCardItemModel_SeriesSummary_News_Feed_AllTab();
					break;
				default:
					return null;
			}
			//处理原接口时间格式
			Date publishTime = StringUtils.isNotEmpty(allTabResult.getPublishTime()) ? DateUtil.parse(allTabResult.getPublishTime(),"yyyy/MM/dd") : new Date();
			if (biCardItemModel == null || biCardItemModel.getCarddata() == null){
				return biCardItemModel;
			}
			biCardItemModel.getCarddata().getCardinfo().getTaginfo().add(new Card_CardInfo_TagModel(DateUtil.format(publishTime,"yyyy-MM-dd"),  TagPositionEnums.Right));
			if(biCardItemModel.getCarddata().getCardtype()==10400){
				biCardItemModel.getExtension().setScheme("autohome://article/videodetail?newsid=" + biCardItemModel.getExtension().getObjinfo().getId() + "&mediatype=" + biCardItemModel.getCarddata().getMediatype());

			}
		}
		return biCardItemModel;
	}


}
