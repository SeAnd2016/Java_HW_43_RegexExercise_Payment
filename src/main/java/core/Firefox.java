package core;

import java.math.*;
import java.text.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import java.util.regex.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class Firefox {
	
static WebDriver driver;
	
	public static void main(String[] args) throws InterruptedException {
		Logger.getLogger("").setLevel(Level.OFF);
		
        String driverPath = "";
		
		// We are checking, which system we are using for test execution
		if (System.getProperty("os.name").toUpperCase().contains("MAC"))
			driverPath = "./resources/webdrivers/mac/geckodriver.sh";

        else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) // If we have WebDrivers for PC
            driverPath = "./resources/webdrivers/pc/geckodriver.exe";

        else 
	        throw new IllegalArgumentException("Unknown OS. Script should be executed on Mac");
		
		System.setProperty("webdriver.gecko.driver", driverPath);
		
		// Disable notifications
		FirefoxOptions options = new FirefoxOptions();
		options.addPreference("dom.webnotifications.enabled", false);
		
		System.out.println("Browser is: \tFirefox\n");
		
		String url = "";
		for (int i = 1; i < 6; i++) {
		
		if (i == 1) {url = "http://alex.academy/exe/payment/index.html";}
		else if (i > 1 && i < 5) {url = "http://alex.academy/exe/payment/index" + i + ".html";}
		else url = "http://alex.academy/exe/payment/indexE.html";
		
		//String url = "http://alex.academy/exe/payment/index.html";
		//String url = "http://alex.academy/exe/payment/index2.html";
		//String url = "http://alex.academy/exe/payment/index3.html";
		//String url = "http://alex.academy/exe/payment/index4.html";
		//String url = "http://alex.academy/exe/payment/indexE.html";
		
		// Firefox on foreground
		options.addArguments("-foreground"); 
			
		driver = new FirefoxDriver(options);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get(url);
		
		final long start = System.currentTimeMillis();
		
		// "$1,654.55";
		String string_monthly_payment = driver.findElement(By.id("id_monthly_payment")).getText();
		//System.out.println("Monthly payment \t" + string_monthly_payment);
		String regex = "^"
					 + "(?:\\$)?"
					 + "(?:\\s*)?"
					 + "((?:\\d{1,3})(?:\\,)?(?:\\d{3})?(?:\\.)?(\\d{0,2})?)"
                     + "$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(string_monthly_payment);
		m.find();
		
		// 1,654.55
		double monthly_payment = Double.parseDouble(m.group(1).replaceAll(",", ""));
		//System.out.println(monthly_payment);
		// 1654.55 * 12 = 19854.60
		double annual_payment = new BigDecimal(monthly_payment * 12).setScale(2, RoundingMode.HALF_UP).doubleValue();
		//System.out.println(annual_payment);
		// 19854.6
		DecimalFormat df = new DecimalFormat("0.00");
		String f_annual_payment = df.format(annual_payment);
		//System.out.println(f_annual_payment);
		
		driver.findElement(By.id("id_annual_payment")).sendKeys(String.valueOf(f_annual_payment));
		driver.findElement(By.id("id_validate_button")).click();
		String actual_result = driver.findElement(By.id("id_result")).getText();
		
		final long finish = System.currentTimeMillis();
		
		System.out.println("String: \t" + m.group(0)); // capturing whole thing
		System.out.println("Annual Payment: " + f_annual_payment);
		System.out.println("Result: \t" + actual_result);
		System.out.println("Responce time: \t" + (finish - start) + " milliseconds\n");

		driver.quit();
		
		}
		
	}

}
