package com.work.p2p.beans.vo;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName:PaginationVO
 * Package:com.work.p2p.beans.vo
 * Description: 描述信息
 *
 * @date:2023/4/18 11:01
 * @author:yueyue
 */
public class PaginationVO<T> implements Serializable {
    //分页List
    private List<T> datas;
    //分页总条数
    private Integer totalSize;


    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }
}
