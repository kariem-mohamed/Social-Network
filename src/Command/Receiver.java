

package Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.FCI.SWE.Controller.Connection;

public class Receiver {
  
	String  currentUserEmail , friendEmail ;
	
	public Receiver(String currentUserEmail , String friendEmail){
		
		this.currentUserEmail = currentUserEmail ;
		this.friendEmail = friendEmail ;
	}

	/**
	 * Action function to execute the acceptFriendRequest command
	 * 
	 * 
	 * @return String 
	 */
    public String acceptFriendRequest(){
	
        String ret = Connection.connect(
 				"http://localhost:8888/rest/acceptRequestService","currentUserEmail=" +  currentUserEmail +"&friendRequestEmail=" + friendEmail ,
 				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
 		
 		
 		
 		
 		return ret;
    	
	}

	/**
	 * Action function to execute the replySingleMessage command
	 * 
	 * 
	 * @return String 
	 */
	public String replySingleMessage(  ){


		   Map<String, String> map = new HashMap<String, String>();
			map.put("email", friendEmail);
			return "sendMessage" ;


	}

	/**
	 * Action function to execute the replyGroupMessage command
	 * 
	 * 
	 * @return String 
	 */
	public String replyGroupMessage(){

		return "sendGroupMessage";

	}
	
   
}