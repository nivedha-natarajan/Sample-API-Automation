package api.endpoints;

import api.payload.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.ResourceBundle;

import static io.restassured.RestAssured.given;

public class UserEndPoints {

    static ResourceBundle getUrl()
    {
        return ResourceBundle.getBundle("endpoints");
    }

    public static Response createUser(User payload)
    {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
        .when()
                .post(getUrl().getString("post_url"));
    }

    public static Response getUser(String username)
    {
        return given()
                .pathParam("username", username)
                .when()
                .get(getUrl().getString("get_url"));
    }

    public static Response updateUser(String username, User payload)
    {
        return given()
                .pathParam("username", username)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .put(getUrl().getString("update_url"));
    }

    public static Response deleteUser(String username)
    {
        return given()
                .pathParam("username", username)
                .when()
                .delete(getUrl().getString("delete_url"));
    }
}
