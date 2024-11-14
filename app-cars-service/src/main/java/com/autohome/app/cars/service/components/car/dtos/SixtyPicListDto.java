package com.autohome.app.cars.service.components.car.dtos;

import java.util.List;

public class SixtyPicListDto {
    private Integer sixtypicsortid;

    private String name;
    private List<SpecPic> specpic;

    public Integer getSixtypicsortid() {
        return sixtypicsortid;
    }

    public void setSixtypicsortid(Integer sixtypicsortid) {
        this.sixtypicsortid = sixtypicsortid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SpecPic> getSpecpic() {
        return specpic;
    }

    public void setSpecpic(List<SpecPic> specpic) {
        this.specpic = specpic;
    }

    public static class SpecPic {
        private Integer specid;
        private String pic;

        private Integer picid;

        public Integer getSpecid() {
            return specid;
        }

        public void setSpecid(Integer specid) {
            this.specid = specid;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public Integer getPicid() {
            return picid;
        }

        public void setPicid(Integer picid) {
            this.picid = picid;
        }
    }
}
