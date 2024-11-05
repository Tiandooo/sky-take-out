package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    AliOssUtil aliOssUtil;

    @PostMapping("upload")
    public Result<String> upload(MultipartFile file){
        log.info("上传图片！");

        try {
            String OriginalFilename = file.getOriginalFilename();

            String extension = OriginalFilename.substring(OriginalFilename.lastIndexOf("."));
            // 将UUID替换为文件名
            String fileName = UUID.randomUUID().toString() + extension;
            // 上传
            String filePath = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(filePath);

        } catch (IOException e){
            log.error("文件上传失败：{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);

    }

}
