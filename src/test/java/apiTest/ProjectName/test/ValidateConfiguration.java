package apiTest.ProjectName.test;

import apiTest.framework.config.Configuration;
import apiTest.framework.init.RestInit;
import apiTest.ProjectName.controller.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.EnumUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ValidateConfiguration extends RestInit {

    public static void verifyConfiguration() throws Exception {
        testCaseLog("verifyConfiguration");
            boolean isFail = verifyDomain(Configuration.BASE_URL);

        try {
            if (verifyConfigFiles(Configuration.headerConfig, HEADERS)) {
                isFail = true;
            }

            if (verifyConfigFiles(Configuration.manifestConfig, MANIFESTS)) {
                isFail = true;
            }
            if (verifyConfigFiles(Configuration.scenariosConfig, SCENARIOS)) {
                isFail = true;
            }
            if (verifyConfigFiles(Configuration.secretsConfig, SECRETS)) {
                isFail = true;
            }
            if (verifyConfigFiles(Configuration.testcaseConfig, TESTCASES)) {
                isFail = true;
            }
            if (verifyConfigFiles(Configuration.uriConfig, URI)) {
                isFail = true;
            }
            if (!isFail) {
                if (validateTestCase()) {
                    isFail = true;
                }
                if (verifyTestcaseMandatoryFields()) {
                    isFail = true;
                }
                if (!isFail) {
                    if (validateTestcaseKeywords()) {
                        isFail = true;
                    }
                    if (validateScenarioKeywords()) {
                        isFail = true;
                    }
                }
            }
        } catch (Exception e) {
            testErrorLog(e);
            e.printStackTrace();
        }
        if (isFail) {
            testErrorLog("The configuration failed with above error/s");
            throw new Exception();
        } else {
            testValidationLog("Configuration check completed successfully");
        }
    }

    static boolean validateTestcaseKeywords() {
        AtomicBoolean isFail = new AtomicBoolean(false);
        final boolean[] check = {false};
            TestCaseController.getTestCaseMap().forEach((key, value) -> {
                if (!UriController.getUriObject().containsKey(value.get(REQUEST).get(URI))) {
                    testWarningLog("Given URI key is not present in URI Config file");
                    check[0] = true;
                }

                if (!EnumUtils.isValidEnum(Requests.class, value.get(REQUEST).get(METHOD).toString())) {
                    testWarningLog(key + " : Given Request method not valid - " + value.get(REQUEST).get(METHOD));
                    check[0] = true;
                }

                for (String s : Arrays.asList(HEADERS, BODY, QUERY_PARAM, PATH_PARAM)) {
                    check[0] = validateSubstituteKeyWords(value.get(REQUEST), s);
                }
                for (String s : Arrays.asList(HEADERS, BODY)) {
                    check[0] = validateSubstituteKeyWords(value.get(RESPONSE), s);
                }

                if (validateContentType(value.get(RESPONSE).get(HEADERS).toString(), key)) {
                    check[0] = true;
                }

                if (validateContentType(value.get(REQUEST).get(HEADERS).toString(), key)) {
                    check[0] = true;
                }

                value.get(REQUEST).keySet().forEach(x -> {
                    if (validateRequestKeywords(x.toString(), key.toString())) {
                        check[0] = true;
                    }
                });

                value.get(RESPONSE).keySet().forEach(x -> {
                    if (validateResponseKeywords(x.toString(), key.toString())) {
                        check[0] = true;
                    }
                });

                value.get(REQUEST).values().forEach(x -> {
                    if (validateVariable(x.toString(), key)) {
                        check[0] = true;
                    }
                });

                value.get(RESPONSE).values().forEach(x -> {
                    if (validateVariable(x.toString(), key)) {
                        check[0] = true;
                    }
                });

                if (value.get(REQUEST).containsKey(APPLY_WAIT)) {
                    if (Configuration.waitTimeout <= Long.parseLong(value.get(REQUEST).get(APPLY_WAIT).toString())) {
                        testWarningLog(key + " : Given wait - " + value.get(REQUEST).get(APPLY_WAIT) + " ; must be less than the Given Total Timeout - " + Configuration.waitTimeout);
                        check[0] = true;
                    }
                }

                if (validateStatusCodes(value.get(RESPONSE).get(STATUS).toString(), key)) {
                    check[0] = true;
                }

                if (check[0]) {
                    isFail.set(true);
                }
            });
        return isFail.get();
    }

    static boolean validateSubstituteKeyWords(HashMap<Object, Object> value, String object) {
        AtomicBoolean isFail = new AtomicBoolean(false);
        if (value.containsKey(object)) {
            String[] values = getSplit(value.get(object), ",");
            for (String temp : values) {
                if (temp.contains("@")) {
                    String[] verifyValue = temp.split("=");
                    if (SecretsController.getSecretsObject().containsKey(verifyValue[1].trim())) {
                        if (!SecretsController.getSecretsObject().get(verifyValue[1]).containsKey(verifyValue[0].trim())) {
                            if (HeadersController.getHeaderObject().containsKey(verifyValue[1].trim())) {
                                if (!HeadersController.getHeaderObject().get(verifyValue[1]).containsKey(verifyValue[0].trim())) {
                                    testWarningLog(verifyValue[0] + " - Given Key in Testcase is not Present in HeadersConfig");
                                    isFail.set(true);
                                }
                            } else {
                                testWarningLog(verifyValue[0] + " - Given Key in Testcase is not Present in SecretsConfig");
                                isFail.set(true);
                            }
                        }
                    } else if (HeadersController.getHeaderObject().containsKey(verifyValue[1].trim())) {
                        if (!HeadersController.getHeaderObject().get(verifyValue[1]).containsKey(verifyValue[0].trim())) {
                            testWarningLog(verifyValue[0] + " - Given Key in Testcase is not Present in HeadersConfig");
                            isFail.set(true);
                        }
                    } else {
                        testWarningLog(verifyValue[1] + " - Given Key in Testcase is not Present in SecretsConfig or HeadersConfig");
                        isFail.set(true);
                    }
                }
            }
        }
        return isFail.get();
    }

    static boolean validateScenarioKeywords() {
        AtomicBoolean isFail = new AtomicBoolean(false);
        AtomicReference<ArrayList<String>> al = new AtomicReference<>();

        ScenarioController.getScenarioObject().forEach((key, value) -> {
            al.set(new ArrayList<>());
            String[] testcases = value.toString().replace("[","").replace("]","").split(",");
            Arrays.stream(testcases).iterator().forEachRemaining(x -> {
                x = x.trim();
                al.get().add(x);
            });

            StringBuilder sb = new StringBuilder();
            al.get().forEach(x -> {
                sb.append(x).append(",");
                TestCaseController.getTestCaseMap().get(x).get(REQUEST).values().forEach( tcValue -> {
                    String[] tcValues = tcValue.toString().split(",");

                    for (String temp : tcValues) {
                        if (temp.contains(IS_GET)) {
                            String[] values = getSplit(temp, "=");
                            String checkValue = values[1];
                            try {
                                String keyword = checkValue.substring(checkValue.indexOf("{"), checkValue.lastIndexOf("}") + 1);

                                String[] k = keyword.split(";");

                                if (k[0].startsWith("{{")) {
                                    k[0] = k[0].replace("{{","").trim();
                                    if (!sb.toString().contains(k[0])) {
                                        testWarningLog(x + " - Given Testcase Keyword is not present previously in the scenario - " + k[0]);
                                        isFail.set(true);
                                    }
                                } else {
                                    testWarningLog(x + " - Given Keyword does not follow configuration rules - " + keyword);
                                    isFail.set(true);
                                }

                                if (!k[1].startsWith(DATA)) {
                                    testWarningLog(x + " - Given Keyword path does not follow configuration rules - " + k[1]);
                                    isFail.set(true);
                                }

                                if (!k[2].equals(IS_GET) && !k[2].endsWith("}}")) {
                                    testWarningLog(x + " - Given Keyword does not follow configuration rules - " + k[2]);
                                    isFail.set(true);
                                }

                            } catch (Exception e) {
                                testWarningLog(x + " - Given Keyword does not follow configuration rules - " + values[1]);
                                isFail.set(true);
                            }
                        }
                    }
                });
            });
        });

        return isFail.get();
    }

    static boolean validateRequestKeywords(String value, String testName) {
        if (!EnumUtils.isValidEnum(RequestKeyWords.class, value.toUpperCase())) {
            testWarningLog(testName + " : " + value + " - Given Key in Testcase REQUEST is not valid");
            return true;
        }
        return false;
    }

    static boolean validateResponseKeywords(String value, String testName) {
        if (!EnumUtils.isValidEnum(ResponseKeyWords.class, value.toUpperCase())) {
            testWarningLog(testName + " : " + value + " - Given Key in Testcase RESPONSE is not valid");
            return true;
        }
        return false;
    }

    static boolean validateVariable(String value, Object testName) {
        boolean isFail = false;
        if (value.contains("$")) {
            String[] commaSplit = getSplit(value, ",");
            for (String comma : commaSplit){
                String[] values = comma.split("=");
                for (String temp : values) {
                    if (temp.contains("$")) {
                        String[] temp2 = temp.split(";");
                        for (String path : temp2) {
                            if (path.startsWith("$")) {
                                if (!EnumUtils.isValidEnum(GlobalVariable.class, path.toUpperCase().replace("}}",""))) {
                                    testWarningLog(testName + " : " + path + " - Given global variable is not configured in the Framework");
                                    isFail = true;
                                }
                            }
                            if (path.contains("API")) {
                                if (!path.startsWith("API")) {
                                    values = getSplit(path, "API");
                                    path = "API" + values[1];
                                }
                                if (!TestCaseController.getTestCaseMap().containsKey(path)) {
                                    testWarningLog(testName + " : " + path + " - Given testcase name is not configured in the Framework");
                                    isFail = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return isFail;
    }

    static boolean verifyTestcaseMandatoryFields() {
        AtomicBoolean isFail = new AtomicBoolean(false);

        TestCaseController.getTestCaseMap().forEach((key, value) -> {
            StringBuilder mandatoryFields = new StringBuilder();
            final boolean[] check = {false};

            if (!value.get(REQUEST).containsKey(URI)) {
                mandatoryFields.append(",").append(URI.toUpperCase()).append(" in ").append(REQUEST.toUpperCase());
                check[0] = true;
            }
            if (!value.get(REQUEST).containsKey(METHOD)) {
                mandatoryFields.append(",").append(METHOD.toUpperCase()).append(" in ").append(REQUEST.toUpperCase());
                check[0] = true;
            }
            if (!value.get(REQUEST).containsKey(HEADERS)) {
                mandatoryFields.append(",").append(HEADERS.toUpperCase()).append(" in ").append(REQUEST.toUpperCase());
                check[0] = true;
            }
            if (!value.get(RESPONSE).containsKey(STATUS)) {
                mandatoryFields.append(",").append(STATUS.toUpperCase()).append(" in ").append(RESPONSE.toUpperCase());
                check[0] = true;
            }
            if (!value.get(RESPONSE).containsKey(HEADERS)) {
                mandatoryFields.append(",").append(HEADERS.toUpperCase()).append(" in ").append(RESPONSE.toUpperCase());
                check[0] = true;
            }
            if (check[0]) {
                mandatoryFields.replace(0,1,"");
                isFail.set(true);
                testWarningLog(key + " - Mandatory Fields : " + mandatoryFields);
            }
        });
        if (isFail.get()) {
            testWarningLog("Mandatory Fields for the Testcases are not Given. Please update above field/s and rerun");
        }
        return isFail.get();
    }

    static boolean validateStatusCodes(String value, Object testName) {
        if (!getStatusCodes().contains(value)) {
            testWarningLog(testName + " - Given status code is not valid : " + value);
            return true;
        }
        return false;
    }

    static boolean validateContentType(String value, Object testName) {
        String contentType;
        String[] temp = getSplit(value, ",");
        for (String s : temp) {
            if (s.contains(CONTENT_TYPE)) {
                String[] values = s.split("=");
                if (values[1].contains("@")) {
                    contentType = HeadersController.getHeaderObject().get(values[1]).get(CONTENT_TYPE).toString();
                    s = contentType.substring(0, contentType.indexOf("/"));
                } else if (values[1].contains("/")) {
                    s = values[1].substring(0, values[1].indexOf("/"));
                    contentType = values[1];
                } else {
                    contentType = values[1];
                    s = contentType;
                }
                switch (s) {
                    case APPLICATION -> {
                        if (EnumUtils.isValidEnum(ContentType_Application.class, contentType.substring(contentType.indexOf("/")+1))) {
                            testWarningLog(testName + " : Given Content-Type is not valid - " + contentType);
                            return true;
                        }
                    }
                    default -> {
                        testWarningLog(testName + " : Given Content-Type is not valid - " + contentType);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static boolean verifyConfigFiles(String configFilePath, String configFileName) {

        String fileExtension = FilenameUtils.getExtension(configFilePath);

        switch (fileExtension) {
            case JSON -> {
                return verifyFileEmpty(configFilePath, configFileName);
            }
            default -> {
                testWarningLog("The given " + configFileName.toUpperCase() + " Config file type is not Acceptable by the Framework");
                return true;
            }
        }
    }

    static boolean verifyFileEmpty(String configFilePath, String configFileName) {
        try {
            if (FileUtils.readFileToString(new File(configFilePath), Charset.defaultCharset()).isEmpty()) {
                testWarningLog(configFileName.toUpperCase() + " Config file does not contain any data");
                return true;
            }
        } catch (IOException e) {
            testWarningLog("File is not found in the given path - " + configFilePath);
            return true;
        }
        return false;
    }

    static boolean validateTestCase() {
        final boolean[] isFail = {false};
        Set<String> testCase_Set = new HashSet<>();

        ScenarioController.getScenarioObject().forEach((key, value) -> {
            String[] testcases = value.toString().replace("[","").replace("]","").split(",");
            Arrays.stream(testcases).iterator().forEachRemaining(x -> {
                x = x.trim();
                testCase_Set.add(x);
            });
        });

        testCase_Set.forEach(x -> {
            if (ManifestController.getManifestObject().containsKey(x)) {
                if (ManifestController.getManifestObject().get(x).containsKey(TEST_NAME)) {
                    if (ManifestController.getManifestObject().get(x).get(TEST_NAME).toString().isBlank()) {
                        testWarningLog(x + " - Given TestCase Name cannot be empty in Manifest Config");
                        isFail[0] = true;
                    }
                } else {
                    testWarningLog(x + " - Given TestCase Name is not defined in Manifest Config");
                    isFail[0] = true;
                }
            } else {
                testWarningLog(x + " - Given TestCase is not Added in Manifest Config");
                isFail[0] = true;
            }
            if (!TestCaseController.getTestCaseMap().containsKey(x)) {
                testWarningLog(x + " - Given Testcase does not contain data in TestCase Config");
                isFail[0] = true;
            }
        });
        return isFail[0];
    }
}
