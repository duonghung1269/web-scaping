package sample;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="tyrescollectionwrapper")
public class TyresCollectionWrapper {
	private List<TyresCollection> tyresCollectionList = new ArrayList<>();
	
	public TyresCollectionWrapper() {
		
	}

	@XmlElement(name="tyrescollection")
	public List<TyresCollection> getTyresCollectionList() {
		return tyresCollectionList;
	}

	public void setTyresCollectionList(List<TyresCollection> tyresCollectionList) {
		this.tyresCollectionList = tyresCollectionList;
	}

	
	
	
}
