package com.autohome.app.cars.common.utils;

/**
 * 图片尺寸规则枚举
 */
public enum ImageSizeEnum {
    /**
     * 400x225  16:9 图片
     */
    ImgSize_16x9_400x225("400x225_0_q87_c43_"),
    /**
     * 400x300  4:3 图片
     */
    ImgSize_4x3_400x300("400x300_0_q87_c42_"),

    ImgSize_4x3_400x300_Only("400x300_0_q87_c42_"),

    ImgSize_4x3_1360x1000("1360x1000_0_q87_c42_"),

    ImgSize_4x3_400x300c42("400x300_c42_"),
    /**
     * 200x200  1:1 图片
     */
    ImgSize_1x1_200x200("200x200_0_q87_c40_"),

    /**
     * 50x50  1:1 图片
     */
    ImgSize_1x1_50x50("50x50_0_q87_c40_"),

    ImgSize_1x1_16x16("16x16_0_q87_c40_"),

    ImgSize_2x1_34x16("34x16_0_q87_c40_"),

    ImgSize_4x3_200x150("200x150_"),
    /**
     * 400x400  1:1 图片
     */
    ImgSize_1x1_400x400("400x400_0_q87_c40_"),
    /**
     * 640x360  16:9 图片
     */
    ImgSize_16x9_640x360("640x360_0_q87_c43_"),
    /**
     * 690x388  16:9 图片
     */
    ImgSize_16x9_690x388("690x388_0_q87_c43_"),
    /**
     * 800x450  16:9 图片
     */
    ImgSize_16x9_800x450("800x450_0_q87_c43_"),
    /**
     * 560x315  16:9 图片
     */
    ImgSize_16x9_560x315("560x315_0_q87_c43_"),
    /**
     * 640x480  4:3 图片
     */
    ImgSize_4x3_640x480("640x480_0_q87_c42_"),
    ImgSize_4x3_1280x960("1280x960_0_q87_c42_"),
    ImgSize_4x3_800x600("800x600_0_q87_c42_"),
    /**
     * 690x230  3:1 图片
     */
    ImgSize_3x1_690x230("690x230_0_q87_c43_"),
    /**
     * 640x320  2:1图片
     */
    ImgSize_2x1_640x320("640x320_0_q87_c41_"),
    /**
     * 750x375  2:1图片
     */
    ImgSize_2x1_750x375("750x375_0_q87_c41_"),
    /**
     * 120x120  1:1图片
     */
    ImgSize_1x1_120x120("120x120_0_q87_c40_"),
    /**
     * 100x100  1:1图片
     */
    ImgSize_1x1_100x100("100x100_0_q87_c40_"),

    /**
     * 不带修饰参数的100x100  1:1图片
     */
    ImgSize_1x1_100x100_NO_OPT("100x100_"),
    /**
     * 32x32  1:1图片
     */
    ImgSize_1x1_32x32("32x32_0_q87_c40_"),
    /**
     * 360x640 9:16图片
     */
    ImgSize_9x16_360x640("360x640_0_q87_c47_"),
    /**
     * 60x60  1:1图片
     */
    ImgSize_1x1_60x60("60x60_0_q87_c40_"),
    /**
     * 300x0  宽-300，高-按比例裁切
     */
    ImgSize_WxH_300x0("300x0_q87_"),
    /**
     * 400x0  宽-400，高-按比例裁切
     */
    ImgSize_WxH_400x0("400x0_q87_"),
    /**
     * 论坛图片规则，非通用规则
     */
    ImgSize_4x3_400x0("400_"),
    /**
     * 论坛图片规则，非通用规则
     */
    ImgSize_4x3_500x0("500_"),
    /**
     * 论坛图片规则，非通用规则
     */
    ImgSize_4x3_820x0("820_"),
    /**
     * 论坛图片规则，非通用规则
     */
    ImgSizeVR_4x3_640x0("640x0_"),
    /**
     * 论坛图片规则，非通用规则
     */
    ImgSizeVR_4x3_200x0("200x0_"),
    ImgSize_4x3_400x300_No_Opt("400x300_"),
    ImgSize_4x3_320x240("320x240_"),
    /**
     * VR图片专用
     */
    ImgSize_900x600_k1("900x600_k1_"),
    ImgSize_100x100("100x100_"),
    ImgSize_4x3_800x600_Without_Opt("800x600_"),
    ImgSize_4x3_400x300_Without_Opts("400x300_"),
    ImgSize_WxH_300x0_("300x0_"),
    ImgSize_WxH_800x0_("800x0_"),
    ImgSize_1500x0("1500x0_"),
    ImgSize_16x9_684x0("684x0_0_q100_c43_"),

    ImgSize_16x9_1040x585("1040x585_0_q87_c43_")
    ;
    //ImgSize_750x0("750x0_");

    // 成员变量
    private String size;
    private String desc;

    // 构造方法
    private ImageSizeEnum(String size) {
        this.size = size;
        this.desc = desc;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
