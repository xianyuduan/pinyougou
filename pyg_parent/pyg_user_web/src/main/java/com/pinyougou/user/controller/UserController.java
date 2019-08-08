package com.pinyougou.user.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import entity.PageResult;
import entity.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import com.alibaba.dubbo.config.annotation.Reference;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage/{page}/{rows}")
    public PageResult findPage(@PathVariable("page") int page, @PathVariable("rows") int rows) {
        return userService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param user
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbUser user) {
        try {
            userService.add(user);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param user
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public TbUser findOne(@PathVariable("id") Long id) {
        return userService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete/{ids}")
    public Result delete(@PathVariable("ids") Long[] ids) {
        try {
            userService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param user
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search/{page}/{rows}")
    public PageResult search(@RequestBody TbUser user, @PathVariable("page") int page, @PathVariable("rows") int rows) {
        return userService.findPage(user, page, rows);
    }

    //发送验证码
    @RequestMapping("/sendSms/{phone}")
    public Result sendSms(@PathVariable("phone") String phone) {
        return userService.sendSms(phone);
    }

    //注册
    @RequestMapping("/regist/{code}")
    public Result register(@PathVariable("code") String code, @RequestBody TbUser user) {
        String validCode = userService.findCodeByPhone(user.getPhone());
        if (!StringUtils.isEmpty(validCode) && validCode.equals(code)) {
            user.setCreated(new Date());
            user.setUpdated(new Date());
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            userService.add(user);
            return new Result(true, "注册成功");
        } else {
            return new Result(false, "注册失败");
        }
    }


    //回显登录用户姓名
    @RequestMapping("/findLoginUser")
    public Map findLoginUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//得到登陆人账号
        Map map = new HashMap<>();
        map.put("loginName", name);
        return map;
    }
}
