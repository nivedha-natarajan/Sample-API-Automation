package apiTest.framework.utility;

import apiTest.framework.init.ExtentInit;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.apache.logging.log4j.LogManager;
import org.testng.Reporter;

public class TestLogger extends ExtentInit {

    /**
     * To log the Test Message in report
     *
     * @param log Log Message
     */
    public static void log(String log) {
        System.out.println(log.replaceAll("<[^>]*>", ""));
        Reporter.log("<br></br>" + log);
    }

    /**
     * To log the Test Case Name in report
     *
     * @param log Log Message
     */
    public static void testCaseLog(String log) {
        extentLogger = extentReports.createTest(log).assignAuthor("Automation User");
        log("<strong>" + log + "</strong>");
        String[] temp = log.split(":");
        _logger = LogManager.getLogger(temp[0].trim());
        _logger.info(log);
    }

    /**
     * To log the information passed during the test execution in report
     *
     * @param key   Key/Log Message
     * @param value Value/Entered Details
     */
    public static void testInfoLog(Object key, Object value) {
        extentLogger.info(key + " : " + value);
        _logger.info(key + " : " + value);
        log("<strong>" + key + " : </strong><font color=#9400D3>" + value + "</font>");
    }

    /**
     * To log the test trace steps in the report.
     *
     * @param log Step Information
     */
    public static void testTraceLog(Object log) {
        extentLogger.info(log.toString());
        _logger.trace(log);
        log(log.toString());
    }

    /**
     * To log the test Debug steps
     *
     * @param log Debug Message
     */
    public static void testDebugLog(Object log) {
        extentLogger.info(MarkupHelper.createLabel(log.toString(), ExtentColor.TEAL));
        _logger.debug(log);
        log("<font color=#000080>" + log + "</font>");
    }

    /**
     * To log the Validation Message comes during the test
     *
     * @param log Validation Message
     */
    public static void testValidationLog(Object log) {
        extentLogger.info(MarkupHelper.createLabel(log.toString(), ExtentColor.PINK));
        _logger.debug(log);
        log("Validation -- <Strong><font color=#ff0000>" + log + "</strong></font>");
    }

    /**
     * To log the Confirmation Message comes during the test
     *
     * @param log Confirmation Message
     */
    public static void testConfirmationLog(Object log) {
        _logger.debug(log);
        extentLogger.info(MarkupHelper.createLabel(log.toString(), ExtentColor.TEAL));
        log("Confirmation -- <Strong><font color=#008000>" + log + "</strong></font>");
    }

    /**
     * To log the Warning Message comes during the test
     *
     * @param log Warning Message
     */
    public static void testWarningLog(Object log) {
        extentLogger.warning(MarkupHelper.createLabel(log.toString(), ExtentColor.AMBER));
        _logger.warn(log);
        log("Warning -- <Strong><font color=#FF1870>" + log + "</strong></font>");
    }

    public static void testErrorLog(Object log) {
        log = log.toString().replace("java.lang.Exception: ", "");
        log = log.toString().replace("java.lang.AssertionError: ", "");

        extentLogger.fail(MarkupHelper.createLabel("Failed Reason -- " + log, ExtentColor.RED));
        _logger.error("Failed Reason -- " + log);
        log("Error -- <Strong><font color=#FF0000>" + "Failed Reason -- " + log + "</strong></font>");
    }

    /**
     * To log the test verification step is passed successfully
     */
    public static void success() {
        extentLogger.pass(MarkupHelper.createLabel("PASS", ExtentColor.GREEN));
        _logger.info("Test Passed");
        System.out.println("<Strong><font color=#008000>Pass</font></strong>".replaceAll("<[^>]*>", ""));
        Reporter.log("<br></br><Strong><font color=#008000>Pass</font></strong>");
    }

    /**
     * To log the test verification step is skipped
     */

    public static void skipped(String testcaseName) {
        _logger.warn("Test Skipped -- " + testcaseName);
        log("Skipped -- <Strong><font color=#FF1870>" + testcaseName + "</strong></font>");
        extentLogger.skip(MarkupHelper.createLabel(testcaseName, ExtentColor.ORANGE));
        Reporter.log("<br></br><Strong><font color=#FFA500>Skip</font></strong>");
    }

    /**
     * To log test verification is failed
     */
    public static void failure() {
        extentLogger.fail(MarkupHelper.createLabel("FAIL", ExtentColor.RED));
        _logger.error("Test Failed");
        System.out.println("<Strong><font color=#FF0000>Fail</font></strong>".replaceAll("<[^>]*>", ""));
        Reporter.log("<br></br><Strong><font color=#FF0000>Fail</font></strong>");
    }
}
