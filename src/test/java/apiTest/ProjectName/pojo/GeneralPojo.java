package apiTest.ProjectName.pojo;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class GeneralPojo {

    private final Map<String, Response> scenarioResponseMap = new HashMap<> () ;

    public void setTestcaseResponse(String testcaseName, Response response) {
        scenarioResponseMap.put(testcaseName, response);
    }

    public Response getTestcaseResponse(Object testcaseName) {
        return scenarioResponseMap.get(testcaseName.toString());
    }

    public boolean verifyTestcaseKey(String testcaseName) {
        return scenarioResponseMap.containsKey(testcaseName);
    }
}
