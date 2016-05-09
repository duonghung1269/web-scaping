package sample;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ErrolsTyresScapper {

	private final static String URL = "http://www.errolstyres.co.za/";
//	private static final int NO_TYRES_TEST = 100;

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
				.xpath("//div[@id='top_tyres']/div[@class='Product tyrespecials']"));
		List<TyresCollection> tyresCollectionList = new ArrayList<>();
		for (int i = 0; i < findElements.size(); i++) {
//		for (int i = 0; i < NO_TYRES_TEST; i++) {
			WebElement element = findElements.get(i);
			
//			System.out.println("Element: " + element);
			
			String productTitle = element.findElement(
					By.xpath("./a[@class='ProductTitle']/span")).getText();
			
			String productName = element.findElement(
					By.xpath("./a[@class='ProductTitle']")).getText().trim().split("\n")[1];
			
			String productUrl = element.findElement(
					By.xpath("./a[@class='ProductTitle']"))
					.getAttribute("href");

			String imageURL = ""; 
			
			try {
				String imageStyle = element.findElement(
						By.xpath("./a[@class='ProductPic']/img[contains(@style,'url(')]")).getAttribute("style");
				 String imageRelativeUrl = imageStyle.substring(imageStyle.indexOf("\""), imageStyle.lastIndexOf("\""));
				 imageURL = URL + imageRelativeUrl.substring(imageRelativeUrl.indexOf("/") + 1, imageRelativeUrl.length());
				 
				 
			} catch (Exception ex) {
				System.out.println("No picture element: " + ex);
			}
			
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
		WebElement tableElement = webDriver.findElements(By.xpath("//table[@class='Chart']")).get(0);
		List<WebElement> elements = tableElement.findElements(By.xpath(".//tr[position() > 1]"));
		
		for (int i = 0; i < elements.size(); i++) {
			WebElement trElement = elements.get(i);
			List<WebElement> tdElements = trElement.findElements(By.xpath("./td"));
			if (tdElements.size() < 3) {
				throw new IllegalArgumentException("Number of columns less than 3: " + tdElements.size());
			}
//			tdElements.get(1).getAttribute("class").trim();
			//webDriver.findElements(By.xpath("//table[@class='Chart']")).get(0).findElements(By.xpath(".//tr[position() > 1]"))
			//webDriver.findElements(By.xpath("//table[@class='Chart'][position() = 1]//tr[position() > 1]")).get(1).findElements(By.xpath("./td")).get(1).findElement(By.xpath("./div")).getText().trim()
			Tyres tyres = new Tyres();
			try {
				doUpdateTyresItemColumn(tyres, tdElements.get(0));
				doUpdateTyresPartNoColumn(tyres, tdElements.get(1));
				doUpdateTyresPriceColumn(tyres, tdElements.get(2));	
			} catch (UnsupportedOperationException ex1) {				
				System.out.println("Produt URL: " + tyresCollection.getPageUrl());
				tyres.setSize("");
				tyres.setProfile("");
				tyres.setWidth("");
				tyres.setLoadIndex("");
				tyres.setSi("");
			} catch (Exception ex) {
				System.out.println("Name: " + tyresCollection.getName());
				System.out.println("Branch: " + tyresCollection.getBranch());
				ex.printStackTrace();
			}

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
		List<String> stringList = new ArrayList<>();
		for (String s : strings) {
			if (!"".equals(s.trim())) {
				stringList.add(s);
			}
		}

		int length = strings.length;
		
		// set width and profile
		if (0 < length) {
			String[] sizeProfile = stringList.get(0).trim().split("/");
			if (sizeProfile.length != 2) {
				System.out.println("ERROR=====: Size and Profile is invalid: " + strings[0]);				
				throw new UnsupportedOperationException("Invalid size profile");
				
			}
			
			tyres.setWidth(sizeProfile[0]);
			tyres.setProfile(sizeProfile[1]);
			
		}
		
		// set size
		if (1 < length) {
			tyres.setSize(stringList.get(1).trim());
		}
		
		// set loadIndex and SI
		if (2 < length) {
			String loadIndexSI = stringList.get(2).trim();
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
		for (int i = 0; i < tyresCollectionList.size(); i++) {
			TyresCollection tyresCollection = tyresCollectionList.get(i);
			doUpdateTyres(tyresCollection);
		}
	}

	public static void main(String[] args) throws IOException {
//		ErrolsTyresScapper scrapper = new ErrolsTyresScapper();
//		scrapper.openSite(URL);
//		List<TyresCollection> tyresCollectionList = scrapper.getTyresCollectionList();
//		scrapper.doUpdateTyresList(tyresCollectionList);

//		try {
//
//	            JAXBContext jaxbContext = JAXBContext.newInstance(TyresCollectionWrapper.class);
//	            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//
//	            // output pretty printed
//	            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//	            File f = new File("testMarshal.xml");
//	            TyresCollectionWrapper wrapper = new TyresCollectionWrapper();
//	            wrapper.setTyresCollectionList(tyresCollectionList);
//	            jaxbMarshaller.marshal(wrapper, f);
//	            //jaxbMarshaller.marshal(tyresCollectionList, System.out);
//
//	        } catch (JAXBException e) {
//	            e.printStackTrace();
//	        }
		
		TyresCollectionWrapper loadTyresCollectionWrapper = loadTyresCollectionWrapper();
		
		List<TyresCollection> tyresCollectionList = loadTyresCollectionWrapper.getTyresCollectionList();
		
//		List<TyresCollection> t2 = new ArrayList<>();
//		t2.add(tyresCollectionList.get(0));
//		t2.add(tyresCollectionList.get(1));
		ExcelUtil.exportToExcel(tyresCollectionList, "ErrolsTyres.xls");
//		scrapper.closeBrowser();
	}
	
	public static TyresCollectionWrapper loadTyresCollectionWrapper() {
        try {

        	File f = new File("testMarshal.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(TyresCollectionWrapper.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            TyresCollectionWrapper wrapper = (TyresCollectionWrapper) jaxbUnmarshaller.unmarshal(f);
//            System.out.println(personCollection);
            return wrapper;
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return new TyresCollectionWrapper();
    }

}
