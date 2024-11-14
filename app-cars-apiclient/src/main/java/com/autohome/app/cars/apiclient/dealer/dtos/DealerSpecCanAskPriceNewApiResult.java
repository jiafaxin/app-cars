package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DealerSpecCanAskPriceNewApiResult {

	private int specId;
	private int priceType;
	private List<Specs> specs = new ArrayList<>();
	@Data
	public static class Specs {
		private int newsPrice;
		private int specId;
		private int seriesId;
		private double minOriginalPrice;
		private double maxOriginalPrice;
		private MallInfo mallEntrance;
		private MainButtonOut mainButtonOut;
		private MainButtonOut seriesMainButton;
		private int saleType;
		private int isLowCity;
		private int specState;
	}

	@Data
	public static class MainButtonOut {
		private String mainText;
		private String subText;
		private Integer type;
		private Integer biztype;
		private String amount;
		private String cornerText;
		private int abTag;
	}

	@Data
	public static class MallInfo {
		private int specId;
		private String labelTxt;
		private String position;
		private String url;
	}

}


