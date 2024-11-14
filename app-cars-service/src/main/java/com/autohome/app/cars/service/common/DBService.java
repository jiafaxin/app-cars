package com.autohome.app.cars.service.common;

import com.autohome.app.cars.mapper.appcars.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DBService<T>{

    @Autowired
    BaseMapper baseMapper;

    static ConcurrentHashMap<String, CopyOnWriteArrayList<String>> sqls = new ConcurrentHashMap<>();

    public void updateOrAdd(DBConfig dbConfig, Map<String,Object> params, String data) {
        String tableName = dbConfig.tableName();
        addOrUpdate(tableName,params,data);
//        if (update(tableName, params, data) <= 0) {
//            insert(tableName, params, data);
//            log.info("insert success params:{}", params);
//        }else{
//            log.info("update success params:{}", params);
//        }
    }

    public Integer addOrUpdate(String tableName,Map<String,Object> params, String data) {
        Map<String, Object> sqlParams = new LinkedHashMap<>();
        sqlParams.put("data", data);
        List<String> fields = new ArrayList<>();
        params.forEach((k, v) -> {
            sqlParams.put(k, v);
            fields.add(k);
        });
        String sql = "insert into " + tableName + "(";
        sql += String.join(",", fields);
        sql += ",data,is_del) values(";
        sql += String.join(",", fields.stream().map(x -> "#{" + x + "}").collect(Collectors.toList()));
        sql += ",#{data},0) ON DUPLICATE KEY UPDATE data=#{data}, modified_stime=now(), is_del=0";
        sqlParams.put("sql", sql);
        return baseMapper.insert(sqlParams);
    }

    public Integer addOrUpdateBatch(DBConfig dbConfig,Map<Map<String,Object>, String> datas) {
        if(datas==null||datas.size()==0){
            return 0;
        }
        String tableName = dbConfig.tableName();
        Map<String, Object> sqlParams = new LinkedHashMap<>();
        AtomicInteger index = new AtomicInteger(0);
        List<String> values = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        datas.forEach((params, data) -> {
            int i = index.getAndAdd(1);
            sqlParams.put("data" + i, data);
            if (fields.size() == 0) {
                params.forEach((k, v) -> {
                    fields.add(k);
                });
                fields.add("data");
            }
            params.forEach((k, v) -> {
                sqlParams.put(k + i, v);
            });
            values.add("(" + String.join(",", fields.stream().map(x -> "#{" + x + i + "}").collect(Collectors.toList())) + ")");
        });
        StringBuilder sql = new StringBuilder();
        sql.append("insert into " + tableName + "(" + String.join(",", fields) + ") values \n");
        sql.append(String.join(",", values) + "\n");
        sql.append("on duplicate key update data = values(data),modified_stime = now(), is_del=0;");
        sqlParams.put("sql", sql.toString());
        return baseMapper.insert(sqlParams);
    }

    public Integer update(String tableName,Map<String,Object> params, String data) {
        Map<String, Object> sqlParams = new LinkedHashMap<>();
        sqlParams.put("data", data);
        String sql = "update " + tableName + " set data=#{data}, modified_stime=now(), is_del=0 where ";
        List<String> where = new ArrayList<>();
        params.forEach((k, v) -> {
            sqlParams.put(k, v);
            where.add(k + "=#{" + k + "}");
        });
        sql += String.join(" and ", where);
        sqlParams.put("sql", sql);
        return baseMapper.update(sqlParams);
    }

    public Integer insert(String tableName,Map<String,Object> params, String data) {
        Map<String, Object> sqlParams = new LinkedHashMap<>();
        sqlParams.put("data", data);
        List<String> fields = new ArrayList<>();
        params.forEach((k, v) -> {
            sqlParams.put(k, v);
            fields.add(k);
        });
        String sql = "insert into " + tableName + "(";
        sql += String.join(",", fields);
        sql += ",data,is_del) values(";
        sql += String.join(",", fields.stream().map(x -> "#{" + x + "}").collect(Collectors.toList()));
        sql += ",#{data},0)";
        sqlParams.put("sql", sql);
        return baseMapper.insert(sqlParams);
    }

    public List<Map<String,Object>> page(String tableName,int start,int count){
        return baseMapper.page(tableName,start,count);
    }

    public List<Map<String,Object>> page(String tableName, int start, int count, Date lastUpdateTime){
        if(lastUpdateTime == null){
            return page(tableName,start,count);
        }
        return baseMapper.pageByTime(tableName,start,count,lastUpdateTime);
    }

    public Integer delete(DBConfig dbConfig,Map<String,Object> params) {
        String tableName = dbConfig.tableName();
        Map<String, Object> sqlParams = new LinkedHashMap<>();
        String sql = "update " + tableName + " set is_del=1 where ";
        List<String> where = new ArrayList<>();
        params.forEach((k, v) -> {
            sqlParams.put(k, v);
            where.add(k + "=#{" + k + "}");
        });
        sql += String.join(" and ", where);
        sqlParams.put("sql", sql);
        return baseMapper.update(sqlParams);
    }


    public Integer totalCount(String tableName) {
        Map<String, Object> sqlParams = new LinkedHashMap<>();
        String sql = "select count(1) from " + tableName;
        sqlParams.put("sql", sql);
        return baseMapper.count(sqlParams);
    }

    public Integer oldCount(String tableName,Date lastUpdateTime) {
        Map<String, Object> sqlParams = new LinkedHashMap<>();
        sqlParams.put("lastUpdateTime",lastUpdateTime);
        String sql = "select count(1) from " + tableName + " where modified_stime < #{lastUpdateTime}";
        sqlParams.put("sql", sql);
        return baseMapper.count(sqlParams);
    }

    public List<Map<String,Object>> deleteOld(String tableName,Date lastUpdateTime){
        List<Map<String,Object>> result = baseMapper.oldList(tableName,lastUpdateTime,100);
        List<Object> ids = new ArrayList<>();
        for (Map<String, Object> map : result) {
            if(map.containsKey("id")){
                ids.add(map.get("id"));
            }
        }
        deleteByIds(tableName,ids);
        return result;
    }

    public Integer deleteByIds(String tableName,List<Object> ids) {
        Map<String, Object> sqlParams = new LinkedHashMap<>();
        List<String> ps = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            ps.add(ids.get(i)+"");
        }
        String sql = "update " + tableName + " set is_del=1 where id in ("+String.join(",",ps)+") ";
        sqlParams.put("sql", sql);
        return baseMapper.update(sqlParams);
    }


}
