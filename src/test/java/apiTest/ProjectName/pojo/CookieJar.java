package apiTest.ProjectName.pojo;

import apiTest.ProjectName.contants.Headers;
import io.restassured.http.Cookies;

import java.util.HashMap;
import java.util.Map;

public class CookieJar implements Headers {

    private Map<String, Cookies> cookies;

    public void createCookie() {
        cookies = new HashMap<>();
    }

    public Cookies getCookies(String domain) {
        String[] temp = domain.split("/");
        return cookies.get(temp[2]);
    }

    public void setCookies(Cookies detailedCookies, int testCount) {
        if (testCount == 1) {
            createCookie();
            cookies.put(detailedCookies.asList().get(0).getDomain(), detailedCookies);
        } else {
            if (cookies.containsKey(detailedCookies.asList().get(0).getDomain())) {
                StringBuilder sb;
                sb = new StringBuilder(detailedCookies.toString());
                sb.append("; ").append(detailedCookies);
            } else {
                cookies.put(detailedCookies.asList().get(0).getDomain(), detailedCookies);
            }
        }
    }
}
