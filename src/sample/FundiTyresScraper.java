package sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FundiTyresScraper {
	private final static String URL = "http://www.fundityres.co.za/tyres?id=16&tyre_brand=&tyre_profile=&tyre_quality=&tyre_rimsize=&tyre_width=&p=";
	private static final int NO_TYRES_TEST = 2;
	private static final int MAX_PAGE = 50;  // 184
	private int pageNum = 1;
	
	private WebDriver webDriver;
	
	public FundiTyresScraper() {
		System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
		webDriver = new ChromeDriver();
	}

	public void openSite(String url) {
		webDriver.get(url);
		
		try {
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IllegalStateException("Can't start Web Driver", e);
		}
	}
	
	public List<TyresCollection> getTyresCollectionList() {
		List<TyresCollection> tyresCollectionList = new ArrayList<>();
		int productId = 1;
		for (int page = 1; page <= MAX_PAGE; page++) {
			String url = URL + page;
			openSite(url);
			List<WebElement> productElements = webDriver.findElements(By
					.xpath("//div[@class='category-products']/ol/li"));
			for (int itemIndex = 0; itemIndex < NO_TYRES_TEST; itemIndex++) {
				WebElement element = productElements.get(itemIndex);
				
				String productUrl = element.findElement(
						By.xpath("./a[contains(@class, 'product-image')]"))
						.getAttribute("href");

				String imageURL = element.findElement(
						By.xpath("./a[contains(@class, 'product-image')]/img")).getAttribute("src");
				
				TyresCollection tyresCollection = new TyresCollection(null, productUrl, imageURL);
				tyresCollectionList.add(tyresCollection);
				tyresCollection.setId(productId++);
			}
		}
		
		
		return tyresCollectionList;
	}
	
	public void doUpdateTyres(TyresCollection tyresCollection) {
		openSite(tyresCollection.getPageUrl());
		String productName = webDriver.findElements(By.xpath("//div[contains(@class, 'product-shop')]//div[@class='product-name']/h1")).get(0).getText().trim();
		tyresCollection.setName(productName);
		
		
		String sku = webDriver.findElements(By.xpath("//table[@id='product-attribute-specs-table']//tr[position() = 1]/td[position() = 1]")).get(0).getText().trim();
		String branch = webDriver.findElements(By.xpath("//table[@id='product-attribute-specs-table']//tr[position() = 2]/td[position() = 1]")).get(0).getText().trim();
		String width = webDriver.findElements(By.xpath("//table[@id='product-attribute-specs-table']//tr[position() = 4]/td[position() = 1]")).get(0).getText().trim();
		String profile = webDriver.findElements(By.xpath("//table[@id='product-attribute-specs-table']//tr[position() = 5]/td[position() = 1]")).get(0).getText().trim();
		String rimSize = webDriver.findElements(By.xpath("//table[@id='product-attribute-specs-table']//tr[position() = 6]/td[position() = 1]")).get(0).getText().trim();
		String loadIndexSI = webDriver.findElements(By.xpath("//div[@class='tyre-specs-inner']//span[@class='tyre-li-se']")).get(0).getText().trim();
		
		String loadIndex = "";
		if (loadIndexSI.length() < 2) {
			loadIndex = "";
		} else {
			loadIndex = loadIndexSI.substring(0, loadIndexSI.length() - 1); // remove last SI character
		}
		
		String tyreSI = webDriver.findElements(By.xpath("//table[@id='product-attribute-specs-table']//tr[position() = 7]/td[position() = 1]")).get(0).getText().trim();
		String price = webDriver.findElements(By.xpath("//div[@class='tyre-specs-inner']//span[@class='boldprice']")).get(0).getText().trim();
		
		tyresCollection.setBranch(branch);
		
		Tyres tyres = new Tyres();
		tyres.setLoadIndex(loadIndex);
		tyres.setPrice(price);
		tyres.setProfile(profile);
		tyres.setSi(tyreSI);
		tyres.setSize(rimSize);
		tyres.setSku(sku);
		tyres.setWidth(width);
		
		tyresCollection.getTyresList().add(tyres);
	}
	
	private void closeBrowser() {
		//close the browser
	    webDriver.close();
	    webDriver.quit();
	}

	public void doUpdateTyresList(List<TyresCollection> tyresCollectionList) {
		for (int i = 0; i < tyresCollectionList.size(); i++) {
			TyresCollection tyresCollection = tyresCollectionList.get(i);
			doUpdateTyres(tyresCollection);
		}
	}
	
	public static void main(String[] args) throws IOException {
		FundiTyresScraper scrapper = new FundiTyresScraper();
		scrapper.openSite(URL + scrapper.pageNum++);
		List<TyresCollection> tyresCollectionList = scrapper.getTyresCollectionList();
		scrapper.doUpdateTyresList(tyresCollectionList);

		ExcelUtil.exportToExcel(tyresCollectionList, "FundiTyres.xls");
		scrapper.closeBrowser();
	}
}
