package com.autohome.app.cars.job.jobs;

import com.autohome.app.cars.common.redis.MainDataRedisTemplate;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import io.lettuce.core.cluster.SlotHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

@JobHander("TestJob")
@Service
public class TestJob extends IJobHandler {

     @Autowired
     MainDataRedisTemplate mainDataRedisTemplate;

     @Autowired
    SeriesDetailComponent seriesDetailComponent;

    // 生成带有哈希标签的新键，确保槽均匀分布在10个节点上



    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        //1 MV,NW,EV
//
//        Map<Integer, List<String>> sc = new HashMap<>();
//        String a="A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
//String b="a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z";
//        for (String s : a.split(",")) {
//            for (String s1 : a.split(",")) {
//                int slot = SlotHash.getSlot(s+s1);
//
//                if(slot % 820 <350 || slot % 820 >450) continue;
//
//                int index = slot / 820;
//                if(!sc.containsKey(index)){
//                    sc.put(index,new ArrayList<>());
//                }
//                sc.get(index).add(s+s1);
//            }
//        }
//
//        sc.forEach((k,v)->{
//            System.out.print("\""+v.get(0)+"\",");
//        });



        Map<Integer, AtomicInteger> sc2 = new HashMap<>();

        for (int i = 0; i < 6; i++) {
            sc2.put(i,new AtomicInteger(0));
        }

        for (int i = 0; i < 6000; i++) {
            TreeMap<String,Object> p = new TreeMap<>();
            p.put("seriesId",i);
            String key = seriesDetailComponent.getKey(p);
            int slot2 = SlotHash.getSlot(convergenceKey(key));
            sc2.get(slot2 / 2731).incrementAndGet();
        }


        sc2.forEach((k,v)->{
            System.out.println(k+" - " + v);
        });

        return new ReturnT<>(0,"success");
    }



    static List<String> slotKey = Arrays.asList("BW","AV","AO","DU","FN","BV","AW","AN","EN","FO","BQ","OO","DN","DW","GV","CN","AU","DO","DV","FI");
    static int oneSlotBase = 820;
    public static String convergenceKey(String originalKey) {
        int slot = SlotHash.getSlot(originalKey);
        int index = slot / oneSlotBase;
        if(index >= slotKey.size()){
            index = 1;
        }
        String hashTag = slotKey.get(index);
        return "{" + hashTag + "}" + originalKey;
    }
}
