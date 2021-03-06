package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import entity.Result;
import org.apache.http.HttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;
    //新增货物到购物车
    @RequestMapping("/addItemToCartList/{itemId}/{num}")
    public Result addItemToCartList(HttpServletRequest request,HttpServletResponse response,
                                    @PathVariable("itemId") Long itemId,
                                    @PathVariable("num") int num) {
        try {
            String key = getSession(request, response);
            //获取登录用户
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!"anonymousUser".equals(username)) {
                key=username;
            }
            //查询原来的购物车
            List<Cart> cartList = cartService.findCartListByKey(key);
            //将货物添加到购物车
            cartList = cartService.addItemToCartList(cartList, itemId, num);
            //将新的购物车保存到缓存
            cartService.saveCartListToRedis(key,cartList);
            return new Result(true, "货物已保存到购物车");
        } catch (Exception e) {
            return new Result(false, "添加货物失败");
        }

    }

    //查询购物车货物
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {
        String key = getSession(request, response);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Cart> cartListByKey = cartService.findCartListByKey(key);
        //没登录,直接返回以session为key存的购物车
        if ("anonymousUser".equals(username)) {
            return cartListByKey;
        }
        //登录后,返回合并后的购物车
        List<Cart> cartListByUsername = cartService.findCartListByKey(username);
        if (cartListByKey.size() > 0) {
            //合并
            cartListByUsername=cartService.mergeList(cartListByUsername, cartListByKey);
            //清空未登录购物车,保存用户购物车
            cartService.deleteListByKey(key);
            cartService.saveCartListToRedis(username,cartListByUsername);
        }
        return cartListByUsername;
    }

    //将session存入cookie,固定session
    private String getSession(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = CookieUtil.getCookieValue(request, "sessionId", "utf-8");
        if (StringUtils.isEmpty(sessionId)) {
            sessionId=request.getSession().getId();
            CookieUtil.setCookie(request,response,"sessionId",sessionId,24*60*60,"utf-8");
        }
        return sessionId;
    }

    //upOrderItem 将购物车选中提交的商品，存入临时提交订单，入redis
    @RequestMapping("/upOrderItem")
    public Result upOrderItem(@RequestBody List<TbOrderItem> cartListUp, HttpServletRequest request, HttpServletResponse response){
        String sessionId = getSession(request, response);
        if(cartListUp.size()<=0){
            return new Result(false, "添加失败！");
        }
        try {
            cartService.saveupOrderItem(cartListUp,sessionId);
        }catch (Exception e){
            return  new Result(false, "添加失败！");
        }
        return  new Result(true, "添加成功！");
    }

    //findUpdataCartList 查询已提交的临时订单，从redis中取，以购物车List<cart>的形式返回
    @RequestMapping("/findUpdataCartList")
    public List<Cart> findUpdataCartList( HttpServletRequest request, HttpServletResponse response) {
        List<TbOrderItem> orderItemList= cartService.findUpdataCartList(getSession(request,response));
        List<Cart> cartList=new ArrayList<>();
        if(orderItemList.size()>0){
            //3将本次添加的商品放入集合
            for (TbOrderItem orderItem : orderItemList) {
                cartList = cartService.addItemToCartList(cartList, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList;
    }

}
