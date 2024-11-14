package com.autohome.app.cars.apiclient.rank.dtos;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
@Data
public class SeriesRankHistoryResult implements Serializable {


	@Serial
	private static final long serialVersionUID = 6508645282159867021L;
	private ResultDto result;


    @Data
	public static class ResultDto {
		private List<ItemDto> data;

    }

    @Data
	public static class ItemDto implements Serializable {
		private int id;
		private int seriesid;
		private int min_guidance_price;
		private int max_guidance_price;
		private String month;
		private int salecnt;
		private int sortMonth;
		private Integer iscpca;
		private String week_day;
		private String week_range;

    }

}
