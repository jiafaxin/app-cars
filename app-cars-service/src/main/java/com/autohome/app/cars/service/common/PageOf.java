package com.autohome.app.cars.service.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageOf<T> {
    public PageOf(){
        setCount(0);
        setItems(new ArrayList<>());
        setPageIndex(1);
    }
    int count;
    List<T> items;
    int pageIndex;
}
