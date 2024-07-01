package api.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentReportManager implements ITestListener {

    public static ExtentSparkReporter extentSparkReporter;
    public static ExtentReports extent;
    public static ExtentTest test;

    public void onStart(ITestContext context) {
        extentSparkReporter = new ExtentSparkReporter(".\\reports\\extentReport.html");

        extentSparkReporter.config().setDocumentTitle("API Automation");
        extentSparkReporter.config().setReportName("Pet Store API");
        extentSparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(extentSparkReporter);
        extent.setSystemInfo("application", "Pet Store API");
        extent.setSystemInfo("OS Type", System.getProperty("os.name"));
        extent.setSystemInfo("user", "Nivedha Natarajan");
    }

    public void onTestStart(ITestResult result) {
        test = extent.createTest(result.getName());
    }

    public void onTestSuccess(ITestResult result) {
        test.assignCategory(result.getMethod().getGroups());
        test.createNode(result.getName());
        test.log(Status.PASS, "Test Passed");
    }

    public void onTestFailure(ITestResult result) {
        test.assignCategory(result.getMethod().getGroups());
        test.createNode(result.getName());
        test.log(Status.FAIL, "Test Failed");
        test.log(Status.FAIL, result.getThrowable().getMessage());
    }

    public void onTestSkipped(ITestResult result) {
        test.assignCategory(result.getMethod().getGroups());
        test.createNode(result.getName());
        test.log(Status.SKIP, "Test Skipped");
        test.log(Status.SKIP, result.getThrowable().getMessage());
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        test.log(Status.INFO, "Test case failed but within success percentage: " + result.getName());
    }

    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
