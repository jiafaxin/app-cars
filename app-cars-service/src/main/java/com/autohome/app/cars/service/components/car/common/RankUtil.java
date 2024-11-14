package com.autohome.app.cars.service.components.car.common;

import autohome.rpc.car.app_cars.v1.carbase.Pvitem;
import com.autohome.app.cars.common.utils.ImageSizeEnum;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.common.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import com.autohome.app.cars.common.utils.UrlUtil;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author zhangchengtao
 * @date 2024/4/29 20:46
 */
@Slf4j
public class RankUtil {
    private static final Map<String, Integer> BASE_CLASS_TYPES_MAP = new HashMap<>(11);

    static {
        BASE_CLASS_TYPES_MAP.put("int", 1);
        BASE_CLASS_TYPES_MAP.put("long", 2);
        BASE_CLASS_TYPES_MAP.put("float", 3);
        BASE_CLASS_TYPES_MAP.put("double", 4);
        BASE_CLASS_TYPES_MAP.put("boolean", 5);
        BASE_CLASS_TYPES_MAP.put("java.lang.Integer", 1);
        BASE_CLASS_TYPES_MAP.put("java.lang.Long", 2);
        BASE_CLASS_TYPES_MAP.put("java.lang.Double", 4);
        BASE_CLASS_TYPES_MAP.put("java.lang.Float", 3);
        BASE_CLASS_TYPES_MAP.put("java.lang.Boolean", 5);
        BASE_CLASS_TYPES_MAP.put("java.lang.String", 6);
    }

    /**
     * 通过每页数量和总数据个数计算总页数
     *
     * @param total    数据总个数
     * @param pageSize 每页个数
     * @return 总页数
     */
    public static int calcPageCount(int total, int pageSize) {
        return total / pageSize + ((total % pageSize) == 0 ? 0 : 1);
    }

    /**
     * 重新裁剪车系图片
     *
     * @param url 车系图片URL
     * @return 裁剪后的URL
     */
    public static String resizeSeriesImage(String url) {
        return ImageUtils.convertImageUrl(url, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_No_Opt, true, true, true);
    }

    /**
     * 重新裁剪车系图片
     *
     * @param url 车系图片URL
     * @return 裁剪后的URL
     */
    public static String resizeBrandImage(String url) {
        return ImageUtils.convertImageUrl(url, true, false, false, ImageSizeEnum.ImgSize_1x1_100x100_NO_OPT, true, true, true);
    }

    public static <T> String serializeObject(List<T> objList, Class<T> clazz) {
        Field[] fields = getAllFields(clazz);
        List<String> resultList = new ArrayList<>();
        for (T t : objList) {
            List<Object> jsonArr = new ArrayList<>(fields.length);
            for (Field field : fields) {
                try {
                    jsonArr.add(field.get(t));
                } catch (IllegalAccessException e) {
                    log.error("Failed to serialize object", e);
                }
            }
            resultList.add(jsonArr.toString());
        }
        return resultList.toString();
    }

    public static <T> List<T> deserializeObject(String str, Class<T> clazz) {
        String[] split = str.substring(2, str.length() - 2).split("], \\[");
        Field[] fields = getAllFields(clazz);
        List<T> resultList = new ArrayList<>(split.length);
        for (String object : split) {
            String[] fieldValArr = object.split(", ");
            try {
                if (fields[0].getName().equals("seriesId")) {
                    Constructor<T> constructor = clazz.getConstructor();
                    T t = constructor.newInstance();
                    for (int i = 0; i < fields.length; i++) {
                        String val = fieldValArr[i].trim();
                        Field field = fields[i];
                        if ("null".equals(val)) {
                            continue;
                        }
                        field.set(t, parseBaseObjByType(field.getType(), val));
                    }
                    resultList.add(t);
                }
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                log.error("反序列化数据错误",e);
                throw new RuntimeException(e);
            }
        }
        return resultList;
    }


    public static Object parseBaseObjByType(Type type, String str) {
        String typeName = type.getTypeName();
        return switch (typeName) {
            case "java.lang.Integer","int", "Integer" -> Integer.parseInt(str);
            case "java.lang.Long", "long", "Long"  -> Long.parseLong(str);
            case "Double", "double", "java.lang.Double" -> Double.parseDouble(str);
            case "Float", "float", "java.lang.Float" -> Float.parseFloat(str);
            case "Boolean", "boolean", "java.lang.Boolean" -> Boolean.parseBoolean(str);
            default -> str;
        };
    }

    public static <T, V> Map<T, V> createMap(Field field, String str) {
        String[] split = str.substring(1, str.length() - 1).split(",");
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Map<T, V> map = new HashMap<>();
            for (String s : split) {
                String[] split1 = s.split("=");
                map.put((T) parseBaseObjByType(actualTypeArguments[0], split1[0].trim()), (V) parseBaseObjByType(actualTypeArguments[1], split1[1].trim()));
            }
            return map;
        }

        return null;
    }

    /**
     * 获取时间范围Key 若起止时间相同 则 返回开始时间 若不同 则返回 begin_end 格式
     * @param begin 开始时间
     * @param end 结束时间
     * @return 时间范围Key
     */
    public static String getDataRange(String begin, String end) {
        return begin.equals(end) ? begin : begin + StrPool.UNDERLINE + end;
    }


    /**
     * 获取本类及其父类的字段属性
     * @param clazz 当前类对象
     * @return 字段数组
     */
    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        fieldList.parallelStream().forEach(x-> x.setAccessible(true));
        fieldList.sort((o1, o2) -> {
            if (o1.getName().equals("seriesId")) {
                return -1;
            }
            if (o2.getName().equals("seriesId")) {
                return 1;
            }
            return o1.getName().compareTo(o2.getName());
        });
        return fieldList.toArray(new Field[0]);
    }

    /**
     * 生成priceLinkUrl
     *
     * @param pm       平台
     * @param channel  总榜/场景榜
     * @param seriesId 车系ID
     * @return PriceLinkUrl
     */
    public static String genPriceLinkUrl(int pm, int channel, int seriesId) {
        String priceLinkUrlScheme = "autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=%s";
        String priceUrlScheme = "rn://MallService/AskPrice?panValid=0&pvareaid=6849804&seriesid=%s&eid=%s";
        return String.format(priceLinkUrlScheme,
                UrlUtil.encode(
                        String.format(priceUrlScheme, seriesId,
                                UrlUtil.encode(getRankEid(channel, pm))
                        )
                )
        );
    }

    /**
     * 获取榜单迁移后的EID
     *
     * @param channel 场景榜/总榜
     * @param pm      平台 安卓/iOS
     * @return Eid
     */
    public static String getRankEid(int channel, int pm) {
        String eid;
        if (channel == 0) {
            eid = pm == 1 ? "3|1411002|572|25528|205415|304431" : "3|1412002|572|25528|205415|304430";
        } else {
            eid = pm == 1 ? "3|1411002|572|25529|205414|304429" : "3|1412002|572|25529|205414|304428";
        }
        return eid;
    }


    public static Pvitem genPvItem(Map<String, String> args, String clickEventId, String showEventId) {
        return Pvitem.newBuilder()
                .putAllArgvs(args)
                .setClick(Pvitem.Click.newBuilder().setEventid(clickEventId))
                .setShow(Pvitem.Show.newBuilder().setEventid(showEventId)).build();
    }
}
