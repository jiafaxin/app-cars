package com.autohome.app.cars.apiclient.chejiahao.dtos;


import lombok.Data;

import java.util.List;

@Data
public class SSecondCheJiaHaoNewsResult{

        private Long infoid;
        private Integer infotype;
        private  String nickname;
        private Integer userid;
        private String title;
        private String coverimage;
        private List<String> images;
        private Boolean showImagelist;
        private String publishtime;
        private String address;
        private Integer replycount;
        private Integer vv;
        private Integer duration;
        private String videoid;
}
