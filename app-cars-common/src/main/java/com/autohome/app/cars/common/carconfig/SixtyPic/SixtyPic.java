package com.autohome.app.cars.common.carconfig.SixtyPic;

import java.util.HashMap;
import java.util.Map;

public class SixtyPic {

    public static int get(int key,int defaultVal){
        if(dicSort.containsKey(key))
            return dicSort.get(key);
        return defaultVal;
    }


    static final Map<Integer,Integer> dicSort = new HashMap<Integer,Integer>(){{
        put(10100,1);
        put(10200,2);
        put(10300,3);
        put(10400,4);
        put(10500,5);
        put(10600,6);
        put(10700,7);
        put(20100,8);
        put(20200,9);
        put(20300,10);
        put(20900,11);
        put(20902,12);
        put(21001,13);
        put(20500,14);
        put(20800,15);
        put(21200,16);
        put(21400,17);
        put(21100,18);
        put(21500,19);
        put(21600,20);
        put(23000,21);
        put(21800,22);
        put(21901,23);
        put(30100,24);
        put(30200,25);
        put(30300,26);
        put(30304,27);
        put(30306,28);
        put(30400,29);
        put(30600,30);
        put(30502,31);
        put(30700,32);
        put(31300,33);
        put(31400,34);
        put(31500,35);
        put(31700,36);
        put(31800,37);
        put(31200,38);
        put(30801,39);
        put(30900,40);
        put(32200,41);
        put(32300,42);
        put(32600,43);
        put(32701,44);
        put(32800,45);
        put(40100,46);
        put(40102,47);
        put(40200,48);
        put(40800,49);
        put(40700,50);
        put(41100,51);
        put(41000,52);
        put(41200,53);
        put(41700,54);
        put(41802,55);
        put(42203,56);
        put(43600,57);
        put(42600,58);
        put(42500,59);
        put(42900,60);
    }};

}
