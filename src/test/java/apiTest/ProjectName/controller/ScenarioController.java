package apiTest.ProjectName.controller;

import apiTest.framework.config.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;

public class ScenarioController implements Configuration {

    public static Map<Object, Object> getScenarioObject() {

        Map<Object, Object> scenarioMap = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            File fileObj = new File(scenariosConfig);

            scenarioMap = mapper.readValue(
                    fileObj, new TypeReference<>() {
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return scenarioMap;
    }
}