package com.jay.jerry.http.nio;

import com.jay.jerry.constant.HttpConstants;
import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.entity.MultipartFile;
import com.jay.jerry.exception.BadRequestException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/12/6
 **/
public class ContentDecoder {

    /**
     * 解析form-data
     * @param bytes content
     * @param contentType contentType
     * @param request request
     * @throws BadRequestException BadRequestException
     */
    public static void decodeMultipartFormData(byte[] bytes, String contentType, HttpRequest request) throws BadRequestException {
        // 判断是否有boundary
        if(!contentType.contains(";")){
            throw new BadRequestException("missing boundary for content type: form-data");
        }
        // 切分contentType，获得boundary
        String[] contentTypeParts = contentType.split(";");
        String boundaryLine = contentTypeParts[1].trim();
        String boundary = "--" + boundaryLine.substring(boundaryLine.indexOf("boundary=") + 9).trim();

        // boundary为间隔切分content
        String[] formDatas = new String(bytes, StandardCharsets.UTF_8).split(boundary);

        // 解析每个部分
        for(String formData : formDatas){
            // 属性和实际数据之间的空行
            String[] formDataParts = formData.split("\r\n\r\n");
            if(formDataParts.length != 2){
                continue;
            }
            // 解析属性部分
            String[] attributes = formDataParts[0].split(HttpConstants.CRLF);
            Map<String, String> attributeMap = new HashMap<>();
            for(String attribute : attributes){
                // 格式是否正确
                int splitIndex = attribute.indexOf(":");
                if(splitIndex == -1){
                    continue;
                }
                String name = attribute.substring(0, splitIndex).trim();
                String value = attribute.substring(splitIndex + 1).trim();
                attributeMap.put(name, value);
            }
            // 获取Content-Disposition
            String contentDisposition = attributeMap.get("Content-Disposition");
            if(contentDisposition == null){
                continue;
            }
            // 解析出name字段
            String[] dispositions = contentDisposition.split(";");
            String name = dispositions[1].substring(dispositions[1].indexOf("=") + 1).replace("\"", "").trim();
            String value = formDataParts[1];

            // 非文件表单数据
            if(!attributeMap.containsKey("Content-Type")){
                request.setParameter(name, value);
            }
            // 文件
            else{
                MultipartFile multipartFile = new MultipartFile();
                // 解析出filename
                String filename = dispositions[2].substring(dispositions[2].indexOf("=") + 1).replace("\"", "").trim();
                byte[] buffer = value.getBytes(StandardCharsets.UTF_8);
                InputStream inputStream = new ByteArrayInputStream(buffer);
                multipartFile.setInputStream(inputStream);
                multipartFile.setName(name);
                multipartFile.setFilename(filename);
                request.addFile(multipartFile);
            }
        }
    }

    public static void decodeUrlEncoded(byte[] bytes, HttpRequest request){
        String line = new String(bytes, StandardCharsets.UTF_8);
        String[] pairs = line.split("&");
        for(String pair : pairs){
            int equalsIndex = pair.indexOf("=");
            if(equalsIndex == -1){
                continue;
            }
            String name = pair.substring(0, equalsIndex);
            String value = pair.substring(equalsIndex + 1);
            request.setParameter(name, value);
        }
    }
}
