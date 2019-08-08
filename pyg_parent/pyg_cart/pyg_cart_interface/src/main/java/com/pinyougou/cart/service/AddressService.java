package com.pinyougou.cart.service;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.vo.Cart;

import java.util.List;

public interface AddressService {

    List<TbAddress> findAddress(String username);
}
