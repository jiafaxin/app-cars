package com.autohome.app.cars.apiclient.vr.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
@Data
public class VrSuperCarResult {

    private int browserType;
    private int exhibitionType;
    private int id;
    private String position;
    private HashMap<Integer,List<Integer>> specidinfo;
    private int terminal;
    private String title;
    private String url;
}
