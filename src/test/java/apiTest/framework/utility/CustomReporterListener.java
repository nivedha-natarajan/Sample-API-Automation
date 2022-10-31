package apiTest.framework.utility;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import java.util.List;

public class CustomReporterListener implements IReporter {
    public void generateReport(List<XmlSuite> xml, List<ISuite> suites, String outdir) {
        System.out.println("_________ RUNNING CUSTOM REPORTER _____________");
    }
}
