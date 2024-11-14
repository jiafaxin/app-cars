package com.autohome.app.cars.mapper.appcars.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by dx on 2024/9/5
 */
@Data
@NoArgsConstructor
public class RankSaleRefreshVersionEntity {
    private int id;
    private int currentversion;
    private int preversion;
    private int is_del;
    private Date created_stime;
    private Date modified_stime;
}
