package apiTest.ProjectName.controller;

import apiTest.ProjectName.contants.Headers;
import apiTest.ProjectName.contants.StatusCode;
import apiTest.ProjectName.pojo.CookieJar;
import apiTest.ProjectName.pojo.GeneralPojo;
import apiTest.ProjectName.test.RunAPITests;
import io.restassured.RestAssured;

import java.util.HashMap;

public class APIRequestController extends RunAPITests implements Headers, StatusCode {

    public void getAPIRequest (int _logTest, HashMap<Object, Object> requestMap, HashMap<Object, Object> responseMap, GeneralPojo pojo, String testcaseName, CookieJar cookieJar) throws Exception {
        System.out.println();
        testInfoLog("Test " + _logTest, getStringValue(requestMap, TEST_NAME));
        request = RestAssured.given();

        //Set Content-type
        if (requestMap.containsKey(CONTENT_TYPE)) {
            request.contentType(getStringValue(requestMap, CONTENT_TYPE));
        }

        //Replace URI variable
        if (requestMap.containsKey(PATH_PARAM)) {
            requestMap.put(URI, setPathParam(requestMap, pojo));
        }

        //Set Headers request
        if (requestMap.containsKey(HEADERS)) {
            request.headers(getConvertToMap(requestMap, HEADERS, pojo));
        }

        //Set query param
        if (requestMap.containsKey(QUERY_PARAM)) {
            requestMap.putAll(setQueryParam(requestMap, request, pojo));
        }

        if (requestMap.containsKey(BODY)) {
            request.body(getConvertToMap(requestMap, BODY, pojo));
        }

        if (_logTest != 1) {
            request.cookies(cookieJar.getCookies(BASE_URL));
        }

        switch (getStringValue(requestMap, METHOD)) {
            case GET -> response = get(getStringValue(requestMap, URI), request, responseMap, requestMap);
            case POST -> response = post(getStringValue(requestMap, URI), request, responseMap, requestMap);
            case PUT -> response = put(getStringValue(requestMap, URI), request, responseMap, requestMap);
            case DELETE -> response = delete(getStringValue(requestMap, URI), request, responseMap, requestMap);
            case PATCH -> response = patch(getStringValue(requestMap, URI), request, responseMap, requestMap);
        }

        getVerifyResponseCommand(responseMap, response, pojo, getStringValue(requestMap, TEST_NAME), _logTest, cookieJar);
    }
}
