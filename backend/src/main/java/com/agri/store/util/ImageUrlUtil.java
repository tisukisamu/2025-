package com.agri.store.util;

import com.agri.store.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 图片路径工具类
 * 实现双保底策略：优先使用相对路径(imageUrls)，失败时降级到绝对路径(imageUrl)
 */
@Component
public class ImageUrlUtil {

    /**
     * 路径类型枚举
     */
    public enum ImagePathType {
        RELATIVE("relative", "相对路径"),
        ABSOLUTE("absolute", "绝对路径"),
        FALLBACK("fallback", "降级路径"),
        NONE("none", "无图片");

        private final String code;
        private final String description;

        ImagePathType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 图片路径结果
     */
    public static class ImagePathResult {
        private final String url;
        private final ImagePathType type;
        private final List<String> allUrls;

        public ImagePathResult(String url, ImagePathType type, List<String> allUrls) {
            this.url = url;
            this.type = type;
            this.allUrls = allUrls;
        }

        public String getUrl() {
            return url;
        }

        public ImagePathType getType() {
            return type;
        }

        public List<String> getAllUrls() {
            return allUrls;
        }
    }

    /**
     * 获取产品主图（优先相对路径）
     *
     * @param product 产品实体
     * @return 图片路径结果
     */
    public static ImagePathResult getPrimaryImage(Product product) {
        if (product == null) {
            return new ImagePathResult(null, ImagePathType.NONE, Collections.emptyList());
        }

        // 1. 优先尝试从 imageUrls 获取第一张图（相对路径）
        List<String> relativeUrls = parseImageUrls(product.getImageUrls());
        if (!relativeUrls.isEmpty()) {
            String firstRelativeUrl = relativeUrls.get(0);
            // 确保相对路径以 / 开头
            if (!firstRelativeUrl.startsWith("http") && !firstRelativeUrl.startsWith("/")) {
                firstRelativeUrl = "/" + firstRelativeUrl;
            }
            return new ImagePathResult(firstRelativeUrl, ImagePathType.RELATIVE, relativeUrls);
        }

        // 2. 降级使用 imageUrl（绝对路径）
        if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
            return new ImagePathResult(
                product.getImageUrl(),
                ImagePathType.ABSOLUTE,
                Collections.singletonList(product.getImageUrl())
            );
        }

        // 3. 无图片
        return new ImagePathResult(null, ImagePathType.NONE, Collections.emptyList());
    }

    /**
     * 获取产品所有图片（优先相对路径）
     *
     * @param product 产品实体
     * @return 图片路径结果列表
     */
    public static List<ImagePathResult> getAllImages(Product product) {
        if (product == null) {
            return Collections.emptyList();
        }

        // 1. 优先尝试从 imageUrls 获取所有图（相对路径）
        List<String> relativeUrls = parseImageUrls(product.getImageUrls());
        if (!relativeUrls.isEmpty()) {
            return relativeUrls.stream()
                .map(url -> {
                    // 确保相对路径以 / 开头
                    if (!url.startsWith("http") && !url.startsWith("/")) {
                        url = "/" + url;
                    }
                    return new ImagePathResult(url, ImagePathType.RELATIVE, relativeUrls);
                })
                .toList();
        }

        // 2. 降级使用 imageUrl（绝对路径）
        if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
            return Collections.singletonList(
                new ImagePathResult(
                    product.getImageUrl(),
                    ImagePathType.ABSOLUTE,
                    Collections.singletonList(product.getImageUrl())
                )
            );
        }

        // 3. 无图片
        return Collections.emptyList();
    }

    /**
     * 获取降级路径（用于前端加载失败时）
     *
     * @param product 产品实体
     * @return 降级路径（绝对路径）
     */
    public static String getFallbackUrl(Product product) {
        if (product == null) {
            return null;
        }
        return product.getImageUrl();
    }

    /**
     * 解析 imageUrls 字符串为列表
     *
     * @param imageUrls 逗号分隔的图片URL字符串
     * @return URL列表
     */
    public static List<String> parseImageUrls(String imageUrls) {
        if (imageUrls == null || imageUrls.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(imageUrls.split(","))
            .map(String::trim)
            .filter(url -> !url.isEmpty())
            .toList();
    }

    /**
     * 构建图片路径响应DTO
     *
     * @param product 产品实体
     * @return 图片路径信息
     */
    public static ImagePathInfo buildImagePathInfo(Product product) {
        ImagePathResult primary = getPrimaryImage(product);
        List<ImagePathResult> all = getAllImages(product);

        ImagePathInfo info = new ImagePathInfo();
        info.setPrimaryUrl(primary.getUrl());
        info.setPrimaryType(primary.getType().getCode());
        info.setFallbackUrl(getFallbackUrl(product));
        info.setAllUrls(all.stream().map(ImagePathResult::getUrl).toList());
        info.setPathType(primary.getType().getCode());
        info.setPathTypeDesc(primary.getType().getDescription());

        return info;
    }

    /**
     * 图片路径信息DTO
     */
    public static class ImagePathInfo {
        private String primaryUrl;
        private String primaryType;
        private String fallbackUrl;
        private List<String> allUrls;
        private String pathType;
        private String pathTypeDesc;

        // Getters and Setters
        public String getPrimaryUrl() {
            return primaryUrl;
        }

        public void setPrimaryUrl(String primaryUrl) {
            this.primaryUrl = primaryUrl;
        }

        public String getPrimaryType() {
            return primaryType;
        }

        public void setPrimaryType(String primaryType) {
            this.primaryType = primaryType;
        }

        public String getFallbackUrl() {
            return fallbackUrl;
        }

        public void setFallbackUrl(String fallbackUrl) {
            this.fallbackUrl = fallbackUrl;
        }

        public List<String> getAllUrls() {
            return allUrls;
        }

        public void setAllUrls(List<String> allUrls) {
            this.allUrls = allUrls;
        }

        public String getPathType() {
            return pathType;
        }

        public void setPathType(String pathType) {
            this.pathType = pathType;
        }

        public String getPathTypeDesc() {
            return pathTypeDesc;
        }

        public void setPathTypeDesc(String pathTypeDesc) {
            this.pathTypeDesc = pathTypeDesc;
        }
    }
}
