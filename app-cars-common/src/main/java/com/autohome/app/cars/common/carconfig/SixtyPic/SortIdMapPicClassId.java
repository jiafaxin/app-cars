package com.autohome.app.cars.common.carconfig.SixtyPic;

/**
 * @ Author     ：lvming
 * @ Date       ：Created in 14:09 2020/11/17
 * @ Description：60图排序位id对应图片分类id
 * @ Modified By：
 * @Version: $
 */
public class SortIdMapPicClassId {

    /**
     * 根据60图排位id,返回对应图片分类id
     * 1,2,3,4,5,6,7  ==1 外观
     * 8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 ==10 中控方向盘
     * 24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45 ==3  车厢座椅
     * 46,47,48,49,50,51,52,53,54,55,56,57,58,59,60  === 12 其它细节
     *
     * @param sortId
     * @return
     */
    public static int getPicClassId(int sortId) {
        int picClass = 0;
        if (sortId > 0 && sortId <= 7) {
            picClass = 1; //外观
        } else if (8 <= sortId && sortId <= 23) {
            picClass = 10; //中控方向盘
        } else if (24 <= sortId && sortId <= 45) {
            picClass = 3; //中控方向盘
        } else if (46 <= sortId && sortId <= 60) {
            picClass = 12;  //其它细节
        }
        return picClass;
    }


}
