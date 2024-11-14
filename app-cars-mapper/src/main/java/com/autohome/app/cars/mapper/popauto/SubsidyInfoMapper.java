package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.SubsidyInfoEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author : zzli
 * @description : 消费补贴政策查询
 * @date : 2024/10/15 19:06
 */
@Mapper
@DS("popauto")
public interface SubsidyInfoMapper {

    @Select("select content as subsidytext,province_code as provinceid,city_code as cityid,policy_type as policytype   from Subsidy_Info with(Nolock) where is_del=0")
    List<SubsidyInfoEntity> getSubsidyList();

}
