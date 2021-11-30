package com.jay.jerry.http.nio;

import com.jay.jerry.constant.HttpConstants;
import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.exception.BadRequestException;
import com.jay.jerry.http.nio.pipeline.ChannelContext;
import com.jay.jerry.http.nio.pipeline.PipelineTask;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
public class HttpDecoder extends PipelineTask {
    @Override
    public boolean run(ChannelContext context) {
        try{
            // 分配请求行和头部ByteBuffer
            ByteBuffer headersBuffer = ByteBuffer.allocateDirect(HttpConstants.MAX_LENGTH);
            SocketChannel channel = context.channel();
            // 读取请求行和headers，可能包含数据部分
            int headersLength = channel.read(headersBuffer);
            if(headersLength == -1){
                throw new BadRequestException("request format error");
            }
            headersBuffer.rewind();

            // 获取请求行范围
            int startIndex = 0;
            // 请求行结束下标
            int endIndex = 0;
            while(headersBuffer.hasRemaining()){
                byte b = headersBuffer.get();
                if(b == HttpConstants.LF){
                    break;
                }
                else if(b != HttpConstants.CR){
                    endIndex++;
                }
            }
            byte[] requestLineBuffer = new byte[endIndex - startIndex];
            headersBuffer.rewind();
            // 读取请求行
            headersBuffer.get(requestLineBuffer, startIndex, endIndex - startIndex);

            HttpRequest.HttpRequestBuilder requestBuilder = HttpRequest.builder();
            // 解析请求行
            String requestLine = new String(requestLineBuffer, StandardCharsets.UTF_8);
            parseRequestLine(requestLine, requestBuilder);

            // 读取headers部分
            byte[] headers = new byte[headersLength - endIndex];
            headersBuffer.rewind();
            // 跳过CRLF两个字节
            headersBuffer.position(endIndex + 2);
            headersBuffer.get(headers);
            int headersEnd = parseHeaders(new String(headers, StandardCharsets.UTF_8), requestBuilder);
            if(headersEnd == -1){
                throw new BadRequestException("request header is too big");
            }
            // 传递到下级task
            context.put("request", requestBuilder.build());
            headersBuffer.clear();
            return true;
        }catch (BadRequestException | IOException e){
            e.printStackTrace();
            context.put("error", e);
            return false;
        }
    }

    /**
     * 解析请求行
     * 解析出：请求方法、url、参数列表、协议
     * @param requestLine 请求行字符串
     * @param builder requestBuilder
     * @throws BadRequestException BadRequestException 错误的请求格式
     */
    public void parseRequestLine(String requestLine, HttpRequest.HttpRequestBuilder builder) throws BadRequestException {
        // 空格为间隔切分请求行
        String[] reqLineParts = requestLine.split(HttpConstants.SPACE);

        String method = reqLineParts[0];
        String protocol = reqLineParts[2];
        // url是否包含 ?
        int split = reqLineParts[1].indexOf("?");
        Map<String, String> params = null;
        if(split != -1){
            // 切分出查询参数字符串
            String paramsString = reqLineParts[1].substring(split + 1);
            params = new HashMap<>();
            // 切分参数列表
            String[] paramsList = paramsString.split("&");
            // 遍历每个键值对
            for(String pair : paramsList){
                int equalsIndex = pair.indexOf("=");
                // 参数列表格式错误
                if(equalsIndex == -1){
                    throw new BadRequestException("query params format error");
                }
                String name = pair.substring(0, equalsIndex);
                String value = pair.substring(equalsIndex);
                params.put(name, value);
            }
        }
        // 切分url
        String url = split == -1 ? reqLineParts[1] : reqLineParts[1].substring(0, split);
        builder.method(method)
                .protocol(protocol)
                .requestUrl(url)
                .params(params);
    }

    /**
     * 解析Headers
     * @param headersString header字符串
     * @param builder requestBuilder
     * @return 双CRLF位置
     * @throws BadRequestException BadRequestException 错误的请求格式
     */
    public int parseHeaders(String headersString, HttpRequest.HttpRequestBuilder builder) throws BadRequestException{
        Map<String, String> headers = null;
        int headersEnd = headersString.indexOf(HttpConstants.HEADER_ENDING);
        // 没有找到双CRLF，表示头部过长
        if(headersEnd == -1){
            return -1;
        }

        headers = new HashMap<>();
        String[] headerParts = headersString.substring(0, headersEnd).split(HttpConstants.CRLF);

        for(String pair : headerParts){
            int between = pair.indexOf(":");
            if(between == -1){
                throw new BadRequestException("header format error");
            }
            String key = pair.substring(0, between).trim();
            String value = pair.substring(between + 1).trim();
            headers.put(key, value);
        }
        builder.headers(headers);
        return headersEnd;
    }
}
