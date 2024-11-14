package com.autohome.app.cars.apiclient.koubei.dtos;

import java.util.List;

public class KoubeiPRCResult {
    /**
     * dimSeriesPRCTypes : [{"isSemantic":0,"summary":[{"angleIndicatorLevel2Key":40,"angleIndicatorLevel3Key":40,"angleIndicatorLevel4Key":40,"combination":"性价比高","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":15,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":553366,"typeKey":1,"volume":195},{"angleIndicatorLevel2Key":5,"angleIndicatorLevel3Key":26,"angleIndicatorLevel4Key":26,"combination":"加速强劲","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":268459,"typeKey":1,"volume":178},{"angleIndicatorLevel2Key":7,"angleIndicatorLevel3Key":39,"angleIndicatorLevel4Key":39,"combination":"油耗低","groupIndicator":1,"indicatorClassify":1,"orderNum":0,"originalTypeKey":6,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":228630,"typeKey":1,"volume":146},{"angleIndicatorLevel2Key":20,"angleIndicatorLevel3Key":20,"angleIndicatorLevel4Key":20,"combination":"舒适性不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":553356,"typeKey":1,"volume":96},{"angleIndicatorLevel2Key":2,"angleIndicatorLevel3Key":3,"angleIndicatorLevel4Key":5,"combination":"后排空间足够","groupIndicator":1,"indicatorClassify":1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":783384,"typeKey":1,"volume":83},{"angleIndicatorLevel2Key":27,"angleIndicatorLevel3Key":128,"angleIndicatorLevel4Key":128,"combination":"内饰不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":615107,"typeKey":1,"volume":82},{"angleIndicatorLevel2Key":23,"angleIndicatorLevel3Key":98,"angleIndicatorLevel4Key":98,"combination":"胎噪大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":258427,"typeKey":2,"volume":70},{"angleIndicatorLevel2Key":27,"angleIndicatorLevel3Key":131,"angleIndicatorLevel4Key":131,"combination":"内饰异味大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":845320,"typeKey":2,"volume":60},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":8,"angleIndicatorLevel4Key":16,"combination":"后备厢空间小","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":615103,"typeKey":2,"volume":51},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":6,"angleIndicatorLevel4Key":6,"combination":"储物空间小","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":620136,"typeKey":2,"volume":36},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":92,"angleIndicatorLevel4Key":92,"combination":"车身有抖动","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":259711,"typeKey":2,"volume":35},{"angleIndicatorLevel2Key":6,"angleIndicatorLevel3Key":33,"angleIndicatorLevel4Key":54,"combination":"低速顿挫","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":249123,"typeKey":2,"volume":34}],"typeKey":0,"typeName":"综合"},{"isSemantic":0,"summary":[{"angleIndicatorLevel2Key":40,"angleIndicatorLevel3Key":40,"angleIndicatorLevel4Key":40,"combination":"性价比高","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":15,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":553366,"typeKey":1,"volume":195},{"angleIndicatorLevel2Key":5,"angleIndicatorLevel3Key":26,"angleIndicatorLevel4Key":26,"combination":"加速强劲","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":268459,"typeKey":1,"volume":178},{"angleIndicatorLevel2Key":7,"angleIndicatorLevel3Key":39,"angleIndicatorLevel4Key":39,"combination":"油耗低","groupIndicator":1,"indicatorClassify":1,"orderNum":0,"originalTypeKey":6,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":228630,"typeKey":1,"volume":146},{"angleIndicatorLevel2Key":20,"angleIndicatorLevel3Key":20,"angleIndicatorLevel4Key":20,"combination":"舒适性不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":553356,"typeKey":1,"volume":96},{"angleIndicatorLevel2Key":2,"angleIndicatorLevel3Key":3,"angleIndicatorLevel4Key":5,"combination":"后排空间足够","groupIndicator":1,"indicatorClassify":1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":783384,"typeKey":1,"volume":83},{"angleIndicatorLevel2Key":27,"angleIndicatorLevel3Key":128,"angleIndicatorLevel4Key":128,"combination":"内饰不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":615107,"typeKey":1,"volume":82},{"angleIndicatorLevel2Key":16,"angleIndicatorLevel3Key":79,"angleIndicatorLevel4Key":79,"combination":"方向盘没有虚位","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":738793,"typeKey":1,"volume":76},{"angleIndicatorLevel2Key":27,"angleIndicatorLevel3Key":130,"angleIndicatorLevel4Key":130,"combination":"内饰材质好","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":615109,"typeKey":1,"volume":59},{"angleIndicatorLevel2Key":16,"angleIndicatorLevel3Key":77,"angleIndicatorLevel4Key":77,"combination":"方向盘轻重合适","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":599225,"typeKey":1,"volume":57},{"angleIndicatorLevel2Key":24,"angleIndicatorLevel3Key":101,"angleIndicatorLevel4Key":108,"combination":"车身运动风","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":804966,"typeKey":1,"volume":52},{"angleIndicatorLevel2Key":24,"angleIndicatorLevel3Key":101,"angleIndicatorLevel4Key":106,"combination":"车身霸气","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":804970,"typeKey":1,"volume":51},{"angleIndicatorLevel2Key":32,"angleIndicatorLevel3Key":154,"angleIndicatorLevel4Key":154,"combination":"仪表盘设计不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":599235,"typeKey":1,"volume":51}],"typeKey":1,"typeName":"最满意"},{"isSemantic":0,"summary":[{"angleIndicatorLevel2Key":23,"angleIndicatorLevel3Key":98,"angleIndicatorLevel4Key":98,"combination":"胎噪大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":258427,"typeKey":2,"volume":70},{"angleIndicatorLevel2Key":27,"angleIndicatorLevel3Key":131,"angleIndicatorLevel4Key":131,"combination":"内饰异味大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":845320,"typeKey":2,"volume":60},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":8,"angleIndicatorLevel4Key":16,"combination":"后备厢空间小","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":615103,"typeKey":2,"volume":51},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":6,"angleIndicatorLevel4Key":6,"combination":"储物空间小","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":620136,"typeKey":2,"volume":36},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":92,"angleIndicatorLevel4Key":92,"combination":"车身有抖动","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":259711,"typeKey":2,"volume":35},{"angleIndicatorLevel2Key":6,"angleIndicatorLevel3Key":33,"angleIndicatorLevel4Key":54,"combination":"低速顿挫","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":249123,"typeKey":2,"volume":34},{"angleIndicatorLevel2Key":38,"angleIndicatorLevel3Key":179,"angleIndicatorLevel4Key":180,"combination":"后排没有出风口","groupIndicator":1,"indicatorClassify":-1,"orderNum":2,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":599223,"typeKey":2,"volume":30},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":5,"angleIndicatorLevel4Key":5,"combination":"储物空间设计不合理","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":260723,"typeKey":2,"volume":27},{"angleIndicatorLevel2Key":23,"angleIndicatorLevel3Key":99,"angleIndicatorLevel4Key":99,"combination":"风噪大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":258425,"typeKey":2,"volume":24},{"angleIndicatorLevel2Key":2,"angleIndicatorLevel3Key":3,"angleIndicatorLevel4Key":9,"combination":"后排中间地板凸起大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":620134,"typeKey":2,"volume":21},{"angleIndicatorLevel2Key":22,"angleIndicatorLevel3Key":94,"angleIndicatorLevel4Key":79,"combination":"后排座椅不舒适","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":846084,"typeKey":2,"volume":18},{"angleIndicatorLevel2Key":22,"angleIndicatorLevel3Key":94,"angleIndicatorLevel4Key":82,"combination":"后排座椅靠背角度不舒服","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1041810,"typeKey":2,"volume":15}],"typeKey":2,"typeName":"最不满意"},{"isSemantic":0,"summary":[{"angleIndicatorLevel2Key":2,"angleIndicatorLevel3Key":3,"angleIndicatorLevel4Key":5,"combination":"后排空间足够","groupIndicator":1,"indicatorClassify":1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":783384,"typeKey":3,"volume":83},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":8,"angleIndicatorLevel4Key":16,"combination":"后备厢空间小","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":615103,"typeKey":3,"volume":51},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":6,"angleIndicatorLevel4Key":6,"combination":"储物空间小","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":620136,"typeKey":3,"volume":36},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":5,"angleIndicatorLevel4Key":5,"combination":"储物空间设计不合理","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":260723,"typeKey":3,"volume":27},{"angleIndicatorLevel2Key":2,"angleIndicatorLevel3Key":2,"angleIndicatorLevel4Key":2,"combination":"前排头部空间足够","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":845974,"typeKey":3,"volume":23},{"angleIndicatorLevel2Key":2,"angleIndicatorLevel3Key":3,"angleIndicatorLevel4Key":9,"combination":"后排中间地板凸起大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":620134,"typeKey":3,"volume":21},{"angleIndicatorLevel2Key":2,"angleIndicatorLevel3Key":3,"angleIndicatorLevel4Key":7,"combination":"后排腿部空间足够","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":874251,"typeKey":3,"volume":14},{"angleIndicatorLevel2Key":2,"angleIndicatorLevel3Key":2,"angleIndicatorLevel4Key":3,"combination":"前排腿部空间大","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":738791,"typeKey":3,"volume":14},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":9,"angleIndicatorLevel4Key":21,"combination":"扶手箱空间小","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":845324,"typeKey":3,"volume":12},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":8,"angleIndicatorLevel4Key":19,"combination":"后备厢深度够用","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":924925,"typeKey":3,"volume":6},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":10,"angleIndicatorLevel4Key":25,"combination":"手套箱空间小","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":845326,"typeKey":3,"volume":6},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":9,"angleIndicatorLevel4Key":20,"combination":"扶手箱设计合理","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":1194813,"typeKey":3,"volume":3}],"typeKey":3,"typeName":"空间"},{"isSemantic":0,"summary":[{"angleIndicatorLevel2Key":5,"angleIndicatorLevel3Key":26,"angleIndicatorLevel4Key":26,"combination":"加速强劲","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":268459,"typeKey":4,"volume":178},{"angleIndicatorLevel2Key":5,"angleIndicatorLevel3Key":27,"angleIndicatorLevel4Key":27,"combination":"起步强劲","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":846459,"typeKey":4,"volume":39},{"angleIndicatorLevel2Key":6,"angleIndicatorLevel3Key":33,"angleIndicatorLevel4Key":54,"combination":"低速顿挫","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":249123,"typeKey":4,"volume":34},{"angleIndicatorLevel2Key":4,"angleIndicatorLevel3Key":20,"angleIndicatorLevel4Key":20,"combination":"高速动力够用","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":846078,"typeKey":4,"volume":22},{"angleIndicatorLevel2Key":6,"angleIndicatorLevel3Key":33,"angleIndicatorLevel4Key":55,"combination":"加速换挡平顺","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":959916,"typeKey":4,"volume":14},{"angleIndicatorLevel2Key":4,"angleIndicatorLevel3Key":30,"angleIndicatorLevel4Key":47,"combination":"爬坡动力足够","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":1340202,"typeKey":4,"volume":8},{"angleIndicatorLevel2Key":6,"angleIndicatorLevel3Key":34,"angleIndicatorLevel4Key":34,"combination":"起步不平顺","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1181915,"typeKey":4,"volume":8},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":195,"angleIndicatorLevel4Key":391,"combination":"发动机抖动","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":893227,"typeKey":4,"volume":6},{"angleIndicatorLevel2Key":4,"angleIndicatorLevel3Key":19,"angleIndicatorLevel4Key":19,"combination":"市区路段动力强劲","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":1078949,"typeKey":4,"volume":6},{"angleIndicatorLevel2Key":6,"angleIndicatorLevel3Key":33,"angleIndicatorLevel4Key":56,"combination":"减速换挡不平顺","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":260719,"typeKey":4,"volume":4},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":195,"angleIndicatorLevel4Key":378,"combination":"发动机启动声音大","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1043227,"typeKey":4,"volume":1},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":195,"angleIndicatorLevel4Key":229,"combination":"发动机异响","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":270731,"typeKey":4,"volume":1}],"typeKey":4,"typeName":"动力"},{"isSemantic":0,"summary":[{"angleIndicatorLevel2Key":16,"angleIndicatorLevel3Key":79,"angleIndicatorLevel4Key":79,"combination":"方向盘没有虚位","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":738793,"typeKey":5,"volume":76},{"angleIndicatorLevel2Key":16,"angleIndicatorLevel3Key":77,"angleIndicatorLevel4Key":77,"combination":"方向盘轻重合适","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":599225,"typeKey":5,"volume":57},{"angleIndicatorLevel2Key":17,"angleIndicatorLevel3Key":83,"angleIndicatorLevel4Key":59,"combination":"行驶稳定性不错","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":845332,"typeKey":5,"volume":37},{"angleIndicatorLevel2Key":17,"angleIndicatorLevel3Key":83,"angleIndicatorLevel4Key":60,"combination":"转向稳定性不错","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":265118,"typeKey":5,"volume":35},{"angleIndicatorLevel2Key":16,"angleIndicatorLevel3Key":233,"angleIndicatorLevel4Key":233,"combination":"转向手感不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":615105,"typeKey":5,"volume":19},{"angleIndicatorLevel2Key":18,"angleIndicatorLevel3Key":85,"angleIndicatorLevel4Key":85,"combination":"刹车灵敏","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":810885,"typeKey":5,"volume":15},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":190,"angleIndicatorLevel4Key":373,"combination":"底盘太低","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":248470,"typeKey":5,"volume":12},{"angleIndicatorLevel2Key":36,"angleIndicatorLevel3Key":175,"angleIndicatorLevel4Key":128,"combination":"发动机启停不好","groupIndicator":1,"indicatorClassify":-1,"orderNum":2,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":874249,"typeKey":5,"volume":11},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":190,"angleIndicatorLevel4Key":206,"combination":"底盘异响","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":269423,"typeKey":5,"volume":9},{"angleIndicatorLevel2Key":36,"angleIndicatorLevel3Key":176,"angleIndicatorLevel4Key":379,"combination":"没有电动尾门","groupIndicator":1,"indicatorClassify":-1,"orderNum":2,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1079176,"typeKey":5,"volume":5},{"angleIndicatorLevel2Key":18,"angleIndicatorLevel3Key":87,"angleIndicatorLevel4Key":63,"combination":"刹车踏板轻重不合理","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1375336,"typeKey":5,"volume":4},{"angleIndicatorLevel2Key":36,"angleIndicatorLevel3Key":176,"angleIndicatorLevel4Key":386,"combination":"没有无钥匙进入","groupIndicator":1,"indicatorClassify":-1,"orderNum":2,"originalTypeKey":5,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1342308,"typeKey":5,"volume":3}],"typeKey":5,"typeName":"操控"},{"isSemantic":0,"summary":[{"angleIndicatorLevel2Key":7,"angleIndicatorLevel3Key":39,"angleIndicatorLevel4Key":39,"combination":"油耗低","groupIndicator":1,"indicatorClassify":1,"orderNum":0,"originalTypeKey":6,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":228630,"typeKey":6,"volume":146},{"angleIndicatorLevel2Key":7,"angleIndicatorLevel3Key":42,"angleIndicatorLevel4Key":42,"combination":"高速油耗低","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":6,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":874253,"typeKey":6,"volume":19},{"angleIndicatorLevel2Key":8,"angleIndicatorLevel3Key":46,"angleIndicatorLevel4Key":46,"combination":"油箱容量小","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":6,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":232483,"typeKey":6,"volume":11},{"angleIndicatorLevel2Key":7,"angleIndicatorLevel3Key":43,"angleIndicatorLevel4Key":43,"combination":"空调开启后油耗可接受","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":6,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":886922,"typeKey":6,"volume":1}],"typeKey":6,"typeName":"油耗"},{"isSemantic":0,"summary":[{"angleIndicatorLevel2Key":20,"angleIndicatorLevel3Key":20,"angleIndicatorLevel4Key":20,"combination":"舒适性不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":553356,"typeKey":7,"volume":96},{"angleIndicatorLevel2Key":23,"angleIndicatorLevel3Key":98,"angleIndicatorLevel4Key":98,"combination":"胎噪大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":258427,"typeKey":7,"volume":70},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":92,"angleIndicatorLevel4Key":92,"combination":"车身有抖动","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":259711,"typeKey":7,"volume":35},{"angleIndicatorLevel2Key":38,"angleIndicatorLevel3Key":179,"angleIndicatorLevel4Key":180,"combination":"后排没有出风口","groupIndicator":1,"indicatorClassify":-1,"orderNum":2,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":599223,"typeKey":7,"volume":30},{"angleIndicatorLevel2Key":23,"angleIndicatorLevel3Key":99,"angleIndicatorLevel4Key":99,"combination":"风噪大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":258425,"typeKey":7,"volume":24},{"angleIndicatorLevel2Key":38,"angleIndicatorLevel3Key":179,"angleIndicatorLevel4Key":152,"combination":"空调给力","groupIndicator":1,"indicatorClassify":-1,"orderNum":2,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":846082,"typeKey":7,"volume":20},{"angleIndicatorLevel2Key":22,"angleIndicatorLevel3Key":94,"angleIndicatorLevel4Key":79,"combination":"后排座椅不舒适","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":846084,"typeKey":7,"volume":18},{"angleIndicatorLevel2Key":22,"angleIndicatorLevel3Key":94,"angleIndicatorLevel4Key":82,"combination":"后排座椅靠背角度不舒服","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1041810,"typeKey":7,"volume":15},{"angleIndicatorLevel2Key":22,"angleIndicatorLevel3Key":93,"angleIndicatorLevel4Key":73,"combination":"前排有腿部支撑","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":874247,"typeKey":7,"volume":7},{"angleIndicatorLevel2Key":22,"angleIndicatorLevel3Key":93,"angleIndicatorLevel4Key":78,"combination":"前排座椅调节方便","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":936945,"typeKey":7,"volume":7},{"angleIndicatorLevel2Key":22,"angleIndicatorLevel3Key":93,"angleIndicatorLevel4Key":70,"combination":"前排腰部支撑合理","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":1316486,"typeKey":7,"volume":6},{"angleIndicatorLevel2Key":22,"angleIndicatorLevel3Key":93,"angleIndicatorLevel4Key":69,"combination":"前排有腰部支撑","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":1106857,"typeKey":7,"volume":5}],"typeKey":7,"typeName":"舒适性"},{"isSemantic":0,"summary":[{"angleIndicatorLevel2Key":24,"angleIndicatorLevel3Key":101,"angleIndicatorLevel4Key":108,"combination":"车身运动风","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":804966,"typeKey":8,"volume":52},{"angleIndicatorLevel2Key":24,"angleIndicatorLevel3Key":101,"angleIndicatorLevel4Key":106,"combination":"车身霸气","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":804970,"typeKey":8,"volume":51},{"angleIndicatorLevel2Key":25,"angleIndicatorLevel3Key":111,"angleIndicatorLevel4Key":111,"combination":"车头灯耐看","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":810889,"typeKey":8,"volume":39},{"angleIndicatorLevel2Key":24,"angleIndicatorLevel3Key":102,"angleIndicatorLevel4Key":102,"combination":"车头设计耐看","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":599231,"typeKey":8,"volume":39},{"angleIndicatorLevel2Key":24,"angleIndicatorLevel3Key":101,"angleIndicatorLevel4Key":107,"combination":"车身时尚","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":980985,"typeKey":8,"volume":35},{"angleIndicatorLevel2Key":24,"angleIndicatorLevel3Key":107,"angleIndicatorLevel4Key":107,"combination":"车轮设计耐看","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":738787,"typeKey":8,"volume":28},{"angleIndicatorLevel2Key":26,"angleIndicatorLevel3Key":119,"angleIndicatorLevel4Key":119,"combination":"车漆薄","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1452212,"typeKey":8,"volume":5},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":216,"angleIndicatorLevel4Key":309,"combination":"车窗异响或抖动","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1452823,"typeKey":8,"volume":2},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":219,"angleIndicatorLevel4Key":332,"combination":"车门柱异响","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1452821,"typeKey":8,"volume":2},{"angleIndicatorLevel2Key":24,"angleIndicatorLevel3Key":103,"angleIndicatorLevel4Key":103,"combination":"腰线设计不漂亮","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":553358,"typeKey":8,"volume":2},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":222,"angleIndicatorLevel4Key":339,"combination":"车灯坏掉","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":248781,"typeKey":8,"volume":1},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":221,"angleIndicatorLevel4Key":335,"combination":"车身异响","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":8,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1190049,"typeKey":8,"volume":1}],"typeKey":8,"typeName":"外观"},{"isSemantic":0,"summary":[{"angleIndicatorLevel2Key":27,"angleIndicatorLevel3Key":128,"angleIndicatorLevel4Key":128,"combination":"内饰不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":615107,"typeKey":9,"volume":82},{"angleIndicatorLevel2Key":27,"angleIndicatorLevel3Key":131,"angleIndicatorLevel4Key":131,"combination":"内饰异味大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":845320,"typeKey":9,"volume":60},{"angleIndicatorLevel2Key":27,"angleIndicatorLevel3Key":130,"angleIndicatorLevel4Key":130,"combination":"内饰材质好","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":615109,"typeKey":9,"volume":59},{"angleIndicatorLevel2Key":32,"angleIndicatorLevel3Key":154,"angleIndicatorLevel4Key":154,"combination":"仪表盘设计不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":599235,"typeKey":9,"volume":51},{"angleIndicatorLevel2Key":27,"angleIndicatorLevel3Key":129,"angleIndicatorLevel4Key":111,"combination":"内饰高端","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":1078954,"typeKey":9,"volume":37},{"angleIndicatorLevel2Key":34,"angleIndicatorLevel3Key":170,"angleIndicatorLevel4Key":170,"combination":"座椅材质不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":599233,"typeKey":9,"volume":32},{"angleIndicatorLevel2Key":31,"angleIndicatorLevel3Key":151,"angleIndicatorLevel4Key":151,"combination":"中控屏设计不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":804972,"typeKey":9,"volume":26},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":213,"angleIndicatorLevel4Key":301,"combination":"中控台异响","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":259715,"typeKey":9,"volume":12},{"angleIndicatorLevel2Key":28,"angleIndicatorLevel3Key":238,"angleIndicatorLevel4Key":238,"combination":"没有氛围灯","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":886926,"typeKey":9,"volume":12},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":213,"angleIndicatorLevel4Key":300,"combination":"中控屏故障","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":259713,"typeKey":9,"volume":10},{"angleIndicatorLevel2Key":37,"angleIndicatorLevel3Key":180,"angleIndicatorLevel4Key":163,"combination":"车机不好","groupIndicator":1,"indicatorClassify":-1,"orderNum":2,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":886918,"typeKey":9,"volume":9},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":208,"angleIndicatorLevel4Key":279,"combination":"车内异响","groupIndicator":1,"indicatorClassify":-1,"orderNum":1,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1258540,"typeKey":9,"volume":7}],"typeKey":9,"typeName":"内饰"},{"isSemantic":0,"summary":[{"angleIndicatorLevel2Key":40,"angleIndicatorLevel3Key":40,"angleIndicatorLevel4Key":40,"combination":"性价比高","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":15,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":553366,"typeKey":15,"volume":195},{"angleIndicatorLevel2Key":41,"angleIndicatorLevel3Key":184,"angleIndicatorLevel4Key":184,"combination":"优惠幅度大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":15,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":738800,"typeKey":15,"volume":51},{"angleIndicatorLevel2Key":35,"angleIndicatorLevel3Key":173,"angleIndicatorLevel4Key":173,"combination":"配置高","groupIndicator":1,"indicatorClassify":-1,"orderNum":2,"originalTypeKey":15,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":891909,"typeKey":15,"volume":40},{"angleIndicatorLevel2Key":43,"angleIndicatorLevel3Key":229,"angleIndicatorLevel4Key":229,"combination":"保值率高","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":15,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":846465,"typeKey":15,"volume":9},{"angleIndicatorLevel2Key":42,"angleIndicatorLevel3Key":186,"angleIndicatorLevel4Key":189,"combination":"维修保养贵","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":15,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":1007517,"typeKey":15,"volume":4},{"angleIndicatorLevel2Key":42,"angleIndicatorLevel3Key":187,"angleIndicatorLevel4Key":191,"combination":"没有免费保养","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":15,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":258423,"typeKey":15,"volume":3},{"angleIndicatorLevel2Key":35,"angleIndicatorLevel3Key":174,"angleIndicatorLevel4Key":112,"combination":"安全配置不好","groupIndicator":1,"indicatorClassify":-1,"orderNum":2,"originalTypeKey":15,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":907220,"typeKey":15,"volume":2},{"angleIndicatorLevel2Key":35,"angleIndicatorLevel3Key":180,"angleIndicatorLevel4Key":159,"combination":"内饰配置不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":2,"originalTypeKey":15,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":1331268,"typeKey":15,"volume":1}],"typeKey":15,"typeName":"性价比"}]
     * isElectric : false
     * seriesId : 692
     * seriesName : 奥迪A4L
     */

    private boolean isElectric;
    private int seriesId;
    private String seriesName;
    private List<DimSeriesPRCTypesBean> dimSeriesPRCTypes;

    public boolean isIsElectric() {
        return isElectric;
    }

    public void setIsElectric(boolean isElectric) {
        this.isElectric = isElectric;
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public List<DimSeriesPRCTypesBean> getDimSeriesPRCTypes() {
        return dimSeriesPRCTypes;
    }

    public void setDimSeriesPRCTypes(List<DimSeriesPRCTypesBean> dimSeriesPRCTypes) {
        this.dimSeriesPRCTypes = dimSeriesPRCTypes;
    }

    public static class DimSeriesPRCTypesBean {
        /**
         * isSemantic : 0
         * summary : [{"angleIndicatorLevel2Key":40,"angleIndicatorLevel3Key":40,"angleIndicatorLevel4Key":40,"combination":"性价比高","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":15,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":553366,"typeKey":1,"volume":195},{"angleIndicatorLevel2Key":5,"angleIndicatorLevel3Key":26,"angleIndicatorLevel4Key":26,"combination":"加速强劲","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":268459,"typeKey":1,"volume":178},{"angleIndicatorLevel2Key":7,"angleIndicatorLevel3Key":39,"angleIndicatorLevel4Key":39,"combination":"油耗低","groupIndicator":1,"indicatorClassify":1,"orderNum":0,"originalTypeKey":6,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":228630,"typeKey":1,"volume":146},{"angleIndicatorLevel2Key":20,"angleIndicatorLevel3Key":20,"angleIndicatorLevel4Key":20,"combination":"舒适性不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":553356,"typeKey":1,"volume":96},{"angleIndicatorLevel2Key":2,"angleIndicatorLevel3Key":3,"angleIndicatorLevel4Key":5,"combination":"后排空间足够","groupIndicator":1,"indicatorClassify":1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":783384,"typeKey":1,"volume":83},{"angleIndicatorLevel2Key":27,"angleIndicatorLevel3Key":128,"angleIndicatorLevel4Key":128,"combination":"内饰不错","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":3,"seriesKey":692,"seriesSummaryKey":615107,"typeKey":1,"volume":82},{"angleIndicatorLevel2Key":23,"angleIndicatorLevel3Key":98,"angleIndicatorLevel4Key":98,"combination":"胎噪大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":258427,"typeKey":2,"volume":70},{"angleIndicatorLevel2Key":27,"angleIndicatorLevel3Key":131,"angleIndicatorLevel4Key":131,"combination":"内饰异味大","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":9,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":845320,"typeKey":2,"volume":60},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":8,"angleIndicatorLevel4Key":16,"combination":"后备厢空间小","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":615103,"typeKey":2,"volume":51},{"angleIndicatorLevel2Key":3,"angleIndicatorLevel3Key":6,"angleIndicatorLevel4Key":6,"combination":"储物空间小","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":3,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":620136,"typeKey":2,"volume":36},{"angleIndicatorLevel2Key":1,"angleIndicatorLevel3Key":92,"angleIndicatorLevel4Key":92,"combination":"车身有抖动","groupIndicator":1,"indicatorClassify":-1,"orderNum":0,"originalTypeKey":7,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":259711,"typeKey":2,"volume":35},{"angleIndicatorLevel2Key":6,"angleIndicatorLevel3Key":33,"angleIndicatorLevel4Key":54,"combination":"低速顿挫","groupIndicator":1,"indicatorClassify":0,"orderNum":0,"originalTypeKey":4,"salesTypeKey":1,"sentimentKey":2,"seriesKey":692,"seriesSummaryKey":249123,"typeKey":2,"volume":34}]
         * typeKey : 0
         * typeName : 综合
         */

        private int isSemantic;
        private int typeKey;
        private String typeName;
        private List<SummaryBean> summary;

        public int getIsSemantic() {
            return isSemantic;
        }

        public void setIsSemantic(int isSemantic) {
            this.isSemantic = isSemantic;
        }

        public int getTypeKey() {
            return typeKey;
        }

        public void setTypeKey(int typeKey) {
            this.typeKey = typeKey;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public List<SummaryBean> getSummary() {
            return summary;
        }

        public void setSummary(List<SummaryBean> summary) {
            this.summary = summary;
        }

        public static class SummaryBean {
            /**
             * angleIndicatorLevel2Key : 40
             * angleIndicatorLevel3Key : 40
             * angleIndicatorLevel4Key : 40
             * combination : 性价比高
             * groupIndicator : 1
             * indicatorClassify : -1
             * orderNum : 0
             * originalTypeKey : 15
             * salesTypeKey : 1
             * sentimentKey : 3
             * seriesKey : 692
             * seriesSummaryKey : 553366
             * typeKey : 1
             * volume : 195
             */

            private int angleIndicatorLevel2Key;
            private int angleIndicatorLevel3Key;
            private int angleIndicatorLevel4Key;
            private String combination;
            private int groupIndicator;
            private int indicatorClassify;
            private int orderNum;
            private int originalTypeKey;
            private int salesTypeKey;
            private int sentimentKey;
            private int seriesKey;
            private int seriesSummaryKey;
            private int typeKey;
            private int volume;

            public int getAngleIndicatorLevel2Key() {
                return angleIndicatorLevel2Key;
            }

            public void setAngleIndicatorLevel2Key(int angleIndicatorLevel2Key) {
                this.angleIndicatorLevel2Key = angleIndicatorLevel2Key;
            }

            public int getAngleIndicatorLevel3Key() {
                return angleIndicatorLevel3Key;
            }

            public void setAngleIndicatorLevel3Key(int angleIndicatorLevel3Key) {
                this.angleIndicatorLevel3Key = angleIndicatorLevel3Key;
            }

            public int getAngleIndicatorLevel4Key() {
                return angleIndicatorLevel4Key;
            }

            public void setAngleIndicatorLevel4Key(int angleIndicatorLevel4Key) {
                this.angleIndicatorLevel4Key = angleIndicatorLevel4Key;
            }

            public String getCombination() {
                return combination;
            }

            public void setCombination(String combination) {
                this.combination = combination;
            }

            public int getGroupIndicator() {
                return groupIndicator;
            }

            public void setGroupIndicator(int groupIndicator) {
                this.groupIndicator = groupIndicator;
            }

            public int getIndicatorClassify() {
                return indicatorClassify;
            }

            public void setIndicatorClassify(int indicatorClassify) {
                this.indicatorClassify = indicatorClassify;
            }

            public int getOrderNum() {
                return orderNum;
            }

            public void setOrderNum(int orderNum) {
                this.orderNum = orderNum;
            }

            public int getOriginalTypeKey() {
                return originalTypeKey;
            }

            public void setOriginalTypeKey(int originalTypeKey) {
                this.originalTypeKey = originalTypeKey;
            }

            public int getSalesTypeKey() {
                return salesTypeKey;
            }

            public void setSalesTypeKey(int salesTypeKey) {
                this.salesTypeKey = salesTypeKey;
            }

            public int getSentimentKey() {
                return sentimentKey;
            }

            public void setSentimentKey(int sentimentKey) {
                this.sentimentKey = sentimentKey;
            }

            public int getSeriesKey() {
                return seriesKey;
            }

            public void setSeriesKey(int seriesKey) {
                this.seriesKey = seriesKey;
            }

            public int getSeriesSummaryKey() {
                return seriesSummaryKey;
            }

            public void setSeriesSummaryKey(int seriesSummaryKey) {
                this.seriesSummaryKey = seriesSummaryKey;
            }

            public int getTypeKey() {
                return typeKey;
            }

            public void setTypeKey(int typeKey) {
                this.typeKey = typeKey;
            }

            public int getVolume() {
                return volume;
            }

            public void setVolume(int volume) {
                this.volume = volume;
            }
        }
    }
}
