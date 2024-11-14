package com.autohome.app.cars.common.utils;


import java.util.*;

/**
 * 用户头像工具类
 *
 * @author zhangchengtao
 */
public class UserPhotoUtils {
    private static final String[] USER_PHOTO_ARR = new String[]{"askuicon_220801_00001.jpg", "askuicon_220801_00002.jpg", "askuicon_220801_00003.jpg", "askuicon_220801_00004.jpg", "askuicon_220801_00005.jpg", "askuicon_220801_00006.jpg", "askuicon_220801_00007.jpg", "askuicon_220801_00008.jpg", "askuicon_220801_00009.jpg", "askuicon_220801_00010.jpg", "askuicon_220801_00011.jpg", "askuicon_220801_00012.jpg", "askuicon_220801_00013.jpg", "askuicon_220801_00014.jpg", "askuicon_220801_00015.jpg", "askuicon_220801_00016.jpg", "askuicon_220801_00017.jpg", "askuicon_220801_00018.jpg", "askuicon_220801_00019.jpg", "askuicon_220801_00020.jpg", "askuicon_220801_00021.jpg", "askuicon_220801_00022.jpg"};
    private static final String PREFIX_URL = "http://nfiles3.autohome.com.cn/zrjcpk10/";
    public static final Random random = new Random();


    /**
     * 随机生成指定个数个用户头像
     *
     * @param count 生成个数
     * @return 用户头像地址List
     */
    public static List<String> getRandomByCount(int count) {
        if (count < 1) {
            return Collections.emptyList();
        }
        List<String> userPhotoList = new ArrayList<>(count);
        // 记录已经使用过的URL
        List<Integer> used = new ArrayList<>(USER_PHOTO_ARR.length);
        while (userPhotoList.size() < count) {
            if (used.size() < USER_PHOTO_ARR.length) {
                int index = random.nextInt(USER_PHOTO_ARR.length);
                if (!used.contains(index)) {
                    userPhotoList.add(PREFIX_URL + USER_PHOTO_ARR[index]);
                    used.add(index);
                }
            } else {
                // 当所有头像都用过,但仍未满足需求数量, 则不在随机, 按已经生成的顺序直接获取, 保证相同的头像距离最远
                userPhotoList.add(userPhotoList.get(userPhotoList.size() % USER_PHOTO_ARR.length));

            }
        }
        return userPhotoList;

    }

    private static final List<String> SeriesReplyHeads = new ArrayList<String>() {{
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/1.webp");
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/2.webp");
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/3.webp");
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/4.webp");
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/5.webp");
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/6.webp");
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/7.webp");
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/8.webp");
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/9.webp");
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/10.webp");
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/11.webp");
        add("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/12.webp");
    }};

    /**
     * 获取热评头像信息
     *
     * @param count
     * @return
     */
    public static List<String> getSeriesReplyHeadImg(int count) {
        List<String> imgList = new ArrayList<>();
        Random random = new Random();
        Set<Integer> indices = new HashSet<>();

        while (indices.size() < count && indices.size() < SeriesReplyHeads.size()) {
            int index = random.nextInt(SeriesReplyHeads.size());
            indices.add(index);
        }

        for (int index : indices) {
            imgList.add(SeriesReplyHeads.get(index));
        }

        return imgList;
    }

}
