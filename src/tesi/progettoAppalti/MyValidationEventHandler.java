package tesi.progettoAppalti;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;



public class MyValidationEventHandler implements ValidationEventHandler{

	public boolean handleEvent(ValidationEvent event) {
	     System.out.println("\nEVENT");
	     System.out.println("SEVERITY: "+event.getSeverity());
	     System.out.println("MESSAGE: "+event.getMessage());
	     System.out.println("LINKED EXCEPTION: "+event.getLinkedException());
	  
		return true;
	}

}
