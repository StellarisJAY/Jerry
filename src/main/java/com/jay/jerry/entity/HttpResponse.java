package com.jay.jerry.entity;

import com.jay.jerry.constant.HttpConstants;
import com.jay.jerry.constant.HttpHeaders;
import com.jay.jerry.constant.HttpStatus;
import com.jay.jerry.exception.HttpException;
import com.jay.jerry.exception.ExceptionPage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  HTTP 返回
 * </p>
 *
 * @author Jay
 * @date 2021/11/28
 **/
@Getter
@Builder
@ToString
public class HttpResponse {
    private String protocol;
    @Setter
    private HttpStatus status;

    private Map<String, String> headers;

    private HttpResponseOutputSteam outputSteam;

    public void setHeader(String name, String value){
        headers.put(name, value);
    }

    public String getHeader(String name){
        return headers.get(name);
    }

    /**
     * HTTP返回输出流
     * 通过向该输出流写数据来返回数据
     */
    public static class HttpResponseOutputSteam extends ByteArrayOutputStream{
        public void write(String s)  {
            try{
                write(s.getBytes(StandardCharsets.UTF_8));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public HttpResponseOutputSteam out(){
        if(outputSteam == null){
            outputSteam = new HttpResponseOutputSteam();
        }
        return outputSteam;
    }

    /**
     * 创建 错误返回
     * @param error 异常
     * @return HttpResponse
     */
    public static HttpResponse errorResponse(Exception error){
        HttpResponse response = HttpResponse.builder()
                .protocol(HttpConstants.HTTP_1_1)
                .headers(new HashMap<>())
                // 状态码，异常自带或者500
                .status(error instanceof HttpException ? ((HttpException) error).getStatus() : HttpStatus.INTERNAL_SERVER_ERROR).build();
        // content-type，html页面
        response.setHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
        // 创建错误页面
        ExceptionPage exceptionPage = new ExceptionPage(response.getStatus(), error.getMessage());
        // 输出流写入错误页面
        response.out().write(exceptionPage.getHTML());
        return response;
    }
}
