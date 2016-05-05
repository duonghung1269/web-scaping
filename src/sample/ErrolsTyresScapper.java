package sample;

import java.io.IOException;
import java.net.URLEncoder;
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

import com.google.common.net.UrlEscapers;

public class ErrolsTyresScapper {

	private final static String URL = "http://www.errolstyres.co.za/";
	private static final int NO_TYRES_TEST = 2;

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
		webDriver.get(url);
//		webDriver.navigate().to(url);
		
		try {
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IllegalStateException("Can't start Web Driver", e);
		}
	}

	public List<TyresCollection> getTyresCollectionList() {
		List<WebElement> findElements = webDriver.findElements(By
				.xpath("//div[@class='Product tyrespecials']"));
		List<TyresCollection> tyresCollectionList = new ArrayList<>();
//		for (int i = 0; i < findElements.size(); i++) {
		for (int i = 0; i < NO_TYRES_TEST; i++) {
			WebElement element = findElements.get(i);
			String productTitle = element.findElement(
					By.xpath("./a[@class='ProductTitle']/span")).getText();
			
			String productName = element.findElement(
					By.xpath("./a[@class='ProductTitle']")).getText().trim().split("\n")[1];
			
			String productUrl = element.findElement(
					By.xpath("./a[@class='ProductTitle']"))
					.getAttribute("href");

			String imageStyle = element.findElement(
					By.xpath("./a[@class='ProductPic']/img[contains(@style,'background-image')]")).getAttribute("style");
			 String imageRelativeUrl = imageStyle.substring(imageStyle.indexOf("\""), imageStyle.lastIndexOf("\""));
			 String imageURL = URL + imageRelativeUrl.substring(imageRelativeUrl.indexOf("/") + 1, imageRelativeUrl.length());
			
			TyresCollection tyresCollection = new TyresCollection(productTitle, productUrl, imageURL);
			tyresCollection.setName(productName);
			tyresCollection.setBranch(productTitle);
			tyresCollectionList.add(tyresCollection);
			tyresCollection.setId(i + 1);
		}
		
		return tyresCollectionList;
	}
	
	public void doUpdateTyres(TyresCollection tyresCollection) {
		openSite(tyresCollection.getPageUrl());
		List<WebElement> elements = webDriver.findElements(By.xpath("//table[@class='Chart']//tr[position() > 1]"));
		for (int i = 0; i < elements.size(); i++) {
			WebElement trElement = elements.get(i);
			List<WebElement> tdElements = trElement.findElements(By.xpath("./td"));
			if (tdElements.size() < 3) {
				throw new IllegalArgumentException("Number of columns less than 3: " + tdElements.size());
			}
			
			Tyres tyres = new Tyres();
			
			doUpdateTyresItemColumn(tyres, tdElements.get(0));
			doUpdateTyresPartNoColumn(tyres, tdElements.get(1));
			doUpdateTyresPriceColumn(tyres, tdElements.get(2));
			
			tyresCollection.addTyres(tyres);
		}
		
	}
	
	private void closeBrowser() {
		//close the browser
	    webDriver.close();
	    webDriver.quit();
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
				tyres.setSi(loadIndexSI.substring(loadIndexSILength - 1, loadIndexSILength));
			}
		}
	}
	
	public void doUpdateTyresList(List<TyresCollection> tyresCollectionList) {
		for (int i = 0; i < NO_TYRES_TEST; i++) {
			TyresCollection tyresCollection = tyresCollectionList.get(i);
			doUpdateTyres(tyresCollection);
		}
	}

	public static void main(String[] args) throws IOException {
		ErrolsTyresScapper scrapper = new ErrolsTyresScapper();
		scrapper.openSite(URL);
		List<TyresCollection> tyresCollectionList = scrapper.getTyresCollectionList();
		scrapper.doUpdateTyresList(tyresCollectionList);

		ExcelUtil.exportToExcel(tyresCollectionList);
		scrapper.closeBrowser();
	}
}
