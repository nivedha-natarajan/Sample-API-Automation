package apiTest.framework.utility;

import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EmailReporter extends CustomReporterListener {

    private static final Logger log = Logger.getLogger(CustomReporterListener.class);

    private PrintWriter writer;
    private static long totalExecutionTime = 0L;

    private static final HashMap<String, String> failedReason = new HashMap<>();
    private static final HashMap<List<String>, HashMap<Integer, List<String>>> suiteResults = new HashMap<>();

    public EmailReporter() {
        super();
        failedReason.put("java.lang.AssertionError", "It seems like some assertion failed, please check the expected and actual values for assertion");
        failedReason.put("org.openqa.selenium.SessionNotCreatedException", "It looks like your server isn't yet started, " +
                "please check if you're running Selenium with hub and Node, " +
                "or if you're running on cloud please check if you're connected with internet, " +
                "or if you're running mobile automation please check if Appium server is started.");
        failedReason.put("depends on not successfully finished methods", "It seems the dependent method for this test isn't passed and so skipped this test from execution.");
        failedReason.put("org.openqa.selenium.NoSuchElementException", "The element you searched for isn't display on the DOM, please check the exception for the element which isn't display.");
        failedReason.put("org.openqa.selenium.TimeoutException", "The element you searched for isn't visible on the DOM after gen time wat, please check the exception for the element which isn't display.");
        failedReason.put("java.lang.StringIndexOutOfBoundsException", "It looks like failure on the code, IndexOutOfBound Error, please contact and check with Dev/QA for this error.");
        failedReason.put("org.openqa.selenium.ElementClickInterceptedException", "It looks like there some element comes in between the element was intended to be clicked, possible cause may be a loading overlay.");
    }

    protected PrintWriter startWriter(String outDir) throws IOException {
        new File(outDir).mkdirs();
        return new PrintWriter(new BufferedWriter(new FileWriter(new File(outDir,
                "EmailReport" + ".html"))));
    }

    @Override
    public void generateReport(List<XmlSuite> xml, List<ISuite> suites, String outDir) {
        try {
            writer = startWriter(outDir);
        } catch (IOException e) {
            log.error("Error while designing email report", e);
        }

        initiateReport(writer);
        generateSuiteSummary(suites);
        buildReport(writer);
        flushReport(writer);

        writer.flush();
        writer.close();
    }

    private void buildReport(PrintWriter writer) {
        writer.println("<table width=\"100%\" border=\"5\" cellspacing=\"0\" cellpadding=\"0\" class=\"methodOverview\" id=\"summary\">");
        writer.println("<tbody><tr><td bgcolor=\"white\" colspan=\"5\"><table border=\"0\" width=\"100%\" bgcolor=\"#e6f7ff\"><tbody><tr><td width=\"25%\" bgcolor=\"white\"><img src=\"https://lh6.googleusercontent.com/-o0y0b0qlkRo/AAAAAAAAAAI/AAAAAAAAAAA/_64It_hLXmc/s88-p-k-no-ns-nd/photo.jpg\" width=\"60px\" height=\"60px\"></td><td><center><font color=\"#008bcc\"><b><h1>Test Cases Analysis</h1></b></font></center></td><td width=\"25%\" bgcolor=\"white\"><img src=\"logo.svg\" width=\"160px\" height=\"50px\" align=\"right\"></td></tr></tbody></table></td></tr>");
        writer.println("<tr><td colspan=\"5\">Total Time taken for execution : <b>" + DateTimeFormatter.ofPattern("mm:ss").format(LocalTime.ofSecondOfDay(totalExecutionTime / 1000)) + "</b><br> Date and Time of the execution: <b>" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss")) + "</b><br>Browser : <b>" + "NA" + "NA" + "</b><br>OS: <b>" + "NA" + "</b></td></tr>");
        writer.println("<tr bgcolor=\"SkyBlue\"><th width='15%'>Test Cases</th><th width='5%'>Status</th><th width='35%'>Exception</th><th width='35%'>Failure Description</th><th width='10%'>Total Time</th></tr>");
        for (Map.Entry<List<String>, HashMap<Integer, List<String>>> entry : EmailReporter.suiteResults.entrySet()) {
            if (entry.getKey() != null) {
                List<String> suite = entry.getKey();
                HashMap<Integer, List<String>> tests = entry.getValue();
                for (Map.Entry<Integer, List<String>> entry2 : tests.entrySet()) {
                    List<String> methods = entry2.getValue();
                    writer.println("<tr class='" + methods.get(2).toLowerCase() + "'><td><strong>" + suite.get(0) + " - " + methods.get(1) +
                            "</strong></td><td><strong><center>" + methods.get(2) + "</strong></center></td><td>" + methods.get(3) +
                            "</td><td>" + getDescriptiveError(methods.get(3)) +
                            "</td><td><center>" +
                            DateTimeFormatter.ofPattern("mm:ss").format(LocalTime.ofSecondOfDay(Long.parseLong(methods.get(4)))) +
                            "</center></td></tr>");
                }
            }
        }
    }

    private String getDescriptiveError(String error) {
        String mainReason = "No details found";
        for (Map.Entry<String, String> entry : failedReason.entrySet())
            if (error.contains(entry.getKey())) mainReason = entry.getValue();
            else if (error.isEmpty()) mainReason = "";
        return mainReason;
    }

    private void initiateReport(PrintWriter writer) {
        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        writer.println("<head>");
        writer.println("<title> Automation Build Summary - TestNG Report</title>");
        writer.println("<style type=\"text/css\">");
        writer.println("table {margin-bottom:1px;border-collapse:collapse;empty-cells:show}");
        writer.println("td,th {solid #009;padding:.25em .5em;}");
        writer.println("td,th {solid #009;padding:.25em .5em;}");
        writer.println(".result th {vertical-align:bottom}");
        writer.println(".param th {padding-left:1em;padding-right:1em}");
        writer.println(".param td {padding-left:.5em;padding-right:2em}");
        writer.println(".stripe td,.stripe th {background-color: #E6EBF9}");
        writer.println(".numi,.numi_attn {text-align:right}");
        writer.println(".total td {font-weight:bold}");
        writer.println(".pass {background-color: #98fb98}");
        writer.println(".skip {background-color: #deb887}");
        writer.println(".fail {background-color: #ffb6c1}");
        writer.println(".stacktrace {white-space:pre;font-family:monospace}");
        writer.println(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
        writer.println("</style>");
        writer.println("</head>");
        writer.println("<body>");
    }

    private void generateSuiteSummary(List<ISuite> suites) {
        for (ISuite suite : suites) {
            System.out.println("_________ SUITE STARTED _________");
            List<String> suiteDetails = null;
            HashMap<Integer, List<String>> testResults = new HashMap<>();
            Map<String, ISuiteResult> iTestResult = suite.getResults();
            int testCount = 1;
            for (ISuiteResult iSuiteResult : iTestResult.values()) {
                String testResult;
                System.out.println("_________ TEST STARTED _________");
                ITestContext overview = iSuiteResult.getTestContext();
                totalExecutionTime += overview.getEndDate().getTime() - overview.getStartDate().getTime();
                suiteDetails = new LinkedList<String>() {
                    {
                        add(suite.getName());
                        add(String.valueOf(overview.getEndDate().getTime() - overview.getStartDate().getTime()));
                        add(String.valueOf(overview.getAllTestMethods().length));
                        add(String.valueOf(overview.getPassedTests().size()));
                        add(String.valueOf(overview.getFailedTests().size()));
                        add(String.valueOf(overview.getSkippedTests().size()));
                    }
                };
                for (int i = 0; i < overview.getAllTestMethods().length; i++) {
                    long testTime = 0;
                    String failureReason = "";
                    LinkedList<String> finalResult = new LinkedList<>();
                    String methodName = overview.getAllTestMethods()[i].getMethodName();
                    finalResult.add(overview.getName());
                    finalResult.add(methodName);
                    Set<ITestResult> passedSet = overview.getPassedTests().getResults(overview.getAllTestMethods()[i]);
                    Set<ITestResult> failedSet = overview.getFailedTests().getResults(overview.getAllTestMethods()[i]);
                    Set<ITestResult> skippedTest = overview.getSkippedTests().getResults(overview.getAllTestMethods()[i]);
                    if (passedSet.size() > 0) {
                        testResult = "Pass";
                        for (ITestResult r : passedSet) {
                            testTime = r.getEndMillis() - r.getStartMillis();
                        }
                    } else if (failedSet.size() > 0) {
                        for (ITestResult r : failedSet) {
                            String temp = Utils.shortStackTrace(r.getThrowable(), true);
                            Scanner scanner = new Scanner(temp);
                            failureReason = scanner.nextLine();
                            testTime = r.getEndMillis() - r.getStartMillis();
                        }

                        testResult = "Fail";
                    } else {
                        for (ITestResult r : skippedTest) {
                            String temp = Utils.shortStackTrace(r.getThrowable(), true);
                            Scanner scanner = new Scanner(temp);
                            failureReason = scanner.nextLine();
                            testTime = r.getEndMillis() - r.getStartMillis();
                        }
                        testResult = "Skip";
                    }
                    finalResult.add(testResult);
                    finalResult.add(failureReason);
                    finalResult.add(String.valueOf((testTime / 1000)));
                    testResults.put(testCount++, finalResult);
                }
                System.out.println(testResults);
                System.out.println("_________ TEST ENDED _________");
            }
            suiteResults.put(suiteDetails, testResults);
            System.out.println(suiteResults);
            System.out.println("_________ SUITE ENDED _________");
        }

    }

    private void flushReport(PrintWriter writer) {
        writer.println("<tr bgcolor='SkyBlue'><td align='right' colspan='5'><center><b><i> Report customized by RahulR </i><b><center></center></b></b></center></td></tr>");
        writer.println("</tbody></table>");
        writer.println("</body></html>");
    }


}
