package com.zf.common.product.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;

/**
 * base64转MultipartFile工具类
 * @author:caiLei
 */
public class ImageUtil implements MultipartFile {
    //后缀
    private String suffix;
    //字节数组
    private byte[] content;
    //类型
    private String contentType;


    /**
     * 带dataURI的base64数据
     *
     * @param base64
     */
    public ImageUtil(String base64) {
        String[] data = base64.split(",");
        this.content = Base64.getDecoder().decode(data[1].getBytes());
        this.suffix = data[0].split(";")[0].split("/")[1];
        this.contentType = data[0].split(";")[0].split(":")[1];
    }

    /**
     * 不带dataURI的base64数据
     *
     * @param base64      base64数据
     * @param suffix      文件后缀名
     * @param contentType 内容类型
     */
    public ImageUtil(String base64, String suffix, String contentType) {
        this.content = Base64.getDecoder().decode(base64.getBytes());
        this.suffix = suffix;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return "temp_" + System.currentTimeMillis();
    }

    @Override
    public String getOriginalFilename() {
        return "temp_" + System.currentTimeMillis() + "." + suffix;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        }
    }


    public static MultipartFile getMultipartFile(String base64) {
        return new ImageUtil(base64);
    }

    public static MultipartFile getMultipartFile(String base64, String suffix, String contentType) {
        return new ImageUtil(base64, suffix, contentType);
    }
}