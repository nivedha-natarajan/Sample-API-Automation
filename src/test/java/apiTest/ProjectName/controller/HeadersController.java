package apiTest.ProjectName.controller;

import apiTest.framework.config.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import apiTest.framework.utility.TestLogger;
import apiTest.ProjectName.contants.Headers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HeadersController extends TestLogger implements Configuration, Headers {

    public static Map<Object, HashMap<Object, Object>> getHeaderObject() {
        Map<Object, HashMap<Object, Object>> headerMap = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            File fileObj = new File(headerConfig);

            headerMap = mapper.readValue(
                    fileObj, new TypeReference<>() {
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return headerMap;
    }
}
