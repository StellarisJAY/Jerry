package com.jay.jerry.http.nio;

import com.jay.jerry.constant.ContentTypes;
import com.jay.jerry.constant.HttpConstants;
import com.jay.jerry.constant.HttpHeaders;
import com.jay.jerry.constant.JerryConstants;
import com.jay.jerry.entity.Cookie;
import com.jay.jerry.entity.HttpRequest;
import com.jay.jerry.entity.HttpSession;
import com.jay.jerry.exception.BadRequestException;
import com.jay.jerry.http.nio.common.AppendableByteArray;
import com.jay.jerry.http.nio.pipeline.ChannelContext;
import com.jay.jerry.http.nio.pipeline.PipelineTask;
import com.jay.jerry.session.SessionContainer;
import com.jay.jerry.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  Http解码器
 * </p>
 *
 * @author Jay
 * @date 2021/11/30
 **/
@Slf4j
public class HttpDecoder extends PipelineTask {
    @Override
    public boolean run(ChannelContext context) {
        try{
            // 分配请求行和头部ByteBuffer，默认大小8K
            ByteBuffer buffer = ByteBuffer.allocateDirect(HttpConstants.MAX_HEADER_LENGTH);
            SocketChannel channel = context.channel();
            // 读取请求行和headers，可能包含数据部分
            int bufferSize = channel.read(buffer);
            if(bufferSize == -1){
                throw new BadRequestException("request format error");
            }
            buffer.rewind();
            /*
                读取&解析请求行
             */
            AppendableByteArray requestLine = readLine(buffer, 0);
            // 请求行结束位置
            int requestLineEnd = requestLine.size();
            HttpRequest.HttpRequestBuilder requestBuilder = HttpRequest.builder();

            // 解析请求行
            parseRequestLine(new String(requestLine.array(), StandardCharsets.UTF_8), requestBuilder);

            /*
                读取&解析请求头
             */
            Map<String, String> headers = new HashMap<>();
            readHeaders(buffer, requestLineEnd + 2, headers);
            requestBuilder.headers(headers);
            requestBuilder.cookies(new HashMap<>());

            HttpRequest request = requestBuilder.build();
            /*
                读取content
             */
            if(headers.containsKey(HttpHeaders.CONTENT_LENGTH)){
                int contentLength = Integer.parseInt(headers.get(HttpHeaders.CONTENT_LENGTH));
                // 从channel读取content
                AppendableByteArray byteArray = readContent(buffer, channel, contentLength, bufferSize);
                // 解析content
                parseContent(byteArray.array(), headers, request);
            }

            /*
                读取Cookie
             */
            if(headers.containsKey(HttpHeaders.COOKIE)){
                parseCookies(request);
            }

            /*
                创建session
                是否开启session，默认开启，只有配置了enable-session=false才关闭
             */
            String enableSessionProperty = PropertiesUtil.get("enable-session");
            boolean enableSession = enableSessionProperty == null || Boolean.getBoolean(enableSessionProperty);
            if(enableSession){
                // 如果cookie中没有sessionId，且参数中也没有sessionId，新建session
                Cookie cookie = request.getCookie(JerryConstants.COOKIES_SESSION_TAG);
                // 新建session
                if(cookie == null){
                    HttpSession session = SessionContainer.newSession();
                    // sessionId记录在cookie中
                    cookie = Cookie.builder().name(JerryConstants.COOKIES_SESSION_TAG).value(session.getSessionId()).build();
                    request.setCookie(cookie);
                }
            }

            // 传递到下级task
            context.put("request", request);
            buffer.clear();
            return true;
        }catch (BadRequestException | IOException e){
            context.put("error", e);
            return false;
        }
    }

    /**
     * 读取以CRLF为换行符的一行数据
     * @param buffer ByteBuffer
     * @param offset 偏移，读取起点
     * @return @see AppendableByteArray
     */
    public AppendableByteArray readLine(ByteBuffer buffer, int offset){
        if(offset >= 0){
            buffer.rewind();
            buffer.position(offset);
        }
        AppendableByteArray bytes = new AppendableByteArray();
        boolean CR = false, LF = false;
        while(buffer.hasRemaining()){
            byte b = buffer.get();
            if(b == HttpConstants.LF){
                LF = true;
                break;
            }
            else if(b != HttpConstants.CR){
                CR = true;
                bytes.append(b);
            }
        }
        return CR&&LF ? bytes : bytes.clear();
    }

    /**
     * 从上一行结束位置读取下一行
     * @param buffer buffer
     * @return @see AppendableByteArray
     */
    public AppendableByteArray nextLine(ByteBuffer buffer){
        return readLine(buffer, -1);
    }

    private AppendableByteArray readContent(ByteBuffer buffer, SocketChannel channel, int contentLength, int readable) throws IOException {
        int len = contentLength;
        AppendableByteArray byteArray = new AppendableByteArray();
        while(len > 0){
            // headers读完，position在空行位置
            int position = buffer.position();
            int readCount = 0;
            while (position < readable) {
                byteArray.append(buffer.get());
                position++;
                readCount++;
            }

            len -= readCount;
            if(len > 0){
                buffer.compact();
                buffer.rewind();
                readable = channel.read(buffer);
                buffer.rewind();
            }
        }
        return byteArray;
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

        String method = reqLineParts[0].trim();
        String protocol = reqLineParts[2].trim();
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
                String name = pair.substring(0, equalsIndex).trim();
                String value = pair.substring(equalsIndex + 1).trim();
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
     * 读取headers
     * @param buffer ByteBuffer
     * @param startIndex header开始位置
     * @param headers headers
     * @throws BadRequestException BadRequest
     */
    private void readHeaders(ByteBuffer buffer, int startIndex, Map<String, String> headers) throws BadRequestException {
        AppendableByteArray byteArray = readLine(buffer, startIndex);
        String line = new String(byteArray.array(), StandardCharsets.UTF_8);
        parseHeader(line, headers);
        while((byteArray = nextLine(buffer)).size() != 0){
            line = new String(byteArray.array(), StandardCharsets.UTF_8);
            parseHeader(line, headers);
        }
    }

    /**
     * 解析请求头
     * @param line 行
     * @param headers headers
     * @throws BadRequestException BadRequest
     */
    private void parseHeader(String line, Map<String, String> headers) throws BadRequestException {
        if(!line.contains(":")){
            throw new BadRequestException("header format error");
        }
        String[] header = line.split(":");
        headers.put(header[0].trim(), header[1].trim());
    }


    private void parseContent(byte[] bytes, Map<String, String> headers, HttpRequest request) throws BadRequestException {
        String contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        if(contentType == null){
            return;
        }
        // 获取contentType枚举
        ContentTypes contentTypeEnum = ContentTypes.getContentTypeEnum(contentType.contains(";") ? contentType.substring(0, contentType.indexOf(";")) : contentType);
        if(contentTypeEnum == null){
            throw new BadRequestException("unknown content type");
        }

        // 根据ContentType做不同处理
        switch(contentTypeEnum){
            // multipart-form-data
            case MULTIPART_FORM_DATA: ContentDecoder.decodeMultipartFormData(bytes, contentType, request);break;

            // xxx-urlencoded
            case APPLICATION_XXX_URLENCODED: ContentDecoder.decodeUrlEncoded(bytes, request);break;

            default:break;
        }
    }

    private void parseCookies(HttpRequest request){
        Map<String, String> headers = request.getHeaders();
        String value = headers.get(HttpHeaders.COOKIE);
        String[] cookies = value.split(";");
        for(String cookie : cookies){
            int equalsIndex = cookie.indexOf("=");
            if(equalsIndex == -1){
                continue;
            }
            String cookieName = cookie.substring(0, equalsIndex).trim();
            String cookieValue = cookie.substring(equalsIndex + 1).trim();
            request.setCookie(Cookie.builder().name(cookieName).value(cookieValue).build());
        }
    }



}
