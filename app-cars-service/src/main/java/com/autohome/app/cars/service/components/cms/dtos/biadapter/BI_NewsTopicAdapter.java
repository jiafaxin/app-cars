package com.autohome.app.cars.service.components.cms.dtos.biadapter;


import com.autohome.app.bicard.cards.BICardModel_10100_TuText;
import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_ImageModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_TagModel;
import com.autohome.app.bicard.entity.data.Card_Data_ObjInfo;
import com.autohome.app.bicard.enums.TagPositionEnums;
import com.autohome.app.bicard.enums.TagStyleTypeEnums;
import com.autohome.app.cars.apiclient.cms.dtos.SeriesAllTabResult;
import com.autohome.app.cars.apiclient.maindata.dtos.MainDataSeriesSummaryFeedsResult;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.service.components.cms.dtos.MediaTypeEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleBackgroundEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleFontColorEnums;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BI_NewsTopicAdapter {


	private MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed;


	private SeriesAllTabResult seriesAllTabResult;

	public BI_NewsTopicAdapter(SeriesAllTabResult seriesAllTabResult) {
		this.seriesAllTabResult = seriesAllTabResult;
	}
	public BI_NewsTopicAdapter(MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed) {
		this.mainDataSeriesSummaryFeed = mainDataSeriesSummaryFeed;
	}



	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed(){
		Long objId = SafeParamUtil.toSafeLong(mainDataSeriesSummaryFeed.getBiz_id());

		Date publishTime = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getPublish_time()) ? DateUtil.parse(mainDataSeriesSummaryFeed.getPublish_time(),"yyyy/MM/dd HH:mm:ss") : new Date();
		Date updateTime = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getBiz_update_time()) ? DateUtil.parse(mainDataSeriesSummaryFeed.getBiz_update_time(),"yyyy/MM/dd HH:mm:ss") : publishTime;
		String scheme = SchemeUtils.getNewsTopicScheme(objId,0,false, false,DateUtil.format(updateTime,"yyyyMMddHHmmss"),"");

		String title = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getTitle()) ? mainDataSeriesSummaryFeed.getTitle() : "";

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.NewsTopic;

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

		String pvinfo = "";

		List<Card_CardInfo_TagModel>  tagModels = new ArrayList<>();
		tagModels.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
		tagModels.add(new Card_CardInfo_TagModel("话题", "", TagStyleTypeEnums.Bule, TagPositionEnums.Left));
		String joinCount = SafeParamUtil.convertToWan(SafeParamUtil.toSafeInt(mainDataSeriesSummaryFeed.getPv())) + "人参与";
		tagModels.add(new Card_CardInfo_TagModel(joinCount));
		String editorName = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getAuthor_name()) ? mainDataSeriesSummaryFeed.getAuthor_name() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}

		String imgUrl4x3 = ImageUtils.convertImageUrl(mainDataSeriesSummaryFeed.getImg_url(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(imgUrl4x3);

		BICardModel_10100_TuText tuText = new BICardModel_10100_TuText();
		tuText.setData_img(imageModel);
		tuText.setImgicon("");
		tuText.setPlaytime("");
		tuText.setCard_objinfo(objInfo);
		tuText.setCard_pvclick(pvinfo);
		tuText.setCard_pvlight(pvinfo);
		tuText.setCard_scheme(scheme);
		tuText.setData_feednag(new ArrayList<>());
		tuText.setData_tags(tagModels);
		tuText.setObj_id(objId);
		tuText.setObj_mediatypid(mediaTypeEnums.getIndex());
		tuText.setObj_title(title);

		return tuText.initBICardItemModel();
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_AllTab(){
		Long objId = SafeParamUtil.toSafeLong(seriesAllTabResult.getBizId());

		Date publishTime = StringUtils.isNotEmpty(seriesAllTabResult.getPublishTime()) ? DateUtil.parse(seriesAllTabResult.getPublishTime(),"yyyy/MM/dd HH:mm:ss") : new Date();
		Date updateTime = StringUtils.isNotEmpty(seriesAllTabResult.getUpdateTime()) ? DateUtil.parse(seriesAllTabResult.getUpdateTime(),"yyyy/MM/dd HH:mm:ss") : publishTime;
		String scheme = SchemeUtils.getNewsTopicScheme(objId,0,false, false,DateUtil.format(updateTime,"yyyyMMddHHmmss"),"");

		String title = StringUtils.isNotEmpty(seriesAllTabResult.getTitle()) ? seriesAllTabResult.getTitle() : "";

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.NewsTopic;

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

		String pvinfo = seriesAllTabResult.getType()+"";

		List<Card_CardInfo_TagModel>  tagModels = new ArrayList<>();
		if (seriesAllTabResult.getType() == 1){
			tagModels.add(new Card_CardInfo_TagModel("置顶", TagStyleFontColorEnums.Orange.getColor(), TagStyleBackgroundEnums.Orange.getColor(), TagPositionEnums.Left));
		}
		tagModels.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
		tagModels.add(new Card_CardInfo_TagModel("话题", "", TagStyleTypeEnums.Bule, TagPositionEnums.Left));
		//todo 待确定
//		String joinCount = SafeParamUtil.convertToWan(SafeParamUtil.toSafeInt(seriesAllTabResult.getPv())) + "人参与";
//		tagModels.add(new Card_CardInfo_TagModel(joinCount));
		String editorName = StringUtils.isNotEmpty(seriesAllTabResult.getAuthorName()) ? seriesAllTabResult.getAuthorName() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}

		String imgUrl4x3 = ImageUtils.convertImageUrl(seriesAllTabResult.getImgUrl(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(imgUrl4x3);

		BICardModel_10100_TuText tuText = new BICardModel_10100_TuText();
		tuText.setData_img(imageModel);
		tuText.setImgicon("");
		tuText.setPlaytime("");
		tuText.setCard_objinfo(objInfo);
		tuText.setCard_pvclick(pvinfo);
		tuText.setCard_pvlight(pvinfo);
		tuText.setCard_scheme(scheme);
		tuText.setData_feednag(new ArrayList<>());
		tuText.setData_tags(tagModels);
		tuText.setObj_id(objId);
		tuText.setObj_mediatypid(mediaTypeEnums.getIndex());
		tuText.setObj_title(title);

		return tuText.initBICardItemModel();
	}

}
