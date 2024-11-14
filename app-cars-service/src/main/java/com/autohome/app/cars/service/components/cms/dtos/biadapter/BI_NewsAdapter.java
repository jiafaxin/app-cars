package com.autohome.app.cars.service.components.cms.dtos.biadapter;

import com.autohome.app.bicard.cards.BICardModel_10100_TuText;
import com.autohome.app.bicard.cards.BICardModel_10200_OnlyTu;
import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_ImageModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_RelationWordsModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_TagModel;
import com.autohome.app.bicard.entity.data.Card_Data_ObjInfo;
import com.autohome.app.bicard.enums.TagPositionEnums;
import com.autohome.app.bicard.enums.TagStyleTypeEnums;
import com.autohome.app.cars.apiclient.clubcard.dtos.SBI_RcmDataResult;
import com.autohome.app.cars.apiclient.cms.dtos.STopSeriesNewsResult;
import com.autohome.app.cars.apiclient.cms.dtos.SeriesAllTabResult;
import com.autohome.app.cars.apiclient.maindata.dtos.MainDataSeriesSummaryFeedsResult;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.common.utils.news.BusinessUtil;
import com.autohome.app.cars.common.utils.news.DateHelper;
import com.autohome.app.cars.common.utils.news.DateUtils;
import com.autohome.app.cars.service.components.cms.dtos.MediaTypeEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleBackgroundEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleFontColorEnums;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BI_NewsAdapter {

	private STopSeriesNewsResult topSeriesNews;

	private MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed;

	private SeriesAllTabResult seriesAllTabResult;

	private SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem;

	public BI_NewsAdapter(SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem) {
		this.rcmDataItem = rcmDataItem;
	}

	public BI_NewsAdapter(MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed) {
		this.mainDataSeriesSummaryFeed = mainDataSeriesSummaryFeed;
	}

	public BI_NewsAdapter(STopSeriesNewsResult topSeriesNews) {
		this.topSeriesNews = topSeriesNews;
	}

	public BI_NewsAdapter(SeriesAllTabResult seriesAllTabResult) {
		this.seriesAllTabResult = seriesAllTabResult;
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed(){
		Long id = SafeParamUtil.toSafeLong(mainDataSeriesSummaryFeed.getBiz_id());

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.PuTongNew;

		Date publishTime = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getPublish_time()) ? DateUtil.parse(mainDataSeriesSummaryFeed.getPublish_time(),"yyyy/MM/dd HH:mm:ss") : new Date();
		Date updateTime = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getBiz_update_time()) ? DateUtil.parse(mainDataSeriesSummaryFeed.getBiz_update_time(),"yyyy/MM/dd HH:mm:ss") : publishTime;
		String lastUpdateTimeStr = DateUtil.format(updateTime, "yyyyMMddHHmmss");
		String scheme = SchemeUtils.getArticleDetailScheme(id,0,false,lastUpdateTimeStr,"");

		String title = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getSmall_title()) ? mainDataSeriesSummaryFeed.getSmall_title() : mainDataSeriesSummaryFeed.getTitle();

		String[] images = StringUtils.split(mainDataSeriesSummaryFeed.getMulti_images(),",");
		boolean hasMorePic = images != null && images.length >= 3;
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel();
		List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
		if(hasMorePic){
			for (String image : images) {
				String imgTemp = ImageUtils.convertImageUrl(image, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
				imageModels.add(new Card_CardInfo_ImageModel(imgTemp));
			}
		} else {
			String imgUrl4x3 = ImageUtils.convertImageUrl(mainDataSeriesSummaryFeed.getImg_url(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
			imageModel = new Card_CardInfo_ImageModel(imgUrl4x3);
		}

		//标签集合：
		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if (mainDataSeriesSummaryFeed.getHot_event()==1){
			Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
			hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
			tagModels.add(hotTag);
		}
		if(mainDataSeriesSummaryFeed.getCms_kind() == 1){
			tagModels.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
		}
		//标签2：作者标签
		String editorName = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getAuthor_name()) ? mainDataSeriesSummaryFeed.getAuthor_name() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}
		//标签1：评论标签
		if (!mainDataSeriesSummaryFeed.is_close_comment()) {
			tagModels.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(mainDataSeriesSummaryFeed.getReply_count()) + "评论"));
		}
		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();
		objInfo.setLastupdatetime(lastUpdateTimeStr);

		if(!hasMorePic) {
			BICardModel_10100_TuText tuText = new BICardModel_10100_TuText();
			tuText.setData_img(imageModel);
			tuText.setImgicon("");
			tuText.setPlaytime("");
			tuText.setObj_title(title);
			tuText.setObj_mediatypid(mediaTypeEnums.getIndex());
			tuText.setObj_id(id);
			tuText.setData_tags(tagModels);
			tuText.setData_feednag(new ArrayList<>());
			tuText.setCard_scheme(scheme);
			tuText.setCard_pvlight("");
			tuText.setCard_pvclick("");
			tuText.setCard_objinfo(objInfo);
			tuText.setRelationword(new Card_CardInfo_RelationWordsModel());
			return  tuText.initBICardItemModel();
		} else {
			BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
			onlyTu.setData_imgs(imageModels);
			onlyTu.setCard_scheme(scheme);
			onlyTu.setObj_title(title);
			onlyTu.setObj_mediatypid(mediaTypeEnums.getIndex());
			onlyTu.setObj_id(id);
			onlyTu.setData_tags(tagModels);
			onlyTu.setData_feednag(new ArrayList<>());
			onlyTu.setCard_pvlight("");
			onlyTu.setCard_pvclick("");
			onlyTu.setCard_objinfo(objInfo);
			onlyTu.setRelationword(new Card_CardInfo_RelationWordsModel());
			return  onlyTu.initBICardItemModel();
		}
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_Top(){
		Long id = SafeParamUtil.toSafeLong(topSeriesNews.getId());

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.PuTongNew;

		Date publishTime = StringUtils.isNotEmpty(topSeriesNews.getPublishtime()) ? DateUtil.parseDateFromWithDateStr(topSeriesNews.getPublishtime()) : new Date();
		Date updateTime = StringUtils.isNotEmpty(topSeriesNews.getLastupdatetime()) ? DateUtil.parseDateFromWithDateStr(topSeriesNews.getLastupdatetime()) : publishTime;
		if(updateTime==null){
			updateTime = publishTime;
		}
		String lastUpdateTimeStr = DateUtil.format(updateTime, "yyyyMMddHHmmss");
		String scheme = SchemeUtils.getArticleDetailScheme(id,0,false,lastUpdateTimeStr,"");

		String title = StringUtils.isNotEmpty(topSeriesNews.getTitle2()) ? topSeriesNews.getTitle2() : topSeriesNews.getTitle();

		boolean hasMorePic = StringUtils.isNotEmpty(topSeriesNews.getFirstappcoverimg()) && StringUtils.isNotEmpty(topSeriesNews.getSecondappcoverimg()) && StringUtils.isNotEmpty(topSeriesNews.getThirdappcoverimg());
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel();
		List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
		if(hasMorePic){
			String imgTemp1 = ImageUtils.convertImageUrl(topSeriesNews.getFirstappcoverimg(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
			imageModels.add(new Card_CardInfo_ImageModel(imgTemp1));
			String imgTemp2 = ImageUtils.convertImageUrl(topSeriesNews.getSecondappcoverimg(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
			imageModels.add(new Card_CardInfo_ImageModel(imgTemp2));
			String imgTemp3 = ImageUtils.convertImageUrl(topSeriesNews.getThirdappcoverimg(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
			imageModels.add(new Card_CardInfo_ImageModel(imgTemp3));

		} else {
			String imgUrl4x3 = ImageUtils.convertImageUrl(topSeriesNews.getImg(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
			imageModel = new Card_CardInfo_ImageModel(imgUrl4x3);
		}

		//标签集合：
		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if(topSeriesNews.getKind() == 1){
			tagModels.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
		}
		//标签2：作者标签
		String editorName = StringUtils.isNotEmpty(topSeriesNews.getEditorname()) ? topSeriesNews.getEditorname() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}
		//标签1：评论标签
		if (!topSeriesNews.isIsclosecomment() ) {
			tagModels.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(topSeriesNews.getReplycount()) + "评论"));
		}

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();
		objInfo.setLastupdatetime(lastUpdateTimeStr);

		if(!hasMorePic) {
			BICardModel_10100_TuText tuText = new BICardModel_10100_TuText();
			tuText.setData_img(imageModel);
			tuText.setImgicon("");
			tuText.setPlaytime("");
			tuText.setObj_title(title);
			tuText.setObj_mediatypid(mediaTypeEnums.getIndex());
			tuText.setObj_id(id);
			tuText.setData_tags(tagModels);
			tuText.setData_feednag(new ArrayList<>());
			tuText.setCard_scheme(scheme);
			tuText.setCard_pvlight("");
			tuText.setCard_pvclick("");
			tuText.setCard_objinfo(objInfo);
			tuText.setRelationword(new Card_CardInfo_RelationWordsModel());
			return  tuText.initBICardItemModel();
		} else {
			BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
			onlyTu.setData_imgs(imageModels);
			onlyTu.setCard_scheme(scheme);
			onlyTu.setObj_title(title);
			onlyTu.setObj_mediatypid(mediaTypeEnums.getIndex());
			onlyTu.setObj_id(id);
			onlyTu.setData_tags(tagModels);
			onlyTu.setData_feednag(new ArrayList<>());
			onlyTu.setCard_pvlight("");
			onlyTu.setCard_pvclick("");
			onlyTu.setCard_objinfo(objInfo);
			onlyTu.setRelationword(new Card_CardInfo_RelationWordsModel());
			return  onlyTu.initBICardItemModel();
		}
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_AllTab(){
		Long id = SafeParamUtil.toSafeLong(seriesAllTabResult.getBizId());

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.PuTongNew;

		Date publishTime = StringUtils.isNotEmpty(seriesAllTabResult.getPublishTime()) ? DateUtil.parse(seriesAllTabResult.getPublishTime(),"yyyy/MM/dd HH:mm:ss") : new Date();
		Date updateTime = StringUtils.isNotEmpty(seriesAllTabResult.getUpdateTime()) ? DateUtil.parse(seriesAllTabResult.getUpdateTime(),"yyyy/MM/dd HH:mm:ss") : publishTime;
		String lastUpdateTimeStr = DateUtil.format(updateTime, "yyyyMMddHHmmss");
		String scheme = SchemeUtils.getArticleDetailScheme(id,0,false,lastUpdateTimeStr,"");

		String title = StringUtils.isNotEmpty(seriesAllTabResult.getTitle()) ? seriesAllTabResult.getTitle() : "";

		String multiImages = String.join(",", seriesAllTabResult.getMultiImages());
		String[] images = StringUtils.split(multiImages,",");
		boolean hasMorePic = images != null && images.length >= 3;
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel();
		List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
		if(hasMorePic){
			for (String image : images) {
				String imgTemp = ImageUtils.convertImageUrl(image, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
				imageModels.add(new Card_CardInfo_ImageModel(imgTemp));
			}
		} else {
			String imgUrl4x3 = ImageUtils.convertImageUrl(seriesAllTabResult.getImgUrl(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
			imageModel = new Card_CardInfo_ImageModel(imgUrl4x3);
		}

		//标签集合：
		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if (seriesAllTabResult.getType() == 1){
			tagModels.add(new Card_CardInfo_TagModel("置顶", TagStyleFontColorEnums.Orange.getColor(), TagStyleBackgroundEnums.Orange.getColor(), TagPositionEnums.Left));
		}
		if (seriesAllTabResult.getType() == 2){
			Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
			hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
			tagModels.add(hotTag);
		}
		//之家原创
//		if(seriesAllTabResult.getCms_kind() == 1){
//			tagModels.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
//		}
		//标签2：作者标签
		String editorName = StringUtils.isNotEmpty(seriesAllTabResult.getAuthorName()) ? seriesAllTabResult.getAuthorName() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();
		objInfo.setLastupdatetime(lastUpdateTimeStr);

		if(!hasMorePic) {
			BICardModel_10100_TuText tuText = new BICardModel_10100_TuText();
			tuText.setData_img(imageModel);
			tuText.setImgicon("");
			tuText.setPlaytime("");
			tuText.setObj_title(title);
			tuText.setObj_mediatypid(mediaTypeEnums.getIndex());
			tuText.setObj_id(id);
			tuText.setData_tags(tagModels);
			tuText.setData_feednag(new ArrayList<>());
			tuText.setCard_scheme(scheme);
			tuText.setCard_pvlight("");
			tuText.setCard_pvclick(seriesAllTabResult.getType()+"");
			tuText.setCard_objinfo(objInfo);
			tuText.setRelationword(new Card_CardInfo_RelationWordsModel());
			return  tuText.initBICardItemModel();
		} else {
			BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
			onlyTu.setData_imgs(imageModels);
			onlyTu.setCard_scheme(scheme);
			onlyTu.setObj_title(title);
			onlyTu.setObj_mediatypid(mediaTypeEnums.getIndex());
			onlyTu.setObj_id(id);
			onlyTu.setData_tags(tagModels);
			onlyTu.setData_feednag(new ArrayList<>());
			onlyTu.setCard_pvlight("");
			onlyTu.setCard_pvclick(seriesAllTabResult.getType()+"");
			onlyTu.setCard_objinfo(objInfo);
			onlyTu.setRelationword(new Card_CardInfo_RelationWordsModel());
			return  onlyTu.initBICardItemModel();
		}
	}

	public BICardItemModel initCardItemModel_SeriesSummary_Rcm_Feed() {
		//源接口必要节点校验
		if (rcmDataItem == null || rcmDataItem.getResourceobj() == null
				|| rcmDataItem.getResourceobj().getShow() == null) {
			return null;
		}

		SBI_RcmDataResult.SBI_RcmData_Item_ResourceObj resourceObj = rcmDataItem.getResourceobj();
		SBI_RcmDataResult.SBI_RcmData_Item_ResourceObj_Show resourceObjShow = resourceObj.getShow();
		SBI_RcmDataResult.SBI_RcmData_Item_ResourceObj_Base resourceObjBase = resourceObj.getBase();
		SBI_RcmDataResult.SBI_RcmData_Item_ResourceObj_Hot resourceObjHot = resourceObj.getHot();

		//卡片-id
		Long objId = resourceObj.getBiz_id();

		//卡片-标题
		String title = StringUtils.isNotEmpty(resourceObjShow.getTitle()) ? resourceObjShow.getTitle() : "";

		//卡片-图片
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel();
		List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
		boolean hasMorePic = !CollectionUtils.isEmpty(resourceObjShow.getGraphic_img_list3()) && resourceObjShow.getGraphic_img_list3().size() >= 3;
		if (hasMorePic) {
			for (String imgUrl : resourceObjShow.getGraphic_img_list3()) {
				imageModels.add(new Card_CardInfo_ImageModel(ImageUtils.convertImageUrl(imgUrl,true,true,true,ImageSizeEnum.ImgSize_4x3_400x300)));
			}
		} else {
			String originalImg = StringUtils.isNotEmpty(resourceObjShow.getImg_url()) ? resourceObjShow.getImg_url() : "";
			imageModel = new Card_CardInfo_ImageModel(ImageUtils.convertImageUrl(originalImg,true,true,true,ImageSizeEnum.ImgSize_4x3_400x300));
		}

		//卡片-底部标签位
		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if (rcmDataItem.isTop()) {
			Card_CardInfo_TagModel topTag = new Card_CardInfo_TagModel("置顶", TagPositionEnums.Left);
			topTag.setStyletype(TagStyleTypeEnums.Red.getIndex());
			topTag.setFontcolor(TagStyleFontColorEnums.Orange.getColor());
			topTag.setBgcolor(TagStyleBackgroundEnums.Orange.getColor());
			tagModels.add(topTag);
		} else {
			//标签1-之家精选
			tagModels.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
		}
		//标签2-作者昵称
		String authorName = StringUtils.isNotEmpty(resourceObjShow.getAuthor()) ? resourceObjShow.getAuthor() : "";
		if (StringUtils.isNotEmpty(authorName)) {
			tagModels.add(new Card_CardInfo_TagModel(authorName, "", TagStyleTypeEnums.NoStyle, TagPositionEnums.Center));
		}
		//标签3-评论
		if (SafeParamUtil.toSafeInt(resourceObjBase.getIs_close_comment(),0) == 0) {
			int replyCount = resourceObjHot != null ? SafeParamUtil.toSafeInt(resourceObjHot.getReply(),0) : 0;
			tagModels.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(replyCount) + "评论"));
		}
		//标签4：发布时间标签
		Date publishTime = DateUtils.parseDateFromWithUTCDateStr(resourceObjShow.getPublish_time());
		Date updateTime = StringUtils.isNotEmpty(resourceObjBase.getUpdate_at()) ? DateUtils.parseDateFromWithUTCDateStr(resourceObjBase.getUpdate_at()) : publishTime;
		String lastUpdateTime = DateUtils.formatDate(updateTime, "yyyyMMddHHmmss");
		String timeline = publishTime != null ? DateHelper.formatPublishTime(publishTime) : "";
		if (StringUtils.isNotEmpty(timeline)) {
			tagModels.add(new Card_CardInfo_TagModel(timeline, TagPositionEnums.Right));
		}

		//卡片-媒体类型
		MediaTypeEnums mediaTypeEnum = MediaTypeEnums.PuTongNew;

		//卡片-承载的业务信息节点
		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();
		objInfo.setLastupdatetime(lastUpdateTime);

		//pv信息
		String pvInfo = "";
		String ext = "";
		if (rcmDataItem.getPvobj() != null) {
			pvInfo = StringUtils.isNotEmpty(rcmDataItem.getPvobj().getPvinfo()) ? rcmDataItem.getPvobj().getPvinfo() : "";
			ext = StringUtils.isNotEmpty(rcmDataItem.getPvobj().getExt()) ? rcmDataItem.getPvobj().getExt() : "";
		}

		//卡片-跳转协议
		String scheme = SchemeUtils.getArticleDetailScheme(objId,0,false,lastUpdateTime,rcmDataItem.getPvid());
		scheme = BusinessUtil.appendExtToScheme(scheme,ext);

		if (hasMorePic) {
			BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
			onlyTu.setData_imgs(imageModels);
			onlyTu.setCard_scheme(scheme);
			onlyTu.setObj_title(title);
			onlyTu.setObj_mediatypid(mediaTypeEnum.getIndex());
			onlyTu.setObj_id(objId);
			onlyTu.setData_tags(tagModels);
			onlyTu.setData_feednag(new ArrayList<>());
			onlyTu.setCard_pvlight(pvInfo);
			onlyTu.setCard_pvclick(pvInfo);
			onlyTu.setCard_objinfo(objInfo);
			onlyTu.setRelationword(new Card_CardInfo_RelationWordsModel());
			return  onlyTu.initBICardItemModel();
		} else {
			BICardModel_10100_TuText tuText = new BICardModel_10100_TuText();
			tuText.setData_img(imageModel);
			tuText.setObj_title(title);
			tuText.setObj_mediatypid(mediaTypeEnum.getIndex());
			tuText.setObj_id(objId);
			tuText.setData_tags(tagModels);
			tuText.setData_feednag(new ArrayList<>());
			tuText.setCard_scheme(scheme);
			tuText.setCard_pvlight(pvInfo);
			tuText.setCard_pvclick(pvInfo);
			tuText.setCard_objinfo(objInfo);
			tuText.setRelationword(new Card_CardInfo_RelationWordsModel());
			return  tuText.initBICardItemModel();
		}
	}
}
