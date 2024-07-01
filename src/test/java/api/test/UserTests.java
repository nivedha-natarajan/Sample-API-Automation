package api.test;

import api.endpoints.UserEndPoints;
import api.payload.User;
import api.utilities.ExtentReportManager;
import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

public class UserTests extends ExtentReportManager {
    User user;
    Faker faker;
    Response response;
    private Logger logger;

    @BeforeClass
    public void setupDate()
    {
        user = new User();
        faker = new Faker();

        user.setId(faker.idNumber().hashCode());
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setUsername(faker.name().username());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password());
        user.setPhone(faker.phoneNumber().cellPhone());

        logger = LogManager.getLogger(this.getClass());
    }

    @Test(priority = 1)
    public void createUser()
    {
        logger.info("Create user");
        response = UserEndPoints.createUser(user);
        response.then().log().all();

        test.info(response.prettyPrint());

        assertEquals(response.statusCode(), 200);
    }

    @Test(priority = 2)
    public void getUserDetails()
    {
        logger.info("Get user details");
        response = UserEndPoints.getUser(user.getUsername());
        response.then()
                .log().all()
                .statusCode(200);

        test.info(response.prettyPrint());

        assertEquals(response.jsonPath().getString("username"), user.getUsername());
    }

    @Test(priority = 3)
    public void getUpdateDetails()
    {
        logger.info("Update user");
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setUsername(faker.name().username());

        response = UserEndPoints.updateUser(user.getUsername(), user);
        response.then()
                .log().all()
                .statusCode(200);

        test.info(response.prettyPrint());

        UserEndPoints.getUser(user.getUsername()).then().body("username", equalTo(user.getUsername()));
    }

    @Test(priority = 4)
    public void getDeleteUserDetails()
    {
        logger.info("Delete user");
        response = UserEndPoints.deleteUser(user.getUsername());
        response.then()
                .log().all()
                .statusCode(200);

        test.info(response.prettyPrint());
    }
}
