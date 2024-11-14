package com.autohome.app.cars.mapper.appcars;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
@DS("appcars")
public interface BaseMapper {

    @Insert("${sql}")
    Integer insert(Map<String, Object> params);

    @Update("${sql}")
    Integer update(Map<String, Object> params);

    @Select("${sql}")
    Integer count(Map<String, Object> params);

    @Delete("${sql}")
    Integer delete(Map<String, Object> params);

    @Select("select * from ${tableName} order by id desc limit  #{start},#{count};")
    List<Map<String, Object>> page(String tableName, int start, int count);
    
    @Select("select * from ${tableName}  WHERE modified_stime > #{lastUpdateTime} order by modified_stime asc limit #{start},#{count};")
    List<Map<String, Object>> pageByTime(String tableName, int start, int count, Date lastUpdateTime);

    @Select("select * from ${tableName} where modified_stime < #{lastUpdateTime} limit #{count};")
    List<Map<String, Object>> oldList(String tableName,Date lastUpdateTime, int count);
}
