/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.sonma.sdk.android;


public interface API {
    String HOST = "https://api.sonma.net";
    String LOCALHOST = "http://192.168.0.83:8080";
    String PRINT = "/v1/print";
    String ACCESS_TOKEN = "/v1/auth/access_token";

    interface PARAMS {
        String SN = "sn";
        String CONTENT = "content";
        String TEMPLATE = "template";
        String TOKEN = "token";
        String SCOPE = "scope";
        String EXP = "exp";
        String TIMES = "times";
    }

    interface HEADER {
        String AUTHORIZATION = "Authorization";
        String TIMESTAMP = "Timestamp";
    }
}
