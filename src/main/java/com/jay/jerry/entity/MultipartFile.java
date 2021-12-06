package com.jay.jerry.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStream;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/12/3
 **/
@NoArgsConstructor
@Getter
@Setter
public class MultipartFile {

    private String name;
    private String filename;
    private InputStream inputStream;

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
