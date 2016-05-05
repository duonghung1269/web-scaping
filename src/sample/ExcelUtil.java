package sample;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.common.net.UrlEscapers;

public class ExcelUtil {

	private static final short IMAGE_ROW_HEIGHT = 4500;
	private static final short DEFAULT_ROW_HEIGHT = 255;
	
	public static void exportToExcel(List<TyresCollection> tyresCollectionList) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("ErrolsTyres");
		sheet.setDefaultRowHeight(IMAGE_ROW_HEIGHT);
		
		Map<String, Object[]> data = new LinkedHashMap<String, Object[]>();
		data.put("1", new Object[] { "Name", "SKU", "Branch",
				"Tyre Width", "Tyre Profile", "Rim Size", "Load Index",
				"Tyre SI", "Price", "Image" });
		
		// prepare data
		int rowNumber = 2;
		for (int i = 0; i < tyresCollectionList.size(); i++) {
			TyresCollection tyresCollection = tyresCollectionList.get(i);
			for (int j = 0; j < tyresCollection.getTyresList().size(); j++) {
				Tyres tyres = tyresCollection.getTyresList().get(j);
				Object[] objArray = new Object[] {
						tyresCollection.getName(),
						tyres.getSku(),
						tyresCollection.getBranch(),
						tyres.getWidth(),
						tyres.getProfile(),
						tyres.getSize(),
						tyres.getLoadIndex(),
						tyres.getSi(),
						tyres.getPrice(), 
						""
				};
				
				objArray[objArray.length - 1] = getImageStream(tyresCollection.getImageUrl());
				data.put(String.valueOf(rowNumber++), objArray);
			}
			
		}
		
		// export data to excel file
		Set<String> keyset = data.keySet();
	    int rownum = 0;
	    List<Integer> imagesId = new ArrayList<>();
	    for (String key : keyset) {
	        Row row = sheet.createRow(rownum);
	        Object [] objArr = data.get(key);
	        int cellnum = 0;
	        for (Object obj : objArr) {
	            Cell cell = row.createCell(cellnum++);
	            if(obj instanceof String)
	                cell.setCellValue((String)obj);
	            else if(obj instanceof Double)
	                cell.setCellValue((Double)obj);
	        }
	        
	        // draw images for images column (last column)
	        if (key.endsWith("1")) {
	        	rownum++;
	        	continue;
	        }
	        
	        int imageColum = objArr.length - 1;
			addImages( (InputStream) objArr[imageColum], workbook, sheet, rownum, imageColum);
			row.setHeight(IMAGE_ROW_HEIGHT);
		
			rownum++;
	    }
	    
	    try {
	        FileOutputStream out =new FileOutputStream(new File("D:/MyApps/Tyres/TyresResult.xls"));
	        workbook.write(out);
	        out.close();
	        System.out.println("Excel written successfully..");
	         
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private static InputStream getImageStream(String imageUrl) throws IOException {
		URL url = new URL(UrlEscapers.urlFragmentEscaper().escape(imageUrl));
	    return url.openStream();
	}
	
	private static Dimension addImages(InputStream in, Workbook requestReport, Sheet sheet, int row, int  col) throws IOException {
		Drawing patriarch = sheet.createDrawingPatriarch();
	    CreationHelper helper = requestReport.getCreationHelper();
	    ClientAnchor anchor = helper.createClientAnchor();

	    byte[] bytes = IOUtils.toByteArray(in);
	    int pictureIndex = requestReport.addPicture(bytes, HSSFWorkbook.PICTURE_TYPE_PNG);
	  in.close();
	    if (patriarch == null) {
	        patriarch = sheet.createDrawingPatriarch();
	    }
//	    anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
	    anchor.setRow1(row);
	    anchor.setCol1(col);
	    
//	    anchor.setRow2(row + 1);
//	    anchor.setCol2(col + 1);
	    Picture picture = patriarch.createPicture(anchor, pictureIndex);
	    
	    //  to calculate the original height of the image, it use the row.getHeight value
	    picture.resize();
	    return picture.getImageDimension();
//	    picture.setLineStyle(HSSFPicture.LINESTYLE_DASHDOTGEL);
	}

}
