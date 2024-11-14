package com.autohome.app.cars.apiclient.rank.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/7/12
 */
@Data
public class KoubeiRankResult {


    private int returncode;

    private String message;

    private List<KoubeiRankResultDto> result;

    @Data
    public static class KoubeiRankResultDto {

        private int number;

        private int rank;

        private float score;

        private int seriesId;

    }


}
