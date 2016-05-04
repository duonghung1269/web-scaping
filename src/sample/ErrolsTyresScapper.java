package sample;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ErrolsTyresScapper {

	private final static String URL = "http://www.errolstyres.co.za/";

	private WebDriver webDriver;

	// define an Excel Work Book
	private HSSFWorkbook workbook;
	// define an Excel Work sheet
	private HSSFSheet sheet;

	private Map<String, Object[]> testresultdata;

	public ErrolsTyresScapper() {
		System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
		webDriver = new ChromeDriver();

//		workbook = new HSSFWorkbook();
//		sheet = workbook.createSheet("Test Result");
//		testresultdata = new LinkedHashMap<String, Object[]>();
//		// write the header in the first row
//		testresultdata.put("1", new Object[] { "Test Step Id", "Action",
//				"Expected Result", "Actual Result" });
	}

	public void openSite(String url) {
		webDriver.navigate().to(url);

		try {
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IllegalStateException("Can't start Web Driver", e);
		}
	}

	public List<Tyres> getTyresList() {
		List<WebElement> findElements = webDriver.findElements(By
				.xpath("//div[@class='Product tyrespecials']"));
		List<Tyres> tyresList = new ArrayList<>();
		for (int i = 0; i < findElements.size(); i++) {
			Tyres tyres = new Tyres();
			WebElement element = findElements.get(i);
			String productTitle = element.findElement(
					By.xpath("./a[@class='ProductTitle']/span")).getText();
			tyres.setBranch(productTitle);
			System.out.println(productTitle);

			String productUrl = element.findElement(
					By.xpath("./a[@class='ProductTitle']"))
					.getAttribute("href");
			tyres.setUrl(productUrl);
			System.out.println(productUrl);

			tyresList.add(tyres);
		}
		
		return tyresList;
	}
	
	public void doUpdateTyres(Tyres tyres) {
		openSite(tyres.getUrl());
		List<WebElement> elements = webDriver.findElements(By.xpath("//table[@class='Chart']//tr[position() > 1]"));
		for (int i = 0; i < elements.size(); i++) {
			WebElement trElement = elements.get(i);
			List<WebElement> tdElements = trElement.findElements(By.xpath("./td"));
			if (tdElements.size() < 3) {
				throw new IllegalArgumentException("Number of columns less than 3: " + tdElements.size());
			}
			
			doUpdateTyresItemColumn(tyres, tdElements.get(0));
			doUpdateTyresPartNoColumn(tyres, tdElements.get(1));
			doUpdateTyresPriceColumn(tyres, tdElements.get(2));
		}
	}
	
	private void doUpdateTyresPriceColumn(Tyres tyres, WebElement tdElement) {
		String price = tdElement.getText().trim();
		tyres.setPrice(price);
	}

	private void doUpdateTyresPartNoColumn(Tyres tyres, WebElement tdElement) {
		String sku = tdElement.findElement(By.xpath("./div")).getText().trim();
		tyres.setSku(sku);
	}

	private void doUpdateTyresItemColumn(Tyres tyres, WebElement tdElement) {
		String string = tdElement.getText().trim();
		
		String[] strings = string.split(" ");
		int length = strings.length;
		
		// set width and profile
		if (0 < length) {
			String[] sizeProfile = strings[0].trim().split("/");
			if (sizeProfile.length != 2) {
				throw new IllegalArgumentException("Size and Profile is invalid: " + strings[0]);
			}
			
			tyres.setWidth(sizeProfile[0]);
			tyres.setProfile(sizeProfile[1]);
			
		}
		
		// set size
		if (1 < length) {
			tyres.setSize(strings[1].trim());
		}
		
		// set loadIndex and SI
		if (2 < length) {
			String loadIndexSI = strings[2].trim();
			int loadIndexSILength = loadIndexSI.length();
			if (loadIndexSILength == 1) { // SI only
				tyres.setSi(loadIndexSI);
			} else {
				tyres.setLoadIndex(loadIndexSI.substring(0, loadIndexSILength - 1));
				tyres.setLoadIndex(loadIndexSI.substring(loadIndexSILength - 1, loadIndexSILength));
			}
		}
	}
	
	public void doUpdateTyresList(List<Tyres> tyresList) {
		for (int i = 0; i < 3; i++) {
			Tyres tyres = tyresList.get(i);
			doUpdateTyres(tyres);
		}
	}

	public static void main(String[] args) {
		ErrolsTyresScapper scrapper = new ErrolsTyresScapper();
		scrapper.openSite(URL);
		List<Tyres> tyresList = scrapper.getTyresList();
		scrapper.doUpdateTyresList(tyresList);
	}
}
