package apiTest.ProjectName.controller;

import apiTest.framework.config.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SecretsController implements Configuration {

    public static Map<Object, HashMap<Object, Object>> getSecretsObject() {

        Map<Object, HashMap<Object, Object>> secretsMap = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            File fileObj = new File(secretsConfig);

            secretsMap = mapper.readValue(
                    fileObj, new TypeReference<>() {
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return secretsMap;
    }
}
