package com.autohome.app.cars.service.components.cms.dtos.biadapter;

import com.autohome.app.bicard.cards.BICardModel_10400_VideoBigPic;
import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_ImageModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_TagModel;
import com.autohome.app.bicard.entity.data.Card_Data_ObjInfo;
import com.autohome.app.bicard.enums.TagPositionEnums;
import com.autohome.app.bicard.enums.TagStyleTypeEnums;
import com.autohome.app.cars.apiclient.chejiahao.dtos.SSecondCheJiaHaoNewsResult;
import com.autohome.app.cars.apiclient.clubcard.dtos.SBI_RcmDataResult;
import com.autohome.app.cars.apiclient.cms.dtos.SeriesAllTabResult;
import com.autohome.app.cars.apiclient.maindata.dtos.MainDataSeriesSummaryFeedsResult;
import com.autohome.app.cars.common.utils.ImageSizeEnum;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.common.utils.SafeParamUtil;
import com.autohome.app.cars.common.utils.SchemeUtils;
import com.autohome.app.cars.common.utils.news.BusinessUtil;
import com.autohome.app.cars.common.utils.news.DateHelper;
import com.autohome.app.cars.common.utils.news.DateUtils;
import com.autohome.app.cars.common.utils.news.SchemeUtil;
import com.autohome.app.cars.service.components.cms.dtos.MediaTypeEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleBackgroundEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleFontColorEnums;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BI_YCVideoAdapter {
	private MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed;
	private SSecondCheJiaHaoNewsResult secondCheJiaHaoNewsBody;

	private SeriesAllTabResult seriesAllTabResult;

	private SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem;
	public BI_YCVideoAdapter(SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem) {
		this.rcmDataItem = rcmDataItem;
	}
	public BI_YCVideoAdapter(SeriesAllTabResult seriesAllTabResult) {
		this.seriesAllTabResult = seriesAllTabResult;
	}
	public BI_YCVideoAdapter(MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed) {
		this.mainDataSeriesSummaryFeed = mainDataSeriesSummaryFeed;
	}

	public BI_YCVideoAdapter(SSecondCheJiaHaoNewsResult secondCheJiaHaoNewsBody){
		this.secondCheJiaHaoNewsBody = secondCheJiaHaoNewsBody;
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed(){
		Long id = SafeParamUtil.toSafeLong(mainDataSeriesSummaryFeed.getBiz_id());

		String scheme = SchemeUtils.getYC_VideoScheme(id, false, 20033, "");;

		String title = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getTitle()) ? mainDataSeriesSummaryFeed.getTitle() : "";

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.YCVideo;

		String imgUrl = ImageUtils.convertImageUrl(mainDataSeriesSummaryFeed.getImg_url(), true, false, false, ImageSizeEnum.ImgSize_16x9_640x360);
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(imgUrl);

		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if (mainDataSeriesSummaryFeed.getHot_event()==1){
			Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
			hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
			tagModels.add(hotTag);
		}
		String editname = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getAuthor_name()) ? mainDataSeriesSummaryFeed.getAuthor_name() : "";
		if(StringUtils.isNotEmpty(editname)) {
			tagModels.add(new Card_CardInfo_TagModel(editname));
		}
		Integer playCount = SafeParamUtil.toSafeInt(mainDataSeriesSummaryFeed.getPv());
		tagModels.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(playCount) + "播放"));


		String pvinfo = "";

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();
		objInfo.setSeriesids("");
		objInfo.setPlaycount(SafeParamUtil.convertToWan(playCount));
		objInfo.setAuthorname(editname);
		objInfo.setAuthorheadimg("");

		int duration = SafeParamUtil.toSafeInt(Long.valueOf(mainDataSeriesSummaryFeed.getDuration()).intValue(),0);
		String playTime = SafeParamUtil.convertVideoPlayTime(duration);

		String videoId = SafeParamUtil.toSafeString(mainDataSeriesSummaryFeed.getVideo_source());

		BICardModel_10400_VideoBigPic videoBigPic = new BICardModel_10400_VideoBigPic();
		videoBigPic.setObj_title(title);
		videoBigPic.setObj_mediatypid(mediaTypeEnums.getIndex());
		videoBigPic.setObj_id(id);
		videoBigPic.setData_tags(tagModels);
		videoBigPic.setData_feednag(new ArrayList<>());
		videoBigPic.setCard_scheme(scheme);
		videoBigPic.setCard_pvlight(pvinfo);
		videoBigPic.setCard_pvclick(pvinfo);
		videoBigPic.setCard_objinfo(objInfo);
		videoBigPic.setVideourl("");
		videoBigPic.setVideoid(videoId);
		videoBigPic.setPlaytime(playTime);
		videoBigPic.setJumptype(0);
		videoBigPic.setImgicon("");
		videoBigPic.setData_img(imageModel);

		return videoBigPic.initBICardItemModel();
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_Second(){
		Long id = SafeParamUtil.toSafeLong(secondCheJiaHaoNewsBody.getInfoid());

		String scheme = SchemeUtils.getYC_VideoScheme(id, false, 20033, "");;

		String title = SafeParamUtil.toSafeString(secondCheJiaHaoNewsBody.getTitle());

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.YCVideo;

		String imgUrl = ImageUtils.convertImageUrl(secondCheJiaHaoNewsBody.getCoverimage(), true, false, false, ImageSizeEnum.ImgSize_16x9_640x360);
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(imgUrl);

		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		String editname = SafeParamUtil.toSafeString(secondCheJiaHaoNewsBody.getNickname());;
		if(StringUtils.isNotEmpty(editname)) {
			tagModels.add(new Card_CardInfo_TagModel(editname));
		}
		Integer playCount = SafeParamUtil.toSafeInt(secondCheJiaHaoNewsBody.getVv());
		tagModels.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(playCount) + "播放"));

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("is_cjh_second_data",1);
		String pvinfo = jsonObject.toString();

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();
		objInfo.setSeriesids("");
		objInfo.setPlaycount(SafeParamUtil.convertToWan(playCount));
		objInfo.setAuthorname(editname);
		objInfo.setAuthorheadimg("");

		int duration = SafeParamUtil.toSafeInt(Long.valueOf(secondCheJiaHaoNewsBody.getDuration()).intValue(),0);
		String playTime = SafeParamUtil.convertVideoPlayTime(duration);

		String videoId = SafeParamUtil.toSafeString(secondCheJiaHaoNewsBody.getVideoid());

		BICardModel_10400_VideoBigPic videoBigPic = new BICardModel_10400_VideoBigPic();
		videoBigPic.setObj_title(title);
		videoBigPic.setObj_mediatypid(mediaTypeEnums.getIndex());
		videoBigPic.setObj_id(id);
		videoBigPic.setData_tags(tagModels);
		videoBigPic.setData_feednag(new ArrayList<>());
		videoBigPic.setCard_scheme(scheme);
		videoBigPic.setCard_pvlight(pvinfo);
		videoBigPic.setCard_pvclick(pvinfo);
		videoBigPic.setCard_objinfo(objInfo);
		videoBigPic.setVideourl("");
		videoBigPic.setVideoid(videoId);
		videoBigPic.setPlaytime(playTime);
		videoBigPic.setJumptype(0);
		videoBigPic.setImgicon("");
		videoBigPic.setData_img(imageModel);

		return videoBigPic.initBICardItemModel();
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_AllTab(){
		Long id = SafeParamUtil.toSafeLong(seriesAllTabResult.getBizId());

		String scheme = SchemeUtils.getYC_VideoScheme(id, false, 20033, "");;

		String title = StringUtils.isNotEmpty(seriesAllTabResult.getTitle()) ? seriesAllTabResult.getTitle() : "";

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.YCVideo;

		String imgUrl = ImageUtils.convertImageUrl(seriesAllTabResult.getImgUrl(), true, false, false, ImageSizeEnum.ImgSize_16x9_640x360);
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(imgUrl);

		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if (seriesAllTabResult.getType() == 1){
			tagModels.add(new Card_CardInfo_TagModel("置顶", TagStyleFontColorEnums.Orange.getColor(), TagStyleBackgroundEnums.Orange.getColor(), TagPositionEnums.Left));
		}
		if (seriesAllTabResult.getType() == 2){
			Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
			hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
			tagModels.add(hotTag);
		}
		String editname = StringUtils.isNotEmpty(seriesAllTabResult.getAuthorName()) ? seriesAllTabResult.getAuthorName() : "";
		if(StringUtils.isNotEmpty(editname)) {
			tagModels.add(new Card_CardInfo_TagModel(editname));
		}
		//todo 待定
//		Integer playCount = SafeParamUtil.toSafeInt(seriesAllTabResult.getPv());
//		tagModels.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(playCount) + "播放"));


		String pvinfo = seriesAllTabResult.getType()+"";

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();
		objInfo.setSeriesids("");
//		objInfo.setPlaycount(SafeParamUtil.convertToWan(playCount));todo 待定
		objInfo.setAuthorname(editname);
		objInfo.setAuthorheadimg("");

		int duration = SafeParamUtil.toSafeInt(Long.valueOf(seriesAllTabResult.getDuration()).intValue(),0);
		String playTime = SafeParamUtil.convertVideoPlayTime(duration);

		String videoId = SafeParamUtil.toSafeString(seriesAllTabResult.getVideoSource());

		BICardModel_10400_VideoBigPic videoBigPic = new BICardModel_10400_VideoBigPic();
		videoBigPic.setObj_title(title);
		videoBigPic.setObj_mediatypid(mediaTypeEnums.getIndex());
		videoBigPic.setObj_id(id);
		videoBigPic.setData_tags(tagModels);
		videoBigPic.setData_feednag(new ArrayList<>());
		videoBigPic.setCard_scheme(scheme);
		videoBigPic.setCard_pvlight(pvinfo);
		videoBigPic.setCard_pvclick(pvinfo);
		videoBigPic.setCard_objinfo(objInfo);
		videoBigPic.setVideourl("");
		videoBigPic.setVideoid(videoId);
		videoBigPic.setPlaytime(playTime);
		videoBigPic.setJumptype(0);
		videoBigPic.setImgicon("");
		videoBigPic.setData_img(imageModel);

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
		String originalImg = StringUtils.isNotEmpty(resourceObjShow.getImg_url()) ? resourceObjShow.getImg_url() : resourceObjShow.getImg_url2();
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel(ImageUtils.convertImageUrl(originalImg,true,true,true,ImageSizeEnum.ImgSize_16x9_1040x585));

		//卡片-底部标签位
		List<Card_CardInfo_TagModel> tagModels = new ArrayList<>();
		if (rcmDataItem.isTop()) {
			Card_CardInfo_TagModel topTag = new Card_CardInfo_TagModel("置顶", TagPositionEnums.Left);
			topTag.setStyletype(TagStyleTypeEnums.Red.getIndex());
			topTag.setFontcolor(TagStyleFontColorEnums.Orange.getColor());
			topTag.setBgcolor(TagStyleBackgroundEnums.Orange.getColor());
			tagModels.add(topTag);
		}
		//标签1-作者昵称
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
		MediaTypeEnums mediaTypeEnum = MediaTypeEnums.YCVideo;

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
