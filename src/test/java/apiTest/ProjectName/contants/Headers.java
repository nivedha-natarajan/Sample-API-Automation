package apiTest.ProjectName.contants;

public interface Headers {

    enum GlobalVariable {
        $GET, $PRESENT
    }

    enum RequestKeyWords {
        URI, METHOD, HEADERS, BODY, COOKIES, QUERY_PARAM, PATH_PARAM, WAIT
    }

    enum ResponseKeyWords {
        STATUS, MESSAGE, VALIDATE_TIME, BODY, COOKIES, HEADERS
    }

    enum Requests {
        GET, POST, PUT, DELETE, PATCH, CONNECT, HEAD, OPTIONS, TRACE
    }

    enum ContentType_Application {
        JSON
    }

    String TEST_NAME = "test-name";
    String CONTENT_TYPE = "Content-Type";
    String STATUS = "status";
    String MESSAGE = "message";
    String URI = "uri";
    String COOKIES = "cookies";
    String TOKEN = "token";
    String BODY = "body";
    String BODY_SECRETS = "body-secrets";
    String METHOD = "method";
    String HEADERS = "headers";
    String MANIFESTS = "manifests";
    String SCENARIOS = "scenarios";
    String SECRETS = "secrets";
    String TESTCASES = "testcase";
    String REQUEST = "request";
    String RESPONSE = "response";
    String DOMAIN = "Domain";
    String GET = "GET";
    String POST = "POST";
    String PUT = "PUT";
    String DELETE = "DELETE";
    String PATCH = "PATCH";
    String CONNECT = "connect";
    String HEAD = "head";
    String OPTIONS = "options";
    String TRACE = "trace";
    String IS_PRESENT = "$PRESENT";
    String IS_GET = "$GET";
    String DATA = "data";
    String QUERY_PARAM = "query_param";
    String QUERY_STRING = "queryValue";
    String APPLY_WAIT = "wait";
    String RESPONSE_VALIDATE_TIME = "validate_time";
    String CONFIG = "config";
    String PATH_PARAM = "path_param";
    String JSON = "json";
    String APPLICATION = "application";
    String APPLICATION_JSON = "application/json";
}
