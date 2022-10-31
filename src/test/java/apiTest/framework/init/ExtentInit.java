package apiTest.framework.init;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import apiTest.framework.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExtentInit implements Configuration {

    protected static ExtentTest extentLogger;
    protected static Logger _logger;
    protected static ExtentReports extentReports;
    public static String REPORT_PATH;
    public static String FOLDER_PATH;

    protected long scenarioStartTime;
    protected long testcaseStartTime;
    protected  NumberFormat formatter = new DecimalFormat("#0.00000");


    static void initializeReport(String suiteName) {

        File directory = new File(PROJECT_DIR + File.separator + "ExtentReports");
        if (!directory.exists()) directory.mkdir();

        String dateDir = PROJECT_DIR + File.separator + "ExtentReports" + File.separator +
                LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMMyyyy"));
        File dateDirectory = new File(dateDir);
        if (!dateDirectory.exists()) dateDirectory.mkdir();

        String timeDir = PROJECT_DIR + File.separator + "ExtentReports" + File.separator +
                LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMMyyyy")) +
                File.separator + LocalTime.now().format(DateTimeFormatter.ofPattern("hhmmss"));
        File timeDirectory = new File(timeDir);
        if (!timeDirectory.exists()) timeDirectory.mkdir();

        REPORT_PATH = timeDir;
        FOLDER_PATH = dateDir;

        ExtentSparkReporter extentSparkReporter;
        extentSparkReporter = new ExtentSparkReporter(timeDir + File.separator + "Report_" + suiteName + ".html");

        extentReports = new ExtentReports();
        extentReports.attachReporter(extentSparkReporter);

        extentReports.setSystemInfo("OS : ", System.getProperty("os.name"));
        extentReports.setSystemInfo("OS Architecture : ", System.getProperty("os.arch"));
        extentReports.setSystemInfo("Java Version : ", System.getProperty("java.version"));
        extentReports.setSystemInfo("User Name : ", System.getProperty("user.name"));
        extentReports.setSystemInfo("Machine Name : ", System.getProperty("machine.name"));
        extentReports.setSystemInfo("IP Address : ", System.getProperty("machine.address"));

        extentReports.setAnalysisStrategy(AnalysisStrategy.TEST);

        extentSparkReporter.config().setEncoding("utf-8");
        extentSparkReporter.config().setDocumentTitle("API Automation Test Report");
        extentSparkReporter.config().setTheme(Theme.DARK);
        extentSparkReporter.config().setReportName("API Test Report");
        extentSparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");

        String js =
                "let timeline = document.getElementsByClassName('panel-lead')[4];" +
                        "const time = parseInt(timeline.innerText.replace(',',''));" +
                        "const hours = Math.floor((time / (1000 * 60 * 60)) % 24);" +
                        "const minutes = Math.floor((time / (1000 * 60)) % 60);" +
                        "const seconds = Math.floor((time / 1000) % 60);" +
                        "const milliseconds  = parseInt((time % 1000) / 100);" +
                        "timeline.innerText = hours + \"h \" + minutes + \"m \" + seconds + \"s \" + milliseconds + \"ms\";" +
                        "let ver = document.querySelectorAll('.darken-3')[2];" +
                        "ver.style.display='none';" +
                        "let title = document.querySelector('.report-name');" +
                        "title.className='';" +
                        "title.innerHTML='<img src=\"/logo.svg\" width=100px/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Automation Report</sub>'" +
                        "let logo = document.getElementsByClassName('brand-logo')[0];" +
                        "logo.style.display='none';";

        extentSparkReporter.config().setJs(js);

    }

    static void flushReport() {
        extentReports.flush();
    }

}
