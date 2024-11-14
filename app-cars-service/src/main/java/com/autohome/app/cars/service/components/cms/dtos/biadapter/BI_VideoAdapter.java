package com.autohome.app.cars.service.components.cms.dtos.biadapter;

import com.autohome.app.bicard.cards.BICardModel_10400_VideoBigPic;
import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_ImageModel;
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
import com.autohome.app.cars.common.utils.news.SchemeUtil;
import com.autohome.app.cars.service.components.cms.dtos.MediaTypeEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleBackgroundEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleFontColorEnums;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BI_VideoAdapter {

	private STopSeriesNewsResult topSeriesNews;

	private MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed;

	private SeriesAllTabResult seriesAllTabResult;

	private SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem;

	public BI_VideoAdapter(SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem) {
		this.rcmDataItem = rcmDataItem;
	}

	public BI_VideoAdapter(SeriesAllTabResult seriesAllTabResult) {
		this.seriesAllTabResult = seriesAllTabResult;
	}

	public BI_VideoAdapter(MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed) {
		this.mainDataSeriesSummaryFeed = mainDataSeriesSummaryFeed;
	}

	public BI_VideoAdapter(STopSeriesNewsResult topSeriesNews) {
		this.topSeriesNews = topSeriesNews;
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed(){
		//卡片-id
		Long objId = Long.valueOf(mainDataSeriesSummaryFeed.getBiz_id());

		//卡片-标题
		String title = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getTitle()) ? mainDataSeriesSummaryFeed.getTitle() : "";

		//卡片-图片
		String imgUrl = ImageUtils.convertImageUrl(mainDataSeriesSummaryFeed.getImg_url(), true, false, false, ImageSizeEnum.ImgSize_16x9_640x360);
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(imgUrl);

		//卡片-底部标签位
		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if (mainDataSeriesSummaryFeed.getHot_event()==1){
			Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
			hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
			tagModels.add(hotTag);
		}
		//标签位-作者名
		String editorName = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getAuthor_name()) ? mainDataSeriesSummaryFeed.getAuthor_name() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}
		int playCount = SafeParamUtil.toSafeInt(mainDataSeriesSummaryFeed.getPv(),0);
		tagModels.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(playCount) + "播放"));


		//卡片属性-视频播放时长
		int duration = SafeParamUtil.toSafeInt(Long.valueOf(mainDataSeriesSummaryFeed.getDuration()).intValue(),0);
		String playTime = DateUtil.convertDuration(duration);

		//卡片属性-视频源id
		String videoId = SafeParamUtil.toSafeString(mainDataSeriesSummaryFeed.getVideo_source());

		//卡片-跳转协议
		String scheme = SchemeUtils.getVideoDetailScheme(objId, false, "", "",videoId,1, playCount);

		//卡片-媒体类型
		MediaTypeEnums mediaTypeEnum = MediaTypeEnums.Video;

		//卡片-承载的业务信息节点
		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();
		objInfo.setSeriesids("");
		objInfo.setPlaycount(SafeParamUtil.convertToWan(playCount));
		objInfo.setPvcontent("");


		BICardModel_10400_VideoBigPic videoBigPic = new BICardModel_10400_VideoBigPic();
		videoBigPic.setData_img(imageModel);
		videoBigPic.setImgicon("");
		videoBigPic.setJumptype(0);
		videoBigPic.setPlaytime(playTime);
		videoBigPic.setVideoid(videoId);
		videoBigPic.setVideourl("");
		videoBigPic.setCard_objinfo(objInfo);
		videoBigPic.setCard_pvclick("");
		videoBigPic.setCard_pvlight("");
		videoBigPic.setCard_scheme(scheme);
		videoBigPic.setData_feednag(new ArrayList<>());
		videoBigPic.setData_tags(tagModels);
		videoBigPic.setObj_id(objId);
		videoBigPic.setObj_mediatypid(mediaTypeEnum.getIndex());
		videoBigPic.setObj_title(title);
		return videoBigPic.initBICardItemModel();
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_Top(){
		//卡片-id
		Long objId = Long.valueOf(topSeriesNews.getId());

		//卡片-标题
		String title = StringUtils.isNotEmpty(topSeriesNews.getTitle2()) ? topSeriesNews.getTitle2() : topSeriesNews.getTitle();

		//卡片-图片
		String imgUrl = ImageUtils.convertImageUrl(topSeriesNews.getImg(), true, false, false, ImageSizeEnum.ImgSize_16x9_640x360);
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(imgUrl);

		//卡片-底部标签位
		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		//标签位-作者名
		String editorName = StringUtils.isNotEmpty(topSeriesNews.getEditorname()) ? topSeriesNews.getEditorname() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}
		int playCount = SafeParamUtil.toSafeInt(topSeriesNews.getClickcount(),0);
		tagModels.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(playCount) + "播放"));


		//卡片属性-视频播放时长
		int duration = SafeParamUtil.toSafeInt(topSeriesNews.getDuration(),0);
		String playTime = DateUtil.convertDuration(duration);

		//卡片属性-视频源id
		String videoId = SafeParamUtil.toSafeString(topSeriesNews.getVideoid());

		//卡片-跳转协议
		String scheme = SchemeUtils.getVideoDetailScheme(objId, false, "", "",videoId,1, playCount);

		//卡片-媒体类型
		MediaTypeEnums mediaTypeEnum = MediaTypeEnums.Video;

		//卡片-承载的业务信息节点
		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();
		objInfo.setSeriesids(StringUtils.isNotEmpty(topSeriesNews.getSeriesids()) ? topSeriesNews.getSeriesids() : "");
		objInfo.setPlaycount(SafeParamUtil.convertToWan(playCount));
		objInfo.setPvcontent("");


		BICardModel_10400_VideoBigPic videoBigPic = new BICardModel_10400_VideoBigPic();
		videoBigPic.setData_img(imageModel);
		videoBigPic.setImgicon("");
		videoBigPic.setJumptype(0);
		videoBigPic.setPlaytime(playTime);
		videoBigPic.setVideoid(videoId);
		videoBigPic.setVideourl("");
		videoBigPic.setCard_objinfo(objInfo);
		videoBigPic.setCard_pvclick("");
		videoBigPic.setCard_pvlight("");
		videoBigPic.setCard_scheme(scheme);
		videoBigPic.setData_feednag(new ArrayList<>());
		videoBigPic.setData_tags(tagModels);
		videoBigPic.setObj_id(objId);
		videoBigPic.setObj_mediatypid(mediaTypeEnum.getIndex());
		videoBigPic.setObj_title(title);
		return videoBigPic.initBICardItemModel();
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_AllTab(){
		//卡片-id
		Long objId = Long.valueOf(seriesAllTabResult.getBizId());

		//卡片-标题
		String title = StringUtils.isNotEmpty(seriesAllTabResult.getTitle()) ? seriesAllTabResult.getTitle() : "";

		//卡片-图片
		String imgUrl = ImageUtils.convertImageUrl(seriesAllTabResult.getImgUrl(), true, false, false, ImageSizeEnum.ImgSize_16x9_640x360);
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(imgUrl);

		//卡片-底部标签位
		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if (seriesAllTabResult.getType() == 1){
			tagModels.add(new Card_CardInfo_TagModel("置顶", TagStyleFontColorEnums.Orange.getColor(), TagStyleBackgroundEnums.Orange.getColor(), TagPositionEnums.Left));
		}
		if (seriesAllTabResult.getType() == 2){
			Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
			hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
			tagModels.add(hotTag);
		}
		//标签位-作者名
		String editorName = StringUtils.isNotEmpty(seriesAllTabResult.getAuthorName()) ? seriesAllTabResult.getAuthorName() : "";
		if(StringUtils.isNotEmpty(editorName)) {
			tagModels.add(new Card_CardInfo_TagModel(editorName));
		}
		//TODO 待定
//		int playCount = SafeParamUtil.toSafeInt(seriesAllTabResult.getPv(),0);
//		tagModels.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(playCount) + "播放"));


		//卡片属性-视频播放时长
		int duration = SafeParamUtil.toSafeInt(Long.valueOf(seriesAllTabResult.getDuration()).intValue(),0);
		String playTime = DateUtil.convertDuration(duration);

		//卡片属性-视频源id
		String videoId = SafeParamUtil.toSafeString(seriesAllTabResult.getVideoSource());

		//卡片-跳转协议 todo 播放数待定
//		String scheme = SchemeUtils.getVideoDetailScheme(objId, false, "", "",videoId,1, playCount);
		String scheme = SchemeUtils.getVideoDetailScheme(objId, false, "", "",videoId,1);

		//卡片-媒体类型
		MediaTypeEnums mediaTypeEnum = MediaTypeEnums.Video;

		//卡片-承载的业务信息节点
		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();
		objInfo.setSeriesids("");
//		objInfo.setPlaycount(SafeParamUtil.convertToWan(playCount));todo 待定
		objInfo.setPvcontent("");


		BICardModel_10400_VideoBigPic videoBigPic = new BICardModel_10400_VideoBigPic();
		videoBigPic.setData_img(imageModel);
		videoBigPic.setImgicon("");
		videoBigPic.setJumptype(0);
		videoBigPic.setPlaytime(playTime);
		videoBigPic.setVideoid(videoId);
		videoBigPic.setVideourl("");
		videoBigPic.setCard_objinfo(objInfo);
		videoBigPic.setCard_pvclick(seriesAllTabResult.getType()+"");
		videoBigPic.setCard_pvlight("");
		videoBigPic.setCard_scheme(scheme);
		videoBigPic.setData_feednag(new ArrayList<>());
		videoBigPic.setData_tags(tagModels);
		videoBigPic.setObj_id(objId);
		videoBigPic.setObj_mediatypid(mediaTypeEnum.getIndex());
		videoBigPic.setObj_title(title);
		return videoBigPic.initBICardItemModel();
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
		String originalImg = StringUtils.isNotEmpty(resourceObjShow.getImg_url2()) ? resourceObjShow.getImg_url2() : resourceObjShow.getImg_url();
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(ImageUtils.convertImageUrl(originalImg,true,true,true,ImageSizeEnum.ImgSize_16x9_1040x585));

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
			tagModels.add(new Card_CardInfo_TagModel(authorName));
		}
		//标签3-播放数
		int playCount = resourceObjHot != null ? SafeParamUtil.toSafeInt(resourceObjHot.getPlay(),0) : 0;
		tagModels.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(playCount) + "播放"));

		//标签4：发布时间标签
		Date publishTime = DateUtils.parseDateFromWithUTCDateStr(resourceObjShow.getPublish_time());
		String timeline = publishTime != null ? DateHelper.formatPublishTime(publishTime) : "";
		if (StringUtils.isNotEmpty(timeline)) {
			tagModels.add(new Card_CardInfo_TagModel(timeline, TagPositionEnums.Right));
		}

		//卡片-媒体类型
		MediaTypeEnums mediaTypeEnum = MediaTypeEnums.Video;

		//卡片-视频播放时长
		String playTime = SafeParamUtil.convertVideoPlayTime(SafeParamUtil.toSafeInt(resourceObj.getOther().getDuration()));

		//卡片-视频源id
		String videoId = StringUtils.isNotEmpty(resourceObjBase.getV_id()) ? resourceObjBase.getV_id() : "";

		//卡片-承载的业务信息节点
		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();
		String seriesIds = "";
		if (CollectionUtils.isNotEmpty(resourceObjBase.getCms_series_ids())) {
			seriesIds = StringUtils.join(resourceObjBase.getCms_series_ids(),",");
		}
		objInfo.setSeriesids(seriesIds);
		objInfo.setVideoid(videoId);

		//pv信息
		String pvInfo = "";
		String ext = "";
		if (rcmDataItem.getPvobj() != null) {
			pvInfo = StringUtils.isNotEmpty(rcmDataItem.getPvobj().getPvinfo()) ? rcmDataItem.getPvobj().getPvinfo() : "";
			ext = StringUtils.isNotEmpty(rcmDataItem.getPvobj().getExt()) ? rcmDataItem.getPvobj().getExt() : "";
		}

		//卡片-跳转协议
		String scheme = SchemeUtil.getNewsVideoFinalPageScheme(objId.intValue(),0,mediaTypeEnum.getIndex(),seriesIds,videoId,0,1,0,"");
		scheme = BusinessUtil.appendExtToScheme(scheme,ext);

		BICardModel_10400_VideoBigPic videoBigPic = new BICardModel_10400_VideoBigPic();
		videoBigPic.setData_img(imageModel);
		videoBigPic.setImgicon("");
		videoBigPic.setPlaytime(playTime);
		videoBigPic.setVideoid(videoId);
		videoBigPic.setVideourl("");
		videoBigPic.setCard_objinfo(objInfo);
		videoBigPic.setCard_pvclick(pvInfo);
		videoBigPic.setCard_pvlight(pvInfo);
		videoBigPic.setCard_scheme(scheme);
		videoBigPic.setData_feednag(new ArrayList<>());
		videoBigPic.setData_tags(tagModels);
		videoBigPic.setObj_id(objId);
		videoBigPic.setObj_mediatypid(mediaTypeEnum.getIndex());
		videoBigPic.setObj_title(title);
		return videoBigPic.initBICardItemModel();
	}
}
