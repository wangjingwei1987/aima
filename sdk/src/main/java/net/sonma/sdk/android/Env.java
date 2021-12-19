/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.sonma.sdk.android;

public enum Env {
    DEMO_ONLINE("123456789", "123456789", API.HOST);
    private String accessKey;
    private String secretKey;
    private String host;

    Env(String accessKey, String secretKey, String host) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.host = host;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getHost() {
        return host;
    }
}