package com.autohome.app.cars.service.services.dtos;

public class ParamConfigVideoInfo {

	private String name;
	private String contentid;
	private int playstarttime;
	
	public ParamConfigVideoInfo(String name, String contentid, int playstarttime) {
		super();
		this.name = name;
		this.contentid = contentid;
		this.playstarttime = playstarttime;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContentid() {
		return contentid;
	}
	public void setContentid(String contentid) {
		this.contentid = contentid;
	}
	public int getPlaystarttime() {
		return playstarttime;
	}
	public void setPlaystarttime(int playstarttime) {
		this.playstarttime = playstarttime;
	}
}