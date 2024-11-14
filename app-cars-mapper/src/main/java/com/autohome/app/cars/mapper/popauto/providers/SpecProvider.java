package com.autohome.app.cars.mapper.popauto.providers;

import com.autohome.app.cars.common.carconfig.Spec;
import com.autohome.app.cars.common.utils.StrPool;

import java.util.List;
import java.util.stream.Collectors;

public class SpecProvider {

    public String getSpecAll() {
        String sql = "" +
                "select A.id,\n" +
                "       A.model  as name,\n" +
                "       A.spec_price as minPrice,\n" +
                "       A.spec_price as maxPrice,\n" +
                "       REPLACE(REPLACE(A.img,'~',''),'/l_','/') as img,\n" +
                "       E.year      as yearName,\n" +
                "       A.spec_year as yearId,\n" +
                "       A.SpecState as state,\n" +
                "       A.ColorState,\n" +
                "       A.IsPreferential,\n" +
                "       A.IsTaxRelief,\n" +
                "       A.booked,\n" +
                "       A.pricedescription,\n" +
                "       A.TimeMarket,\n" +
                "       A.isForeignCar,\n" +
                "       A.isshow as paramisshow,\n" +
                "       A.IsImageSpec,\n" +
                "       A.isclassic,\n" +
                "       CASE WHEN A.SpecState <=30 AND A.isshow=0 THEN 0 ELSE 1 END AS paramIsShowByState,\n" +
                "       CASE WHEN A.booked=1 THEN 1 ELSE 0 END AS isBooked,\n" +
                "       CASE WHEN A.specstate>=10 and A.specstate<=40 and A.IsTaxRelief > 1 THEN IsTaxRelief ELSE 0 END  AS specTaxType,\n" +
                "       A2.fueltypedetail as fuelType,\n" +
                "       A2.SpecDisplacement as displacement,\n" +
                "       A2.pricedescription as priceDescription,\n" +
                "       A2.FlowMode,\n" +
                //"       A2.ElectricMotorGrossPower,\n" +
                "       A2.endurancemileage,\n" +
                "       A2.specEngineHP as SpecEnginePower,\n" +
                "       A2.specStructureSeat as seats,\n" +
                "       B.id        as seriesId,\n" +
                "       B.name   as seriesName,\n" +
                "       B.jb     as levelId,\n" +
                "       C.id     as brandId,\n" +
                "       C.name   as brandName,\n" +
                "       C.img    as brandLogo,\n" +
                "       D.id     as manufactoryId,\n" +
                "       D.name   as manufactoryName,\n" +
                "       A.ordercls  as Orders,\n"+
                "       CP.PicNumber\n" +
                "from spec_new (nolock) as A\n" +
                "         join SpecView (nolock) as A2 on A.id = A2.specId\n" +
                "         join Brands (nolock) as B on A.parent = B.id\n" +
                "         join [group] (nolock) as C on B.newFctid = C.id\n" +
                "         join Manufactory (nolock) as D on B.M = D.id\n" +
                "         join spec_year (nolock) as E on A.parent = E.series_id and A.spec_year = E.id\n" +
                "         LEFT JOIN CarSpecPictureStatistics AS CP WITH(NOLOCK) ON A.id=CP.SpecId\n";
        return sql;
    }

    public String getCvSpecAll() {
        String sql = "" +
                "select A.id,\n" +
                "       A.SpecName  as name,\n" +
                "       A.minPrice,\n" +
                "       A.maxPrice,\n" +
                "       REPLACE(A.SpecLogo,'~','') as img,\n" +
                "       A.YearId    as yearId,\n" +
                "       E.year      as yearName,\n" +
                "       A2.SpecState as state,\n" +
                "       null        as ColorState,\n" +
                "       A.IsPreferential,\n" +
                "       A.IsTaxRelief,\n" +
                "       A.booked,\n" +
                "       A.pricedescription,\n" +
                "       A.TimeMarket,\n" +
                "       CASE WHEN A.booked=1 THEN 1 ELSE 0 END AS isBooked,\n" +
                "       CASE WHEN A.specstate>=10 and A.specstate<=40 and A.IsTaxRelief > 1 THEN IsTaxRelief ELSE 0 END  AS specTaxType,\n" +
                "       null        as isForeignCar,\n" +
                "       A.ParamShow as paramisshow,\n" +
                "       0 as IsImageSpec,\n" +
                "       0 as isclassic,\n" +
                "       CASE WHEN A.SpecState <=30 AND A.ParamShow=0 THEN 0 ELSE 1 END AS paramIsShowByState,\n" +
                "       A2.fueltype as fuelType,\n" +
                "       A2.DeCapacity as displacement,\n" +
                "       A2.pricedescription as priceDescription,\n" +
                "       A2.FlowMode,\n" +
                //"       A2.ElectricMotorGrossPower,\n" +
                "       A2.endurancemileage,\n" +
                "       A2.HorsePower as SpecEnginePower,\n" +
                "       A2.Seats as seats,\n" +
                "       B.id        as seriesId,\n" +
                "       B.name      as seriesName,\n" +
                "       B.jb        as levelId,\n" +
                "       C.id        as brandId,\n" +
                "       C.name      as brandName,\n" +
                "       C.img       as brandLogo,\n" +
                "       D.id        as manufactoryId,\n" +
                "       D.name      as manufactoryName,\n" +
                "       A.Orders,\n"+
                "       CP.PicNumber\n" +
                "from CV_Spec (nolock) as A\n" +
                "         join CV_SpecView as A2 on A.id = A2.specId\n" +
                "         join Brands (nolock) as B on A.SeriesId = B.id\n" +
                "         join [group] (nolock) as C on B.newFctid = C.id\n" +
                "         join Manufactory (nolock) as D on B.M = D.id\n" +
                "         join spec_year (nolock) as E on A.SeriesId = E.series_id and A.YearId = E.id\n" +
                "         LEFT JOIN CarSpecPictureStatistics AS CP WITH(NOLOCK) ON A.id=CP.SpecId\n";
        return sql;
    }

    public String getSpec(int specId) {
        if (Spec.isCvSpec(specId)) {
            return getCvSpec(specId);
        }
        String sql = getSpecAll();
        sql += " WHERE A.id = #{specId}";
        return sql;
    }

    public String getCvSpec(int specId) {
        String sql = getCvSpecAll();
        sql += " WHERE A.id = #{specId}";
        return sql;
    }

    public String getSpecBySeriesId(int seriesId) {
        String sql = getSpecAll();
        sql += " WHERE B.id = #{seriesId}";
        return sql;
    }

    public String getCvSpecBySeriesId(int seriesId) {
        String sql = getCvSpecAll();
        sql += " WHERE B.id = #{seriesId}";
        return sql;
    }

    public String getSpecList(List<Integer> specIds) {
        String sql = getSpecAll();
        sql += " WHERE A.id in (<foreach collection=\"specIds\" item=\"__item\" separator=\",\" >#{__item}</foreach>)";
        return "<script>" + sql + "</script>";
    }

    public String getCvSpecList(List<Integer> specIds) {
        String sql = getCvSpecAll();
        sql += " WHERE A.id in (<foreach collection=\"specIds\" item=\"__item\" separator=\",\" >#{__item}</foreach>)";
        return "<script>" + sql + "</script>";
    }

    public String getAllSpecBySeriesId(int seriesId, boolean isCV) {
        String sql = "SELECT SpecId,specPrice as minprice,specPrice as maxprice,SyearId,Syear,specimg,SpecDrivingMode,SpecState,FlowMode,SpecDisplacement,specEngineHP as SpecEnginePower,SpecOrdercls,SpecIsImage,isclassic,specStructureType,fueltype,fueltypedetail,specIsshow,batteryCapacity,officialFastChargetime, officialSlowChargetime\n" +
                ",SpecPhotoNum as specPicNum,0 AS engineId,SpecEngine as engineName,SpecStructureDoor as doors,SpecOilOffical as officalOil,SpecWidth as width,SpecLength as length,SpecHeight as height,SpecWeight as weightkg,SpecQuality as quality,SeriesIsImport as seriesIsImport,specStructureSeat as seats " +
                ",endurancemileage,isNUll(specStructureSeat,0)seat\n" +
                " ,seriesId, specOilOffical AS officalOil, specQuality as quality,specIsImage,specSpeedupOffical as ssuo,specName " +
                ",CASE SpecState when 20 then 0 when 30 then 0.5 ELSE 1 END AS specOrder " +
                ",CASE SpecIsPublic WHEN 1 THEN 9999  ELSE Syear END AS AppointOrder,SpecIsPublic,SpecOrdercls " +
                "\t\t                   FROM  SpecView  WITH(NOLOCK) WHERE SeriesId = #{seriesId}";
        if (isCV) {
            sql = "SELECT SpecId,MinPrice,MaxPrice,SyearId,Syear,img as specimg,DriveForm,SpecState,FlowMode,DeCapacity as SpecDisplacement,HorsePower as SpecEnginePower,Orders AS SpecOrdercls,structtype,fueltype,endurancemileage,seats as seat,batteryCapacity,officialFastChargetime, officialSlowChargetime\n" +
                    ",specIsshow ,SpecPicNum as specPicNum,EngineId as engineId,EngineName as engineName,Doors as doors,OfficalOil as officalOil,Width as width,[Length] as length,Height as height,Weightkg as weightkg,Quality as quality,SeriesIsImport as seriesIsImportNum,Seats as seats  " +
                    " ,seriesId, officalOil,quality, 0 as specIsImage ,0 as ssuo,specName" +
                    ", CASE SpecState when 20 then 0 when 30 then 0.5 ELSE 1 END AS specOrder " +
                    ", CASE SpecState WHEN 20 THEN 9999 WHEN 30 THEN 9999 ELSE syear END AS AppointOrder,CASE SpecState WHEN 0 THEN 0 WHEN 10 THEN 0 WHEN 20 THEN 1 WHEN 30 THEN 1 WHEN 40 THEN 2 END AS SpecIsPublic,Orders as SpecOrdercls " +
                    "FROM CV_SpecView WITH(NOLOCK)  WHERE SeriesId = #{seriesId}";
        }
        return sql;
    }

    public String isSpecElectric(int specId) {
        return "SELECT COUNT(1) FROM Electric_SpecView WHERE specId = #{specId}";
    }

    public String getSpecBySeriesIds(List<Integer> seriesIds) {
        String sql = getSpecAll();
        String seriesIdStr = "0";
        if (!seriesIds.isEmpty()) {
            seriesIdStr = seriesIds.stream().map(Object::toString).collect(Collectors.joining(StrPool.COMMA));
        }
        sql += " WHERE B.id in (" + seriesIdStr + ")";
        return sql;
    }
}
