package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    //private AliOssUtil aliOssUtil; //使用@Autowired注解注入AliOssUtil类的对象
    private AliOssUtil ossUtil; //最好还是保持与配置类中的名称一致

    /**
     * @param file 文件
     * @return 返回url
     */
    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public Result<String> upload(MultipartFile file){
        log.info("上传文件：{}", file.getOriginalFilename());
        try {
            //获取文件名
            String fileName = file.getOriginalFilename();
            //获取文件后缀
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            //生成UUID文件名
            fileName = UUID.randomUUID().toString() + suffix;
            //上传文件
            String url = ossUtil.upload(file.getBytes(), fileName);

            return Result.success(url);
        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw new RuntimeException(e);
        }
    }
}
