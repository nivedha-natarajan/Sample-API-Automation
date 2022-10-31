package apiTest.framework.config;

import apiTest.framework.utility.TestLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface Configuration {

    Properties configProp = new Properties();
    String configurationPath = "Resources/configuration/apiConfig.properties";
    Long waitTimeout = Long.valueOf(getProperty("wait.timeout"));
    String defaultValidateTimeout = getProperty("default.validate.timeout");
    String headerConfig = getProperty("header.config");
    String manifestConfig = getProperty("manifest.config");
    String scenariosConfig = getProperty("scenarios.config");
    String secretsConfig = getProperty("secrets.config");
    String testcaseConfig = getProperty("testcase.config");
    String uriConfig = getProperty("uri.config");

    String PROJECT_DIR = getProjectDir();
    String BASE_URL = getProperty("base.url");


    static String getProjectDir() {
        return System.getProperty("user.dir");
    }

    static String getProperty(String key) {
        InputStream input = null;
        try {
            input = new FileInputStream(configurationPath);
            configProp.load(input);
        } catch (Exception e) {
            TestLogger.log("Error occurred while reading the file.");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return configProp.getProperty(key);
    }

}
