package apiTest.framework.generics;

import apiTest.ProjectName.contants.Headers;
import apiTest.ProjectName.contants.StatusCode;
import apiTest.ProjectName.controller.HeadersController;
import apiTest.ProjectName.controller.SecretsController;
import apiTest.ProjectName.pojo.CookieJar;
import apiTest.framework.utility.TestLogger;
import apiTest.ProjectName.pojo.GeneralPojo;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.validator.routines.DomainValidator;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.hamcrest.Matchers;
import org.testng.*;
import org.testng.internal.Utils;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Generics extends TestLogger implements StatusCode, Headers {

    public static void queryParam(String key, Object value, RequestSpecification request) {
        testTraceLog(key + " : " + value.toString());
        request.queryParam(key, value);
    }

    public static Response get(String url, RequestSpecification request, HashMap<Object, Object> responseMap, HashMap<Object, Object> requestMap) throws Exception {
        testInfoLog(GET , "Request initialized");
        printURI(url, requestMap);
        if (requestMap.containsKey(APPLY_WAIT)) {
            return getStatus(request, url, responseMap, GET, Long.parseLong(getStringValue(requestMap, APPLY_WAIT)));
        } else {
            return request.get(url);
        }
    }

    public static void printURI (String url, HashMap<Object, Object> requestMap) {
        if (requestMap.containsKey(QUERY_STRING)) {
            testInfoLog("Resource", BASE_URL + url + requestMap.get(QUERY_STRING));
        } else {
            testInfoLog("Resource", BASE_URL + url);
        }
    }

    public static Response getStatus(RequestSpecification request, String url, HashMap<Object, Object> responseMap, String api, long wait) throws Exception {
        final Response[] response = new Response[1];
        try {
            Awaitility.await().atMost(waitTimeout, TimeUnit.SECONDS).pollDelay(wait, TimeUnit.SECONDS).until(() ->
            {
                boolean status = false;
                switch (api) {
                    case GET -> {
                        response[0] = request.get(url);
                        status = getStringValue(responseMap, STATUS).equals(String.valueOf(response[0].getStatusCode()));
                    }
                    case POST -> {
                        response[0] = request.post(url);
                        status = getStringValue(responseMap, STATUS).equals(String.valueOf(response[0].getStatusCode()));
                    }
                    case DELETE -> {
                        response[0]= request.delete(url);
                        status = getStringValue(responseMap, STATUS).equals(String.valueOf(response[0].getStatusCode()));
                    }
                    case PATCH -> {
                        response[0] = request.patch(url);
                        status = getStringValue(responseMap, STATUS).equals(String.valueOf(response[0].getStatusCode()));
                    }
                    case PUT -> {
                        response[0] = request.put(url);
                        status = getStringValue(responseMap, STATUS).equals(String.valueOf(response[0].getStatusCode()));
                    }
                }
                return status;
            });
        } catch (ConditionTimeoutException e) {
            testErrorLog("ConditionTimeoutException : Expected status ( " + getStringValue(responseMap, STATUS) + " ) is not matching with actual status ( " + response[0].getStatusCode() + " )");
            throw new Exception();
        }
        return response[0];
    }

    public static Response post(String url, RequestSpecification request, HashMap<Object, Object> responseMap, HashMap<Object, Object> requestMap) throws Exception {
        testInfoLog(POST, "Request initialized");
        printURI(url, requestMap);
        if (requestMap.containsKey(APPLY_WAIT)) {
            return getStatus(request, url, responseMap, POST, Long.parseLong(getStringValue(requestMap, APPLY_WAIT)));
        } else {
            return request.post(url);
        }
    }

    public static Response put(String url, RequestSpecification request, HashMap<Object, Object> responseMap, HashMap<Object, Object> requestMap) throws Exception {
        testInfoLog(PUT, "Request initialized");
        printURI(url, requestMap);
        if (requestMap.containsKey(APPLY_WAIT)) {
            return getStatus(request, url, responseMap, PUT, Long.parseLong(getStringValue(requestMap, APPLY_WAIT)));
        } else {
            return request.put(url);
        }
    }

    public static Response delete(String url, RequestSpecification request, HashMap<Object, Object> responseMap, HashMap<Object, Object> requestMap) throws Exception {
        testInfoLog(DELETE, "Request initialized");
        printURI(url, requestMap);
        if (requestMap.containsKey(APPLY_WAIT)) {
            return getStatus(request, url, responseMap, DELETE, Long.parseLong(getStringValue(requestMap, APPLY_WAIT)));
        } else {
            return request.delete(url);
        }
    }

    public static Response patch(String url, RequestSpecification request, HashMap<Object, Object> responseMap, HashMap<Object, Object> requestMap) throws Exception {
        testInfoLog(PATCH, "Request initialized");
        printURI(url, requestMap);
        if (requestMap.containsKey(APPLY_WAIT)) {
            return getStatus(request, url, responseMap, PATCH, Long.parseLong(getStringValue(requestMap, APPLY_WAIT)));
        } else {
            return request.patch(url);
        }
    }

    public static String getStringValue(Map<Object, Object> map, String value) {
        return map.get(value).toString();
    }

    public static Object getObjectValue(HashMap<Object, Object> map, String value) {
        return map.get(value);
    }

    public static void verifyResponse(HashMap<Object, Object> map, String key, Response response, GeneralPojo pojo) {
        String value = getStringValue(map, key);
        ResponseBody responseBody = response.body();
        String[] values = getSplit(value, ",");
        String[] data;
        for (String s : values) {
            data = s.split("=");
            data[0] = data[0].trim();
            data[1] = data[1].trim();

            if (key.equalsIgnoreCase(BODY)) {
                if (data[1].equalsIgnoreCase(IS_PRESENT)) {
                    Assert.assertNotNull(responseBody.path(data[0]));
                    testConfirmationLog(data[0].toUpperCase() + " - Key in BODY is present");
                } else {
                    if (data[1].contains(IS_GET)) {
                        verifyTrue(responseBody.path(data[0]), getPathValues(data[1], pojo));
                    } else {
                        verifyTrue(responseBody.path(data[0]), data[1]);
                    }
                    testConfirmationLog(data[0].toUpperCase() + " - Key and its Value in BODY is verified");
                }
            } else if (key.equalsIgnoreCase(HEADERS)) {
                if (!(data[1].equalsIgnoreCase(IS_PRESENT))) {
                    if (data[1].contains(IS_GET)) {
                        verifyTrue(response.getHeader(data[0]), getPathValues(data[1], pojo));
                    } else {
                        verifyTrue(response.getHeader(data[0]), data[1]);
                    }
                    testConfirmationLog(data[0].toUpperCase() + " - Key and its Value in HEADER is verified");
                } else {
                    if(response.getHeaders().hasHeaderWithName(data[0])){
                        testConfirmationLog(data[0].toUpperCase() + " - Key in HEADER is present");
                    }
                }
            }
        }
    }

    public static String[] getSplit(Object value, String symbol) {
        String temp = value.toString();
        if (temp.endsWith("}") && !temp.endsWith("$GET}}")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        if (temp.startsWith("{") && !temp.startsWith("{{API")) {
            temp = temp.replaceFirst("\\{","");
        }
        return temp.trim().split(symbol);
    }

    public static HashMap<Object, Object> setQueryParam(HashMap<Object, Object> map, RequestSpecification request, GeneralPojo pojo) {
        String key, value;
        String[] query = getSplit(getStringValue(map, QUERY_PARAM), ",");
        StringBuilder queryValue = new StringBuilder("?");
        testDebugLog("Query params pushed");
        for (String tempQuery : query) {
            String[] temp = tempQuery.split("=");
            key = temp[0].trim();
            if (temp[1].contains(IS_GET)) {
                value = getPathValues(temp[1].trim(), pojo);
            } else {
                value = temp[1].trim();
            }
            queryParam(key, value, request);
            queryValue.append("&").append(key).append("=").append(value);
        }
        queryValue = new StringBuilder(queryValue.toString().replaceFirst("\\?&", "?"));
        map.put(QUERY_STRING, queryValue.toString());
        return map;
    }

    public static String setPathParam(HashMap<Object, Object> map, GeneralPojo pojo) {
        String path = getStringValue(map, URI);
        String pathString, pathObject;
        String[] pathParams = getSplit(getStringValue(map, PATH_PARAM), ",");
        testDebugLog("Path params updated");
        for (String temp : pathParams) {
            String[] pathParam = temp.split("=");
            pathString = "{" + pathParam[0].trim() + "}";
            pathObject = getPathValues(pathParam[1].trim(), pojo);
            path = path.replace(pathString, pathObject);
            testTraceLog(pathString + " : " + pathObject);
        }
        return path;
    }

    public static HashMap<Object, Object> replaceRequestKeyContent(HashMap<Object, Object> map, String object) {

        if (map.containsKey(object) && !Objects.equals(getStringValue(map, object), "")) {
            Map<Object, HashMap<Object, Object>> replaceMap = new HashMap<>();
            String[] tempArray = getStringValue(map, object).split(",");
            for (String temp : tempArray) {
                String[] val = getSplit(temp, "=");
                String key = val[0].trim();
                String value = val[1].trim();
                if (value.startsWith("@")) {
                    if (SecretsController.getSecretsObject().containsKey(value)) {
                        replaceMap.putAll(SecretsController.getSecretsObject());
                        if (replaceMap.get(value).containsKey(key)) {
                            map.put(object, getStringValue(map, object).replaceFirst(value, replaceMap.get(value).get(key).toString()));
                            if (object.equalsIgnoreCase(BODY)) {
                                StringBuilder sb = new StringBuilder(getStringValue(map, BODY_SECRETS));
                                sb.append(getStringValue(map, object).replaceFirst(value, replaceMap.get(value).get(key).toString())).append(",");
                                map.put(BODY_SECRETS, sb);
                            }
                        }
                    }
                    if (HeadersController.getHeaderObject().containsKey(value)) {
                        replaceMap.putAll(HeadersController.getHeaderObject());
                        if (replaceMap.get(value).containsKey(key)) {
                            map.put(object, getStringValue(map, object).replaceFirst(value, replaceMap.get(value).get(key).toString()));
                        }
                    }
                }
            }
        }
        return map;
    }

    public static HashMap<Object, Object> replaceVariable(HashMap<Object, Object> map) {
        map.putAll(replaceRequestKeyContent(map, HEADERS));
        map.put(BODY_SECRETS, "");
        map.putAll(replaceRequestKeyContent(map, BODY));
        map.putAll(replaceRequestKeyContent(map, PATH_PARAM));
        map.putAll(replaceRequestKeyContent(map, CONFIG));
        map.putAll(replaceRequestKeyContent(map, QUERY_PARAM));
        return map;
    }

    public static String getPathValues(String value, GeneralPojo pojo) {
        String tc, path, temp;
        int index = -1;

        String variable = value.substring(value.indexOf("{"),value.lastIndexOf("}") + 1);

        if (value.contains(IS_GET)) {
            String[] values = getSplit(variable.replace("{{","").replace("}}",""), ";");
            path = values[1];
            tc = values[0];
            if (path.contains("[")) {
                temp = path;
                path = path.substring(0, path.length()-3);
                index = Integer.parseInt(temp.substring(temp.length()-2, temp.length()-1));
            }
            if (index>=0) {
                temp = pojo.getTestcaseResponse(tc).body().path(path).toString();
                String[] array = temp.replace("[","").replace("]","").split(",");
                path = array[index];
            } else {
                path = pojo.getTestcaseResponse(tc).body().path(path).toString();
            }
            value = value.replace(variable, path);
        }
        return value.trim();
    }

    public static HashMap<String, Object> getConvertToMap(HashMap<Object, Object> map, String object, GeneralPojo pojo) {

        HashMap<String, Object> headersMap = new LinkedHashMap<>();
        String headers = getStringValue(map, object);
        String[] header = getSplit(headers, ",");
        if (object.equalsIgnoreCase(BODY) && getStringValue(map, BODY_SECRETS).length()>3) {
            testConfirmationLog("Key and its Value pushed in BODY");
        }
        for (String s : header) {
            String[] temp = s.split("=");
            String key = temp[0].trim();
            String value = temp[1].trim();
            if (value.contains(IS_GET)) {
                value = getPathValues(value, pojo);
            }

            headersMap.put(key,value);

            if (getStringValue(map, BODY_SECRETS).contains(key)) {
                testTraceLog(key.toUpperCase() + " : ********");
            }
        }
        return headersMap;
    }

    public static void verifyEquals(Object actual, Object expected) {
        Assert.assertEquals(actual, expected, expected + " : is not present in the response call");
    }

    public static void verifyTrue(Object actual, Object expected) {
        Assert.assertTrue(actual.toString().contains(expected.toString()), expected + " : is not present in the response call");
    }

    public static void getShortException(IResultMap tests) {

        for (ITestResult result : tests.getAllResults()) {

            Throwable exception = result.getThrowable();
            List<String> msgs = Reporter.getOutput(result);
            boolean hasReporterOutput = msgs.size() > 0;
            boolean hasThrowable = exception != null;
            if (hasThrowable) {
                boolean wantsMinimalOutput = result.getStatus() == ITestResult.SUCCESS;
                if (hasReporterOutput) {
                    testInfoLog((wantsMinimalOutput ? "Expected Exception" : "Failure Reason"), "");
                }

                String str = Utils.shortStackTrace(exception, true);
                System.out.println(str);
                Scanner scanner = new Scanner(str);
                String firstLine = scanner.nextLine();
                testValidationLog(firstLine);
            }
        }
    }

    public static void getVerifyResponseCommand(HashMap<Object, Object> map, Response response, GeneralPojo pojo, String testcaseName, int _logTest, CookieJar cookieJar) throws Exception {

        try {
            String[] values = getSplit(getStringValue(map, HEADERS), ",");
            for (String s : values) {
                if (s.contains("/")) {
                    values = getSplit(s, "=");
                    String contentType = values[1];
                    switch (contentType) {
                        case APPLICATION_JSON -> {
                            ResponseBody responseBody = response.body();

                            values = testcaseName.split("-");
                            testcaseName = values[0].trim();

                            pojo.setTestcaseResponse(testcaseName, response);

                            cookieJar.setCookies(response.getDetailedCookies(), _logTest);

                            String responseTime = String.valueOf(response.getTimeIn(TimeUnit.MILLISECONDS)*0.0001);
                            if (responseTime.length()>6) {
                                testInfoLog("Response Time in Seconds" , responseTime.substring(0,6));
                            } else {

                                testInfoLog("Response Time in Seconds" , responseTime);
                            }

                            ValidatableResponse v = response.then();
                            if (map.containsKey(RESPONSE_VALIDATE_TIME)) {
                                v.time(Matchers.lessThanOrEqualTo(Long.parseLong(getStringValue(map, RESPONSE_VALIDATE_TIME))*1000));
                            } else {
                                v.time(Matchers.lessThanOrEqualTo(Long.parseLong(defaultValidateTimeout)));
                            }

                            verifyEquals(response.getStatusCode(), getObjectValue(map, STATUS));

                            if (responseBody.path(MESSAGE) != null && map.containsKey(MESSAGE)) {
                                testDebugLog("Message : " + responseBody.path(MESSAGE));
                                if (!(getStringValue(map, MESSAGE).equalsIgnoreCase(IS_PRESENT))) {
                                    verifyTrue(responseBody.path(MESSAGE), getStringValue(map, MESSAGE));
                                }
                            }

                            if (map.containsKey(HEADERS)) {
                                verifyResponse(map, HEADERS, response, pojo);
                            }

                            if (map.containsKey(BODY)) {
                                verifyResponse(map, BODY, response, pojo);
                            }

                        }
                        default -> throw new Exception("Given Content Type is not supported yet - " + contentType);
                    }
                }
            }
        } catch (AssertionError | Exception e) {
            failure();
            testWarningLog("Response status - " + response.getStatusCode());
            testWarningLog("Response message - " + response.body().path(MESSAGE));
            throw new Exception(e);
        }
    }

    public static SortedSet<String> getStatusCodes() {
        SortedSet<String> codes = new TreeSet<>();

        Collections.addAll(codes, StatusCode.codes);

        return codes;
    }

    public static boolean verifyDomain(Object value) {
        String uri;
        try {
            uri = value.toString();
        } catch (Exception e) {
            testWarningLog("Base URI is not configured");
            return true;
        }

        if (uri.length()<1) {
            testWarningLog("Base URI is null");
            return true;
        }

        String[] temp = uri.split("/");
        uri = temp[2];
        if (!DomainValidator.getInstance().isValid(uri)) {
            testWarningLog("Domain is not present - " + uri);
            return true;
        }
        return false;
    }
}
