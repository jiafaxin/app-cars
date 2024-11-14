package com.autohome.app.cars.apiclient.openApi.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

public class SeriesRecommendLikeResult {
    private int returncode = 0;
    private String message = "";
    private String bsdata;
    private String requestid;
    private SNewYouLoveSeriesResultInfo result;

    public String getBsdata() {
        return bsdata;
    }

    public void setBsdata(String bsdata) {
        this.bsdata = bsdata;
    }

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    public SNewYouLoveSeriesResultInfo getResult() {
        return result;
    }

    public void setResult(SNewYouLoveSeriesResultInfo result) {
        this.result = result;
    }

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

    public static class SNewYouLoveSeriesResultInfo  implements Serializable {
        private List<SNewYouLoveSeriesItem> itemlist;


        public List<SNewYouLoveSeriesItem> getItemlist() {
            return itemlist;
        }


        public void setItemlist(List<SNewYouLoveSeriesItem> itemlist) {
            this.itemlist = itemlist;
        }


        public static class SNewYouLoveSeriesItem  implements Serializable{
            private SNewYouLoveResourceObj resourceobj;
            private SourceNewUserPortraitpvobj pvobj;
            private List<SNewYouLoveSeriesObj> objlist;

            public SourceNewUserPortraitpvobj getPvobj() {
                return pvobj;
            }

            public void setPvobj(SourceNewUserPortraitpvobj pvobj) {
                this.pvobj = pvobj;
            }

            public static class SourceNewUserPortraitpvobj implements Serializable{
                private  String pvinfo;

                public String getPvinfo() {
                    return pvinfo;
                }

                public void setPvinfo(String pvinfo) {
                    this.pvinfo = pvinfo;
                }
            }
            public SNewYouLoveResourceObj getResourceobj() {
                return resourceobj;
            }
            public void setResourceobj(SNewYouLoveResourceObj resourceobj) {
                this.resourceobj = resourceobj;
            }
            public List<SNewYouLoveSeriesObj> getObjlist() {
                return objlist;
            }
            public void setObjlist(List<SNewYouLoveSeriesObj> objlist) {
                this.objlist = objlist;
            }
            public static class SNewYouLoveSeriesObj  implements Serializable{
                private String itemext;

                public String getItemext() {
                    return itemext;
                }

                public void setItemext(String itemext) {
                    this.itemext = itemext;
                }

            }
            public static class SNewYouLoveResourceObj  implements Serializable{
                private int biz_type;
                private int biz_id;

                public int getBiz_id() {
                    return biz_id;
                }

                public void setBiz_id(int biz_id) {
                    this.biz_id = biz_id;
                }

                public int getBiz_type() {
                    return biz_type;
                }

                public void setBiz_type(int biz_type) {
                    this.biz_type = biz_type;
                }

            }
        }
    }
}
