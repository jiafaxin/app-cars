package com.autohome.app.cars.common.enums;

public enum ClubCardTypeEnum {
	// 20100:3图卡片；20200:大图卡片；20300:纯文本；20400直播；20500视频；20600小视频；20700图集；20800活动大图,20900左文右图（含置顶直播、活动贴）
	THREEIMGCARD(20100), BIGIMGCARD(20200), TEXT(20300), LIVE(20400), VIDEO(20500), SMALLVIDEO(20600), TUJI(20700),
	ACTIVITY(20800), LTEXTRIMG(20900);
	private int id;

	private ClubCardTypeEnum(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
