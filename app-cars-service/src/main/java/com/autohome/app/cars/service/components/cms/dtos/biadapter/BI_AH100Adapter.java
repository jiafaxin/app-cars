package com.autohome.app.cars.service.components.cms.dtos.biadapter;


import com.autohome.app.bicard.cards.BICardModel_10100_TuText;
import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_ImageModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_RelationWordsModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_TagModel;
import com.autohome.app.bicard.entity.data.Card_Data_ObjInfo;
import com.autohome.app.bicard.enums.TagPositionEnums;
import com.autohome.app.bicard.enums.TagStyleTypeEnums;
import com.autohome.app.cars.apiclient.cms.dtos.SeriesAllTabResult;
import com.autohome.app.cars.apiclient.maindata.dtos.MainDataSeriesSummaryFeedsResult;
import com.autohome.app.cars.common.utils.ImageSizeEnum;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.common.utils.SafeParamUtil;
import com.autohome.app.cars.common.utils.SchemeUtils;
import com.autohome.app.cars.service.components.cms.dtos.MediaTypeEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleBackgroundEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleFontColorEnums;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BI_AH100Adapter {

	private MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed;

	private SeriesAllTabResult seriesAllTabResult;

	public BI_AH100Adapter(SeriesAllTabResult seriesAllTabResult) {
		this.seriesAllTabResult = seriesAllTabResult;
	}
	public BI_AH100Adapter(MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed) {
		this.mainDataSeriesSummaryFeed = mainDataSeriesSummaryFeed;
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed(){
		Long id = SafeParamUtil.toSafeLong(mainDataSeriesSummaryFeed.getBiz_id());

		String title = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getSmall_title()) ? mainDataSeriesSummaryFeed.getSmall_title() : mainDataSeriesSummaryFeed.getTitle();

		String scheme = SchemeUtils.getAH100Scheme(id);

		String imgUrl4x3 = ImageUtils.convertImageUrl(mainDataSeriesSummaryFeed.getImg_url(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(imgUrl4x3);

		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		tagModels.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
		String editorName = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getAuthor_name()) ? mainDataSeriesSummaryFeed.getAuthor_name() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}
		String replyCount = SafeParamUtil.convertToWan(mainDataSeriesSummaryFeed.getReply_count()) + "评论";
		tagModels.add(new Card_CardInfo_TagModel(replyCount));


		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

		BICardModel_10100_TuText tuText = new BICardModel_10100_TuText();
		tuText.setData_img(imageModel);
		tuText.setImgicon("");
		tuText.setPlaytime("");
		tuText.setObj_title(title);
		tuText.setObj_mediatypid(MediaTypeEnums.AH_100.getIndex());
		tuText.setObj_id(id);
		tuText.setData_tags(tagModels);
		tuText.setData_feednag(new ArrayList<>());
		tuText.setCard_scheme(scheme);
		tuText.setCard_pvlight("");
		tuText.setCard_pvclick("");
		tuText.setCard_objinfo(objInfo);
		tuText.setRelationword(new Card_CardInfo_RelationWordsModel());
		return  tuText.initBICardItemModel();
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_AllTab(){
		Long id = SafeParamUtil.toSafeLong(seriesAllTabResult.getBizId());

		String title = StringUtils.isNotEmpty(seriesAllTabResult.getTitle()) ? seriesAllTabResult.getTitle() : "";

		String scheme = SchemeUtils.getAH100Scheme(id);

		String imgUrl4x3 = ImageUtils.convertImageUrl(seriesAllTabResult.getImgUrl(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(imgUrl4x3);

		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if (seriesAllTabResult.getType() == 1){
			tagModels.add(new Card_CardInfo_TagModel("置顶", TagStyleFontColorEnums.Orange.getColor(), TagStyleBackgroundEnums.Orange.getColor(), TagPositionEnums.Left));
		}
		tagModels.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
		String editorName = StringUtils.isNotEmpty(seriesAllTabResult.getAuthorName()) ? seriesAllTabResult.getAuthorName() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}

		//todo 评论数待确定
//		String replyCount = SafeParamUtil.convertToWan(seriesAllTabResult.getReply_count()) + "评论";
//		tagModels.add(new Card_CardInfo_TagModel(replyCount));


		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

		BICardModel_10100_TuText tuText = new BICardModel_10100_TuText();
		tuText.setData_img(imageModel);
		tuText.setImgicon("");
		tuText.setPlaytime("");
		tuText.setObj_title(title);
		tuText.setObj_mediatypid(MediaTypeEnums.AH_100.getIndex());
		tuText.setObj_id(id);
		tuText.setData_tags(tagModels);
		tuText.setData_feednag(new ArrayList<>());
		tuText.setCard_scheme(scheme);
		tuText.setCard_pvlight("");
		tuText.setCard_pvclick(seriesAllTabResult.getType()+"");
		tuText.setCard_objinfo(objInfo);
		tuText.setRelationword(new Card_CardInfo_RelationWordsModel());
		return tuText.initBICardItemModel();
	}

}
