package genericUtility;

import java.io.IOException;
import java.sql.SQLException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import objRepository.HomePage_Mod;
import objRepository.LoginPage_Mod;

@Listeners(genericUtility.ListenersImplements.class)
public class BaseClass_Utilities {
	public DBUtility dLib= new DBUtility();
	public JProperties jLib=  new JProperties();
	public DataFromExcel exLib= new  DataFromExcel();
	public RandomNumGenerator ranLib=new RandomNumGenerator();
	public WebDriverMethodSource wLib= new WebDriverMethodSource();
	public WebDriver driver=null;
	public static ThreadLocal<WebDriver> wdriver= new ThreadLocal<WebDriver>();
	
	@BeforeSuite(alwaysRun = true)
	public void connectToDb() throws SQLException
	{
		dLib.createDriverandRegisterDB();
		System.out.println("--DB connected--");
	}
	
	//@Parameters("BROWSER")
	@BeforeClass(alwaysRun = true)
	public void launchbrowser(/*String BROWSER*/) throws IOException
	{
		String BROWSER=jLib.readDataFromProperties("browser");
		String URL= jLib.readDataFromProperties("url");
		
		if(BROWSER.equalsIgnoreCase("chrome"))
			driver= new ChromeDriver();
		else if(BROWSER.equalsIgnoreCase("firefox"))
			driver= new FirefoxDriver();
		else if(BROWSER.equalsIgnoreCase("edge"))
			driver= new EdgeDriver();
		
		wdriver.set(driver);
		
		driver.get(URL);
		wLib.maxiwindow(driver);
		wLib.waitforallele(driver, 5);
		System.out.println("--Browser Launched--");
	}
	
	@BeforeMethod(alwaysRun = true)
	public void logintoapp() throws IOException
	{
		String USERNAME=jLib.readDataFromProperties("username");
		String PASSWORD=jLib.readDataFromProperties("password");
		LoginPage_Mod lp= new LoginPage_Mod(driver);
		lp.businessLog(USERNAME, PASSWORD,driver);
		System.out.println("--Login successful--");
	}
	
	@AfterMethod(alwaysRun = true)
	public void logouttoapp()
	{
		HomePage_Mod hp= new HomePage_Mod(driver);
		hp.clickOnSignout(driver);
		System.out.println("LoggedOut successfully");
	}
	
	@AfterClass(alwaysRun = true)
	public void closethebrowser()
	{
		
		driver.quit();
		System.out.println("Browser closed successfully");
	}
	
	@AfterSuite(alwaysRun = true)
	public void disconnectDB() throws SQLException
	{
	//	dLib.disconnectDB();
		System.out.println("--DB Disconnected--");
	}
}
