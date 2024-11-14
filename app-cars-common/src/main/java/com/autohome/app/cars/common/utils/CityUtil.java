package com.autohome.app.cars.common.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityUtil {

    static Map<Integer,String> Citys;
    static Map<String,Integer> CityNames;

    static Map<Integer,ZoneInfo> CityZone;

    static Map<Integer, String> Provinces = new HashMap<>();
    static Map<String, Integer> ProvinceNames = new HashMap<>();
    static {
        try {
            Citys = new HashMap<>();
            CityNames = new HashMap<>();
            String city_string = "[{\"code\":\"522600\",\"name\":\"黔东南\"},{\"code\":\"340800\",\"name\":\"安庆\"},{\"code\":\"340300\",\"name\":\"蚌埠\"},{\"code\":\"341600\",\"name\":\"亳州\"},{\"code\":\"341700\",\"name\":\"池州\"},{\"code\":\"341100\",\"name\":\"滁州\"},{\"code\":\"341200\",\"name\":\"阜阳\"},{\"code\":\"340400\",\"name\":\"淮南\"},{\"code\":\"340100\",\"name\":\"合肥\"},{\"code\":\"341000\",\"name\":\"黄山\"},{\"code\":\"340600\",\"name\":\"淮北\"},{\"code\":\"341500\",\"name\":\"六安\"},{\"code\":\"340500\",\"name\":\"马鞍山\"},{\"code\":\"341300\",\"name\":\"宿州\"},{\"code\":\"340700\",\"name\":\"铜陵\"},{\"code\":\"340200\",\"name\":\"芜湖\"},{\"code\":\"341800\",\"name\":\"宣城\"},{\"code\":\"820100\",\"name\":\"澳门\"},{\"code\":\"110100\",\"name\":\"北京\"},{\"code\":\"500100\",\"name\":\"重庆\"},{\"code\":\"350100\",\"name\":\"福州\"},{\"code\":\"350800\",\"name\":\"龙岩\"},{\"code\":\"350900\",\"name\":\"宁德\"},{\"code\":\"350700\",\"name\":\"南平\"},{\"code\":\"350300\",\"name\":\"莆田\"},{\"code\":\"350500\",\"name\":\"泉州\"},{\"code\":\"350400\",\"name\":\"三明\"},{\"code\":\"350200\",\"name\":\"厦门\"},{\"code\":\"350600\",\"name\":\"漳州\"},{\"code\":\"445100\",\"name\":\"潮州\"},{\"code\":\"441900\",\"name\":\"东莞\"},{\"code\":\"440600\",\"name\":\"佛山\"},{\"code\":\"440100\",\"name\":\"广州\"},{\"code\":\"441300\",\"name\":\"惠州\"},{\"code\":\"441600\",\"name\":\"河源\"},{\"code\":\"440700\",\"name\":\"江门\"},{\"code\":\"445200\",\"name\":\"揭阳\"},{\"code\":\"440900\",\"name\":\"茂名\"},{\"code\":\"441400\",\"name\":\"梅州\"},{\"code\":\"441800\",\"name\":\"清远\"},{\"code\":\"440500\",\"name\":\"汕头\"},{\"code\":\"441500\",\"name\":\"汕尾\"},{\"code\":\"440200\",\"name\":\"韶关\"},{\"code\":\"440300\",\"name\":\"深圳\"},{\"code\":\"441700\",\"name\":\"阳江\"},{\"code\":\"445300\",\"name\":\"云浮\"},{\"code\":\"442000\",\"name\":\"中山\"},{\"code\":\"440400\",\"name\":\"珠海\"},{\"code\":\"441200\",\"name\":\"肇庆\"},{\"code\":\"440800\",\"name\":\"湛江\"},{\"code\":\"450500\",\"name\":\"北海\"},{\"code\":\"451000\",\"name\":\"百色\"},{\"code\":\"451400\",\"name\":\"崇左\"},{\"code\":\"450600\",\"name\":\"防城港\"},{\"code\":\"450800\",\"name\":\"贵港\"},{\"code\":\"450300\",\"name\":\"桂林\"},{\"code\":\"451100\",\"name\":\"贺州\"},{\"code\":\"451200\",\"name\":\"河池\"},{\"code\":\"451300\",\"name\":\"来宾\"},{\"code\":\"450200\",\"name\":\"柳州\"},{\"code\":\"450100\",\"name\":\"南宁\"},{\"code\":\"450700\",\"name\":\"钦州\"},{\"code\":\"450400\",\"name\":\"梧州\"},{\"code\":\"450900\",\"name\":\"玉林\"},{\"code\":\"520400\",\"name\":\"安顺\"},{\"code\":\"520500\",\"name\":\"毕节\"},{\"code\":\"522700\",\"name\":\"黔南\"},{\"code\":\"520100\",\"name\":\"贵阳\"},{\"code\":\"520200\",\"name\":\"六盘水\"},{\"code\":\"520600\",\"name\":\"铜仁\"},{\"code\":\"522300\",\"name\":\"黔西南\"},{\"code\":\"520300\",\"name\":\"遵义\"},{\"code\":\"620400\",\"name\":\"白银\"},{\"code\":\"621100\",\"name\":\"定西\"},{\"code\":\"623000\",\"name\":\"甘南\"},{\"code\":\"620900\",\"name\":\"酒泉\"},{\"code\":\"620200\",\"name\":\"嘉峪关\"},{\"code\":\"620300\",\"name\":\"金昌\"},{\"code\":\"620100\",\"name\":\"兰州\"},{\"code\":\"621200\",\"name\":\"陇南\"},{\"code\":\"622900\",\"name\":\"临夏\"},{\"code\":\"620800\",\"name\":\"平凉\"},{\"code\":\"621000\",\"name\":\"庆阳\"},{\"code\":\"620500\",\"name\":\"天水\"},{\"code\":\"620600\",\"name\":\"武威\"},{\"code\":\"620700\",\"name\":\"张掖\"},{\"code\":\"469025\",\"name\":\"白沙\"},{\"code\":\"469029\",\"name\":\"保亭\"},{\"code\":\"469026\",\"name\":\"昌江\"},{\"code\":\"469023\",\"name\":\"澄迈\"},{\"code\":\"469007\",\"name\":\"东方\"},{\"code\":\"469021\",\"name\":\"定安\"},{\"code\":\"460400\",\"name\":\"儋州\"},{\"code\":\"460100\",\"name\":\"海口\"},{\"code\":\"469024\",\"name\":\"临高\"},{\"code\":\"469027\",\"name\":\"乐东\"},{\"code\":\"469028\",\"name\":\"陵水\"},{\"code\":\"469030\",\"name\":\"琼中\"},{\"code\":\"469002\",\"name\":\"琼海\"},{\"code\":\"460200\",\"name\":\"三亚\"},{\"code\":\"469022\",\"name\":\"屯昌\"},{\"code\":\"469001\",\"name\":\"五指山\"},{\"code\":\"469005\",\"name\":\"文昌\"},{\"code\":\"469006\",\"name\":\"万宁\"},{\"code\":\"410500\",\"name\":\"安阳\"},{\"code\":\"410600\",\"name\":\"鹤壁\"},{\"code\":\"410800\",\"name\":\"焦作\"},{\"code\":\"419001\",\"name\":\"济源市\"},{\"code\":\"410200\",\"name\":\"开封\"},{\"code\":\"410300\",\"name\":\"洛阳\"},{\"code\":\"411100\",\"name\":\"漯河\"},{\"code\":\"411300\",\"name\":\"南阳\"},{\"code\":\"410900\",\"name\":\"濮阳\"},{\"code\":\"410400\",\"name\":\"平顶山\"},{\"code\":\"411200\",\"name\":\"三门峡\"},{\"code\":\"411400\",\"name\":\"商丘\"},{\"code\":\"411500\",\"name\":\"信阳\"},{\"code\":\"411000\",\"name\":\"许昌\"},{\"code\":\"410700\",\"name\":\"新乡\"},{\"code\":\"411600\",\"name\":\"周口\"},{\"code\":\"411700\",\"name\":\"驻马店\"},{\"code\":\"410100\",\"name\":\"郑州\"},{\"code\":\"420700\",\"name\":\"鄂州\"},{\"code\":\"422800\",\"name\":\"恩施\"},{\"code\":\"421100\",\"name\":\"黄冈\"},{\"code\":\"420200\",\"name\":\"黄石\"},{\"code\":\"420800\",\"name\":\"荆门\"},{\"code\":\"421000\",\"name\":\"荆州\"},{\"code\":\"429005\",\"name\":\"潜江\"},{\"code\":\"429021\",\"name\":\"神农架\"},{\"code\":\"421300\",\"name\":\"随州\"},{\"code\":\"420300\",\"name\":\"十堰\"},{\"code\":\"429006\",\"name\":\"天门\"},{\"code\":\"420100\",\"name\":\"武汉\"},{\"code\":\"420600\",\"name\":\"襄阳\"},{\"code\":\"420900\",\"name\":\"孝感\"},{\"code\":\"421200\",\"name\":\"咸宁\"},{\"code\":\"429004\",\"name\":\"仙桃\"},{\"code\":\"420500\",\"name\":\"宜昌\"},{\"code\":\"430100\",\"name\":\"长沙\"},{\"code\":\"430700\",\"name\":\"常德\"},{\"code\":\"431000\",\"name\":\"郴州\"},{\"code\":\"431200\",\"name\":\"怀化\"},{\"code\":\"430400\",\"name\":\"衡阳\"},{\"code\":\"431300\",\"name\":\"娄底\"},{\"code\":\"430500\",\"name\":\"邵阳\"},{\"code\":\"430300\",\"name\":\"湘潭\"},{\"code\":\"433100\",\"name\":\"湘西\"},{\"code\":\"431100\",\"name\":\"永州\"},{\"code\":\"430900\",\"name\":\"益阳\"},{\"code\":\"430600\",\"name\":\"岳阳\"},{\"code\":\"430200\",\"name\":\"株洲\"},{\"code\":\"430800\",\"name\":\"张家界\"},{\"code\":\"130600\",\"name\":\"保定\"},{\"code\":\"130800\",\"name\":\"承德\"},{\"code\":\"130900\",\"name\":\"沧州\"},{\"code\":\"130400\",\"name\":\"邯郸\"},{\"code\":\"131100\",\"name\":\"衡水\"},{\"code\":\"131000\",\"name\":\"廊坊\"},{\"code\":\"130300\",\"name\":\"秦皇岛\"},{\"code\":\"130100\",\"name\":\"石家庄\"},{\"code\":\"130200\",\"name\":\"唐山\"},{\"code\":\"130500\",\"name\":\"邢台\"},{\"code\":\"130700\",\"name\":\"张家口\"},{\"code\":\"230600\",\"name\":\"大庆\"},{\"code\":\"232700\",\"name\":\"大兴安岭\"},{\"code\":\"231100\",\"name\":\"黑河\"},{\"code\":\"230100\",\"name\":\"哈尔滨\"},{\"code\":\"230400\",\"name\":\"鹤岗\"},{\"code\":\"230800\",\"name\":\"佳木斯\"},{\"code\":\"230300\",\"name\":\"鸡西\"},{\"code\":\"231000\",\"name\":\"牡丹江\"},{\"code\":\"230900\",\"name\":\"七台河\"},{\"code\":\"230200\",\"name\":\"齐齐哈尔\"},{\"code\":\"230500\",\"name\":\"双鸭山\"},{\"code\":\"231200\",\"name\":\"绥化\"},{\"code\":\"230700\",\"name\":\"伊春\"},{\"code\":\"320400\",\"name\":\"常州\"},{\"code\":\"320800\",\"name\":\"淮安\"},{\"code\":\"320700\",\"name\":\"连云港\"},{\"code\":\"320600\",\"name\":\"南通\"},{\"code\":\"320100\",\"name\":\"南京\"},{\"code\":\"320500\",\"name\":\"苏州\"},{\"code\":\"321300\",\"name\":\"宿迁\"},{\"code\":\"321200\",\"name\":\"泰州\"},{\"code\":\"320200\",\"name\":\"无锡\"},{\"code\":\"320300\",\"name\":\"徐州\"},{\"code\":\"320900\",\"name\":\"盐城\"},{\"code\":\"321000\",\"name\":\"扬州\"},{\"code\":\"321100\",\"name\":\"镇江\"},{\"code\":\"361000\",\"name\":\"抚州\"},{\"code\":\"360700\",\"name\":\"赣州\"},{\"code\":\"360800\",\"name\":\"吉安\"},{\"code\":\"360200\",\"name\":\"景德镇\"},{\"code\":\"360400\",\"name\":\"九江\"},{\"code\":\"360100\",\"name\":\"南昌\"},{\"code\":\"360300\",\"name\":\"萍乡\"},{\"code\":\"361100\",\"name\":\"上饶\"},{\"code\":\"360500\",\"name\":\"新余\"},{\"code\":\"360600\",\"name\":\"鹰潭\"},{\"code\":\"360900\",\"name\":\"宜春\"},{\"code\":\"220600\",\"name\":\"白山\"},{\"code\":\"220800\",\"name\":\"白城\"},{\"code\":\"220100\",\"name\":\"长春\"},{\"code\":\"220200\",\"name\":\"吉林\"},{\"code\":\"220400\",\"name\":\"辽源\"},{\"code\":\"220300\",\"name\":\"四平\"},{\"code\":\"220700\",\"name\":\"松原\"},{\"code\":\"220500\",\"name\":\"通化\"},{\"code\":\"222400\",\"name\":\"延边\"},{\"code\":\"210300\",\"name\":\"鞍山\"},{\"code\":\"210500\",\"name\":\"本溪\"},{\"code\":\"211300\",\"name\":\"朝阳\"},{\"code\":\"210600\",\"name\":\"丹东\"},{\"code\":\"210200\",\"name\":\"大连\"},{\"code\":\"210400\",\"name\":\"抚顺\"},{\"code\":\"210900\",\"name\":\"阜新\"},{\"code\":\"211400\",\"name\":\"葫芦岛\"},{\"code\":\"210700\",\"name\":\"锦州\"},{\"code\":\"211000\",\"name\":\"辽阳\"},{\"code\":\"211100\",\"name\":\"盘锦\"},{\"code\":\"210100\",\"name\":\"沈阳\"},{\"code\":\"211200\",\"name\":\"铁岭\"},{\"code\":\"210800\",\"name\":\"营口\"},{\"code\":\"152900\",\"name\":\"阿拉善盟\"},{\"code\":\"150200\",\"name\":\"包头\"},{\"code\":\"150800\",\"name\":\"巴彦淖尔\"},{\"code\":\"150400\",\"name\":\"赤峰\"},{\"code\":\"150600\",\"name\":\"鄂尔多斯\"},{\"code\":\"150700\",\"name\":\"呼伦贝尔\"},{\"code\":\"150100\",\"name\":\"呼和浩特\"},{\"code\":\"150500\",\"name\":\"通辽\"},{\"code\":\"150300\",\"name\":\"乌海\"},{\"code\":\"150900\",\"name\":\"乌兰察布\"},{\"code\":\"152200\",\"name\":\"兴安盟\"},{\"code\":\"152500\",\"name\":\"锡林郭勒盟\"},{\"code\":\"640400\",\"name\":\"固原\"},{\"code\":\"640200\",\"name\":\"石嘴山\"},{\"code\":\"640300\",\"name\":\"吴忠\"},{\"code\":\"640100\",\"name\":\"银川\"},{\"code\":\"640500\",\"name\":\"中卫\"},{\"code\":\"632600\",\"name\":\"果洛\"},{\"code\":\"632800\",\"name\":\"海西\"},{\"code\":\"630200\",\"name\":\"海东\"},{\"code\":\"632200\",\"name\":\"海北\"},{\"code\":\"632300\",\"name\":\"黄南\"},{\"code\":\"632500\",\"name\":\"海南\"},{\"code\":\"630100\",\"name\":\"西宁\"},{\"code\":\"632700\",\"name\":\"玉树\"},{\"code\":\"610900\",\"name\":\"安康\"},{\"code\":\"610300\",\"name\":\"宝鸡\"},{\"code\":\"610700\",\"name\":\"汉中\"},{\"code\":\"611000\",\"name\":\"商洛\"},{\"code\":\"610200\",\"name\":\"铜川\"},{\"code\":\"610500\",\"name\":\"渭南\"},{\"code\":\"610100\",\"name\":\"西安\"},{\"code\":\"610400\",\"name\":\"咸阳\"},{\"code\":\"610600\",\"name\":\"延安\"},{\"code\":\"610800\",\"name\":\"榆林\"},{\"code\":\"513200\",\"name\":\"阿坝\"},{\"code\":\"511900\",\"name\":\"巴中\"},{\"code\":\"510100\",\"name\":\"成都\"},{\"code\":\"510600\",\"name\":\"德阳\"},{\"code\":\"511700\",\"name\":\"达州\"},{\"code\":\"511600\",\"name\":\"广安\"},{\"code\":\"510800\",\"name\":\"广元\"},{\"code\":\"513300\",\"name\":\"甘孜\"},{\"code\":\"513400\",\"name\":\"凉山\"},{\"code\":\"510500\",\"name\":\"泸州\"},{\"code\":\"511100\",\"name\":\"乐山\"},{\"code\":\"511400\",\"name\":\"眉山\"},{\"code\":\"510700\",\"name\":\"绵阳\"},{\"code\":\"511300\",\"name\":\"南充\"},{\"code\":\"511000\",\"name\":\"内江\"},{\"code\":\"510400\",\"name\":\"攀枝花\"},{\"code\":\"510900\",\"name\":\"遂宁\"},{\"code\":\"511800\",\"name\":\"雅安\"},{\"code\":\"511500\",\"name\":\"宜宾\"},{\"code\":\"510300\",\"name\":\"自贡\"},{\"code\":\"512000\",\"name\":\"资阳\"},{\"code\":\"310100\",\"name\":\"上海\"},{\"code\":\"140400\",\"name\":\"长治\"},{\"code\":\"140200\",\"name\":\"大同\"},{\"code\":\"140500\",\"name\":\"晋城\"},{\"code\":\"140700\",\"name\":\"晋中\"},{\"code\":\"141000\",\"name\":\"临汾\"},{\"code\":\"141100\",\"name\":\"吕梁\"},{\"code\":\"140600\",\"name\":\"朔州\"},{\"code\":\"140100\",\"name\":\"太原\"},{\"code\":\"140900\",\"name\":\"忻州\"},{\"code\":\"140800\",\"name\":\"运城\"},{\"code\":\"140300\",\"name\":\"阳泉\"},{\"code\":\"371600\",\"name\":\"滨州\"},{\"code\":\"371400\",\"name\":\"德州\"},{\"code\":\"370500\",\"name\":\"东营\"},{\"code\":\"371700\",\"name\":\"菏泽\"},{\"code\":\"370800\",\"name\":\"济宁\"},{\"code\":\"370100\",\"name\":\"济南\"},{\"code\":\"371500\",\"name\":\"聊城\"},{\"code\":\"371300\",\"name\":\"临沂\"},{\"code\":\"370200\",\"name\":\"青岛\"},{\"code\":\"371100\",\"name\":\"日照\"},{\"code\":\"370900\",\"name\":\"泰安\"},{\"code\":\"371000\",\"name\":\"威海\"},{\"code\":\"370700\",\"name\":\"潍坊\"},{\"code\":\"370600\",\"name\":\"烟台\"},{\"code\":\"370300\",\"name\":\"淄博\"},{\"code\":\"370400\",\"name\":\"枣庄\"},{\"code\":\"120100\",\"name\":\"天津\"},{\"code\":\"710100\",\"name\":\"台湾\"},{\"code\":\"810100\",\"name\":\"香港\"},{\"code\":\"652900\",\"name\":\"阿克苏\"},{\"code\":\"654300\",\"name\":\"阿勒泰\"},{\"code\":\"659002\",\"name\":\"阿拉尔\"},{\"code\":\"652700\",\"name\":\"博尔塔拉\"},{\"code\":\"652800\",\"name\":\"巴音郭楞\"},{\"code\":\"652300\",\"name\":\"昌吉\"},{\"code\":\"653200\",\"name\":\"和田\"},{\"code\":\"650500\",\"name\":\"哈密\"},{\"code\":\"650200\",\"name\":\"克拉玛依\"},{\"code\":\"653000\",\"name\":\"克孜勒苏\"},{\"code\":\"653100\",\"name\":\"喀什\"},{\"code\":\"659001\",\"name\":\"石河子\"},{\"code\":\"659003\",\"name\":\"图木舒克\"},{\"code\":\"654200\",\"name\":\"塔城\"},{\"code\":\"650400\",\"name\":\"吐鲁番\"},{\"code\":\"650100\",\"name\":\"乌鲁木齐\"},{\"code\":\"659004\",\"name\":\"五家渠\"},{\"code\":\"654000\",\"name\":\"伊犁\"},{\"code\":\"542500\",\"name\":\"阿里\"},{\"code\":\"540300\",\"name\":\"昌都\"},{\"code\":\"540100\",\"name\":\"拉萨\"},{\"code\":\"540400\",\"name\":\"林芝\"},{\"code\":\"540600\",\"name\":\"那曲\"},{\"code\":\"540200\",\"name\":\"日喀则\"},{\"code\":\"540500\",\"name\":\"山南\"},{\"code\":\"530500\",\"name\":\"保山\"},{\"code\":\"532300\",\"name\":\"楚雄\"},{\"code\":\"532900\",\"name\":\"大理\"},{\"code\":\"533100\",\"name\":\"德宏\"},{\"code\":\"533400\",\"name\":\"迪庆\"},{\"code\":\"532500\",\"name\":\"红河\"},{\"code\":\"530100\",\"name\":\"昆明\"},{\"code\":\"530900\",\"name\":\"临沧\"},{\"code\":\"530700\",\"name\":\"丽江\"},{\"code\":\"533300\",\"name\":\"怒江\"},{\"code\":\"530800\",\"name\":\"普洱\"},{\"code\":\"530300\",\"name\":\"曲靖\"},{\"code\":\"532600\",\"name\":\"文山\"},{\"code\":\"532800\",\"name\":\"西双版纳\"},{\"code\":\"530400\",\"name\":\"玉溪\"},{\"code\":\"530600\",\"name\":\"昭通\"},{\"code\":\"330100\",\"name\":\"杭州\"},{\"code\":\"330500\",\"name\":\"湖州\"},{\"code\":\"330700\",\"name\":\"金华\"},{\"code\":\"330400\",\"name\":\"嘉兴\"},{\"code\":\"331100\",\"name\":\"丽水\"},{\"code\":\"330200\",\"name\":\"宁波\"},{\"code\":\"330800\",\"name\":\"衢州\"},{\"code\":\"330600\",\"name\":\"绍兴\"},{\"code\":\"331000\",\"name\":\"台州\"},{\"code\":\"330300\",\"name\":\"温州\"},{\"code\":\"330900\",\"name\":\"舟山\"},{\"code\":\"659007\",\"name\":\"双河市\"},{\"code\":\"659006\",\"name\":\"铁门关市\"},{\"code\":\"659008\",\"name\":\"可克达拉市\"},{\"code\":\"659009\",\"name\":\"昆玉\"},{\"code\":\"659005\",\"name\":\"北屯市\"},{\"code\":\"460300\",\"name\":\"三沙市\"}]";
            JSONArray jsonArray = new JSONArray(city_string);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                int id = Integer.parseInt(obj.getString("code"));
                String name = obj.getString("name");
                Citys.put(id, name );
                CityNames.put(name,id);
            }
        }catch (Exception e){

        }
        try {
            initCityZone();
        }catch (Exception e){

        }

        try {
            Provinces  = new HashMap<>();
            ProvinceNames = new HashMap<>();
            String province_string ="[{\"pcode\":\"340000\",\"province\":\"安徽\"},{\"pcode\":\"820000\",\"province\":\"澳门\"},{\"pcode\":\"110000\",\"province\":\"北京\"},{\"pcode\":\"500000\",\"province\":\"重庆\"},{\"pcode\":\"350000\",\"province\":\"福建\"},{\"pcode\":\"440000\",\"province\":\"广东\"},{\"pcode\":\"450000\",\"province\":\"广西\"},{\"pcode\":\"520000\",\"province\":\"贵州\"},{\"pcode\":\"620000\",\"province\":\"甘肃\"},{\"pcode\":\"460000\",\"province\":\"海南\"},{\"pcode\":\"410000\",\"province\":\"河南\"},{\"pcode\":\"420000\",\"province\":\"湖北\"},{\"pcode\":\"430000\",\"province\":\"湖南\"},{\"pcode\":\"130000\",\"province\":\"河北\"},{\"pcode\":\"230000\",\"province\":\"黑龙江\"},{\"pcode\":\"320000\",\"province\":\"江苏\"},{\"pcode\":\"360000\",\"province\":\"江西\"},{\"pcode\":\"220000\",\"province\":\"吉林\"},{\"pcode\":\"210000\",\"province\":\"辽宁\"},{\"pcode\":\"150000\",\"province\":\"内蒙古\"},{\"pcode\":\"640000\",\"province\":\"宁夏\"},{\"pcode\":\"630000\",\"province\":\"青海\"},{\"pcode\":\"610000\",\"province\":\"陕西\"},{\"pcode\":\"510000\",\"province\":\"四川\"},{\"pcode\":\"310000\",\"province\":\"上海\"},{\"pcode\":\"140000\",\"province\":\"山西\"},{\"pcode\":\"370000\",\"province\":\"山东\"},{\"pcode\":\"120000\",\"province\":\"天津\"},{\"pcode\":\"710000\",\"province\":\"台湾\"},{\"pcode\":\"810000\",\"province\":\"香港\"},{\"pcode\":\"650000\",\"province\":\"新疆\"},{\"pcode\":\"540000\",\"province\":\"西藏\"},{\"pcode\":\"530000\",\"province\":\"云南\"},{\"pcode\":\"330000\",\"province\":\"浙江\"}]";
            JSONArray provinceArray = new JSONArray(province_string);
            for (int i = 0; i < provinceArray.length(); i++) {
                JSONObject obj = provinceArray.getJSONObject(i);
                int id = Integer.parseInt(obj.getString("pcode"));
                String name =  obj.getString("province");
                Provinces.put(id, name);
                ProvinceNames.put(name,id);
            }
        } catch (Exception e) {
        }
    }

    public static List<Integer> getAllCityIds(){
        return Citys.keySet().stream().toList();
    }

    public static List<Integer> getAllProvinceIds(){
        return Provinces.keySet().stream().toList();
    }

    public static String getCityName(int cityId){
        return Citys.get(cityId);
    }

    public static boolean isDefaultCity(int cityId,int noDefaultCityId){
        if(cityId==110100 && noDefaultCityId != cityId)
            return true;
        return false;
    }

    public static boolean isGangAoTaiCity(int cityId){
        return Arrays.asList(810100,820100,710100).contains(cityId);
    }

    public static int getProvinceId(int cityId){
        return cityId / 10000 * 10000;
    }


    public static int getProvinceByName(String name) {
        if (ProvinceNames.containsKey(name)) {
            return ProvinceNames.get(name);
        }
        return 0;
    }

    public static int getCityByName(String name){
        if(CityNames.containsKey(name)) {
            return CityNames.get(name);
        }
        return 0;
    }


    public static String getProvinceName(int cityid) {
        int pcode = getProvinceId(cityid);
        if (Provinces.containsKey(pcode)) {
            return Provinces.get(pcode);
        } else {
            return "";
        }
    }
    public record ZoneInfo(String bl, int kb){}

    public static ZoneInfo getZone(int cityId){
        if(CityZone==null||CityZone.size()==0){
            initCityZone();
        }
        return CityZone.get(cityId);
    }


    static void initCityZone() {
        CityZone = new HashMap<>();
        CityZone.put(110100, new ZoneInfo("8", 1));
        CityZone.put(120100, new ZoneInfo("8", 1));
        CityZone.put(130100, new ZoneInfo("8", 1));
        CityZone.put(130200, new ZoneInfo("8", 1));
        CityZone.put(130300, new ZoneInfo("8", 1));
        CityZone.put(130400, new ZoneInfo("8", 1));
        CityZone.put(130500, new ZoneInfo("8", 1));
        CityZone.put(130600, new ZoneInfo("8", 1));
        CityZone.put(130700, new ZoneInfo("5", 9));
        CityZone.put(130800, new ZoneInfo("8", 9));
        CityZone.put(130900, new ZoneInfo("8", 1));
        CityZone.put(131000, new ZoneInfo("8", 1));
        CityZone.put(131100, new ZoneInfo("8", 1));
        CityZone.put(140100, new ZoneInfo("5", 1));
        CityZone.put(140200, new ZoneInfo("5", 1));
        CityZone.put(140300, new ZoneInfo("5", 1));
        CityZone.put(140400, new ZoneInfo("5", 1));
        CityZone.put(140500, new ZoneInfo("5", 1));
        CityZone.put(140600, new ZoneInfo("5", 1));
        CityZone.put(140700, new ZoneInfo("5", 1));
        CityZone.put(140800, new ZoneInfo("5", 1));
        CityZone.put(140900, new ZoneInfo("5", 1));
        CityZone.put(141000, new ZoneInfo("5", 1));
        CityZone.put(141100, new ZoneInfo("5", 1));
        CityZone.put(150100, new ZoneInfo("5", 9));
        CityZone.put(150200, new ZoneInfo("5", 9));
        CityZone.put(150300, new ZoneInfo("5", 9));
        CityZone.put(150400, new ZoneInfo("9", 9));
        CityZone.put(150500, new ZoneInfo("9", 9));
        CityZone.put(150600, new ZoneInfo("5", 9));
        CityZone.put(150700, new ZoneInfo("9", 9));
        CityZone.put(150800, new ZoneInfo("5", 10));
        CityZone.put(150900, new ZoneInfo("5", 9));
        CityZone.put(152200, new ZoneInfo("9", 9));
        CityZone.put(152500, new ZoneInfo("5", 9));
        CityZone.put(152900, new ZoneInfo("5", 10));
        CityZone.put(210100, new ZoneInfo("9", 3));
        CityZone.put(210200, new ZoneInfo("8", 2));
        CityZone.put(210300, new ZoneInfo("9", 3));
        CityZone.put(210400, new ZoneInfo("9", 3));
        CityZone.put(210500, new ZoneInfo("9", 3));
        CityZone.put(210600, new ZoneInfo("8", 2));
        CityZone.put(210700, new ZoneInfo("9", 1));
        CityZone.put(210800, new ZoneInfo("8", 2));
        CityZone.put(210900, new ZoneInfo("9", 9));
        CityZone.put(211000, new ZoneInfo("9", 3));
        CityZone.put(211100, new ZoneInfo("8", 1));
        CityZone.put(211200, new ZoneInfo("9", 3));
        CityZone.put(211300, new ZoneInfo("9", 9));
        CityZone.put(211400, new ZoneInfo("8", 1));
        CityZone.put(220100, new ZoneInfo("9", 3));
        CityZone.put(220200, new ZoneInfo("9", 3));
        CityZone.put(220300, new ZoneInfo("9", 3));
        CityZone.put(220400, new ZoneInfo("9", 3));
        CityZone.put(220500, new ZoneInfo("9", 3));
        CityZone.put(220600, new ZoneInfo("9", 3));
        CityZone.put(220700, new ZoneInfo("9", 9));
        CityZone.put(220800, new ZoneInfo("9", 9));
        CityZone.put(222400, new ZoneInfo("9", 3));
        CityZone.put(230100, new ZoneInfo("9", 3));
        CityZone.put(230200, new ZoneInfo("9", 9));
        CityZone.put(230300, new ZoneInfo("9", 3));
        CityZone.put(230400, new ZoneInfo("9", 3));
        CityZone.put(230500, new ZoneInfo("9", 3));
        CityZone.put(230600, new ZoneInfo("9", 9));
        CityZone.put(230700, new ZoneInfo("9", 3));
        CityZone.put(230800, new ZoneInfo("9", 3));
        CityZone.put(230900, new ZoneInfo("9", 3));
        CityZone.put(231000, new ZoneInfo("9", 3));
        CityZone.put(231100, new ZoneInfo("9", 3));
        CityZone.put(231200, new ZoneInfo("9", 3));
        CityZone.put(232700, new ZoneInfo("9", 11));
        CityZone.put(310100, new ZoneInfo("7", 4));
        CityZone.put(320100, new ZoneInfo("7", 4));
        CityZone.put(320200, new ZoneInfo("7", 4));
        CityZone.put(320300, new ZoneInfo("8", 1));
        CityZone.put(320400, new ZoneInfo("7", 4));
        CityZone.put(320500, new ZoneInfo("7", 4));
        CityZone.put(320600, new ZoneInfo("7", 4));
        CityZone.put(320700, new ZoneInfo("8", 1));
        CityZone.put(320800, new ZoneInfo("8", 4));
        CityZone.put(320900, new ZoneInfo("8", 4));
        CityZone.put(321000, new ZoneInfo("7", 4));
        CityZone.put(321100, new ZoneInfo("7", 4));
        CityZone.put(321200, new ZoneInfo("7", 4));
        CityZone.put(321300, new ZoneInfo("8", 1));
        CityZone.put(330100, new ZoneInfo("7", 4));
        CityZone.put(330200, new ZoneInfo("7", 4));
        CityZone.put(330300, new ZoneInfo("7", 5));
        CityZone.put(330400, new ZoneInfo("7", 4));
        CityZone.put(330500, new ZoneInfo("7", 4));
        CityZone.put(330600, new ZoneInfo("7", 4));
        CityZone.put(330700, new ZoneInfo("7", 5));
        CityZone.put(330800, new ZoneInfo("7", 5));
        CityZone.put(330900, new ZoneInfo("7", 4));
        CityZone.put(331000, new ZoneInfo("7", 5));
        CityZone.put(331100, new ZoneInfo("7", 5));
        CityZone.put(340100, new ZoneInfo("7", 4));
        CityZone.put(340200, new ZoneInfo("7", 4));
        CityZone.put(340300, new ZoneInfo("7", 4));
        CityZone.put(340400, new ZoneInfo("7", 4));
        CityZone.put(340500, new ZoneInfo("7", 4));
        CityZone.put(340600, new ZoneInfo("8", 1));
        CityZone.put(340700, new ZoneInfo("7", 4));
        CityZone.put(340800, new ZoneInfo("7", 4));
        CityZone.put(341000, new ZoneInfo("7", 5));
        CityZone.put(341100, new ZoneInfo("7", 4));
        CityZone.put(341200, new ZoneInfo("7", 1));
        CityZone.put(341300, new ZoneInfo("8", 1));
        CityZone.put(341500, new ZoneInfo("7", 4));
        CityZone.put(341600, new ZoneInfo("8", 1));
        CityZone.put(341700, new ZoneInfo("7", 4));
        CityZone.put(341800, new ZoneInfo("7", 4));
        CityZone.put(350100, new ZoneInfo("6", 5));
        CityZone.put(350200, new ZoneInfo("6", 6));
        CityZone.put(350300, new ZoneInfo("6", 5));
        CityZone.put(350400, new ZoneInfo("6", 5));
        CityZone.put(350500, new ZoneInfo("6", 6));
        CityZone.put(350600, new ZoneInfo("6", 6));
        CityZone.put(350700, new ZoneInfo("7", 5));
        CityZone.put(350800, new ZoneInfo("6", 5));
        CityZone.put(350900, new ZoneInfo("6", 5));
        CityZone.put(360100, new ZoneInfo("7", 5));
        CityZone.put(360200, new ZoneInfo("7", 5));
        CityZone.put(360300, new ZoneInfo("7", 5));
        CityZone.put(360400, new ZoneInfo("7", 4));
        CityZone.put(360500, new ZoneInfo("7", 5));
        CityZone.put(360600, new ZoneInfo("7", 5));
        CityZone.put(360700, new ZoneInfo("6", 5));
        CityZone.put(360800, new ZoneInfo("7", 5));
        CityZone.put(360900, new ZoneInfo("7", 5));
        CityZone.put(361000, new ZoneInfo("7", 5));
        CityZone.put(361100, new ZoneInfo("7", 5));
        CityZone.put(370100, new ZoneInfo("8", 1));
        CityZone.put(370200, new ZoneInfo("8", 2));
        CityZone.put(370300, new ZoneInfo("8", 1));
        CityZone.put(370400, new ZoneInfo("8", 1));
        CityZone.put(370500, new ZoneInfo("8", 1));
        CityZone.put(370600, new ZoneInfo("8", 2));
        CityZone.put(370700, new ZoneInfo("8", 1));
        CityZone.put(370800, new ZoneInfo("8", 1));
        CityZone.put(370900, new ZoneInfo("8", 1));
        CityZone.put(371000, new ZoneInfo("8", 2));
        CityZone.put(371100, new ZoneInfo("8", 1));
        CityZone.put(371200, new ZoneInfo("8", 1));
        CityZone.put(371300, new ZoneInfo("8", 1));
        CityZone.put(371400, new ZoneInfo("8", 1));
        CityZone.put(371500, new ZoneInfo("8", 1));
        CityZone.put(371600, new ZoneInfo("8", 1));
        CityZone.put(371700, new ZoneInfo("8", 1));
        CityZone.put(410100, new ZoneInfo("8", 1));
        CityZone.put(410200, new ZoneInfo("8", 1));
        CityZone.put(410300, new ZoneInfo("5", 1));
        CityZone.put(410400, new ZoneInfo("8", 1));
        CityZone.put(410500, new ZoneInfo("8", 1));
        CityZone.put(410600, new ZoneInfo("8", 1));
        CityZone.put(410700, new ZoneInfo("8", 1));
        CityZone.put(410800, new ZoneInfo("8", 1));
        CityZone.put(410900, new ZoneInfo("8", 1));
        CityZone.put(411000, new ZoneInfo("8", 1));
        CityZone.put(411100, new ZoneInfo("8", 1));
        CityZone.put(411200, new ZoneInfo("5", 1));
        CityZone.put(411300, new ZoneInfo("7", 4));
        CityZone.put(411400, new ZoneInfo("8", 1));
        CityZone.put(411500, new ZoneInfo("7", 4));
        CityZone.put(411600, new ZoneInfo("8", 1));
        CityZone.put(411700, new ZoneInfo("8", 1));
        CityZone.put(419001, new ZoneInfo("5", 1));
        CityZone.put(420100, new ZoneInfo("7", 4));
        CityZone.put(420200, new ZoneInfo("7", 4));
        CityZone.put(420300, new ZoneInfo("4", 4));
        CityZone.put(420500, new ZoneInfo("7", 5));
        CityZone.put(420600, new ZoneInfo("7", 4));
        CityZone.put(420700, new ZoneInfo("7", 4));
        CityZone.put(420800, new ZoneInfo("7", 4));
        CityZone.put(420900, new ZoneInfo("7", 4));
        CityZone.put(421000, new ZoneInfo("7", 4));
        CityZone.put(421100, new ZoneInfo("7", 4));
        CityZone.put(421200, new ZoneInfo("7", 4));
        CityZone.put(421300, new ZoneInfo("7", 4));
        CityZone.put(422800, new ZoneInfo("4", 5));
        CityZone.put(429004, new ZoneInfo("7", 4));
        CityZone.put(429005, new ZoneInfo("7", 4));
        CityZone.put(429006, new ZoneInfo("7", 4));
        CityZone.put(429021, new ZoneInfo("4", 4));
        CityZone.put(430100, new ZoneInfo("7", 5));
        CityZone.put(430200, new ZoneInfo("7", 5));
        CityZone.put(430300, new ZoneInfo("7", 5));
        CityZone.put(430400, new ZoneInfo("6", 5));
        CityZone.put(430500, new ZoneInfo("7", 5));
        CityZone.put(430600, new ZoneInfo("7", 4));
        CityZone.put(430700, new ZoneInfo("7", 5));
        CityZone.put(430800, new ZoneInfo("4", 5));
        CityZone.put(430900, new ZoneInfo("7", 5));
        CityZone.put(431000, new ZoneInfo("6", 5));
        CityZone.put(431100, new ZoneInfo("6", 5));
        CityZone.put(431200, new ZoneInfo("7", 5));
        CityZone.put(431300, new ZoneInfo("7", 5));
        CityZone.put(433100, new ZoneInfo("4", 5));
        CityZone.put(440100, new ZoneInfo("6", 6));
        CityZone.put(440200, new ZoneInfo("6", 5));
        CityZone.put(440300, new ZoneInfo("6", 6));
        CityZone.put(440400, new ZoneInfo("6", 6));
        CityZone.put(440500, new ZoneInfo("6", 6));
        CityZone.put(440600, new ZoneInfo("6", 6));
        CityZone.put(440700, new ZoneInfo("6", 6));
        CityZone.put(440800, new ZoneInfo("6", 7));
        CityZone.put(440900, new ZoneInfo("6", 6));
        CityZone.put(441200, new ZoneInfo("6", 6));
        CityZone.put(441300, new ZoneInfo("6", 6));
        CityZone.put(441400, new ZoneInfo("6", 5));
        CityZone.put(441500, new ZoneInfo("6", 6));
        CityZone.put(441600, new ZoneInfo("6", 5));
        CityZone.put(441700, new ZoneInfo("6", 6));
        CityZone.put(441800, new ZoneInfo("6", 5));
        CityZone.put(441900, new ZoneInfo("6", 6));
        CityZone.put(442000, new ZoneInfo("6", 6));
        CityZone.put(445100, new ZoneInfo("6", 6));
        CityZone.put(445200, new ZoneInfo("6", 6));
        CityZone.put(445300, new ZoneInfo("6", 6));
        CityZone.put(450100, new ZoneInfo("6", 6));
        CityZone.put(450200, new ZoneInfo("6", 5));
        CityZone.put(450300, new ZoneInfo("6", 5));
        CityZone.put(450400, new ZoneInfo("6", 6));
        CityZone.put(450500, new ZoneInfo("6", 6));
        CityZone.put(450600, new ZoneInfo("6", 6));
        CityZone.put(450700, new ZoneInfo("6", 6));
        CityZone.put(450800, new ZoneInfo("6", 6));
        CityZone.put(450900, new ZoneInfo("6", 6));
        CityZone.put(451000, new ZoneInfo("3", 6));
        CityZone.put(451100, new ZoneInfo("6", 5));
        CityZone.put(451200, new ZoneInfo("3", 6));
        CityZone.put(451300, new ZoneInfo("6", 6));
        CityZone.put(451400, new ZoneInfo("3", 6));
        CityZone.put(460100, new ZoneInfo("6", 7));
        CityZone.put(460200, new ZoneInfo("6", 7));
        CityZone.put(460300, new ZoneInfo("6", 7));
        CityZone.put(460400, new ZoneInfo("6", 7));
        CityZone.put(469001, new ZoneInfo("6", 7));
        CityZone.put(469002, new ZoneInfo("6", 7));
        CityZone.put(469005, new ZoneInfo("6", 7));
        CityZone.put(469006, new ZoneInfo("6", 7));
        CityZone.put(469007, new ZoneInfo("6", 7));
        CityZone.put(469021, new ZoneInfo("6", 7));
        CityZone.put(469022, new ZoneInfo("6", 7));
        CityZone.put(469023, new ZoneInfo("6", 7));
        CityZone.put(469024, new ZoneInfo("6", 7));
        CityZone.put(469025, new ZoneInfo("6", 7));
        CityZone.put(469026, new ZoneInfo("6", 7));
        CityZone.put(469027, new ZoneInfo("6", 7));
        CityZone.put(469028, new ZoneInfo("6", 7));
        CityZone.put(469029, new ZoneInfo("6", 7));
        CityZone.put(469030, new ZoneInfo("6", 7));
        CityZone.put(500100, new ZoneInfo("4", 5));
        CityZone.put(510100, new ZoneInfo("4", 5));
        CityZone.put(510300, new ZoneInfo("4", 5));
        CityZone.put(510400, new ZoneInfo("1", 6));
        CityZone.put(510500, new ZoneInfo("4", 5));
        CityZone.put(510600, new ZoneInfo("4", 5));
        CityZone.put(510700, new ZoneInfo("4", 5));
        CityZone.put(510800, new ZoneInfo("4", 5));
        CityZone.put(510900, new ZoneInfo("4", 5));
        CityZone.put(511000, new ZoneInfo("4", 5));
        CityZone.put(511100, new ZoneInfo("4", 5));
        CityZone.put(511300, new ZoneInfo("4", 5));
        CityZone.put(511400, new ZoneInfo("4", 5));
        CityZone.put(511500, new ZoneInfo("4", 5));
        CityZone.put(511600, new ZoneInfo("4", 5));
        CityZone.put(511700, new ZoneInfo("4", 5));
        CityZone.put(511800, new ZoneInfo("4", 5));
        CityZone.put(511900, new ZoneInfo("4", 5));
        CityZone.put(512000, new ZoneInfo("4", 5));
        CityZone.put(513200, new ZoneInfo("4", 12));
        CityZone.put(513300, new ZoneInfo("1", 12));
        CityZone.put(513400, new ZoneInfo("4", 8));
        CityZone.put(520100, new ZoneInfo("3", 5));
        CityZone.put(520200, new ZoneInfo("3", 8));
        CityZone.put(520300, new ZoneInfo("4", 5));
        CityZone.put(520400, new ZoneInfo("3", 5));
        CityZone.put(520500, new ZoneInfo("4", 8));
        CityZone.put(520600, new ZoneInfo("4", 5));
        CityZone.put(522300, new ZoneInfo("3", 5));
        CityZone.put(522600, new ZoneInfo("3", 5));
        CityZone.put(522700, new ZoneInfo("3", 6));
        CityZone.put(530100, new ZoneInfo("3", 8));
        CityZone.put(530300, new ZoneInfo("3", 8));
        CityZone.put(530400, new ZoneInfo("3", 8));
        CityZone.put(530500, new ZoneInfo("1", 8));
        CityZone.put(530600, new ZoneInfo("4", 8));
        CityZone.put(530700, new ZoneInfo("1", 8));
        CityZone.put(530800, new ZoneInfo("1", 8));
        CityZone.put(530900, new ZoneInfo("1", 8));
        CityZone.put(532300, new ZoneInfo("1", 8));
        CityZone.put(532500, new ZoneInfo("3", 8));
        CityZone.put(532600, new ZoneInfo("3", 6));
        CityZone.put(532800, new ZoneInfo("1", 7));
        CityZone.put(532900, new ZoneInfo("1", 8));
        CityZone.put(533100, new ZoneInfo("1", 7));
        CityZone.put(533300, new ZoneInfo("1", 8));
        CityZone.put(533400, new ZoneInfo("1", 12));
        CityZone.put(540100, new ZoneInfo("1", 12));
        CityZone.put(540200, new ZoneInfo("1", 12));
        CityZone.put(540300, new ZoneInfo("1", 12));
        CityZone.put(540400, new ZoneInfo("1", 12));
        CityZone.put(540500, new ZoneInfo("1", 12));
        CityZone.put(542400, new ZoneInfo("1", 12));
        CityZone.put(542500, new ZoneInfo("1", 12));
        CityZone.put(610100, new ZoneInfo("5", 1));
        CityZone.put(610200, new ZoneInfo("5", 1));
        CityZone.put(610300, new ZoneInfo("5", 1));
        CityZone.put(610400, new ZoneInfo("5", 1));
        CityZone.put(610500, new ZoneInfo("5", 1));
        CityZone.put(610600, new ZoneInfo("5", 1));
        CityZone.put(610700, new ZoneInfo("5", 4));
        CityZone.put(610800, new ZoneInfo("5", 9));
        CityZone.put(610900, new ZoneInfo("4", 4));
        CityZone.put(611000, new ZoneInfo("5", 4));
        CityZone.put(620100, new ZoneInfo("5", 1));
        CityZone.put(620200, new ZoneInfo("2", 10));
        CityZone.put(620300, new ZoneInfo("5", 10));
        CityZone.put(620400, new ZoneInfo("5", 1));
        CityZone.put(620500, new ZoneInfo("5", 1));
        CityZone.put(620600, new ZoneInfo("5", 10));
        CityZone.put(620700, new ZoneInfo("2", 10));
        CityZone.put(620800, new ZoneInfo("5", 1));
        CityZone.put(620900, new ZoneInfo("2", 10));
        CityZone.put(621000, new ZoneInfo("5", 1));
        CityZone.put(621100, new ZoneInfo("5", 1));
        CityZone.put(621200, new ZoneInfo("5", 4));
        CityZone.put(622900, new ZoneInfo("5", 1));
        CityZone.put(623000, new ZoneInfo("5", 12));
        CityZone.put(630100, new ZoneInfo("1", 1));
        CityZone.put(630200, new ZoneInfo("5", 1));
        CityZone.put(632200, new ZoneInfo("1", 12));
        CityZone.put(632300, new ZoneInfo("5", 12));
        CityZone.put(632500, new ZoneInfo("1", 12));
        CityZone.put(632600, new ZoneInfo("1", 12));
        CityZone.put(632700, new ZoneInfo("1", 12));
        CityZone.put(632800, new ZoneInfo("2", 12));
        CityZone.put(640100, new ZoneInfo("5", 9));
        CityZone.put(640200, new ZoneInfo("5", 9));
        CityZone.put(640300, new ZoneInfo("5", 9));
        CityZone.put(640400, new ZoneInfo("5", 1));
        CityZone.put(640500, new ZoneInfo("5", 9));
        CityZone.put(650100, new ZoneInfo("2", 9));
        CityZone.put(650200, new ZoneInfo("2", 9));
        CityZone.put(650400, new ZoneInfo("2", 10));
        CityZone.put(650500, new ZoneInfo("2", 10));
        CityZone.put(652300, new ZoneInfo("2", 9));
        CityZone.put(652700, new ZoneInfo("2", 9));
        CityZone.put(652800, new ZoneInfo("2", 10));
        CityZone.put(652900, new ZoneInfo("2", 10));
        CityZone.put(653000, new ZoneInfo("2", 10));
        CityZone.put(653100, new ZoneInfo("2", 10));
        CityZone.put(653200, new ZoneInfo("2", 10));
        CityZone.put(654000, new ZoneInfo("2", 9));
        CityZone.put(654200, new ZoneInfo("2", 9));
        CityZone.put(654300, new ZoneInfo("2", 9));
        CityZone.put(659001, new ZoneInfo("2", 9));
        CityZone.put(659002, new ZoneInfo("2", 10));
        CityZone.put(659003, new ZoneInfo("2", 10));
        CityZone.put(659004, new ZoneInfo("2", 9));
        CityZone.put(659005, new ZoneInfo("2", 9));
        CityZone.put(659006, new ZoneInfo("2", 10));
        CityZone.put(659007, new ZoneInfo("2", 9));
        CityZone.put(659008, new ZoneInfo("2", 9));
        CityZone.put(659009, new ZoneInfo("2", 10));
        CityZone.put(710100, new ZoneInfo("999", 6));
        CityZone.put(810100, new ZoneInfo("999", 6));
        CityZone.put(820100, new ZoneInfo("999", 6));
    }
}
