package observerPattern;

import java.awt.List;
import java.util.ArrayList;

public class EventSubject {
	  private ArrayList<Observer> observers=new ArrayList<Observer>();
	
	  public void attach( Observer o ) {
	    observers.add(o);
	  }

	  public String setState( String userEmail ) {
		  String html = "" ; 
		  for (int i=0; i < observers.size(); i++) 
			  html += observers.get(i).update( userEmail);
		  
		  return html ;
		  
	  }
}
