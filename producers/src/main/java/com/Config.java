package com;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private String serverIp;
    private String schemaRgstryUrl;

    public Config() throws IOException {
        String propFile = "config.properties";  // 클래스패스에서 찾을 파일 이름

        // 클래스패스에서 프로퍼티 파일 스트림을 가져옴
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFile);

        if (inputStream == null) {
            throw new FileNotFoundException("Property file '" + propFile + "' not found in the classpath");
        }

        Properties prop = new Properties();
        prop.load(inputStream);

        this.serverIp = prop.getProperty("SERVER_IP");
        this.schemaRgstryUrl = prop.getProperty("SCHEMA_REGISTRY_URL");
    }

    public String getServerIp() {
        return serverIp;
    }
    public String getSchemaRgstryUrl() { return schemaRgstryUrl; }

}
