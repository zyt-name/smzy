package com.clou.common.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysPagePermission implements Serializable {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 页面权限编码（鉴权核心标识，如：user_page）
     */
    private String pageCode;
    /**
     * 页面跳转路径（如：/pages/user.html）
     */
    private String pagePath;
    /**
     * 页面名称
     */
    private String pageName;
}
