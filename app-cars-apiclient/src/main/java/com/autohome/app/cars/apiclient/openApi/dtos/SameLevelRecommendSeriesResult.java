package com.autohome.app.cars.apiclient.openApi.dtos;

import java.util.List;

public class SameLevelRecommendSeriesResult {
    private static final long serialVersionUID = 1L;
    private SameLevelRecommendSeriesInfo result;
    private int returncode;
    private String message;

    public SameLevelRecommendSeriesInfo getResult() {
        return result;
    }

    public void setResult(SameLevelRecommendSeriesInfo result) {
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

    public static class SameLevelRecommendSeriesInfo {
        private List<RecommendSeriesSpecPair> data;
        private String device_id;
        private String pvid;
        private String rid;
        private String pvdata;

        public List<RecommendSeriesSpecPair> getData() {
            return data;
        }

        public void setData(List<RecommendSeriesSpecPair> data) {
            this.data = data;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getPvid() {
            return pvid;
        }

        public void setPvid(String pvid) {
            this.pvid = pvid;
        }

        public String getRid() {
            return rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public String getPvdata() {
            return pvdata;
        }

        public void setPvdata(String pvdata) {
            this.pvdata = pvdata;
        }

        public static class RecommendSeriesSpecPair {
            private int series_id;
            private int spec_id;
            private int is_ad;

            public int getSeries_id() {
                return series_id;
            }

            public void setSeries_id(int series_id) {
                this.series_id = series_id;
            }

            public int getSpec_id() {
                return spec_id;
            }

            public void setSpec_id(int spec_id) {
                this.spec_id = spec_id;
            }

            public int getIs_ad() {
                return is_ad;
            }

            public void setIs_ad(int is_ad) {
                this.is_ad = is_ad;
            }
        }

    }

}