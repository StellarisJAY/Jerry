package com.jay.jerry.entity;

import com.jay.jerry.constant.HttpConstants;
import com.jay.jerry.constant.HttpHeaders;
import com.jay.jerry.constant.HttpStatus;
import com.jay.jerry.exception.HttpException;
import com.jay.jerry.http.common.ExceptionPage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sun.java2d.pipe.OutlineTextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
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

    private Map<String, String> headers = new HashMap<>();

    private HttpResponseOutputSteam outputSteam;

    public void setHeader(String name, String value){
        headers.put(name, value);
    }

    public String getHeader(String name){
        return headers.get(name);
    }

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

    public static HttpResponse errorResponse(Exception error){
        HttpResponse response = HttpResponse.builder()
                .protocol(HttpConstants.HTTP_1_1)
                .headers(new HashMap<>())
                .status(error instanceof HttpException ? ((HttpException) error).getStatus() : HttpStatus.INTERNAL_SERVER_ERROR).build();
        response.setHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
        ExceptionPage exceptionPage = new ExceptionPage(response.getStatus(), error.getMessage());
        response.out().write(exceptionPage.getHTML());
        return response;
    }
}
