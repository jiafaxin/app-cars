package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/4/26
 */
@Data
public class SpecChannelResult {

    private int returncode;
    private String message;
    private Result result;

    @Data
    public static class Result {

        private boolean success;
        private int rowsCount;
        private String message;
        private int pageIndex;
        private int pageSize;
        private int pageCount;
        private List<Rows> rows;
    }

    @Data
    public static class Rows {

        private int channelId;
        private String channelName;
        private boolean fourS;
        private boolean exhibition;
        private boolean comprehensive;
        private int status;
        private Date createTime;
        private String createUser;
        private boolean autoSyncSeries;
        private boolean autoSyncSpec;
        private int type;
        private String typeName;
        private int department;
        private int fortest;
        private int brandCategory;
        private List<Clerk> clerkList;
        private List<Spec> specList;
        private List<Manager> managerList;
        private int bcgType;
        private String bcgTypeName;
        private int channelLevel;
        private int jiCaiFactory;
        private int hasFactoryRelation;
    }

    @Data
    public static class Clerk {

        private String clerkId;
        private int busLineId;
        private Date assignTime;
        private boolean isAvailable;
    }

    @Data
    public static class Spec {

        private int seriesId;
        private int specId;
        private String name;
        private int brandId;
        private int factoryId;
        private String brandName;
        private String factoryName;
    }

    @Data
    public static class Manager {

        private int managerType;
        private String managerId;
    }
}
