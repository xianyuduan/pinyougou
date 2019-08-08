package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

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
