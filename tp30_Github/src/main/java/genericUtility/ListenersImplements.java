package genericUtility;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ListenersImplements implements ITestListener,ISuiteListener {
	ExtentSparkReporter spark;//It will Create UI the Extent Report html page
	ExtentReports report;// It will pass the common data to extent report like key value pairs
	ExtentTest test;//It will pass the every test report separately 
	public static ThreadLocal<ExtentTest> extents=new ThreadLocal<ExtentTest>();
	
	@Override
	public void onStart(ISuite suite) {
		spark= new ExtentSparkReporter("./ExtentReport/report"+new DateMethodSource().sysDate()+".html");
		spark.config().setDocumentTitle("SampleMethods");
		spark.config().setTheme(Theme.DARK);
		spark.config().setReportName("Vtiger");
		
		report = new ExtentReports();
		report.attachReporter(spark);
		report.setSystemInfo("Base_Browser", "Chrome");
		report.setSystemInfo("Base_Platform", "Windows");
		report.setSystemInfo("Base_Url", "http://localhost:8888/");
		report.setSystemInfo("Reporter Name", "Ravi"); 
	}
	
	@Override
	public void onStart(ITestContext context) {
		//test.log(Status.INFO, "Test Started successfully");
	}
	
	@Override
	public void onTestStart(ITestResult result) {
		String name=result.getMethod().getMethodName();	
		test=report.createTest(name);
		extents.set(test);
		test.log(Status.INFO, name+"----->Execution Starts");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		String methodname=result.getMethod().getMethodName();
		test.log(Status.PASS,methodname+"---->Passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		new IretryAnalyzer().retry(result);
		String mname=result.getMethod().getMethodName();
		TakesScreenshot ts=(TakesScreenshot)BaseClass_Utilities.wdriver.get();//D
		File src= ts.getScreenshotAs(OutputType.FILE);
		File loc= new File("./screenshots/"+mname+new DateMethodSource().sysDate()+".png");
		try {
			FileHandler.copy(src, loc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String path=loc.getAbsolutePath();
		test.addScreenCaptureFromPath(path);
		test.log(Status.FAIL, result.getThrowable());//D
		test.log(Status.FAIL, mname+"----> Failed");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		String mname=result.getMethod().getMethodName();
		test.log(Status.SKIP, mname+"--->Skipped");
	}

	
	@Override
	public void onFinish(ITestContext context) {
		test.log(Status.INFO, "Test Completed");
	}
	
	@Override
	public void onFinish(ISuite suite) {
		report.flush();
	}


}
