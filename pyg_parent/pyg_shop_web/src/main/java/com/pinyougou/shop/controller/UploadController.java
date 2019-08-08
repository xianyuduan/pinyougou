package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/file")
public class UploadController {

	@Value("${STORAGE_SERVER}")
	private String STORAGE_SERVER;

	//上传文件
	@RequestMapping("/upload")
	public Result upload(MultipartFile file){
		String originalFilename = file.getOriginalFilename();
 		String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		try {
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fsdf_conf.conf");
			String path = fastDFSClient.uploadFile(file.getBytes(), extName);
			return new Result(true, STORAGE_SERVER + path);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
	}


}
