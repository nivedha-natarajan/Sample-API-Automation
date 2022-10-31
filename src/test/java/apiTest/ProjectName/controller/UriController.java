package apiTest.ProjectName.controller;

import apiTest.framework.config.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import apiTest.ProjectName.contants.Headers;

import java.io.File;
import java.util.Map;

public class UriController implements Configuration, Headers {

    public static Map<Object, Object> getUriObject() {

        Map<Object, Object> uriMap = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            File fileObj = new File(uriConfig);

            uriMap = mapper.readValue(
                    fileObj, new TypeReference<Map<Object, Object>>() {
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return uriMap;
    }
}