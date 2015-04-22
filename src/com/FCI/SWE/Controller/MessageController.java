package com.FCI.SWE.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/")
@Produces("text/html")
public class MessageController {

	/**
	 * Action function to render OpenGroupMessaging page this function will be executed using
	 * url like this /rest/OpenGroupMessaging
	 * 
	 * @return sendMessages page
	 */
	@GET
	@Path("/OpenGroupMessaging")
	public Response openGroupMessaging() {
		
		if ( UserController.messageMembers == null )
			return null ;
		else
		return Response.ok(new Viewable("/jsp/sendGroupMessage")).build();
	}
	/**
	 * Action function to render sendMessages page this function will be executed using
	 * url like this /rest/sendMessages
	 * 
	 * @return sendMessages page
	 */
	@POST
	@Path("/sendMessages")
	public Response sendMessage(@FormParam("email") String email) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("email", email);
		return Response.ok(new Viewable("/jsp/sendMessage", map)).build();

		
	}
	/**
	 * Action function to send message to friends, This function will act as
	 * a controller part and it will calls showFriendsService and user will choose his friend
	 * 
	 * 
	 * 
	 * @return String of jsp
	 */
	@GET
	@Path("/sendMessageFriends")
	@Produces("text/html")
	public String sendMessageFriends() {
		
		String retJson = Connection.connect(
				"http://localhost:8888/rest/showFriendsService","currentUserEmail=" + UserController.currentActiveUser.getEmail(),
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(retJson);
			JSONObject object = (JSONObject)obj;
			ArrayList<String> friends = (ArrayList<String>)object.get("friends");
			String html =  "<form action=\"/social/sendMessages\" method=\"post\"> ";
			if(friends.size() == 0)
				return "You Have No Friends.";
			for(int i = 0; i < friends.size(); i++)
				html += "<input type=\"submit\" name = \"email\" value=\"" + friends.get(i)	+"\">" + "<br>";
			html += "</form>";
			return html;
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
		return "";

	}


	/**
	 * Action function to send message to friends, This function will act as
	 * a controller part and it will calls showFriendsService and user will choose his friend
	 * 
	 * 
	 * 
	 * @return String of jsp
	 */
	
	@POST
	@Path("/sendMessage")
	@Produces("text/html")
	public String sendMessages(@FormParam("email") String email,
			@FormParam("Message") String Message ) {

	
        	String ret = Connection.connect(
				"http://localhost:8888/rest/sendMessageService","currentUserEmail=" + UserController.currentActiveUser.getEmail()+"&friendEmail=" + email + "&Message=" + Message,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		
		
		
		return "Send Message success";

	}

	@GET
	@Path("/GroupMessaging")
	@Produces("text/html")
	public String groupMessaging() {
		UserController.messageMembers = new ArrayList<String>() ;
		String retJson = Connection.connect(
				"http://localhost:8888/rest/showFriendsService","currentUserEmail=" + UserController.currentActiveUser.getEmail(),
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(retJson);
			JSONObject object = (JSONObject)obj;
			ArrayList<String> friends = (ArrayList<String>)object.get("friends");


			String html =  "<form action=\"/social/AddMember\" method=\"post\"> ";
			if(friends.size() == 0)
				return "You Have No Friends.";
			for(int i = 0; i < friends.size(); i++)
				html += "<input type=\"submit\" name = \"MemberEmail\" value=\'" + friends.get(i)	+"' > <br>";
			html += "  </form>";
			return html;
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
		return "";

	}
	@POST
	@Path("/AddMember")
	@Produces("text/html")
	public String AddMember(@FormParam("MemberEmail") String MemberEmail) {
		

	  	if ( !UserController.messageMembers.contains(UserController.currentActiveUser.getEmail()) )
	  		UserController.messageMembers.add(UserController.currentActiveUser.getEmail());
	       	if ( !UserController.messageMembers.contains(MemberEmail) )
	       		UserController.messageMembers.add(MemberEmail);


		String retJson = Connection.connect(
				"http://localhost:8888/rest/showFriendsService","currentUserEmail=" + UserController.currentActiveUser.getEmail(),
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(retJson);
			JSONObject object = (JSONObject)obj;
			ArrayList<String> friends = (ArrayList<String>)object.get("friends");


			String html =  "<form action=\"/social/AddMember\" method=\"post\"> ";
			if(friends.size() == 0)
				return "You Have No Friends.";
			for(int i = 0; i < friends.size(); i++)
				html += "<input type=\"submit\" name = \"MemberEmail\" value=\'" + friends.get(i)	+"' > <br>";
			html += "  </form>";
			
			html += "<form action='/social/createGroupMessaging' method='get'> <input type='submit' name ='done' value='Done'/> </form>";
			return html;
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
		return "";

		
	}


	@GET
	@Path("/createGroupMessaging")
	@Produces("text/html")
	public Response createGroupMessaging() {


		String retJson = Connection.connect(
				"http://localhost:8888/rest/createGroupMessageService","Members=" + UserController.messageMembers.get(0),
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		JSONParser parser = new JSONParser();
		Object obj;
	
		try {
			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("status").equals("OK"))
			
				return Response.ok(new Viewable("/jsp/sendGroupMessage")).build();
			

			
		}
		catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}


			
			return null;

	}

	

	/**
	 * Action function to send message to group, This function will act as
	 * a controller part and it will calls sendMessageGroupService 
	 * 
	 * 
	 * 
	 * @return String status
	 */
	
	@POST
	@Path("/sendMessageGroup")
	@Produces("text/html")
	public String sendMessageGroup(	@FormParam("Message") String Message ) {
		
           String ret = Connection.connect(
				"http://localhost:8888/rest/sendMessageGroupService", "currentUserEmail=" + UserController.currentActiveUser.getEmail() + "&Message=" + Message,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		
		
		
		return "Send Message success";

	}

}
