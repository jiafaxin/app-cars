package com.autohome.app.cars.apiclient.car.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SpecConfigResult implements Serializable {

	private int returncode;
	private String message;
	private Result result;

	public int getReturncode() {
		return returncode;
	}

	public void setReturncode(int returncode) {
		this.returncode = returncode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public static class Result implements Serializable {
		private List<Configtypeitems> configtypeitems;

		public void setConfigtypeitems(List<Configtypeitems> configtypeitems) {
			this.configtypeitems = configtypeitems;
		}

		public List<Configtypeitems> getConfigtypeitems() {
			return this.configtypeitems;
		}

	}

	public static class Configtypeitems implements Serializable {
		private String name;
		private String groupname;

		private List<Configitems> configitems =  new ArrayList<>();

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public void setConfigitems(List<Configitems> configitems) {
			this.configitems = configitems;
		}

		public List<Configitems> getConfigitems() {
			return this.configitems;
		}

		public String getGroupname() {
			return groupname;
		}

		public void setGroupname(String groupname) {
			this.groupname = groupname;
		}
	}

	public static class Configitems implements Serializable{
		private int configid;
		private String name;
		private String logo;
		private int disptype;
		private List<Valueitems> valueitems = new ArrayList<>();

		public String getLogo() {
			return logo;
		}

		public void setLogo(String logo) {
			this.logo = logo;
		}

		public int getConfigid() {
			return configid;
		}

		public void setConfigid(int configid) {
			this.configid = configid;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public void setDisptype(int disptype) {
			this.disptype = disptype;
		}

		public int getDisptype() {
			return this.disptype;
		}

		public void setValueitems(List<Valueitems> valueitems) {
			this.valueitems = valueitems;
		}

		public List<Valueitems> getValueitems() {
			return this.valueitems;
		}

	}

	public static class Valueitems implements Serializable{
		private int specid;

		private String value;

		private List<Price> price = new ArrayList<>();

		private List<Sublist> sublist = new ArrayList<>();

		public void setSpecid(int specid) {
			this.specid = specid;
		}

		public int getSpecid() {
			return this.specid;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

		public void setPrice(List<Price> price) {
			this.price = price;
		}

		public List<Price> getPrice() {
			return this.price;
		}

		public void setSublist(List<Sublist> sublist) {
			this.sublist = sublist;
		}

		public List<Sublist> getSublist() {
			return this.sublist;
		}

	}

	public static class Sublist implements Serializable{
		private String subname;
		private int subvalue;
		private int price;

		private int subitemid;
		private String logo;

		public int getSubitemid() {
			return subitemid;
		}

		public void setSubitemid(int subitemid) {
			this.subitemid = subitemid;
		}

		public String getLogo() {
			return logo;
		}

		public void setLogo(String logo) {
			this.logo = logo;
		}

		public String getSubname() {
			return subname;
		}

		public void setSubname(String subname) {
			this.subname = subname;
		}

		public int getSubvalue() {
			return subvalue;
		}

		public void setSubvalue(int subvalue) {
			this.subvalue = subvalue;
		}

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}

		
	}

	public static class Price implements Serializable{
		private String subname;
		private String price;

		public String getSubname() {
			return subname;
		}

		public void setSubname(String subname) {
			this.subname = subname;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}
	}

}
