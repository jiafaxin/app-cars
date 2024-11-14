package com.autohome.app.cars.service.components.recrank.dtos;


import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor
@Data
public class RankShareParamInfo {
    private String shareext;
    private Integer canlongshare;
    private List<ShareBtn> sharelist;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShareBtn {
        private String id;
        private String icon;
        private String title;
        private int sharetypeid;
        private PvItem pvitem;

        public static ShareBtn getInstance(String  id, String icon, String title, int sharetypeid, RankResultDto.ListDTO item, RankResultShareParam param) {
            PvItem pvItem = new PvItem();
            Map<String, String> args = new HashMap<>(5);
            args.put("sharetypeid", String.valueOf(sharetypeid));
            if (Objects.nonNull(param.getSubranktypeid()) && param.getSubranktypeid() != 0) {
                args.put("subranktypeid", param.getSubranktypeid().toString());
            }
            args.put("rank", item.getRank());
            args.put("typeid", param.getTypeid().toString());
            args.put("seriesid", param.getSeriesid() + "");
            PvItem.PvObj show = new PvItem.PvObj();
            show.setEventid("car_rank_longpress_share_show");
            pvItem.setShow(show);
            pvItem.setArgvs(args);
            return new ShareBtn(id, icon, title, sharetypeid, pvItem);
        }
    }
}
