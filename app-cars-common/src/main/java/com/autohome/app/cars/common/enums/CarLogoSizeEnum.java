package com.autohome.app.cars.common.enums;

public enum CarLogoSizeEnum {
    /// <summary>
    /// Logo原尺寸或接口给定尺寸
    /// </summary>
    SouceLogoSize(0),
    /// <summary>
    /// 800有水印),前缀u_
    /// </summary>
    Watermark800(1),
    /// <summary>
    /// 800无水印),前缀ys_
    /// </summary>
    NoWatermark800(2),
    /// <summary>
    /// 500无水印),前缀cw_
    /// </summary>
    NoWatermark500(3),
    /// <summary>
    /// 500有水印),前缀w_
    /// </summary>
    Watermark500(4),
    /// <summary>
    /// 400有水印),前缀k_
    /// </summary>
    Watermark400(5),
    /// <summary>
    /// 347有水印),前缀cp_
    /// </summary>
    Watermark347(6),
    /// <summary>
    /// 320无水印),前缀tp_
    /// </summary>
    NoWatermark320(7),
    /// <summary>
    /// 240无水印),前缀t_
    /// </summary>
    NoWatermark240(8),
    /// <summary>
    /// 220无水印),前缀m_
    /// </summary>
    NoWatermark220(9),
    /// <summary>
    /// 120无水印),前缀s_
    /// </summary>
    NoWatermark120(10),
    /// <summary>
    /// 80无水印),前缀l_
    /// </summary>
    NoWatermark80(11);

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private CarLogoSizeEnum(int value) {
        this.value = value;
    }
}
