package com;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private String serverIp;
    private Properties prop;

    public Config() throws IOException {
        String propFile = "config/config.properties";

        // 프로퍼티 파일 스트림에 담기
        FileInputStream fis = new FileInputStream(propFile);
        // 프로퍼티 파일 로딩
        prop.load(new java.io.BufferedInputStream(fis));

        this.serverIp = prop.getProperty("SERVER_IP");
    }

    public String getServerIp() {
        return serverIp;
    }
}
