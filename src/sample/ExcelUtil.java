package sample;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.beust.jcommander.Strings;
import com.google.common.net.UrlEscapers;

public class ExcelUtil {

	private static final short IMAGE_ROW_HEIGHT = 4500;
	private static final short DEFAULT_ROW_HEIGHT = 255;
	private static final int IMAGE_COLUMN_INDEX = 9;
	private static final int ID_COLUMN_INDEX = 10;
	
	public static void exportToExcel(List<TyresCollection> tyresCollectionList, String fileName) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("ErrolsTyres");
		sheet.setDefaultRowHeight(IMAGE_ROW_HEIGHT);
		
		Map<String, Object[]> data = new LinkedHashMap<String, Object[]>();
		data.put("1", new Object[] { "Name", "SKU", "Branch",
				"Tyre Width", "Tyre Profile", "Rim Size", "Load Index",
				"Tyre SI", "Price", "Image", "XXX" });
		
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
						"",
						tyresCollection.getId()
				};
				
				if (!Strings.isStringEmpty(tyresCollection.getImageUrl())) {
					objArray[IMAGE_COLUMN_INDEX] = getImageStream(tyresCollection.getImageUrl());
				} else {
					objArray[IMAGE_COLUMN_INDEX] = "";
				}

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
	        for (int i = 0; i < objArr.length; i++) {
	        	Object obj = objArr[i];
	        	
	        	if (i == ID_COLUMN_INDEX) {
	        		continue;
	        	}
	        	
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
	        
	        Integer id = (Integer) objArr[ID_COLUMN_INDEX];
    		if (imagesId.contains(id)) {
    			rownum++;
    			row.setHeight(DEFAULT_ROW_HEIGHT);
    			continue;
    		} else {
    			imagesId.add(id);
    		}
	        
    		if (objArr[IMAGE_COLUMN_INDEX] instanceof byte[]) {
    			addImages( (byte[]) objArr[IMAGE_COLUMN_INDEX], workbook, sheet, rownum, IMAGE_COLUMN_INDEX);
    			row.setHeight(IMAGE_ROW_HEIGHT);
    		} else {
    			// no need to add images
    			System.out.println("no need to add images for: " + objArr[0] + " " + objArr[2]);
    		}
		
			rownum++;
	    }
	    
	    try {
	        FileOutputStream out =new FileOutputStream(new File(fileName));
	        workbook.write(out);
	        out.close();
	        System.out.println("Excel written successfully..");
	         
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private static byte[] getImageStream(String imageUrl) throws IOException {
//		if (Strings.isStringEmpty(imageUrl)) {
//			return new ByteArrayInputStream(new byte[0]);
//		}
		URL url = new URL(UrlEscapers.urlFragmentEscaper().escape(imageUrl));
		URLConnection urlConnection = url.openConnection();
//		urlConnection.connect();
		urlConnection.addRequestProperty("User-Agent", "Chrome/50.0.2661.94");
		urlConnection.addRequestProperty("Connection", "keep-alive");
	    InputStream inputStream = urlConnection.getInputStream();
	    byte[] bytes = IOUtils.toByteArray(inputStream);
	    inputStream.close();
		return bytes;
	}
	
	private static Dimension addImages(byte[] bytes, Workbook requestReport, Sheet sheet, int row, int  col) throws IOException {
		Drawing patriarch = sheet.createDrawingPatriarch();
	    CreationHelper helper = requestReport.getCreationHelper();
	    ClientAnchor anchor = helper.createClientAnchor();
	    
//	    byte[] bytes = IOUtils.toByteArray(in);
	    int pictureIndex = requestReport.addPicture(bytes, HSSFWorkbook.PICTURE_TYPE_PNG);
//	  in.close();
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
