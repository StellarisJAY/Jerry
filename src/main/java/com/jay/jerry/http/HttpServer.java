package com.jay.jerry.http;

/**
 * @author Jay
 */
public interface HttpServer {
    void start(int port);

    void doService();
}
