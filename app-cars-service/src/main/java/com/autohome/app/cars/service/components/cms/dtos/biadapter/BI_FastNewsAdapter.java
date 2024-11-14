package com.autohome.app.cars.service.components.cms.dtos.biadapter;

import com.autohome.app.bicard.cards.*;
import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_ImageModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_MoreModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_RelationWordsModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_TagModel;
import com.autohome.app.bicard.entity.data.Card_Data_ObjInfo;
import com.autohome.app.bicard.enums.TagPositionEnums;
import com.autohome.app.bicard.enums.TagStyleTypeEnums;
import com.autohome.app.cars.apiclient.clubcard.dtos.SBI_RcmDataResult;
import com.autohome.app.cars.apiclient.cms.dtos.SeriesAllTabResult;
import com.autohome.app.cars.apiclient.maindata.dtos.MainDataSeriesSummaryFeedsResult;
import com.autohome.app.cars.apiclient.maindata.dtos.SFastNewsParagraphItem;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.common.utils.news.BusinessUtil;
import com.autohome.app.cars.common.utils.news.DateUtils;
import com.autohome.app.cars.common.utils.news.SchemeUtil;
import com.autohome.app.cars.service.components.cms.dtos.MediaTypeEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleBackgroundEnums;
import com.autohome.app.cars.service.components.cms.dtos.TagStyleFontColorEnums;
import org.apache.dubbo.common.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BI_FastNewsAdapter {
    private MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed;

    private SeriesAllTabResult seriesAllTabResult;


    private SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem;
    public BI_FastNewsAdapter(SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem) {
        this.rcmDataItem = rcmDataItem;
    }
    public BI_FastNewsAdapter(SeriesAllTabResult seriesAllTabResult) {
        this.seriesAllTabResult = seriesAllTabResult;
    }
    public BI_FastNewsAdapter(MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed) {
        this.mainDataSeriesSummaryFeed = mainDataSeriesSummaryFeed;
    }

    public BICardItemModel initCardItemModel_SeriesSummary_News_Feed() {
        //卡片-id
        Long id = SafeParamUtil.toSafeLong(mainDataSeriesSummaryFeed.getBiz_id());

        //卡片-标题
        String title = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getSmall_title()) ? mainDataSeriesSummaryFeed.getSmall_title() : mainDataSeriesSummaryFeed.getTitle();

        //卡片-媒体类型
        MediaTypeEnums mediaTypeEnums = MediaTypeEnums.FAST_NEWS;


        //卡片-标签集合
        List<Card_CardInfo_TagModel> tags = new ArrayList<>();

        //标签：热点
        if (mainDataSeriesSummaryFeed.getHot_event()==1){
            Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
            hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
            tags.add(hotTag);
        }
        //标签1:之家原创
        tags.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
        //标签2：作者
        String authorName = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getAuthor_name()) ? mainDataSeriesSummaryFeed.getAuthor_name() : "";
        tags.add(new Card_CardInfo_TagModel(authorName));
        //标签3：评论
        if (!mainDataSeriesSummaryFeed.is_close_comment()) {
            tags.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(mainDataSeriesSummaryFeed.getReply_count()) + "评论"));
        }
        //卡片-跳转协议
        String scheme = SchemeUtils.getNewsFastnewsDetailScheme(id,0);

        //卡片-承载的业务信息节点
        Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

        //卡片-图片、卡片标题截取（超过28字加...)
        List<String> imageList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(mainDataSeriesSummaryFeed.getCms_passage_list())) {
            imageList = mainDataSeriesSummaryFeed.getCms_passage_list().stream().filter(p -> p.getPType() != null && p.getPType() == 2
                    && StringUtils.isNotEmpty(p.getPImage())).map(o -> o.getPImage()).collect(Collectors.toList());
            Optional<SFastNewsParagraphItem> sFastNewsParagraphItemOptional = mainDataSeriesSummaryFeed.getCms_passage_list().stream().filter(p -> p.getPType() != null && p.getPType() == 1
                    && StringUtils.isNotEmpty(p.getPText())).findFirst();
            if (sFastNewsParagraphItemOptional.isPresent()) {
                SFastNewsParagraphItem sFastNewsParagraphItem = sFastNewsParagraphItemOptional.get();
                title = sFastNewsParagraphItem.getPText();
            }
        }
        if (imageList.size() == 0) {
            //如果图片数量等于0，不返回物料
            return null;
        } else if (imageList.size() < 3) {
            BICardModel_10100_TuText tuText = new BICardModel_10100_TuText();

            String imgUrl4x3 = ImageUtils.convertImageUrl(imageList.get(0), true, true, true, ImageSizeEnum.ImgSize_4x3_400x300);
            tuText.setData_img(new Card_CardInfo_ImageModel(imgUrl4x3));

            tuText.setObj_title(title);
            tuText.setObj_mediatypid(mediaTypeEnums.getIndex());
            tuText.setObj_id(id);
            tuText.setData_tags(tags);
            tuText.setData_feednag(new ArrayList<>());
            tuText.setCard_scheme(scheme);
            tuText.setCard_pvlight("");
            tuText.setCard_pvclick("");
            tuText.setCard_objinfo(objInfo);
            tuText.setRelationword(new Card_CardInfo_RelationWordsModel());
            return tuText.initBICardItemModel();
        } else {
            BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
            List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
            for (String img : imageList) {
                if (imageModels.size() == 3){
                    break;
                }
                String imgUrl4x3 = ImageUtils.convertImageUrl(img, true, true, true, ImageSizeEnum.ImgSize_4x3_400x300);
                imageModels.add(new Card_CardInfo_ImageModel(imgUrl4x3));
            }
            onlyTu.setData_imgs(imageModels);
            onlyTu.setCard_scheme(scheme);
            onlyTu.setObj_title(title);
            onlyTu.setObj_mediatypid(mediaTypeEnums.getIndex());
            onlyTu.setObj_id(id);
            onlyTu.setData_tags(tags);
            onlyTu.setData_feednag(new ArrayList<>());
            onlyTu.setCard_pvlight("");
            onlyTu.setCard_pvclick("");
            onlyTu.setCard_objinfo(objInfo);
            onlyTu.setRelationword(new Card_CardInfo_RelationWordsModel());
            return onlyTu.initBICardItemModel();
        }
    }

    public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_AllTab() {
        //卡片-id
        Long id = SafeParamUtil.toSafeLong(seriesAllTabResult.getBizId());

        //卡片-标题
        String title = StringUtils.isNotEmpty(seriesAllTabResult.getTitle()) ? seriesAllTabResult.getTitle() : "";

        //卡片-媒体类型
        MediaTypeEnums mediaTypeEnums = MediaTypeEnums.FAST_NEWS;


        //卡片-标签集合
        List<Card_CardInfo_TagModel> tags = new ArrayList<>();

        if (seriesAllTabResult.getType() == 1){
            tags.add(new Card_CardInfo_TagModel("置顶", TagStyleFontColorEnums.Orange.getColor(), TagStyleBackgroundEnums.Orange.getColor(), TagPositionEnums.Left));
        }
        //标签：热点
        if (seriesAllTabResult.getType() == 2){
            Card_CardInfo_TagModel hotTag = new Card_CardInfo_TagModel("热点", "#FF6600","", TagPositionEnums.Center);
            hotTag.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tab_news_hot_20240401.png");
            tags.add(hotTag);
        }
        //标签1:之家原创
        tags.add(new Card_CardInfo_TagModel("之家原创", "", TagStyleTypeEnums.Bule_Font_BgColor, TagPositionEnums.Left));
        //标签2：作者
        String authorName = StringUtils.isNotEmpty(seriesAllTabResult.getAuthorName()) ? seriesAllTabResult.getAuthorName() : "";
        tags.add(new Card_CardInfo_TagModel(authorName));

        //标签3：评论 todo 待定
//        if (!seriesAllTabResult.is_close_comment()) {
//            tags.add(new Card_CardInfo_TagModel(SafeParamUtil.convertToWan(seriesAllTabResult.getReply_count()) + "评论"));
//        }
        //卡片-跳转协议
        String scheme = SchemeUtils.getNewsFastnewsDetailScheme(id,0);

        //卡片-承载的业务信息节点
        Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

        //卡片-图片、卡片标题截取（超过28字加...)
        List<String> imageList = seriesAllTabResult.getMultiImages();

        if (imageList.size() == 0) {
            //如果图片数量等于0，不返回物料
            return null;
        } else if (imageList.size() < 3) {
            BICardModel_10100_TuText tuText = new BICardModel_10100_TuText();

            String imgUrl4x3 = ImageUtils.convertImageUrl(imageList.get(0), true, true, true, ImageSizeEnum.ImgSize_4x3_400x300);
            tuText.setData_img(new Card_CardInfo_ImageModel(imgUrl4x3));

            tuText.setObj_title(title);
            tuText.setObj_mediatypid(mediaTypeEnums.getIndex());
            tuText.setObj_id(id);
            tuText.setData_tags(tags);
            tuText.setData_feednag(new ArrayList<>());
            tuText.setCard_scheme(scheme);
            tuText.setCard_pvlight("");
            tuText.setCard_pvclick(seriesAllTabResult.getType()+"");
            tuText.setCard_objinfo(objInfo);
            tuText.setRelationword(new Card_CardInfo_RelationWordsModel());
            return tuText.initBICardItemModel();
        } else {
            BICardModel_10200_OnlyTu onlyTu = new BICardModel_10200_OnlyTu();
            List<Card_CardInfo_ImageModel> imageModels = new ArrayList<>();
            for (String img : imageList) {
                if (imageModels.size() == 3){
                    break;
                }
                String imgUrl4x3 = ImageUtils.convertImageUrl(img, true, true, true, ImageSizeEnum.ImgSize_4x3_400x300);
                imageModels.add(new Card_CardInfo_ImageModel(imgUrl4x3));
            }
            onlyTu.setData_imgs(imageModels);
            onlyTu.setCard_scheme(scheme);
            onlyTu.setObj_title(title);
            onlyTu.setObj_mediatypid(mediaTypeEnums.getIndex());
            onlyTu.setObj_id(id);
            onlyTu.setData_tags(tags);
            onlyTu.setData_feednag(new ArrayList<>());
            onlyTu.setCard_pvlight("");
            onlyTu.setCard_pvclick(seriesAllTabResult.getType()+"");
            onlyTu.setCard_objinfo(objInfo);
            onlyTu.setRelationword(new Card_CardInfo_RelationWordsModel());
            return onlyTu.initBICardItemModel();
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
        boolean hasMorePic = CollectionUtils.isNotEmpty(resourceObjShow.getGraphic_img_list3()) && resourceObjShow.getGraphic_img_list3().size() >= 3;
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
        Date publishTime = DateUtils.parseDate(resourceObjShow.getPublish_time());
        tagModels.add(new Card_CardInfo_TagModel(DateUtils.formatRomDataTimeSelf(publishTime), TagPositionEnums.Right));

        //卡片-媒体类型
        MediaTypeEnums mediaTypeEnum = MediaTypeEnums.FAST_NEWS;

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
        String scheme = SchemeUtil.getNewsFastnewsDetailScheme(objId,0);
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
