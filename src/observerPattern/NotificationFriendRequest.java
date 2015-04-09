package observerPattern;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.FCI.SWE.Controller.Connection;

public class NotificationFriendRequest extends Observer{

	
	 public NotificationFriendRequest( EventSubject s ) { 
		    subj = s; 
		    subj.attach( this ); 
	 }

	@Override
	public String update(String userEmail) {


		String retJson = Connection.connect(
				"http://localhost:8888/rest/showFriendRequestService","currentUserEmail=" + userEmail ,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		String html =  "<form action=\"/social/performCommand\" method=\"post\"> "
				+ "<input type='text' name='command' value='3' />" ;
		int check = 0 ;
		
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(retJson);
			JSONObject object = (JSONObject)obj;
			ArrayList<String> friendRequests = (ArrayList<String>)object.get("friendRequests");
				if(friendRequests.size() != 0)
					check = 1 ;
				
			for(int i = 0; i < friendRequests.size(); i++)
				html += "<p> You got a friend request from </p> <input type='text' name='email' value='"  +friendRequests.get(i)  + "' /> <input type=\"submit\" name = \"Accept\" value='Accept' > <br>";
		
			
			html += "</form>";
			if (check == 1 )
			return html ;
		}
		
			
		
		catch(Exception e){
			e.printStackTrace();
		}
		return "" ;
	}
}
