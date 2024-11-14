package com.autohome.app.cars.service.components.car.dtos.paramconfig;

import java.io.Serializable;
import java.util.List;

public class SeriesParamTypeModel implements Serializable {
    /**
     * 分组名称
     */
    private String groupname;
    private String name;
    private List<ParamitemsBean> paramitems;

    public SeriesParamTypeModel() {
    }

    public SeriesParamTypeModel(String name, List<ParamitemsBean> paramitems) {
        this.name = name;
        this.paramitems = paramitems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParamitemsBean> getParamitems() {
        return paramitems;
    }

    public void setParamitems(List<ParamitemsBean> paramitems) {
        this.paramitems = paramitems;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public static class ParamitemsBean implements Serializable{
        private int id;
        private String name;
        private int displaytype;

        private int itemsort;
        private List<ValueitemsBean> valueitems;

        public ParamitemsBean() {
        }

        public ParamitemsBean(int id, String name, int displaytype, List<ValueitemsBean> valueitems) {
            this.id = id;
            this.name = name;
            this.displaytype = displaytype;
            this.valueitems = valueitems;
        }
        public ParamitemsBean(int id, String name, int displaytype, List<ValueitemsBean> valueitems,int itemsort) {
            this.id = id;
            this.name = name;
            this.displaytype = displaytype;
            this.valueitems = valueitems;
            this.itemsort = itemsort;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDisplaytype() {
            return displaytype;
        }

        public void setDisplaytype(int displaytype) {
            this.displaytype = displaytype;
        }

        public int getItemsort() {
            return itemsort;
        }

        public void setItemsort(int itemsort) {
            this.itemsort = itemsort;
        }

        public List<ValueitemsBean> getValueitems() {
            return valueitems;
        }

        public void setValueitems(List<ValueitemsBean> valueitems) {
            this.valueitems = valueitems;
        }

        public static class ValueitemsBean implements Serializable {
            private int specid;
            private String value;
            private List<SublistBean> sublist;

            public ValueitemsBean() {
            }

            public ValueitemsBean(int specid, String value, List<SublistBean> sublist) {
                this.specid = specid;
                this.value = value;
                this.sublist = sublist;
            }

            public int getSpecid() {
                return specid;
            }

            public void setSpecid(int specid) {
                this.specid = specid;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public List<SublistBean> getSublist() {
                return sublist;
            }


            public void setSublist(List<SublistBean> sublist) {
                this.sublist = sublist;
            }

            public static class SublistBean implements Serializable{
                private String subname;
                private String subvalue;
                private int optiontype;
                private int price;

                public SublistBean() {
                }

                public SublistBean(String subname, String subvalue, int optiontype, int price) {
                    this.subname = subname;
                    this.subvalue = subvalue;
                    this.optiontype = optiontype;
                    this.price = price;
                }

                public int getOptiontype() {
                    return optiontype;
                }

                public void setOptiontype(int optiontype) {
                    this.optiontype = optiontype;
                }

                public String getSubname() {
                    return subname;
                }

                public void setSubname(String subname) {
                    this.subname = subname;
                }

                public String getSubvalue() {
                    return subvalue;
                }

                public void setSubvalue(String subvalue) {
                    this.subvalue = subvalue;
                }

                public int getPrice() {
                    return price;
                }

                public void setPrice(int price) {
                    this.price = price;
                }

            }
        }
    }
}
