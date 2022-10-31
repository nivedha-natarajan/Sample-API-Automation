package apiTest.ProjectName.controller;

import apiTest.framework.config.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import apiTest.ProjectName.contants.Headers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TestCaseController implements Configuration, Headers {

    public static Map<Object, HashMap<Object, HashMap<Object, Object>>> getTestCaseMap () {

        Map<Object, HashMap<Object, HashMap<Object, Object>>> apiTestsMap = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            File fileObj = new File(testcaseConfig);
                 apiTestsMap = mapper.readValue(
                        fileObj, new TypeReference<>() {
                        });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiTestsMap;
    }
}
