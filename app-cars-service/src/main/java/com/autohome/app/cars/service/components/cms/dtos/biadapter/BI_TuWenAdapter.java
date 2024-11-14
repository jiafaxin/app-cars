package com.autohome.app.cars.service.components.cms.dtos.biadapter;

import com.autohome.app.bicard.cards.BICardModel_10200_OnlyTu;
import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_ImageModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_TagModel;
import com.autohome.app.bicard.entity.data.Card_Data_ObjInfo;
import com.autohome.app.bicard.enums.TagPositionEnums;
import com.autohome.app.bicard.enums.TagStyleTypeEnums;
import com.autohome.app.cars.apiclient.cms.dtos.STopSeriesNewsResult;
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

public class BI_TuWenAdapter {

	private STopSeriesNewsResult topSeriesNews;

	private MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed;

	private SeriesAllTabResult seriesAllTabResult;

	public BI_TuWenAdapter(SeriesAllTabResult seriesAllTabResult) {
		this.seriesAllTabResult = seriesAllTabResult;
	}

	public BI_TuWenAdapter(MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed) {
		this.mainDataSeriesSummaryFeed = mainDataSeriesSummaryFeed;
	}

	public BI_TuWenAdapter(STopSeriesNewsResult topSeriesNews) {
		this.topSeriesNews = topSeriesNews;
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed(){
		Long id = Long.valueOf(mainDataSeriesSummaryFeed.getBiz_id());

		String title = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getSmall_title()) ? mainDataSeriesSummaryFeed.getSmall_title() : mainDataSeriesSummaryFeed.getTitle();

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.TuWen;

		List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
		List<String> pics = new ArrayList<>();
		String[] images = StringUtils.split(mainDataSeriesSummaryFeed.getMulti_images(),",");
		for (String image : images) {
			String imgTemp = ImageUtils.convertImageUrl(image, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
			imageModels.add(new Card_CardInfo_ImageModel(imgTemp));
			pics.add(imgTemp);
		}

		String scheme = SchemeUtils.getTuWenScheme(id, StringUtils.join(pics, "㊣"), "");

		String pvinfo = "";

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if (mainDataSeriesSummaryFeed.getHot_event()==1){
			Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
			hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
			tagModels.add(hotTag);
		}
		tagModels.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
		tagModels.add(new Card_CardInfo_TagModel("图说", "", TagStyleTypeEnums.Bule, TagPositionEnums.Left));
		if(!mainDataSeriesSummaryFeed.is_close_comment()) {
			String replyCount = SafeParamUtil.convertToWan(SafeParamUtil.toSafeInt(mainDataSeriesSummaryFeed.getReply_count())) + "评论";
			tagModels.add(new Card_CardInfo_TagModel(replyCount));
		}
		String editorName = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getAuthor_name()) ? mainDataSeriesSummaryFeed.getAuthor_name() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}

		BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
		onlyTu.setData_imgs(imageModels);
		onlyTu.setCard_objinfo(objInfo);
		onlyTu.setCard_pvclick(pvinfo);
		onlyTu.setCard_pvlight(pvinfo);
		onlyTu.setCard_scheme(scheme);
		onlyTu.setData_feednag(new ArrayList<>());
		onlyTu.setData_tags(tagModels);
		onlyTu.setObj_id(id);
		onlyTu.setObj_mediatypid(mediaTypeEnums.getIndex());
		onlyTu.setObj_title(title);
		return onlyTu.initBICardItemModel();
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_Top(){
		Long id = Long.valueOf(topSeriesNews.getId());

		String title = StringUtils.isNotEmpty(topSeriesNews.getTitle2()) ? topSeriesNews.getTitle2() : topSeriesNews.getTitle();

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.TuWen;

		List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
		List<String> pics = new ArrayList<>();
		String imgTemp1 = ImageUtils.convertImageUrl(topSeriesNews.getFirstcoverimg(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
		imageModels.add(new Card_CardInfo_ImageModel(imgTemp1));
		pics.add(imgTemp1);
		String imgTemp2 = ImageUtils.convertImageUrl(topSeriesNews.getSecondcoverimg(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
		imageModels.add(new Card_CardInfo_ImageModel(imgTemp2));
		pics.add(imgTemp2);
		String imgTemp3 = ImageUtils.convertImageUrl(topSeriesNews.getThirdcoverimg(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
		imageModels.add(new Card_CardInfo_ImageModel(imgTemp3));
		pics.add(imgTemp3);

		String scheme = SchemeUtils.getTuWenScheme(id, StringUtils.join(pics, "㊣"), "");

		String pvinfo = "";

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		tagModels.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
		tagModels.add(new Card_CardInfo_TagModel("图说", "", TagStyleTypeEnums.Bule, TagPositionEnums.Left));
		if(!topSeriesNews.isIsclosecomment()) {
			String replyCount = SafeParamUtil.convertToWan(SafeParamUtil.toSafeInt(topSeriesNews.getReplycount())) + "评论";
			tagModels.add(new Card_CardInfo_TagModel(replyCount));
		}
		String editorName = StringUtils.isNotEmpty(topSeriesNews.getEditorname()) ? topSeriesNews.getEditorname() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}

		BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
		onlyTu.setData_imgs(imageModels);
		onlyTu.setCard_objinfo(objInfo);
		onlyTu.setCard_pvclick(pvinfo);
		onlyTu.setCard_pvlight(pvinfo);
		onlyTu.setCard_scheme(scheme);
		onlyTu.setData_feednag(new ArrayList<>());
		onlyTu.setData_tags(tagModels);
		onlyTu.setObj_id(id);
		onlyTu.setObj_mediatypid(mediaTypeEnums.getIndex());
		onlyTu.setObj_title(title);
		return onlyTu.initBICardItemModel();
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_AllTab(){
		Long id = Long.valueOf(seriesAllTabResult.getBizId());

		String title = StringUtils.isNotEmpty(seriesAllTabResult.getTitle()) ? seriesAllTabResult.getTitle() : "";

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.TuWen;

		List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
		List<String> pics = new ArrayList<>();
		String multiImages = String.join(",", seriesAllTabResult.getMultiImages());
		String[] images = StringUtils.split(multiImages,",");
		for (String image : images) {
			String imgTemp = ImageUtils.convertImageUrl(image, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
			imageModels.add(new Card_CardInfo_ImageModel(imgTemp));
			pics.add(imgTemp);
		}

		String scheme = SchemeUtils.getTuWenScheme(id, StringUtils.join(pics, "㊣"), "");

		String pvinfo = seriesAllTabResult.getType()+"";

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if (seriesAllTabResult.getType() == 1){
			tagModels.add(new Card_CardInfo_TagModel("置顶", TagStyleFontColorEnums.Orange.getColor(), TagStyleBackgroundEnums.Orange.getColor(), TagPositionEnums.Left));
		}
		if (seriesAllTabResult.getType() == 2){
			Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
			hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
			tagModels.add(hotTag);
		}
		tagModels.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
		tagModels.add(new Card_CardInfo_TagModel("图说", "", TagStyleTypeEnums.Bule, TagPositionEnums.Left));

		//TODO 评论
//		if(!seriesAllTabResult.is_close_comment()) {
//			String replyCount = SafeParamUtil.convertToWan(SafeParamUtil.toSafeInt(seriesAllTabResult.getReply_count())) + "评论";
//			tagModels.add(new Card_CardInfo_TagModel(replyCount));
//		}
		String editorName = StringUtils.isNotEmpty(seriesAllTabResult.getAuthorName()) ? seriesAllTabResult.getAuthorName() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}

		BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
		onlyTu.setData_imgs(imageModels);
		onlyTu.setCard_objinfo(objInfo);
		onlyTu.setCard_pvclick(pvinfo);
		onlyTu.setCard_pvlight(pvinfo);
		onlyTu.setCard_scheme(scheme);
		onlyTu.setData_feednag(new ArrayList<>());
		onlyTu.setData_tags(tagModels);
		onlyTu.setObj_id(id);
		onlyTu.setObj_mediatypid(mediaTypeEnums.getIndex());
		onlyTu.setObj_title(title);
		return onlyTu.initBICardItemModel();
	}
}
