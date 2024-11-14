package com.autohome.app.cars.provider.test;


import autohome.rpc.car.app_cars.v1.carcfg.*;
import com.autohome.app.cars.common.utils.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompareUtils {

    private final static Logger logger = LoggerFactory.getLogger(CompareUtils.class);

    public static void compareValueNew(NewSpecConfigResult oldResponse, GetSpecParamConfigInfoResponse newResponse) {
        if (oldResponse == null || oldResponse.getResult() == null) {
            //printlnError("=======老接口返回数据为空：oldResponse=" + oldResponse);
            return;
        }
        if (newResponse == null || newResponse.getResult() == null) {
            printlnError("=======新接口返回数据为空：newResponse=" + newResponse);
            return;
        }

        if (ListUtil.isEmpty(oldResponse.getResult().getSpecinfo().getSpecitems())) {
            //printlnError("=======老接口车型列表为空");
            return;
        }

        if(ListUtil.isEmpty(newResponse.getResult().getDatalistList())){
            printlnError("=======新接口车型列表为空");
            return;
        }

        List<Integer> oldSpecIds = new ArrayList<>();
        oldResponse.getResult().getSpecinfo().getSpecitems().forEach(item -> {
            oldSpecIds.add(item.getSpecid());
        });
        List<Integer> newSpecIds = new ArrayList<>();
        newResponse.getResult().getDatalistList().forEach(datalist -> {
            newSpecIds.add(datalist.getSpecinfo().getSpecid());
        });
        if (oldSpecIds.size() != newSpecIds.size()) {
            printlnError("=======新老接口车型列表数量不同");
            return;
        }
        for (int i = 0; i < newSpecIds.size(); i++) {
            if (!oldSpecIds.contains(newSpecIds.get(i))) {
                printlnError("=======新老接口车型列表车型id不同");
                return;
            }
        }

//        compareSpecInfo(oldResponse, newResponse);
        compareParamInfo(oldResponse, newResponse);
        compareConfigInfo(oldResponse, newResponse);
//        compareCpsInfo(oldResponse, newResponse);
    }

    private static void compareParamInfo(NewSpecConfigResult oldResponse, GetSpecParamConfigInfoResponse newResponse) {
        try {
            oldResponse.getResult().getParamitems().forEach(group -> {
                String groupname = group.getGroupname();
                String itemtype = group.getItemtype();
                boolean showtips = group.isShowtips();
                //System.out.println("===========groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips);
                group.getItems().forEach(item -> {
                    int itemId = item.getParamitemid();
                    String itemName = item.getName();
                    //System.out.println("===========itemName=" + itemName + " itemId=" + itemId);
                    item.getModelexcessids().forEach(spec -> {
                        int specId = spec.getId();
                        //System.out.println("===========specId=" + specId);
                        GetSpecParamConfigInfoResponse.Result.Datalist dataItem = newResponse.getResult().getDatalistList().stream().filter(data -> data.getSpecinfo().getSpecid() == specId).findFirst().orElse(null);
                        if (dataItem != null) {
                            Paramitem groupInfo = dataItem.getParamitemsList().stream().filter(group_new -> group_new.getGroupname().equals(groupname) && group_new.getItemtype().equals(itemtype)).findFirst().orElse(null);
                            if (groupInfo == null) {
                                printlnError("新接口的车型配置的分组不存在：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype);
                            } else {
                                Item newItem = groupInfo.getItemsList().stream().filter(item1 -> itemName.equals(item1.getName())).findFirst().orElse(null);
                                if (newItem == null) {
                                    if(!Arrays.asList("优惠信息").contains(itemName)){
                                        printlnError("新接口的车型配置的分组的参配不存在：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " itemName=" + itemName);
                                    }
                                } else {
                                    if (!compare(item.getParamitemid(), newItem.getParamitemid())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getParamitemid：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                    if (!compare(item.getSubid(), newItem.getSubid())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getSubid：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                    if (!compare(item.getName(), newItem.getName())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getName：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                    if (!compare(item.getId(), newItem.getId())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getId：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                    if (!compare(item.getContentid(), newItem.getContentid())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getContentid：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
//                                    if (!compare(item.getDatatype(), newItem.getDatatype())) {
//                                        printlnError("新接口的车型的参配分组的参配值不一致：getDatatype：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
//                                    }
//                                    if (!compare(item.getLinkurl(), newItem.getLinkurl())) {
//                                        printlnError("新接口的车型的参配分组的参配值不一致：getLinkurl：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
//                                    }
                                    if (!compare(item.getVideoid(), newItem.getVideoid())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getVideoid：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                    if (!compare(item.getPlaystarttime(), newItem.getPlaystarttime())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getPlaystarttime：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                }
                            }
                        }
                    });
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void compareConfigInfo(NewSpecConfigResult oldResponse, GetSpecParamConfigInfoResponse newResponse) {
        try {
            oldResponse.getResult().getConfigitems().forEach(group -> {
                String groupname = group.getGroupname();
                String itemtype = group.getItemtype();
                boolean showtips = group.isShowtips();
                //System.out.println("===========groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips);
                group.getItems().forEach(item -> {
                    int itemId = item.getParamitemid();
                    String itemName = item.getName();
                    //System.out.println("===========itemName=" + itemName + " itemId=" + itemId);
                    item.getModelexcessids().forEach(spec -> {
                        int specId = spec.getId();
                        //System.out.println("===========specId=" + specId);
                        GetSpecParamConfigInfoResponse.Result.Datalist dataItem = newResponse.getResult().getDatalistList().stream().filter(data -> data.getSpecinfo().getSpecid() == specId).findFirst().orElse(null);
                        if (dataItem != null) {
                            Configitem groupInfo = dataItem.getConfigitemsList().stream().filter(group_new -> group_new.getGroupname().equals(groupname) && group_new.getItemtype().equals(itemtype)).findFirst().orElse(null);
                            if (groupInfo == null) {
                                printlnError("新接口的车型配置的分组不存在：" + "groupname=" + groupname + " itemtype=" + itemtype);
                            } else {
                                Item newItem = groupInfo.getItemsList().stream().filter(item1 -> itemName.equals(item1.getName())).findFirst().orElse(null);
                                if (newItem == null) {
                                    printlnError("新接口的车型配置的分组的参配不存在：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " itemName=" + itemName);
                                } else {
                                    if (!compare(item.getParamitemid(), newItem.getParamitemid())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getParamitemid：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                    if (!compare(item.getSubid(), newItem.getSubid())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getSubid：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                    if (!compare(item.getName(), newItem.getName())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getName：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                    if (!compare(item.getId(), newItem.getId())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getId：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                    if (!compare(item.getContentid(), newItem.getContentid())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getContentid：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
//                                    if (!compare(item.getDatatype(), newItem.getDatatype())) {
//                                        printlnError("新接口的车型的参配分组的参配值不一致：getDatatype：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
//                                    }
//                                    if (!compare(item.getLinkurl(), newItem.getLinkurl())) {
//                                        printlnError("新接口的车型的参配分组的参配值不一致：getLinkurl：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
//                                    }
                                    if (!compare(item.getVideoid(), newItem.getVideoid())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getVideoid：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                    if (!compare(item.getPlaystarttime(), newItem.getPlaystarttime())) {
                                        printlnError("新接口的车型的参配分组的参配值不一致：getPlaystarttime：" + "specid=" + specId + "groupname=" + groupname + " itemtype=" + itemtype + " showtips=" + showtips + " itemId=" + itemId);
                                    }
                                }
                            }
                        }
                    });
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void compareCpsInfo(NewSpecConfigResult oldResponse, GetSpecParamConfigInfoResponse newResponse) {
        try {
            NewSpecConfigResult.NewSpecConfigResult_CpsInfo old_cpsinfo = oldResponse.getResult().getCpsinfo();
            Cpsinfo new_cpsinfo = newResponse.getResult().getCpsinfo();
            if (old_cpsinfo != null && new_cpsinfo != null) {
                if (!compare(old_cpsinfo.getTitle(), new_cpsinfo.getTitle())) {
                    printlnError("cpsinfo.getTitle:不一致");
                }
//                if (!compare(old_cpsinfo.getTypeid(), new_cpsinfo.getTypeid())) {
//                    printlnError("cpsinfo.getTypeid:不一致");
//                }
                if (!compare(old_cpsinfo.getLinkurl(), new_cpsinfo.getLinkurl())) {
                    printlnError("cpsinfo.getLinkurl:不一致");
                }
            } else if (old_cpsinfo == null || new_cpsinfo == null) {
                printlnError("cpsinfo:不一致");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void compareSpecInfo(NewSpecConfigResult oldResponse, GetSpecParamConfigInfoResponse newResponse) {
        try {
            oldResponse.getResult().getSpecinfo().getSpecitems().forEach(oldSpecInfo -> {
                GetSpecParamConfigInfoResponse.Result.Datalist datalist = newResponse.getResult().getDatalistList().stream().filter(data -> data.getSpecinfo().getSpecid() == oldSpecInfo.getSpecid()).findFirst().orElse(null);
                if (datalist == null) {
                    Specitem newSpecInfo = datalist.getSpecinfo();
                    if (!compare(oldSpecInfo.getArscheme(), newSpecInfo.getArscheme())) {
                        printlnError("oldSpecInfo.getArscheme:不一致");
                    }
                    if (!compare(oldSpecInfo.getBrandid(), newSpecInfo.getBrandid())) {
                        printlnError("oldSpecInfo.getBrandid:不一致");
                    }
                    if (!compare(oldSpecInfo.getCount(), newSpecInfo.getCount())) {
                        printlnError("oldSpecInfo.getCount:不一致");
                    }
                    if (!compare(oldSpecInfo.getDownprice(), newSpecInfo.getDownprice())) {
                        printlnError("downprice不一致：oldSpecInfo.getDownprice()=" + oldSpecInfo.getDownprice() + ", newSpecInfo.getDownprice()=" + newSpecInfo.getDownprice());
                    }
                    if (!compare(oldSpecInfo.getDynamicprice(), newSpecInfo.getDynamicprice())) {
                        printlnError("oldSpecInfo.getDynamicprice:不一致");
                    }
                    if (!compare(oldSpecInfo.getMinprice(), newSpecInfo.getMinprice())) {
                        printlnError("oldSpecInfo.getMinprice:不一致");
                    }

                    //字段onsaleorder：服务端车型排序使用，客户端没有用到。
//                    if (!compare(oldSpecInfo.getOnsaleOrder(), newSpecInfo.getOnsaleOrder())) {
//                        printlnError("oldSpecInfo.getOnsaleOrder:不一致");
//                    }
                    if (!compare(oldSpecInfo.getParamisshow(), newSpecInfo.getParamisshow())) {
                        printlnError("paramisshow不一致：oldSpecInfo.getParamisshow()=" + oldSpecInfo.getParamisshow() + ", newSpecInfo.getParamisshow()=" + newSpecInfo.getParamisshow());
                    }
                    if (!compare(oldSpecInfo.getPicitems().size(), newSpecInfo.getPicitemsCount())) {
                        printlnError("picitems.size()不一致：oldSpecInfo.getPicitems().size()=" + oldSpecInfo.getPicitems().size() + ", newSpecInfo.getPicitems().size()=" + newSpecInfo.getPicitemsCount());
                    }
                    if (!compare(oldSpecInfo.getPresell(), newSpecInfo.getPresell())) {
                        printlnError("presell不一致：oldSpecInfo.getPresell()=" + oldSpecInfo.getPresell() + ", newSpecInfo.getPresell()=" + newSpecInfo.getPresell());
                    }
                    if (!compare(oldSpecInfo.getPricetitle(), newSpecInfo.getPricetitle())) {
                        printlnError("oldSpecInfo.getPricetitle:不一致");
                    }
                    if (!compare(oldSpecInfo.getSeriesid(), newSpecInfo.getSeriesid())) {
                        printlnError("seriesid不一致：oldSpecInfo.getSeriesid()=" + oldSpecInfo.getSeriesid() + ", newSpecInfo.getSeriesid()=" + newSpecInfo.getSeriesid());
                    }
                    if (!compare(oldSpecInfo.getSeriesname(), newSpecInfo.getSeriesname())) {
                        printlnError("seriesname不一致：oldSpecInfo.getSeriesname()=" + oldSpecInfo.getSeriesname() + ", newSpecInfo.getSeriesname()=" + newSpecInfo.getSeriesname());
                    }
                    if (!compare(oldSpecInfo.getSpecid(), newSpecInfo.getSpecid())) {
                        printlnError("specid不一致：oldSpecInfo.getSpecid()=" + oldSpecInfo.getSpecid() + ", newSpecInfo.getSpecid()=" + newSpecInfo.getSpecid());
                    }
                    if (!compare(oldSpecInfo.getSpecisbooked(), newSpecInfo.getSpecisbooked())) {
                        printlnError("oldSpecInfo.getSpecisbooked:不一致");
                    }
                    if (!compare(oldSpecInfo.getSpecname(), newSpecInfo.getSpecname())) {
                        printlnError("specname不一致：oldSpecInfo.getSpecname()=" + oldSpecInfo.getSpecname() + ", newSpecInfo.getSpecname()=" + newSpecInfo.getSpecname());
                    }
                    if (!compare(oldSpecInfo.getSpecstatus(), newSpecInfo.getSpecstatus())) {
                        printlnError("oldSpecInfo.getSpecstatus:不一致");
                    }
                    if (!compare(oldSpecInfo.getYear(), newSpecInfo.getYear())) {
                        printlnError("oldSpecInfo.getYear:不一致");
                    }

//                    if (!compare(oldSpecInfo.getDealerprice(), newSpecInfo.getDealerprice())) {
//                        printlnError("dealerprice不一致：oldSpecInfo.getDealerprice()=" + oldSpecInfo.getDealerprice() + ", newSpecInfo.getDealerprice()=" + newSpecInfo.getDealerprice());
//                    }
//                    if (!compare(oldSpecInfo.getDealerpricetip(), newSpecInfo.getDealerpricetip())) {
//                        printlnError("dealerpricetip不一致：oldSpecInfo.getDealerpricetip()=" + oldSpecInfo.getDealerpricetip() + ", newSpecInfo.getDealerpricetip()=" + newSpecInfo.getDealerpricetip());
//                    }
//                    if (!compare(oldSpecInfo.getNoshowprice(), newSpecInfo.getNoshowprice())) {
//                        printlnError("noshowprice不一致：oldSpecInfo.getNoshowprice()=" + oldSpecInfo.getNoshowprice() + ", newSpecInfo.getNoshowprice()=" + newSpecInfo.getNoshowprice());
//                    }
//                    if (oldSpecInfo.getIminfo() != null && newSpecInfo.getIminfo() != null) {
//                        if (!compare(oldSpecInfo.getIminfo().getImiconurl(), newSpecInfo.getIminfo().getImiconurl())) {
//                            printlnError("oldSpecInfo.getIminfo().getImiconurl():不一致");
//                        }
//                        if (!compare(oldSpecInfo.getIminfo().getImtitle(), newSpecInfo.getIminfo().getImtitle())) {
//                            printlnError("oldSpecInfo.getIminfo().getImtitle():不一致");
//                        }
//                        if (!compare(oldSpecInfo.getIminfo().getImlinkurl(), newSpecInfo.getIminfo().getImlinkurl())) {
//                            printlnError("oldSpecInfo.getIminfo().getImlinkurl():不一致");
//                        }
//                    } else if (oldSpecInfo.getIminfo() == null || oldSpecInfo.getIminfo() == null) {
//                        printlnError("oldSpecInfo.getIminfo():不一致");
//                    }

//                    if (oldSpecInfo.getAskpriceinfo() != null && newSpecInfo.getAskpriceinfo() != null) {
//                        NewSpecConfigResult.AskPriceInfo oldAskpriceinfo = oldSpecInfo.getAskpriceinfo();
//                        GetSpecParamConfigInfoResponse.Result.Datalist.Specinfo.Askpriceinfo newAskpriceinfo = newSpecInfo.getAskpriceinfo();
//
//                        if (!compare(oldAskpriceinfo.getAskpricesubtitle(), newAskpriceinfo.getAskpricesubtitle())) {
//                            printlnError("oldAskpriceinfo.getAskpricesubtitle():不一致");
//                        }
//                        if (!compare(oldAskpriceinfo.getAskpricetitle(), newAskpriceinfo.getAskpricetitle())) {
//                            printlnError("oldAskpriceinfo.getAskpricetitle():不一致");
//                        }
//                        if (!compare(oldAskpriceinfo.getAskpriceurl(), newAskpriceinfo.getAskpriceurl())) {
//                            printlnError("oldAskpriceinfo.getAskpriceurl():不一致");
//                        }
//                        if (!compare(oldAskpriceinfo.getCanaskprice(), newAskpriceinfo.getCanaskprice())) {
//                            printlnError("oldAskpriceinfo.getCanaskprice():不一致");
//                        }
//                        if (!compare(oldAskpriceinfo.getExt(), newAskpriceinfo.getExt())) {
//                            printlnError("oldAskpriceinfo.getExt():不一致");
//                        }
//                        if (!compare(oldAskpriceinfo.getCopa(), newAskpriceinfo.getCopa())) {
//                            printlnError("oldAskpriceinfo.getCopa():不一致");
//                        }
//                        if (!compare(oldAskpriceinfo.getType(), newAskpriceinfo.getType())) {
//                            printlnError("oldAskpriceinfo.getType():不一致");
//                        }
//                        if (!compare(oldAskpriceinfo.getScheme(), newAskpriceinfo.getScheme())) {
//                            printlnError("oldAskpriceinfo.getScheme:不一致");
//                        }
//                    } else if (oldSpecInfo.getAskpriceinfo() == null || oldSpecInfo.getAskpriceinfo() == null) {
//                        printlnError("oldSpecInfo.getAskpriceinfo():不一致");
//                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean compare(Object a, Object b) {
        //System.out.println("compare a=" + a + ", b=" + b);
        if (a == b) {
            return true;
        } else if (a == null && "".equals(b)) {
            return true;
        } else if (a == null || b == null) {
            return false;
        } else if (!a.getClass().getTypeName().equals(b.getClass().getTypeName())) {
            return false;
        } else {
            if (a instanceof String) {
                String stra = (String) a;
                String strb = (String) b;
                return stra.equals(strb);
            } else if (a instanceof Integer) {
                int inta = (int) a;
                int intb = (int) b;
                return inta == intb;
            }
        }
        return false;
    }

    private static void printlnError(String str) {
        System.out.println("errorinfo: "+str);
    }

}
