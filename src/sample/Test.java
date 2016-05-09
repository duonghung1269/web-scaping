package sample;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class Test {

	public static void main(String[] args) {
		TyresCollection coll = new TyresCollection();
		coll.setBranch("branch");
		Tyres tyre = new Tyres("name", "sku", "branch", "width", "profile", "size", "loadIndex", "si", "price");
		coll.getTyresList().add(tyre);
	
		TyresCollectionWrapper wrapper = new TyresCollectionWrapper();
		wrapper.getTyresCollectionList().add(coll);
		try {

//			File file = new File("src/data/clubMember.xml");
	            JAXBContext jaxbContext = JAXBContext.newInstance(TyresCollectionWrapper.class);
	            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

	            // output pretty printed
	            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	            File f = new File("testMarshal.xml");
	            jaxbMarshaller.marshal(wrapper, f);
	            jaxbMarshaller.marshal(wrapper, System.out);

	        } catch (JAXBException e) {
	            e.printStackTrace();
	        }
	}

}
