package com.autohome.app.cars.apiclient.cms.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 车系页资讯-全部tab优选数据
 */
@Data
@NoArgsConstructor
public class SeriesAllTabResult {
    //{
    //  "returncode": 0,
    //  "message": "",
    //  "result": [
    //    {
    //      "authorName": "汽车人有话说",
    //      "imgUrl": "https://www2.autoimg.cn/chejiahaodfs/g32/M06/60/EB/160x90_0_autohomecar__ChxkPWcq_z-AMAcDAAMvaKhCshs139.png",
    //      "title": "126.6万起 两款V8车型！ 2025款路虎卫士90/110上市！",
    //      "duration": 75,
    //      "publishTime": "2024/11/06 13:33:10",
    //      "videoSource": "569B45990092689E6F15C4841F4F2CE2",
    //      "multiImages": [
    //      ],
    //      "updateTime": "2024/11/06 14:28:00",
    //      "authorId": 215498057,
    //      "seriesId": 256,
    //      "bizType": 3,
    //      "bizId": 2248243,
    //      "type": 1
    //    }
    //  ]
    //}
    private String authorName;
    private String imgUrl;
    private String title;
    private String duration;
    private String publishTime;
    private String videoSource;
    private List<String> multiImages;
    private String updateTime;
    private int authorId;
    private int seriesId;
    private String summary;
    private int parentBizId;
    /**
     * 1,700112 -->cms
     * 3-->video
     * 12,13,14-->chejiahao
     */
    private int bizType;//类型
    private int bizId;
    private int type;//1-置顶 2-热点 3-ogc
}
