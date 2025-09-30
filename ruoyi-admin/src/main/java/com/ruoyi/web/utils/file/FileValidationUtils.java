package com.ruoyi.web.utils.file;

import com.ruoyi.web.constant.FileConstants;
import com.ruoyi.web.exception.file.FileException;
import com.ruoyi.web.exception.file.FileSizeLimitExceededException;
import com.ruoyi.web.exception.file.InvalidExtensionException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * 文件验证工具类
 *
 * @author ruoyi
 */
public class FileValidationUtils {
    /**
     * 验证上传的文件
     *
     * @param file              上传的文件
     * @param allowedExtensions 允许的扩展名
     * @param maxSize           最大文件大小
     * @throws FileSizeLimitExceededException 文件大小超限异常
     * @throws InvalidExtensionException      不允许的文件类型异常
     */
    public static void validateFile(MultipartFile file, String[] allowedExtensions, long maxSize)
            throws FileSizeLimitExceededException, InvalidExtensionException {
        if (file == null || file.isEmpty()) {
            throw new FileException("upload.file.empty", new Object[]{});
        }

        // 检查文件大小
        if (maxSize > 0 && file.getSize() > maxSize) {
            throw new FileSizeLimitExceededException(maxSize / (1024 * 1024));
        }

        // 验证文件扩展名
        String fileName = file.getOriginalFilename();
        String extension = getFileExtension(fileName);
        if (allowedExtensions != null && !isAllowedExtension(extension, allowedExtensions)) {
            if (allowedExtensions == MimeTypeUtils.IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(
                        allowedExtensions, extension, fileName);
            } else if (allowedExtensions == MimeTypeUtils.FLASH_EXTENSION) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(
                        allowedExtensions, extension, fileName);
            } else if (allowedExtensions == MimeTypeUtils.MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(
                        allowedExtensions, extension, fileName);
            } else if (allowedExtensions == MimeTypeUtils.VIDEO_EXTENSION) {
                throw new InvalidExtensionException.InvalidVideoExtensionException(
                        allowedExtensions, extension, fileName);
            } else {
                throw new InvalidExtensionException(allowedExtensions, extension, fileName);
            }
        }
    }

    /**
     * 验证头像文件
     *
     * @param file 头像文件
     */
    public static void validateAvatarFile(MultipartFile file) throws InvalidExtensionException {
        validateFile(file, FileConstants.AVATAR_ALLOWED_EXTENSION, FileConstants.AVATAR_MAX_SIZE);
    }

    /**
     * 验证图片文件
     *
     * @param file 图片文件
     */
    public static void validateImageFile(MultipartFile file) throws InvalidExtensionException {
        validateFile(file, MimeTypeUtils.IMAGE_EXTENSION, FileConstants.AVATAR_MAX_SIZE);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            return fileName.substring(index + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 判断扩展名是否被允许
     *
     * @param extension         扩展名
     * @param allowedExtensions 允许的扩展名数组
     * @return 是否允许
     */
    public static boolean isAllowedExtension(String extension, String[] allowedExtensions) {
        if (allowedExtensions == null || extension == null) {
            return false;
        }

        return Arrays.stream(allowedExtensions)
                .anyMatch(allowedExt -> allowedExt.equalsIgnoreCase(extension));
    }
}