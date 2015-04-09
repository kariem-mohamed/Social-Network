package observerPattern;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.FCI.SWE.Controller.Connection;

public class NotificationGroupMessage extends Observer{

	 public NotificationGroupMessage( EventSubject s ) { 
		    subj = s; 
		    subj.attach( this ); 
		  }

	@Override
	public String update(String userEmail) {
        
		
		String retJson = Connection.connect(
				"http://localhost:8888/rest/showGroupMessagesService","currentUserEmail=" + userEmail ,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
      String html = "" ;

		JSONParser parser = new JSONParser();
		int check = 0 ;
	try{
		Object obj = parser.parse(retJson);
		JSONObject object = (JSONObject)obj;
		ArrayList<String> Messages = (ArrayList<String>)object.get("Messages");
		 html +=  "<p>Group Message  <br> --------------------------------  <br> </p><form action=\"/social/performCommand\" method=\"post\">  "
		 		+ "<input type='text' name='command' value='1' />";
		if(Messages.size() != 0)
         check =1 ;
		for(int i = 0; i < Messages.size(); i+=2)
			html += "<p>" + Messages.get(i) +" : "+  Messages.get(i+1) + "<br>";
		
        html += " </p> <input type='submit' value='Reply' />";
		html += "</form>";
		
		if ( check == 1 )
		
		return html;
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return "";
		

	}
}
