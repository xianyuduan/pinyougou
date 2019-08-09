package com.pinyougou.vo;

import com.pinyougou.pojo.TbOrder;

import java.io.Serializable;
import java.util.List;

public class UpOrder implements Serializable {
    private TbOrder tbOrder;
    private List<Cart> cartList ;

    public UpOrder(TbOrder tbOrder, List<Cart> cartList) {
        this.tbOrder = tbOrder;
        this.cartList = cartList;
    }

    public UpOrder() {
    }

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }

    public List<Cart> getCartList() {
        return cartList;
    }

    public void setCartList(List<Cart> cartList) {
        this.cartList = cartList;
    }
}
