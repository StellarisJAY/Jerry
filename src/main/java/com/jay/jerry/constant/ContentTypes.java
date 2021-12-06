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
    TEXT_HTML("html", "text/html"),
    TEXT_PLAIN("", "text/plain"),
    TEXT_XML("xml", "text/xml"),

    IMAGE_GIF("gif", "image/gif"),
    IMAGE_PNG("png", "image/png"),

    APPLICATION_JSON("json", "application/json"),
    APPLICATION_PDF("pdf", "application/pdf"),

    MULTIPART_FORM_DATA("form-data", "multipart/form-data"),
    APPLICATION_XXX_URLENCODED("url-encoded", "application/x-www-form-urlencoded")
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

    public static ContentTypes getContentTypeEnum(String type){
        for (ContentTypes value : ContentTypes.values()) {
            if(value.contentType.equalsIgnoreCase(type)){
                return value;
            }
        }
        return null;
    }

    public String getContentType() {
        return contentType;
    }
}
