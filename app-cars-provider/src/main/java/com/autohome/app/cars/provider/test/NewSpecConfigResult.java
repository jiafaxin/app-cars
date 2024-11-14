package com.autohome.app.cars.provider.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewSpecConfigResult extends BaseCarsEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private SSpecResult result = new SSpecResult();

    public SSpecResult getResult() {
        return result;
    }

    public void setResult(SSpecResult result) {
        this.result = result;
    }

    public static class SSpecResult implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String bigpicprefix;
        private String smallpicprefix;
        private String seriesids;
        private String deletespecids;
        private String deletetip;
        private SpecInfo specinfo = new SpecInfo();
        private List<ParamItem> paramitems = new ArrayList<>();
        private List<ConfigItem> configitems = new ArrayList<>();
        private List<ConfigItem> selectconfig = new ArrayList<>();
        private List<ConditionItem> conditionlist = new ArrayList<>();
        private NewSpecConfigResult_AttentionSpec attentionspecinfo = new NewSpecConfigResult_AttentionSpec();
        private FootAskPirceInfo footaskpriceinfo = new FootAskPirceInfo();
        private int hasmore;//商用车用

        private UsedInfoForTruck usedinfo = new UsedInfoForTruck();
        private ToolboxentryDTO toolboxentry;
        private List<MustSeeItem> mustseelist = new ArrayList<>();

        public List<MustSeeItem> getMustseelist() {
            return mustseelist;
        }

        public void setMustseelist(List<MustSeeItem> mustseelist) {
            this.mustseelist = mustseelist;
        }

        public ToolboxentryDTO getToolboxentry() {
            return toolboxentry;
        }

        public void setToolboxentry(ToolboxentryDTO toolboxentry) {
            this.toolboxentry = toolboxentry;
        }

        public UsedInfoForTruck getUsedinfo() {
            return usedinfo;
        }

        public void setUsedinfo(UsedInfoForTruck usedinfo) {
            this.usedinfo = usedinfo;
        }

        public int getHasmore() {
            return hasmore;
        }

        public void setHasmore(int hasmore) {
            this.hasmore = hasmore;
        }

        public FootAskPirceInfo getFootaskpriceinfo() {
            return footaskpriceinfo;
        }

        public void setFootaskpriceinfo(FootAskPirceInfo footaskpriceinfo) {
            this.footaskpriceinfo = footaskpriceinfo;
        }

        public String getDeletespecids() {
            return deletespecids;
        }

        public void setDeletespecids(String deletespecids) {
            this.deletespecids = deletespecids;
        }

        public String getDeletetip() {
            return deletetip;
        }

        public void setDeletetip(String deletetip) {
            this.deletetip = deletetip;
        }

        public String getSeriesids() {
            return seriesids;
        }

        public void setSeriesids(String seriesids) {
            this.seriesids = seriesids;
        }

        public NewSpecConfigResult_AttentionSpec getAttentionspecinfo() {
            return attentionspecinfo;
        }

        public void setAttentionspecinfo(NewSpecConfigResult_AttentionSpec attentionspecinfo) {
            this.attentionspecinfo = attentionspecinfo;
        }

        private NewSpecConfigResult_CpsInfo cpsinfo = new NewSpecConfigResult_CpsInfo();

        public NewSpecConfigResult_CpsInfo getCpsinfo() {
            return cpsinfo;
        }

        public void setCpsinfo(NewSpecConfigResult_CpsInfo cpsinfo) {
            this.cpsinfo = cpsinfo;
        }

        public List<ConditionItem> getConditionlist() {
            return conditionlist;
        }

        public void setConditionlist(List<ConditionItem> conditionlist) {
            this.conditionlist = conditionlist;
        }

        public String getBigpicprefix() {
            return bigpicprefix;
        }

        public void setBigpicprefix(String bigpicprefix) {
            this.bigpicprefix = bigpicprefix;
        }

        public String getSmallpicprefix() {
            return smallpicprefix;
        }

        public void setSmallpicprefix(String smallpicprefix) {
            this.smallpicprefix = smallpicprefix;
        }

        public SpecInfo getSpecinfo() {
            return specinfo;
        }

        public void setSpecinfo(SpecInfo specinfo) {
            this.specinfo = specinfo;
        }

        public List<ParamItem> getParamitems() {
            return paramitems;
        }

        public void setParamitems(List<ParamItem> paramitems) {
            this.paramitems = paramitems;
        }

        public List<ConfigItem> getConfigitems() {
            return configitems;
        }

        public void setConfigitems(List<ConfigItem> configitems) {
            this.configitems = configitems;
        }

        public List<ConfigItem> getSelectconfig() {
            return selectconfig;
        }

        public void setSelectconfig(List<ConfigItem> selectconfig) {
            this.selectconfig = selectconfig;
        }
    }

    public static class UsedInfoForTruck implements Serializable {
        private int usedtype;
        private String usedname;

        public String getUsedname() {
            return usedname;
        }

        public void setUsedname(String usedname) {
            this.usedname = usedname;
        }

        public int getUsedtype() {
            return usedtype;
        }

        public void setUsedtype(int usedtype) {
            this.usedtype = usedtype;
        }
    }

    public static class FootAskPirceInfo implements Serializable{
        private String askpricetitle;
        private String askpricesubtitle;
        private int canaskprice;
        private String askpriceurl;
        private String imtitle;
        private String imlinkurl;
        private String imsubtitle;
        private String imiconurl;
        private Phoneinfo phoneinfo = new Phoneinfo();
        private String askpricescheme;

        public String getAskpricescheme() {
            return askpricescheme;
        }

        public void setAskpricescheme(String askpricescheme) {
            this.askpricescheme = askpricescheme;
        }

        public Phoneinfo getPhoneinfo() {
            return phoneinfo;
        }

        public void setPhoneinfo(Phoneinfo phoneinfo) {
            this.phoneinfo = phoneinfo;
        }

        public int getCanaskprice() {
            return canaskprice;
        }

        public void setCanaskprice(int canaskprice) {
            this.canaskprice = canaskprice;
        }

        public String getAskpricetitle() {
            return askpricetitle;
        }

        public void setAskpricetitle(String askpricetitle) {
            this.askpricetitle = askpricetitle;
        }

        public String getAskpricesubtitle() {
            return askpricesubtitle;
        }

        public void setAskpricesubtitle(String askpricesubtitle) {
            this.askpricesubtitle = askpricesubtitle;
        }

        public String getAskpriceurl() {
            return askpriceurl;
        }

        public void setAskpriceurl(String askpriceurl) {
            this.askpriceurl = askpriceurl;
        }

        public String getImtitle() {
            return imtitle;
        }

        public void setImtitle(String imtitle) {
            this.imtitle = imtitle;
        }

        public String getImlinkurl() {
            return imlinkurl;
        }

        public void setImlinkurl(String imlinkurl) {
            this.imlinkurl = imlinkurl;
        }

        public String getImsubtitle() {
            return imsubtitle;
        }

        public void setImsubtitle(String imsubtitle) {
            this.imsubtitle = imsubtitle;
        }

        public String getImiconurl() {
            return imiconurl;
        }

        public void setImiconurl(String imiconurl) {
            this.imiconurl = imiconurl;
        }
    }

    public static class  NewSpecConfigResult_AttentionSpec implements Serializable {
        private Integer specid;
        private String specname;
        private String priceinfo;
        private Integer seriesid;
        private String seriesname;
        private Integer paramisshow;
        private String seriesimg;

        public String getSeriesimg() {
            return seriesimg;
        }

        public void setSeriesimg(String seriesimg) {
            this.seriesimg = seriesimg;
        }

        public Integer getSpecid() {
            return specid;
        }

        public void setSpecid(Integer specid) {
            this.specid = specid;
        }

        public String getSpecname() {
            return specname;
        }

        public void setSpecname(String specname) {
            this.specname = specname;
        }

        public String getPriceinfo() {
            return priceinfo;
        }

        public void setPriceinfo(String priceinfo) {
            this.priceinfo = priceinfo;
        }

        public Integer getSeriesid() {
            return seriesid;
        }

        public void setSeriesid(Integer seriesid) {
            this.seriesid = seriesid;
        }

        public String getSeriesname() {
            return seriesname;
        }

        public void setSeriesname(String seriesname) {
            this.seriesname = seriesname;
        }

        public Integer getParamisshow() {
            return paramisshow;
        }

        public void setParamisshow(Integer paramisshow) {
            this.paramisshow = paramisshow;
        }

    }

    public static class NewSpecConfigResult_CpsInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private String title;
        private String linkurl;
        private Integer typeid;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLinkurl() {
            return linkurl;
        }

        public void setLinkurl(String linkurl) {
            this.linkurl = linkurl;
        }

        public Integer getTypeid() {
            return typeid;
        }

        public void setTypeid(Integer typeid) {
            this.typeid = typeid;
        }

    }

    public static class ParamItem implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String itemtype;
        private String groupname;
        private List<Item> items = new ArrayList<Item>();
        /**
         * 是否要显示：标配、选配、-无
         */
        private boolean showtips=true;

        public boolean isShowtips() {
            return showtips;
        }

        public void setShowtips(boolean showtips) {
            this.showtips = showtips;
        }

        public String getItemtype() {
            return itemtype;
        }

        public void setItemtype(String itemtype) {
            this.itemtype = itemtype;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public String getGroupname() {
            return groupname;
        }

        public void setGroupname(String groupname) {
            this.groupname = groupname;
        }
    }

    public static class SpecInfo implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private List<SpecItem> specitems = new ArrayList<SpecItem>();

        public List<SpecItem> getSpecitems() {
            return specitems;
        }

        public void setSpecitems(List<SpecItem> specitems) {
            this.specitems = specitems;
        }
    }

    public static class ConfigItem implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String itemtype;
        private String groupname="";
        private List<Item> items = new ArrayList<Item>();
        /**
         * 是否要显示：标配、选配、-无
         */
        private boolean showtips=true;

        public boolean isShowtips() {
            return showtips;
        }

        public void setShowtips(boolean showtips) {
            this.showtips = showtips;
        }

        public String getItemtype() {
            return itemtype;
        }

        public void setItemtype(String itemtype) {
            this.itemtype = itemtype;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public String getGroupname() {
            return groupname;
        }

        public void setGroupname(String groupname) {
            this.groupname = groupname;
        }
    }

    public static class SpecItem implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private int specid;
        private int count;
        private int paramisshow;// 是否显示参数配置
        private int seriesid;
        private String seriesname; // 车系名称 v5.8.5 添加 add by zhaohw
        private int presell; // 是否停预售车型 1是 0否
        private int canaskprice;
        private String specname; // 车型名称
        private String downprice;
        private int noshowprice;
        private String dealerprice;
        private String dealerpricetip;
        private List<PicItem> picitems = new ArrayList<PicItem>();
        private AskPriceInfo askpriceinfo = new AskPriceInfo();

        private Phoneinfo phoneinfo = new Phoneinfo();
        private String minprice;
        private String pricetitle;
        private List<String> condition = new ArrayList<>();
        private MoreSendInfo moresendinfo = new MoreSendInfo();
        private int brandid;
        private SeriesParamImInfo iminfo = new SeriesParamImInfo();
        private String seriesimg;//车系白底图，摩托车用
        private String arscheme = "";
        private int specisbooked;
        private String dynamicprice;
        private int year;
        private int onsaleOrder = 99999;

        private int specstatus;


        public Phoneinfo getPhoneinfo() {
            return phoneinfo;
        }

        public void setPhoneinfo(Phoneinfo phoneinfo) {
            this.phoneinfo = phoneinfo;
        }

        public void setOnsaleOrder(int onsaleOrder) {
            this.onsaleOrder = onsaleOrder;
        }

        public String getPricetitle() {
            return pricetitle;
        }

        public void setPricetitle(String pricetitle) {
            this.pricetitle = pricetitle;
        }

        public int getSpecstatus() {
            return specstatus;
        }

        public void setSpecstatus(int specstatus) {
            this.specstatus = specstatus;
        }

        public Integer getOnsaleOrder() {
            return onsaleOrder;
        }

        public void setOnsaleOrder(Integer onsaleOrder) {
            this.onsaleOrder = onsaleOrder;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getSpecisbooked() {
            return specisbooked;
        }

        public void setSpecisbooked(int specisbooked) {
            this.specisbooked = specisbooked;
        }

        public String getDynamicprice() {
            return dynamicprice;
        }

        public void setDynamicprice(String dynamicprice) {
            this.dynamicprice = dynamicprice;
        }

        public String getDealerpricetip() {
            return dealerpricetip;
        }

        public void setDealerpricetip(String dealerpricetip) {
            this.dealerpricetip = dealerpricetip;
        }

        public String getArscheme() {
            return arscheme;
        }

        public void setArscheme(String arscheme) {
            this.arscheme = arscheme;
        }

        public SeriesParamImInfo getIminfo() {
            return iminfo;
        }

        public String getSeriesimg() {
            return seriesimg;
        }

        public void setSeriesimg(String seriesimg) {
            this.seriesimg = seriesimg;
        }

        public void setIminfo(SeriesParamImInfo iminfo) {
            this.iminfo = iminfo;
        }

        public int getBrandid() {
            return brandid;
        }

        public void setBrandid(int brandid) {
            this.brandid = brandid;
        }

        public MoreSendInfo getMoresendinfo() {
            return moresendinfo;
        }

        public void setMoresendinfo(MoreSendInfo moresendinfo) {
            this.moresendinfo = moresendinfo;
        }


        public List<String> getCondition() {
            return condition;
        }

        public void setCondition(List<String> condition) {
            this.condition = condition;
        }

        public int getSpecid() {
            return specid;
        }

        public void setSpecid(int specid) {
            this.specid = specid;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getParamisshow() {
            return paramisshow;
        }

        public void setParamisshow(int paramisshow) {
            this.paramisshow = paramisshow;
        }

        public int getSeriesid() {
            return seriesid;
        }

        public void setSeriesid(int seriesid) {
            this.seriesid = seriesid;
        }

        public String getSeriesname() {
            return seriesname;
        }

        public void setSeriesname(String seriesname) {
            this.seriesname = seriesname;
        }

        public int getPresell() {
            return presell;
        }

        public void setPresell(int presell) {
            this.presell = presell;
        }

        public int getCanaskprice() {
            return canaskprice;
        }

        public void setCanaskprice(int canaskprice) {
            this.canaskprice = canaskprice;
        }

        public String getSpecname() {
            return specname;
        }

        public void setSpecname(String specname) {
            this.specname = specname;
        }

        public String getDownprice() {
            return downprice;
        }

        public void setDownprice(String downprice) {
            this.downprice = downprice;
        }

        public int getNoshowprice() {
            return noshowprice;
        }

        public void setNoshowprice(int noshowprice) {
            this.noshowprice = noshowprice;
        }

        public String getDealerprice() {
            return dealerprice;
        }

        public void setDealerprice(String dealerprice) {
            this.dealerprice = dealerprice;
        }

        public List<PicItem> getPicitems() {
            return picitems;
        }

        public void setPicitems(List<PicItem> picitems) {
            this.picitems = picitems;
        }

        public AskPriceInfo getAskpriceinfo() {
            return askpriceinfo;
        }

        public void setAskpriceinfo(AskPriceInfo askpriceinfo) {
            this.askpriceinfo = askpriceinfo;
        }

        public String getMinprice() {
            return minprice;
        }

        public void setMinprice(String minprice) {
            this.minprice = minprice;
        }
    }

    public static class SeriesParamImInfo implements Serializable  {
        private String imtitle;
        private String imlinkurl;

        private String imiconurl;

        public String getImlinkurl() {
            return imlinkurl;
        }

        public void setImlinkurl(String imlinkurl) {
            this.imlinkurl = imlinkurl;
        }

        public String getImiconurl() {
            return imiconurl;
        }

        public void setImiconurl(String imiconurl) {
            this.imiconurl = imiconurl;
        }

        public String getImtitle() {
            return imtitle;
        }

        public void setImtitle(String imtitle) {
            this.imtitle = imtitle;
        }


    }

    public static class MoreSendInfo implements Serializable  {
        private String moresendbtnname;
        private String moresendsubbtnname;
        private String moresendlinkurl;
        private String item_type;
        private String yldf_locationid;
        private String product_type;
        private String item_id;
        private String position;
        private int fromtype;
        private String object_id;
        private String stra;


        public int getFromtype() {
            return fromtype;
        }

        public String getObject_id() {
            return object_id;
        }

        public String getStra() {
            return stra;
        }

        public void setFromtype(int fromtype) {
            this.fromtype = fromtype;
        }

        public void setObject_id(String object_id) {
            this.object_id = object_id;
        }

        public void setStra(String stra) {
            this.stra = stra;
        }

        public String getMoresendbtnname() {
            return moresendbtnname;
        }

        public void setMoresendbtnname(String moresendbtnname) {
            this.moresendbtnname = moresendbtnname;
        }

        public String getMoresendsubbtnname() {
            return moresendsubbtnname;
        }

        public void setMoresendsubbtnname(String moresendsubbtnname) {
            this.moresendsubbtnname = moresendsubbtnname;
        }

        public String getMoresendlinkurl() {
            return moresendlinkurl;
        }

        public void setMoresendlinkurl(String moresendlinkurl) {
            this.moresendlinkurl = moresendlinkurl;
        }

        public String getItem_type() {
            return item_type;
        }

        public void setItem_type(String item_type) {
            this.item_type = item_type;
        }

        public String getYldf_locationid() {
            return yldf_locationid;
        }

        public void setYldf_locationid(String yldf_locationid) {
            this.yldf_locationid = yldf_locationid;
        }

        public String getProduct_type() {
            return product_type;
        }

        public void setProduct_type(String product_type) {
            this.product_type = product_type;
        }

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

    }

    public static class ConditionItem implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private int typeid;
        private String name;
        private int isselectmore;
        private int index;
        private String typevalue;
        private List<ConditionValue> list = new ArrayList<>();

        public String getTypevalue() {
            return typevalue;
        }

        public void setTypevalue(String typevalue) {
            this.typevalue = typevalue;
        }

        public int getTypeid() {
            return typeid;
        }

        public void setTypeid(int typeid) {
            this.typeid = typeid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIsselectmore() {
            return isselectmore;
        }

        public void setIsselectmore(int isselectmore) {
            this.isselectmore = isselectmore;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public List<ConditionValue> getList() {
            return list;
        }

        public void setList(List<ConditionValue> list) {
            this.list = list;
        }
    }

    public static class ConditionValue implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String id;
        private String name;
        private int lazyload = 0;

        public int getLazyload() {
            return lazyload;
        }

        public void setLazyload(int lazyload) {
            this.lazyload = lazyload;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class AskPriceInfo implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private int canaskprice;
        private int type;
        private String askpricetitle;
        private String askpricesubtitle;
        private String askpriceurl;
        private String copa;
		private String scheme;
        private String ext;

        public String getExt() {
            return ext;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }

        public String getScheme() {
			return scheme;
		}

		public void setScheme(String scheme) {
			this.scheme = scheme;
		}

        public int getCanaskprice() {
            return canaskprice;
        }

        public void setCanaskprice(int canaskprice) {
            this.canaskprice = canaskprice;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getAskpricetitle() {
            return askpricetitle;
        }

        public void setAskpricetitle(String askpricetitle) {
            this.askpricetitle = askpricetitle;
        }

        public String getAskpricesubtitle() {
            return askpricesubtitle;
        }

        public void setAskpricesubtitle(String askpricesubtitle) {
            this.askpricesubtitle = askpricesubtitle;
        }

        public String getAskpriceurl() {
            return askpriceurl;
        }

        public void setAskpriceurl(String askpriceurl) {
            this.askpriceurl = askpriceurl;
        }

        public String getCopa() {
            return copa;
        }

        public void setCopa(String copa) {
            this.copa = copa;
        }
    }

    public static class Item implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String name;
        private int id;
        private int subid;
        private String videoid;
        private String linkurl;
        private int paramitemid;// 参数配置项id;
        private int datatype;
        private List<ModelExcessId> modelexcessids = new ArrayList<ModelExcessId>();
        // 9.10.5
        private String contentid = "";
        private int playstarttime;

        public int getDatatype() {
            return datatype;
        }

        public void setDatatype(int datatype) {
            this.datatype = datatype;
        }

        public int getParamitemid() {
            return paramitemid;
        }

        public void setParamitemid(int paramitemid) {
            this.paramitemid = paramitemid;
        }

        public String getLinkurl() {
            return linkurl;
        }

        public void setLinkurl(String linkurl) {
            this.linkurl = linkurl;
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

        public int getSubid() {
            return subid;
        }

        public void setSubid(int subid) {
            this.subid = subid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVideoid() {
            return videoid;
        }

        public void setVideoid(String videoid) {
            this.videoid = videoid;
        }

        public List<ModelExcessId> getModelexcessids() {
            return modelexcessids;
        }

        public void setModelexcessids(List<ModelExcessId> modelexcessids) {
            this.modelexcessids = modelexcessids;
        }
    }

    public static class ModelExcessId implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private int id;
        private String value;
        private String priceinfo;
        private String tip;
        private String videoid;
        private String linkurl;
        private String ah100url;
        private int haspic;
        // 10.16.0 统一图片和视频
        private String cornerscheme;
        private int cornertype;
        private int order;
        private SpecColorInfo colorinfo = new SpecColorInfo();
        private String toast="";
        private String subvalue;

        private String playurl;

        public String getPlayurl() {
            return playurl;
        }

        public void setPlayurl(String playurl) {
            this.playurl = playurl;
        }

        public String getSubvalue() {
            return subvalue;
        }

        public void setSubvalue(String subvalue) {
            this.subvalue = subvalue;
        }

        public String getToast() {
            return toast;
        }

        public void setToast(String toast) {
            this.toast = toast;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public SpecColorInfo getColorinfo() {
            return colorinfo;
        }

        public void setColorinfo(SpecColorInfo colorinfo) {
            this.colorinfo = colorinfo;
        }

        public String getCornerscheme() {
            return cornerscheme;
        }

        public void setCornerscheme(String cornerscheme) {
            this.cornerscheme = cornerscheme;
        }

        public int getCornertype() {
            return cornertype;
        }

        public void setCornertype(int cornertype) {
            this.cornertype = cornertype;
        }

        public int getHaspic() {
            return haspic;
        }

        public void setHaspic(int haspic) {
            this.haspic = haspic;
        }

        private List<ModelExcessSubInfo> sublist=new ArrayList<>();

        public String getAh100url() {
            return ah100url;
        }

        public void setAh100url(String ah100url) {
            this.ah100url = ah100url;
        }

        public String getLinkurl() {
            return linkurl;
        }

        public void setLinkurl(String linkurl) {
            this.linkurl = linkurl;
        }

        public String getVideoid() {
            return videoid;
        }

        public void setVideoid(String videoid) {
            this.videoid = videoid;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getPriceinfo() {
            return priceinfo;
        }

        public void setPriceinfo(String priceinfo) {
            this.priceinfo = priceinfo;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public List<ModelExcessSubInfo> getSublist() {
            return sublist;
        }

        public void setSublist(List<ModelExcessSubInfo> sublist) {
            this.sublist = sublist;
        }
    }

    public static class SpecColorInfo implements Serializable  {
        private String title;
        /**
         * 1代表外观，2内饰，用于客户端区分
         */
        private int type;

        private List<SpecColorItem> list = new ArrayList<>();

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<SpecColorItem> getList() {
            return list;
        }

        public void setList(List<SpecColorItem> list) {
            this.list = list;
        }
    }

    public static class SpecColorItem  implements Serializable {
        private String name;
        private String value;
        private boolean isaddprice;
        private String addpricetext;
        private String remark;
        private String picurl="";

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isIsaddprice() {
            return isaddprice;
        }

        public void setIsaddprice(boolean isaddprice) {
            this.isaddprice = isaddprice;
        }

        public String getAddpricetext() {
            return addpricetext;
        }

        public void setAddpricetext(String addpricetext) {
            this.addpricetext = addpricetext;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

    public static class ModelExcessSubInfo implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String name = "";
        private String value = "";
        private String priceinfo = "";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getPriceinfo() {
            return priceinfo;
        }

        public void setPriceinfo(String priceinfo) {
            this.priceinfo = priceinfo;
        }
    }

    @NoArgsConstructor
    @Data
    public static class Phoneinfo implements Serializable  {

        @JsonProperty("title")
        private String title;
        @JsonProperty("subtitle")
        private String subtitle;
        @JsonProperty("phone")
        private String phone;
    }

    public static class PicItem implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String picpath;

        public String getPicpath() {
            return picpath;
        }

        public void setPicpath(String picpath) {
            this.picpath = picpath;
        }
    }
    @NoArgsConstructor
    @Data
    public static class ToolboxentryDTO implements Serializable {
        private PvItem entrypvdata = new PvItem();
        private List<ListDTO> list = new ArrayList<>();

        @NoArgsConstructor
        @Data
        public static class PvItem {
            private Map<String, String> argvs = new HashMap<>();
            private PvObj click = new PvObj();
            private PvObj show = new PvObj();

        }

        @NoArgsConstructor
        @Data
        public static class PvObj {
            private String eventid;
        }

        @NoArgsConstructor
        @Data
        public static class ListDTO {
            private String iconurl;
            private String title;
            private Integer typeid;
            private String linkurl;
            private PvItem pvdata = new PvItem();
        }
    }

    @Data
    public static class MustSeeItem implements Serializable  {
        private String itemtype;        //参配分类名称
        private String paramitemname;   //参配项名称

        public MustSeeItem(){

        }

        public MustSeeItem(String itemtype, String paramitemname){
            this.itemtype = itemtype;
            this.paramitemname = paramitemname;
        }
    }
}
