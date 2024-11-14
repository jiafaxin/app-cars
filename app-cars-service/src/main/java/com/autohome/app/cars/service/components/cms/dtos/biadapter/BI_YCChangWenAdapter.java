package com.autohome.app.cars.service.components.cms.dtos.biadapter;

import com.autohome.app.bicard.cards.BICardModel_10000_OnlyText;
import com.autohome.app.bicard.cards.BICardModel_10100_TuText;
import com.autohome.app.bicard.cards.BICardModel_10200_OnlyTu;
import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_ImageModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_RelationWordsModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_TagModel;
import com.autohome.app.bicard.entity.data.Card_Data_ObjInfo;
import com.autohome.app.bicard.enums.TagPositionEnums;
import com.autohome.app.bicard.enums.TagStyleTypeEnums;
import com.autohome.app.cars.apiclient.chejiahao.dtos.SSecondCheJiaHaoNewsResult;
import com.autohome.app.cars.apiclient.clubcard.dtos.SBI_RcmDataResult;
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
import org.apache.dubbo.common.utils.CollectionUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BI_YCChangWenAdapter {

	private MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed;
	private SSecondCheJiaHaoNewsResult secondCheJiaHaoNewsBody;

	private SeriesAllTabResult seriesAllTabResult;

	private SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem;

	public BI_YCChangWenAdapter(SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem) {
		this.rcmDataItem = rcmDataItem;
	}

	public BI_YCChangWenAdapter(SeriesAllTabResult seriesAllTabResult) {
		this.seriesAllTabResult = seriesAllTabResult;
	}

	public BI_YCChangWenAdapter(MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed) {
		this.mainDataSeriesSummaryFeed = mainDataSeriesSummaryFeed;
	}

	public BI_YCChangWenAdapter(SSecondCheJiaHaoNewsResult secondCheJiaHaoNewsBody){
		this.secondCheJiaHaoNewsBody = secondCheJiaHaoNewsBody;
	}

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed(){
		Long objId = SafeParamUtil.toSafeLong(mainDataSeriesSummaryFeed.getBiz_id());

		String scheme = SchemeUtils.getYC_ChangWenScheme(objId,false, 20033,"");;

		String title = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getSmall_title()) ? mainDataSeriesSummaryFeed.getSmall_title() : mainDataSeriesSummaryFeed.getTitle();

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.YCChangWen;

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

		String pvinfo = "";

		List<Card_CardInfo_TagModel>  tagModels = new ArrayList<>();
		if (mainDataSeriesSummaryFeed.getHot_event()==1){
			Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
			hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
			tagModels.add(hotTag);
		}
		//标签位-作者
		String authorName = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getAuthor_name()) ? mainDataSeriesSummaryFeed.getAuthor_name() : "";
		if(StringUtils.isNotEmpty(authorName)) {
			tagModels.add(new Card_CardInfo_TagModel(authorName));
		}
		//标签位-评论数
		int replyCount = mainDataSeriesSummaryFeed.getReply_count();
		tagModels.add(new Card_CardInfo_TagModel(String.format("%s评论",SafeParamUtil.convertToWan(replyCount))));



		List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel();
		String[] images = StringUtils.split(mainDataSeriesSummaryFeed.getMulti_images(),",");
		boolean hasMorePic = images != null && images.length >= 3;
		if(hasMorePic){
			for (String image :  images) {
				String image4x3 = ImageUtils.convertImageUrl(image, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
				imageModels.add(new Card_CardInfo_ImageModel(image4x3));
			}
		} else if(StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getImg_url())) {
			String img = ImageUtils.convertImageUrl(mainDataSeriesSummaryFeed.getImg_url(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
			imageModel.setUrl(img);
		}

		if (hasMorePic) {
			BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
			onlyTu.setData_imgs(imageModels);
			onlyTu.setCard_objinfo(objInfo);
			onlyTu.setCard_pvclick(pvinfo);
			onlyTu.setCard_pvlight(pvinfo);
			onlyTu.setCard_scheme(scheme);
			onlyTu.setData_feednag(new ArrayList<>());
			onlyTu.setData_tags(tagModels);
			onlyTu.setObj_id(objId);
			onlyTu.setObj_mediatypid(mediaTypeEnums.getIndex());
			onlyTu.setObj_title(title);

			return onlyTu.initBICardItemModel();
		} else if(StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getImg_url())) {
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
		} else {
			BICardModel_10000_OnlyText onlyText = new BICardModel_10000_OnlyText();
			onlyText.setCard_objinfo(objInfo);
			onlyText.setCard_pvclick(pvinfo);
			onlyText.setCard_pvlight(pvinfo);
			onlyText.setCard_scheme(scheme);
			onlyText.setData_feednag(new ArrayList<>());
			onlyText.setData_tags(tagModels);
			onlyText.setObj_id(objId);
			onlyText.setObj_mediatypid(mediaTypeEnums.getIndex());
			onlyText.setObj_title(title);

			return onlyText.initBICardItemModel();
		}
	}


	/**
	 * 编辑流实体生成
	 *
	 * @return
	 */
	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_Second(){
		Long objId = SafeParamUtil.toSafeLong(secondCheJiaHaoNewsBody.getInfoid());

		String scheme = SchemeUtils.getYC_ChangWenScheme(objId,false, 20033,"");;

		String title = secondCheJiaHaoNewsBody.getTitle();

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.YCChangWen;

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();


		JSONObject jsonObject = new JSONObject();
		jsonObject.put("is_cjh_second_data",1);
		String pvinfo = jsonObject.toString();

		List<Card_CardInfo_TagModel>  tagModels = new ArrayList<>();
		//标签位-作者
		String authorName = SafeParamUtil.toSafeString(secondCheJiaHaoNewsBody.getNickname());
		if(StringUtils.isNotEmpty(authorName)) {
			tagModels.add(new Card_CardInfo_TagModel(authorName));
		}
		//标签位-评论数
		int replyCount = secondCheJiaHaoNewsBody.getReplycount();
		tagModels.add(new Card_CardInfo_TagModel(String.format("%s评论",SafeParamUtil.convertToWan(replyCount))));



		List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel();
		boolean hasMorePic = secondCheJiaHaoNewsBody.getShowImagelist() != null && secondCheJiaHaoNewsBody.getShowImagelist();
		if(hasMorePic){
			for (String image : secondCheJiaHaoNewsBody.getImages()) {
				String image4x3 = ImageUtils.convertImageUrl(image, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
				imageModels.add(new Card_CardInfo_ImageModel(image4x3));
			}
		} else {
			String img4x3 = secondCheJiaHaoNewsBody.getCoverimage();
			String imgUrl4x3 = ImageUtils.convertImageUrl(img4x3, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
			imageModel.setUrl(imgUrl4x3);
		}

		if (hasMorePic) {
			BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
			onlyTu.setData_imgs(imageModels);
			onlyTu.setCard_objinfo(objInfo);
			onlyTu.setCard_pvclick(pvinfo);
			onlyTu.setCard_pvlight(pvinfo);
			onlyTu.setCard_scheme(scheme);
			onlyTu.setData_feednag(new ArrayList<>());
			onlyTu.setData_tags(tagModels);
			onlyTu.setObj_id(objId);
			onlyTu.setObj_mediatypid(mediaTypeEnums.getIndex());
			onlyTu.setObj_title(title);

			return onlyTu.initBICardItemModel();
		} else {
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

	public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_AllTab(){
		Long objId = SafeParamUtil.toSafeLong(seriesAllTabResult.getBizId());

		String scheme = SchemeUtils.getYC_ChangWenScheme(objId,false, 20033,"");;

		String title = StringUtils.isNotEmpty(seriesAllTabResult.getTitle()) ? seriesAllTabResult.getTitle() : "";

		MediaTypeEnums mediaTypeEnums = MediaTypeEnums.YCChangWen;

		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

		String pvinfo = seriesAllTabResult.getType()+"";

		List<Card_CardInfo_TagModel>  tagModels = new ArrayList<>();
		if (seriesAllTabResult.getType() == 1){
			tagModels.add(new Card_CardInfo_TagModel("置顶", TagStyleFontColorEnums.Orange.getColor(), TagStyleBackgroundEnums.Orange.getColor(), TagPositionEnums.Left));
		}
		if (seriesAllTabResult.getType() == 2){
			Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
			hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
			tagModels.add(hotTag);
		}
		//标签位-作者
		String authorName = StringUtils.isNotEmpty(seriesAllTabResult.getAuthorName()) ? seriesAllTabResult.getAuthorName() : "";
		if(StringUtils.isNotEmpty(authorName)) {
			tagModels.add(new Card_CardInfo_TagModel(authorName));
		}
		//标签位-评论数 todo 待确定
//		int replyCount = seriesAllTabResult.getReply_count();
//		tagModels.add(new Card_CardInfo_TagModel(String.format("%s评论",SafeParamUtil.convertToWan(replyCount))));



		List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
		Card_CardInfo_ImageModel imageModel = new Card_CardInfo_ImageModel();
		String multiImages = String.join(",", seriesAllTabResult.getMultiImages());
		String[] images = StringUtils.split(multiImages,",");
		boolean hasMorePic = images != null && images.length >= 3;
		if(hasMorePic){
			for (String image :  images) {
				String image4x3 = ImageUtils.convertImageUrl(image, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
				imageModels.add(new Card_CardInfo_ImageModel(image4x3));
			}
		} else if(StringUtils.isNotEmpty(seriesAllTabResult.getImgUrl())) {
			String img = ImageUtils.convertImageUrl(seriesAllTabResult.getImgUrl(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300);
			imageModel.setUrl(img);
		}

		if (hasMorePic) {
			BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
			onlyTu.setData_imgs(imageModels);
			onlyTu.setCard_objinfo(objInfo);
			onlyTu.setCard_pvclick(pvinfo);
			onlyTu.setCard_pvlight(pvinfo);
			onlyTu.setCard_scheme(scheme);
			onlyTu.setData_feednag(new ArrayList<>());
			onlyTu.setData_tags(tagModels);
			onlyTu.setObj_id(objId);
			onlyTu.setObj_mediatypid(mediaTypeEnums.getIndex());
			onlyTu.setObj_title(title);

			return onlyTu.initBICardItemModel();
		} else if(StringUtils.isNotEmpty(seriesAllTabResult.getImgUrl())) {
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
		} else {
			BICardModel_10000_OnlyText onlyText = new BICardModel_10000_OnlyText();
			onlyText.setCard_objinfo(objInfo);
			onlyText.setCard_pvclick(pvinfo);
			onlyText.setCard_pvlight(pvinfo);
			onlyText.setCard_scheme(scheme);
			onlyText.setData_feednag(new ArrayList<>());
			onlyText.setData_tags(tagModels);
			onlyText.setObj_id(objId);
			onlyText.setObj_mediatypid(mediaTypeEnums.getIndex());
			onlyText.setObj_title(title);

			return onlyText.initBICardItemModel();
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
		boolean hasMorePic = CollectionUtils.isNotEmpty(resourceObjShow.getGraphic_img_list()) && resourceObjShow.getGraphic_img_list().size() >= 3;
		if (hasMorePic) {
			for (String imgUrl : resourceObjShow.getGraphic_img_list()) {
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
		}
		//标签1-作者昵称
		String authorName = StringUtils.isNotEmpty(resourceObjShow.getAuthor()) ? resourceObjShow.getAuthor() : "";
		if (StringUtils.isNotEmpty(authorName)) {
			tagModels.add(new Card_CardInfo_TagModel(authorName, "", TagStyleTypeEnums.NoStyle, TagPositionEnums.Center));
		}
		//标签2-评论
		int replyCount = resourceObjHot != null ? SafeParamUtil.toSafeInt(resourceObjHot.getReply(),0) : 0;
		tagModels.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(replyCount) + "评论"));

		//标签3：发布时间
		Date publishTime = DateUtils.parseDateFromWithUTCDateStr(resourceObjShow.getPublish_time());
		String timeline = publishTime != null ? DateHelper.formatPublishTime(publishTime) : "";
		if (StringUtils.isNotEmpty(timeline)) {
			tagModels.add(new Card_CardInfo_TagModel(timeline, TagPositionEnums.Right));
		}

		//卡片-媒体类型
		MediaTypeEnums mediaTypeEnum = MediaTypeEnums.YCChangWen;

		//卡片-承载的业务信息节点
		Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

		//pv信息
		String pvInfo = "";
		String ext = "";
		if (rcmDataItem.getPvobj() != null) {
			pvInfo = StringUtils.isNotEmpty(rcmDataItem.getPvobj().getPvinfo()) ? rcmDataItem.getPvobj().getPvinfo() : "";
			ext = StringUtils.isNotEmpty(rcmDataItem.getPvobj().getExt()) ? rcmDataItem.getPvobj().getExt() : "";
		}

		//卡片-跳转协议
		String scheme = SchemeUtils.getYC_ChangWenScheme(objId,false, 20033,rcmDataItem.getPvid());
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
