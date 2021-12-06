package com.jay.jerry.http.nio;

import com.jay.jerry.constant.HttpConstants;
import com.jay.jerry.constant.HttpStatus;
import com.jay.jerry.entity.Cookie;
import com.jay.jerry.entity.HttpResponse;
import com.jay.jerry.http.nio.pipeline.ChannelContext;
import com.jay.jerry.http.nio.pipeline.PipelineTask;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public class HttpEncoder extends PipelineTask {
    @Override
    public boolean run(ChannelContext context) {
        try{
            Object error = context.get("error");
            HttpResponse response;
            // 请求发生异常，发送异常response
            if(error != null || context.get("response") == null){
                Exception e = (Exception)error;
                response = HttpResponse.errorResponse(e);
            }
            else{
                // 没有异常，获取response
                response = (HttpResponse)context.get("response");
            }
            // 状态行信息
            String protocol = response.getProtocol();
            HttpStatus status = response.getStatus();

            SocketChannel channel = context.channel();

            // 拼接状态行
            StringBuilder respStringBuilder = new StringBuilder();
            respStringBuilder.append(protocol).append(HttpConstants.SPACE)
                    .append(status.getCode()).append(HttpConstants.SPACE)
                    .append(status.getMessage()).append(HttpConstants.CRLF);

            // 写入headers
            if(response.getHeaders() != null){
                for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
                    respStringBuilder.append(entry.getKey())
                            .append(": ")
                            .append(entry.getValue()).
                            append(HttpConstants.CRLF);
                }
            }
            // 写入 Set-Cookie
            if(response.getCookies() != null){
                for(Cookie cookie : response.getCookies().values()){
                    respStringBuilder.append("Set-Cookie: ");
                    respStringBuilder.append(cookie.getName()).append("=");
                    respStringBuilder.append(cookie.getValue()).append(";");
                    respStringBuilder.append(HttpConstants.CRLF);
                }
            }
            // headers结尾空行
            respStringBuilder.append(HttpConstants.CRLF);

            byte[] bytes = respStringBuilder.toString().getBytes(StandardCharsets.UTF_8);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            channel.write(buffer);
            ByteBuffer contentBuffer = ByteBuffer.wrap(response.out().toByteArray());
            channel.write(contentBuffer);
            buffer.clear();
            return false;
        } catch (IOException e) {
            return false;
        }
    }


}
