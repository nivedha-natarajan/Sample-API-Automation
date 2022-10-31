package apiTest.ProjectName.controller;

import apiTest.framework.config.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ManifestController implements Configuration {

    public static Map<Object, HashMap<Object, Object>> getManifestObject() {

        Map<Object, HashMap<Object, Object>> manifestMap = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            File fileObj = new File(manifestConfig);

            manifestMap = mapper.readValue(
                    fileObj, new TypeReference<>() {
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return manifestMap;
    }
}