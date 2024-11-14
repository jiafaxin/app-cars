package com.autohome.app.cars.service.components.cms.dtos.biadapter;

import com.autohome.app.bicard.cards.BICardModel_10000_OnlyText;
import com.autohome.app.bicard.cards.BICardModel_10100_TuText;
import com.autohome.app.bicard.cards.BICardModel_10200_OnlyTu;
import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_ImageModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_TagModel;
import com.autohome.app.bicard.entity.data.Card_Data_ObjInfo;
import com.autohome.app.bicard.enums.TagPositionEnums;
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

public class BI_YCCheDanAdapter {

    private MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed;


    private SeriesAllTabResult seriesAllTabResult;

    public BI_YCCheDanAdapter(SeriesAllTabResult seriesAllTabResult) {
        this.seriesAllTabResult = seriesAllTabResult;
    }
    public BI_YCCheDanAdapter(MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed mainDataSeriesSummaryFeed) {
        this.mainDataSeriesSummaryFeed = mainDataSeriesSummaryFeed;
    }

    public BICardItemModel initCardItemModel_SeriesSummary_News_Feed(){
        Long objId = SafeParamUtil.toSafeLong(mainDataSeriesSummaryFeed.getBiz_id());

        String scheme = SchemeUtils.getYC_CheDanScheme(objId,20033,"");

        String title = StringUtils.isNotEmpty(mainDataSeriesSummaryFeed.getSmall_title()) ? mainDataSeriesSummaryFeed.getSmall_title() : mainDataSeriesSummaryFeed.getTitle();

        MediaTypeEnums mediaTypeEnums = MediaTypeEnums.YC_CheDan;

        Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

        String pvinfo = "";

        List<Card_CardInfo_TagModel>  tagModels = new ArrayList<>();
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

    public BICardItemModel initCardItemModel_SeriesSummary_News_Feed_AllTab(){
        Long objId = SafeParamUtil.toSafeLong(seriesAllTabResult.getBizId());

        String scheme = SchemeUtils.getYC_CheDanScheme(objId,20033,"");

        String title = StringUtils.isNotEmpty(seriesAllTabResult.getTitle()) ? seriesAllTabResult.getTitle() : "";

        MediaTypeEnums mediaTypeEnums = MediaTypeEnums.YC_CheDan;

        Card_Data_ObjInfo objInfo = new Card_Data_ObjInfo();

        String pvinfo = seriesAllTabResult.getType()+"";

        List<Card_CardInfo_TagModel>  tagModels = new ArrayList<>();
        if (seriesAllTabResult.getType() == 1){
            tagModels.add(new Card_CardInfo_TagModel("置顶", TagStyleFontColorEnums.Orange.getColor(), TagStyleBackgroundEnums.Orange.getColor(), TagPositionEnums.Left));
        }
        //标签位-作者
        String authorName = StringUtils.isNotEmpty(seriesAllTabResult.getAuthorName()) ? seriesAllTabResult.getAuthorName() : "";
        if(StringUtils.isNotEmpty(authorName)) {
            tagModels.add(new Card_CardInfo_TagModel(authorName));
        }

        //标签位-评论数 todo 待定
//        int replyCount = seriesAllTabResult.getReply_count();
//        tagModels.add(new Card_CardInfo_TagModel(String.format("%s评论",SafeParamUtil.convertToWan(replyCount))));


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
}
