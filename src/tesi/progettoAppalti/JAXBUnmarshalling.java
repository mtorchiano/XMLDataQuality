package tesi.progettoAppalti;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

public class JAXBUnmarshalling {
	String filePath;
	String context;
	JAXBContext jaxbContext;
	InputStream input;

	public JAXBUnmarshalling(String filePath, String context){
		this.filePath = filePath;
		this.context = context;

		try {
			jaxbContext = JAXBContext.newInstance(context);
		} catch (JAXBException e) {

			e.printStackTrace();
		}
	}
	public Object  getUnmarshalObject() throws FileNotFoundException{
		Unmarshaller  unmarshaller = null;
		Object  objectJAXB = null;
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = sf.newSchema(new File("xsd/datasetAppaltiL190.xsd"));

			try {
				unmarshaller = jaxbContext.createUnmarshaller();
				unmarshaller.setSchema(schema);
				unmarshaller.setEventHandler(new MyValidationEventHandler()); 
				objectJAXB = unmarshaller.unmarshal(new FileInputStream(filePath));
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		} catch (SAXException e1) {
			e1.printStackTrace();
		}catch(FileNotFoundException e){
			System.out.println("File not found");
			System.exit(0);
		}

		return objectJAXB;
	}
}
