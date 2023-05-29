package com.work.p2p.beans.vo;

import java.io.Serializable;

/**
 * ClassName:BidUserVO
 * Package:com.work.p2p.beans.vo
 * Description: 投资排行榜VO类
 *
 * @date:2023/4/28 17:56
 * @author:yueyue
 */
public class BidUserVO implements Serializable {
    private String phone;
    private Double bidMoney;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getBidMoney() {
        return bidMoney;
    }

    public void setBidMoney(Double bidMoney) {
        this.bidMoney = bidMoney;
    }
}
