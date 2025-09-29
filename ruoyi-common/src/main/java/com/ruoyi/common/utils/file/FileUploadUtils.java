package com.ruoyi.common.utils.file;

import cn.hutool.core.lang.UUID;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.file.FileNameLengthLimitExceededException;
import com.ruoyi.common.exception.file.FileSizeLimitExceededException;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * 文件上传工具类
 *
 * @author ruoyi
 */
public class FileUploadUtils {
    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file    上传的文件
     * @return 文件名称
     * @throws IOException
     */
    public static final String upload(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, true);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 文件上传
     *
     * @param baseDir                   相对应用的基目录
     * @param file                      上传的文件
     * @param allowedExtension          上传文件类型
     * @param needDatePathAndRandomName 是否需要日期分类和随机文件名
     * @return 返回上传成功的文件名
     * @throws FileSizeLimitExceededException       文件大小超限异常
     * @throws FileNameLengthLimitExceededException 文件名长度超限异常
     * @throws InvalidExtensionException            不允许的文件类型异常
     */
    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension,
                                      boolean needDatePathAndRandomName) throws FileSizeLimitExceededException, FileNameLengthLimitExceededException,
            InvalidExtensionException, IOException {
        int fileNamelength = Objects.requireNonNull(file.getOriginalFilename()).length();
        if (fileNamelength > DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(DEFAULT_FILE_NAME_LENGTH);
        }

        assertAllowed(file, allowedExtension);

        String fileName = extractFilename(file, needDatePathAndRandomName);

        String absPath = baseDir + "/" + fileName;
        File desc = getAbsoluteFile(absPath);
        file.transferTo(desc);
        String pathFileName = getPathFileName(fileName);
        return pathFileName;
    }

    /**
     * 编码文件名
     */
    public static final String extractFilename(MultipartFile file, boolean needDatePathAndRandomName) {
        String fileName = file.getOriginalFilename();
        int slashIndex = fileName.lastIndexOf("/");
        if (slashIndex == -1) {
            slashIndex = fileName.lastIndexOf("\\");
        }
        if (slashIndex >= 0) {
            fileName = fileName.substring(slashIndex + 1);
        }
        if (needDatePathAndRandomName) {
            fileName = DateUtils.datePath() + "/" + UUID.fastUUID() + "." + getExtension(file);
        }
        return fileName;
    }

    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     * @throws FileSizeLimitExceededException 文件大小超限异常
     */
    public static final void assertAllowed(MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, InvalidExtensionException {
        long size = file.getSize();
        if (DEFAULT_MAX_SIZE != -1 && size > DEFAULT_MAX_SIZE) {
            throw new FileSizeLimitExceededException(DEFAULT_MAX_SIZE / 1024 / 1024);
        }

        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(
                        allowedExtension, extension, fileName);
            } else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(
                        allowedExtension, extension, fileName);
            } else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(
                        allowedExtension, extension, fileName);
            } else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION) {
                throw new InvalidExtensionException.InvalidVideoExtensionException(
                        allowedExtension, extension, fileName);
            } else {
                throw new InvalidExtensionException(allowedExtension, extension, fileName);
            }
        }
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension        上传文件类型
     * @param allowedExtension 允许的扩展名
     * @return true/false
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 上传的文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) {
        String extension = StringUtils.EMPTY;
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            int index = fileName.lastIndexOf(".");
            if (index > 0) {
                extension = fileName.substring(index + 1).toLowerCase();
            }
        }
        return extension;
    }

    /**
     * 编码文件名，不包含路径
     */
    public static final String getPathFileName(String fileName) {
        int separatorIndex = fileName.lastIndexOf("/");
        if (separatorIndex >= 0) {
            fileName = fileName.substring(separatorIndex + 1);
        }
        return fileName;
    }

    public static final File getAbsoluteFile(String fileName) throws IOException {
        File desc = new File(fileName);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

    public static final String getDefaultBaseDir() {
        return RuoYiConfig.getProfile();
    }
}