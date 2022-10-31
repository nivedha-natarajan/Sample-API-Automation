package apiTest.ProjectName.test;

import apiTest.framework.generics.Generics;
import apiTest.framework.init.RestInit;
import apiTest.ProjectName.contants.Headers;
import apiTest.ProjectName.controller.*;
import apiTest.ProjectName.pojo.CookieJar;
import apiTest.ProjectName.pojo.GeneralPojo;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RunAPITests extends RestInit {

    public static final String DP_SCENARIO = "scenarioObject";
    protected GeneralPojo pojo = new GeneralPojo();
    Map<Object, HashMap<Object, HashMap<Object, Object>>> testCaseMap;
    LinkedHashMap<Object, Object> scenarioObject;
    HashMap<Object, Object> requestObject;
    HashMap<Object, Object> responseObject;
    APIRequestController apiRequests;
    String[] scenarios;

    @Test
    public void validateConfiguration() throws Exception {
        ValidateConfiguration.verifyConfiguration();
    }

    @Test (dependsOnMethods = {"validateConfiguration"}, dataProvider = DP_SCENARIO)
    public void scenario(Map<Object, Object> map) throws Exception {

        GeneralPojo pojo = new GeneralPojo();
        CookieJar cookieJar = new CookieJar();

        for (Map.Entry<Object, Object> tempScenarioMap : map.entrySet()) {

            int _logTest = 1;

            boolean skipped = false;

            testCaseLog(tempScenarioMap.getKey().toString().toUpperCase());
            scenarioStartTime = System.currentTimeMillis();
            _logger.debug("Scenario Start Time : " + java.time.LocalTime.now());

            scenarios = Generics.getStringValue(scenarioObject, tempScenarioMap.getKey().toString()).replace("[", "").replace("]", "").split(",");

            for (String scenario : scenarios) {
                try {
                    scenario = scenario.trim();

                    //Request and Response Map Objects from testcaseConfig
                    requestObject = new HashMap<>(testCaseMap.get(scenario).get(Headers.REQUEST));
                    responseObject = new HashMap<>(testCaseMap.get(scenario).get(Headers.RESPONSE));

                    //Get the Test Name from Manifest
                    requestObject.put(Headers.TEST_NAME, scenario + " - " + ManifestController.getManifestObject().get(scenario).get(Headers.TEST_NAME));

                    if (!skipped) {

                        //Get URI from URIConfig
                        requestObject.put(Headers.URI, UriController.getUriObject().get(Generics.getStringValue(requestObject, Headers.URI)));

                        //Replace testcase variables
                        requestObject.putAll(Generics.replaceVariable(requestObject));
                        responseObject.putAll(Generics.replaceVariable(responseObject));

                        //Make api request call
                        testcaseStartTime = System.currentTimeMillis();
                        _logger.debug("Testcase Start Time : " + java.time.LocalTime.now());
                        apiRequests.getAPIRequest(_logTest, requestObject, responseObject, pojo, scenario, cookieJar);
                        _logger.debug("Testcase Execution Time in Seconds : " + formatter.format((System.currentTimeMillis() - testcaseStartTime) / 1000d));
                        success();
                    } else {
                        skipped("Test " + _logTest + " : " + Generics.getStringValue(requestObject, Headers.TEST_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    testErrorLog(e);
                    skipped = true;
                }
                _logTest++;
            }
            _logger.debug("Scenario Execution Time in Seconds : " + formatter.format((System.currentTimeMillis() - scenarioStartTime) / 1000d));
            if (skipped) {
                throw new Exception(tempScenarioMap.getKey().toString().toUpperCase() + " Failed");
            }
        }
    }

    @DataProvider(name = DP_SCENARIO)
    public Object[][] getRunAPITests () {

        apiRequests = new APIRequestController();

        scenarioObject = new LinkedHashMap<>(ScenarioController.getScenarioObject());

        testCaseMap = new HashMap<>(TestCaseController.getTestCaseMap());

        Object[][] obj = new Object[scenarioObject.size()][1];

        int iterate = 0;

        for (Map.Entry<Object, Object> tempScenarioMap : scenarioObject.entrySet()) {

            Map<Object, Object> dataMap = new HashMap<>();

            dataMap.put(tempScenarioMap.getKey(), tempScenarioMap.getValue());

            obj[iterate][0] = dataMap;

            iterate++;
        }
        return obj;
    }
}
