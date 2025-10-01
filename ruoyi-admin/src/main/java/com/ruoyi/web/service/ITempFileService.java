package com.ruoyi.web.service;

/**
 * 临时文件访问服务接口
 * 
 * @author zorroe
 */
public interface ITempFileService {
    /**
     * 生成头像临时访问URL
     * 
     * @param objectName 文件对象名称
     * @param expires 过期时间（秒）
     * @return 临时访问URL
     */
    String generateAvatarTempUrl(String objectName, int expires);
    
    /**
     * 验证头像文件路径是否合法
     * 
     * @param objectName 文件对象名称
     * @return 是否合法
     */
    boolean validateAvatarPath(String objectName);
}