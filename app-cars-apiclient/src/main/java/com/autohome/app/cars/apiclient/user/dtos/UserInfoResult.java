package com.autohome.app.cars.apiclient.user.dtos;

/**
 * @author wbs
 * @date 2024/5/31
 */
public class UserInfoResult {
    private int userid;
    private String headimage;
    private int isbindwlt;
    private String mobilephone;
    private String newnickname;
    private String nickname;
    private int sex;

    private int familycard;
    private String familycardimg;
    private String familycardname;
    private String adddate;
    private String cityname;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getHeadimage() {
        return headimage;
    }

    public void setHeadimage(String headimage) {
        this.headimage = headimage;
    }

    public int getIsbindwlt() {
        return isbindwlt;
    }

    public void setIsbindwlt(int isbindwlt) {
        this.isbindwlt = isbindwlt;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getNewnickname() {
        return newnickname;
    }

    public void setNewnickname(String newnickname) {
        this.newnickname = newnickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getFamilycard() {
        return familycard;
    }

    public void setFamilycard(int familycard) {
        this.familycard = familycard;
    }

    public String getFamilycardimg() {
        return familycardimg;
    }

    public void setFamilycardimg(String familycardimg) {
        this.familycardimg = familycardimg;
    }

    public String getFamilycardname() {
        return familycardname;
    }

    public void setFamilycardname(String familycardname) {
        this.familycardname = familycardname;
    }

    public String getAdddate() {
        return adddate;
    }

    public void setAdddate(String adddate) {
        this.adddate = adddate;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }
}
