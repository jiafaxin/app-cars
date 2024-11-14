package com.autohome.app.cars.service.components.club.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchengtao
 * @date 2024/8/19 21:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecClubTabDto {
    /**
     * 是否有帖子
     */
    private int hasTab;
    /**
     * 帖子数量
     */
    private int topicCount;

    private int bbsId;

    private String bbsType;

    private String bbsName;

    public static SpecClubTabDto getInstance(int topicCount, int bbsId, String bbsType, String bbsName) {
        return new SpecClubTabDto(topicCount > 0 ? 1 : 0, topicCount, bbsId, bbsType, bbsName);
    }

}
