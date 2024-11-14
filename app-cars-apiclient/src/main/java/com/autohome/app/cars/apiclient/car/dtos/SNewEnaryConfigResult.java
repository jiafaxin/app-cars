package com.autohome.app.cars.apiclient.car.dtos;

/**
 * @description:
 * @author: shenzhongsheng
 * @time: 2023-05-11 20:03
 */



import java.util.ArrayList;
import java.util.List;

public class SNewEnaryConfigResult{

    private int returncode;

    private String message;
    private SNewEnaryConfigResult_Result result;

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

    public SNewEnaryConfigResult_Result getResult() {
        return result;
    }

    public void setResult(SNewEnaryConfigResult_Result result) {
        this.result = result;
    }

    public static class SNewEnaryConfigResult_Result {
        private String seriesid;
        private String seriesname;

        private List<NewEnergySpecitems> specitems = new ArrayList<NewEnergySpecitems>();

        public String getSeriesid() {
            return seriesid;
        }

        public void setSeriesid(String seriesid) {
            this.seriesid = seriesid;
        }

        public String getSeriesname() {
            return seriesname;
        }

        public void setSeriesname(String seriesname) {
            this.seriesname = seriesname;
        }

        public List<NewEnergySpecitems> getSpecitems() {
            return specitems;
        }

        public void setSpecitems(List<NewEnergySpecitems> specitems) {
            this.specitems = specitems;
        }

        public static class NewEnergySpecitems {

            private int specid;
            private String specname;
            private int specstate;
            private int minprice;
            private int maxprice;
            private int horsepower;
            private int mileage;
            private float officialfastchargetime;
            private float officialslowchargetime;
            private double batterycapacity;
            private int fueltypedetail;

            public int getFueltypedetail() {
                return fueltypedetail;
            }

            public void setFueltypedetail(int fueltypedetail) {
                this.fueltypedetail = fueltypedetail;
            }

            public double getBatterycapacity() {
                return batterycapacity;
            }

            public void setBatterycapacity(double batterycapacity) {
                this.batterycapacity = batterycapacity;
            }

            public int getSpecid() {
                return specid;
            }

            public void setSpecid(int specid) {
                this.specid = specid;
            }

            public String getSpecname() {
                return specname;
            }

            public void setSpecname(String specname) {
                this.specname = specname;
            }

            public int getSpecstate() {
                return specstate;
            }

            public void setSpecstate(int specstate) {
                this.specstate = specstate;
            }

            public int getMinprice() {
                return minprice;
            }

            public void setMinprice(int minprice) {
                this.minprice = minprice;
            }

            public int getMaxprice() {
                return maxprice;
            }

            public void setMaxprice(int maxprice) {
                this.maxprice = maxprice;
            }

            public int getHorsepower() {
                return horsepower;
            }

            public void setHorsepower(int horsepower) {
                this.horsepower = horsepower;
            }

            public int getMileage() {
                return mileage;
            }

            public void setMileage(int mileage) {
                this.mileage = mileage;
            }

            public float getOfficialfastchargetime() {
                return officialfastchargetime;
            }

            public void setOfficialfastchargetime(float officialfastchargetime) {
                this.officialfastchargetime = officialfastchargetime;
            }

            public float getOfficialslowchargetime() {
                return officialslowchargetime;
            }

            public void setOfficialslowchargetime(float officialslowchargetime) {
                this.officialslowchargetime = officialslowchargetime;
            }
        }
    }
}