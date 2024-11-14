package com.autohome.app.cars.apiclient.club.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 接口文档：http://la.corpautohome.com/doc/detail?laFlowId=1827
 */
@Data
@NoArgsConstructor
public class SeriesClubPostResult {
    private String cache;//hit, miss是否命中缓存
    private List<ItemBean> items;
    @Data
    @NoArgsConstructor
    public static class ItemBean{
        private Number topicId;//帖子id
        private String title;//帖子标题
        private String pic;//图片
        private String publishTime;//发布时间
        private String scheme;//跳转协议
        private String detalscheme;//最终页
        private String listscheme;//帖子列表
    }
}
