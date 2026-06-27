package com.clou.merchantdemo.controller;

import com.clou.common.exception.BusinessException;
import com.clou.merchantdemo.uitl.Imgutil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/merchant/img")
public class MerchantImgController {

    @Value("${img.save-path}")
    private String imgSavePath;

    /**
     * 接收前端上传的图片并保存，返回图片绝对路径字符串
     * @param file 前端上传的图片文件
     * @return 图片绝对路径字符串
     */
    @PostMapping
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "请选择要上传的图片");
            }

            // 获取原始文件名和后缀
            String originalFilename = file.getOriginalFilename();
            // 处理可能的空文件名情况
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "无效的文件格式");
            }
            // 获取文件后缀(.后面的字符串)
            String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 生成唯一文件名（避免重复）
            String fileName = UUID.randomUUID().toString() + fileSuffix;
            // 调用工具类保存图片，获取绝对路径
            String imagePath = Imgutil.saveImage(file.getInputStream(), fileName, imgSavePath);
            if (imagePath == null) {
                throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "图片保存失败");
            }
            return "/img/" + fileName;
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "上传失败：" + e.getMessage());
        }
    }

}