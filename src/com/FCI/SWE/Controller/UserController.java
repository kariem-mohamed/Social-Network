package com.FCI.SWE.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import observerPattern.EventSubject;
import observerPattern.NotificationFriendRequest;
import observerPattern.NotificationGroupMessage;
import observerPattern.NotificationSingleMessage;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Command.AcceptFriendRequest;
import Command.Invoker;
import Command.Receiver;
import Command.ReplyGroupMessage;
import Command.ReplySingleMessage;

import com.FCI.SWE.Models.User;
import com.FCI.SWE.ServicesModels.UserEntity;

/**
 * This class contains REST services, also contains action function for web
 * application
 * 
 * @author Mohamed Samir
 * @version 1.0
 * @since 2014-02-12
 *
 */
@Path("/")
@Produces("text/html")
public class UserController {
	public static UserEntity currentActiveUser = null; 
	private static UserEntity requestedUser = null; 
	public static ArrayList<String> messageMembers = new ArrayList<String>() ;
	/**
	 * Action function to render Signup page, this function will be executed
	 * using url like this /rest/signup
	 * 
	 * @return sign up page
	 */
	@POST
	@Path("/doSearch")
	public Response usersList(@FormParam("email") String email){
		
		String serviceUrl = "http://localhost/rest/SearchService";
		String urlParameters = "email=" + email;
		String retJson = Connection.connect(
				"http://localhost:8888/rest/SearchService", urlParameters,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("Status").equals("Failed"))
				return null;
			else{
				Map<String, String> map = new HashMap<String, String>();
				requestedUser = new UserEntity(object.get("name").toString(), object.get("email").toString(), object.get("password").toString(), Long.parseLong(object.get("id").toString()), (ArrayList<String>)object.get("friends"), (ArrayList<String>)object.get("friendRequests"));
				
				map.put("name", requestedUser.getName());
				map.put("email", requestedUser.getEmail());
				return Response.ok(new Viewable("/jsp/searchResult", map)).build();
			}
		}catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}


		
		return null;
	}
	/**
	 * Action function to render OpenGroupMessaging page this function will be executed using
	 * url like this /rest/OpenGroupMessaging
	 * 
	 * @return sendMessages page
	 */
	@GET
	@Path("/OpenGroupMessaging")
	public Response openGroupMessaging() {
		
		if ( messageMembers == null )
			return null ;
		else
		return Response.ok(new Viewable("/jsp/sendGroupMessage")).build();
	}
	@GET
	@Path("/signup")
	public Response signUp() {
		
		return Response.ok(new Viewable("/jsp/register")).build();
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

	@GET
	@Path("/search")
	public Response search(){
		return Response.ok(new Viewable("/jsp/search")).build();
	}
	/**
	 * Action function to render home page of application, home page contains
	 * only signup and login buttons
	 * 
	 * @return enty point page (Home page of this application)
	 */
	@GET
	@Path("/")
	public Response index() {
		return Response.ok(new Viewable("/jsp/entryPoint")).build();
	}

	/**
	 * Action function to render login page this function will be executed using
	 * url like this /rest/login
	 * 
	 * @return login page
	 */
	@GET
	@Path("/login")
	public Response login() {
		return Response.ok(new Viewable("/jsp/login")).build();
	}

	/**
	 * Action function to response to signup request, This function will act as
	 * a controller part and it will calls RegistrationService to make
	 * registration
	 * 
	 * @param uname
	 *            provided user name
	 * @param email
	 *            provided user email
	 * @param pass
	 *            provided user password
	 * @return Status string
	 */
	@POST
	@Path("/response")
	@Produces(MediaType.TEXT_PLAIN)
	public String response(@FormParam("uname") String uname,
			@FormParam("email") String email, @FormParam("password") String pass) {

		String serviceUrl = "http://localhost:8888/rest/RegistrationService";
		String urlParameters = "uname=" + uname + "&email=" + email
				+ "&password=" + pass;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			// System.out.println(retJson);
			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("Status").equals("OK"))
				return "Registered Successfully";

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * UserEntity user = new UserEntity(uname, email, pass);
		 * user.saveUser(); return uname;
		 */
		return "Failed";
	}

	/**
	 * Action function to response to login request. This function will act as a
	 * controller part, it will calls login service to check user data and get
	 * user from datastore
	 * 
	 * @param uname
	 *            provided user name
	 * @param pass
	 *            provided user password
	 * @return Home page view
	 */
	@POST
	@Path("/home")
	@Produces("text/html")
	public Response home(@FormParam("email") String email,
			@FormParam("password") String pass) {
		String urlParameters = "email=" + email + "&password=" + pass;

		String retJson = Connection.connect(
				"http://localhost:8888/rest/LoginService", urlParameters,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");

		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("Status").equals("Failed"))
				return null;
			Map<String, String> map = new HashMap<String, String>();
			currentActiveUser = new UserEntity(object.get("name").toString(), object.get("email").toString(), object.get("password").toString(), Long.parseLong(object.get("id").toString()), (ArrayList<String>)object.get("friends"), (ArrayList<String>)object.get("friendRequests"));
		
        	map.put("name", currentActiveUser.getName());
			map.put("email", currentActiveUser.getEmail());
			return Response.ok(new Viewable("/jsp/home", map)).build();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * UserEntity user = new UserEntity(uname, email, pass);
		 * user.saveUser(); return uname;
		 */
		return null;

	}
	/**
	 * Action function to string to send friend request. This function will act as a
	 * controller part, it will calls sendFriendRequestService to send to user friend request
	 * 
	 */
	@GET
	@Path("/sendFriendRequest")
	@Produces("text/html")
	public String sendFriendRequest() {
		
		String retJson = Connection.connect(
				"http://localhost:8888/rest/sendFriendRequestService","currentUserEmail=" + currentActiveUser.getEmail() + "&requestedUserEmail="+requestedUser.getEmail(),
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");

		return "Request Sent Successfully";

	}
	/**
	 * Action function to  notify current user. 
	 *  it will calls notification type from observer
	 * 
	 */
	@GET
	@Path("/Notification")
	@Produces("text/html")
	public String notification() {
		
		 String html = "" ; 
		 
		 EventSubject sub = new EventSubject();
		
		 new NotificationFriendRequest( sub );
	     new NotificationSingleMessage( sub );
	     new NotificationGroupMessage( sub );

	     html += sub.setState(currentActiveUser.getEmail());
	     if ( html.length() == 0 )
	    	 return "You Have No Notification" ;
	     return html ;
	}
	
	/**
	 * Action function to response to perform command , that current user execute it 
	 *  
	 * 
	 */
		@POST
		@Path("/performCommand")
		@Produces("text/html")
		public Response performCommand(@FormParam("email") String email ,  @FormParam("command") String command) {
			    
                String path = "";
			    Receiver receiver = new Receiver(currentActiveUser.getEmail(),email);
				Invoker invoker = new Invoker();
		
		
    	  if ( command.equals( "1") )
                path = invoker.execute(new ReplyGroupMessage() , receiver);
			else if ( command.equals("2") )
		          
				path =invoker.execute(new ReplySingleMessage() , receiver);
			else if (command.equals("3") )
			{
				path = "home";
			    invoker.execute(new AcceptFriendRequest() , receiver);
			}
				
				//return  

				Map<String, String> map = new HashMap<String, String>();


				map.put("email", email);

				return Response.ok(new Viewable("/jsp/"+path, map)).build();

		}
		

		/**
		 * Action function to send message, This function will act as
		 * a controller part and it will calls sendMessageService 
		 * 
		 * 
		 * @param email
		 *            provided  receiver's email
		 * @param Message
		 *            provided user's message
		 * @return Status string
		 */
	@POST
	@Path("/sendMessage")
	@Produces("text/html")
	public String sendMessages(@FormParam("email") String email,
			@FormParam("Message") String Message ) {

	
        	String ret = Connection.connect(
				"http://localhost:8888/rest/sendMessageService","currentUserEmail=" + currentActiveUser.getEmail()+"&friendEmail=" + email + "&Message=" + Message,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		
		
		
		return "Send Message success";

	}

	/**
	 * Action function to show friends, This function will act as
	 * a controller part and it will calls showFriendsService 
	 * 
	 * 
	 * 
	 * @return String of jsp
	 */
	@GET
	@Path("/showFriends")
	@Produces("text/html")
	public String showFriends() {
		
		String retJson = Connection.connect(
				"http://localhost:8888/rest/showFriendsService","currentUserEmail=" + currentActiveUser.getEmail(),
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(retJson);
			JSONObject object = (JSONObject)obj;
			ArrayList<String> friends = (ArrayList<String>)object.get("friends");
			String html =  "<form action=\"/social/doSearch\" method=\"post\"> ";
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
	@GET
	@Path("/sendMessageFriends")
	@Produces("text/html")
	public String sendMessageFriends() {
		
		String retJson = Connection.connect(
				"http://localhost:8888/rest/showFriendsService","currentUserEmail=" + currentActiveUser.getEmail(),
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
	@GET
	@Path("/GroupMessaging")
	@Produces("text/html")
	public String groupMessaging() {
		messageMembers = new ArrayList<String>() ;
		String retJson = Connection.connect(
				"http://localhost:8888/rest/showFriendsService","currentUserEmail=" + currentActiveUser.getEmail(),
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
		

	  	if ( !messageMembers.contains(currentActiveUser.getEmail()) )
	  		messageMembers.add(currentActiveUser.getEmail());
	       	if ( !messageMembers.contains(MemberEmail) )
	       		messageMembers.add(MemberEmail);


		String retJson = Connection.connect(
				"http://localhost:8888/rest/showFriendsService","currentUserEmail=" + currentActiveUser.getEmail(),
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
				"http://localhost:8888/rest/createGroupMessageService","Members=" + messageMembers.get(0),
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
				"http://localhost:8888/rest/sendMessageGroupService", "currentUserEmail=" + currentActiveUser.getEmail() + "&Message=" + Message,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		
		
		
		return "Send Message success";

	}

}