package com.jay.jerry.constant;

/**
 * <p>
 *
 * </p>
 *
 * @author Jay
 * @date 2021/12/2
 **/
public enum  ContentTypes {
    /**
     * html
     */
    TEXT_HTML("html", "text/html;charset=utf-8"),
    TEXT_PLAIN("", "text/plain;charset=utf-8"),
    TEXT_XML("xml", "text/xml;charset=utf-8"),

    IMAGE_GIF("gif", "image/gif"),
    IMAGE_PNG("png", "image/png"),

    APPLICATION_JSON("json", "application/json;charset=utf-8"),
    APPLICATION_PDF("pdf", "application/pdf")
    ;

    private String name;
    private String contentType;

    ContentTypes(String name, String contentType) {
        this.name = name;
        this.contentType = contentType;
    }

    public static ContentTypes getContentType(String name){
        for (ContentTypes value : ContentTypes.values()) {
            if(value.name.equalsIgnoreCase(name)){
                return value;
            }
        }
        return null;
    }

    public String getContentType() {
        return contentType;
    }
}
