package apiTest.framework.init;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import apiTest.framework.config.Configuration;
import apiTest.framework.generics.Generics;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

public class RestInit extends Generics implements Configuration {

    public Response response = null;
    public RequestSpecification request = null;

    @BeforeTest(alwaysRun = true)
    public void initAPI() {
        RestAssured.baseURI = BASE_URL;
    }

    @BeforeSuite(alwaysRun = true)
    public void startReport(ITestContext testContext) {
        ExtentInit.initializeReport(testContext.getCurrentXmlTest().getSuite().getName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
        String testName;
        ITestContext ex = testResult.getTestContext();

        try {
            testName = testResult.getName();
            if (testResult.getStatus() == ITestResult.FAILURE) {

                extentLogger.log(Status.FAIL, MarkupHelper.createLabel(testName + " - Failed", ExtentColor.RED));
//                extentLogger.log(Status.FAIL, MarkupHelper.createLabel(testResult.getThrowable() + " - Failed", ExtentColor.RED));

//                System.out.println();
//                System.out.println("TEST FAILED - " + testName);
//                System.out.println();
//                System.out.println("ERROR MESSAGE: " + testResult.getThrowable());
//                System.out.println("\n");

                Reporter.setCurrentTestResult(testResult);
//                failure();

//                getShortException(ex.getFailedTests());

            } else if ((testResult.getStatus() == ITestResult.SUCCESS)) {
                extentLogger.log(Status.PASS, MarkupHelper.createLabel(testName + " PASSED", ExtentColor.GREEN));
                System.out.println("TEST PASSED - " + testName + "\n");
            } else if ((testResult.getStatus() == ITestResult.SKIP)) {
                extentLogger.log(Status.SKIP, MarkupHelper.createLabel(testName + " - Skipped", ExtentColor.ORANGE));
            }

        } catch (Exception throwable) {
//            throwable.printStackTrace();
//            System.err.println("Exception ::\n" + throwable);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void endReport(ITestContext testContext) {
        ExtentInit.flushReport();
        ZipUtil.pack(new File(REPORT_PATH), new File(FOLDER_PATH + File.separator + "report" + System.currentTimeMillis() + ".zip"));
        ZipUtil.pack(new File(REPORT_PATH), new File(PROJECT_DIR + File.separator + "report_" + testContext.getCurrentXmlTest().getSuite().getName() + ".zip"));
    }
}
