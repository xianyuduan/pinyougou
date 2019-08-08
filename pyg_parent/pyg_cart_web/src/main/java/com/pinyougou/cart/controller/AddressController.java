package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.AddressService;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.vo.Cart;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Reference
    private AddressService addressService;

    @RequestMapping("/findAddress")
    public List<TbAddress> findAddress() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findAddress(username);
    }
}
