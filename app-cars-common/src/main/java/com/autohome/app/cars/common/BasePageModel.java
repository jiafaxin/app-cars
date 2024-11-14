package com.autohome.app.cars.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class BasePageModel<T> {
    /**
     * 总条数
     */
    private int rowcount;
    /**
     * 当前页
     */
    private int pageindex;
    /**
     * 总页数
     */
    private int pagecount;
    /**
     * 结果集
     */
    private List<T> datalist;


    public BasePageModel() {
    }

    /**
     * @param pageIndex 当前页
     * @param pageSize  每页多少条
     * @param dataList  总的数据集
     * @description 手动分页构造方法，传入数据集计算返回当前页的结果集
     * @author zzli
     */

    public BasePageModel(Integer pageIndex, Integer pageSize, List<T> dataList) throws Exception {
        // 手动分页处理
        pageIndex = pageIndex <= 0 ? 1 : pageIndex;
        pageSize = pageSize <= 0 ? 20 : pageSize;

        int totalCount = dataList.size();
        int start = (pageIndex - 1) * pageSize;
        int end = Math.min(start + pageSize, totalCount);
        int pageCount = (int) Math.ceil((double) totalCount / pageSize);

        List<T> subList = (start >= 0 && end <= totalCount && start <= end)
                ? dataList.subList(start, end).stream().toList()
                : new ArrayList<>();

        this.rowcount = totalCount;
        this.pagecount = pageCount;
        this.datalist = subList;
        this.pageindex = pageIndex;
    }

    /**
     * @param rowCount  总条数
     * @param pageIndex 当前页
     * @param pageCount 总页数
     * @param dataList  结果集
     * @description BasePageModel
     * @author zzli
     */
    public BasePageModel(Integer rowCount, Integer pageIndex, Integer pageCount, List<T> dataList) {
        this.rowcount = rowCount;
        this.pagecount = pageCount;
        this.datalist = dataList;
        this.pageindex = pageIndex;
    }
}
