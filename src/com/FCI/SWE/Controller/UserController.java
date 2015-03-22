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

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.FCI.SWE.Models.User;
import com.FCI.SWE.ServicesModels.UserEntity;

/**
 * This class contains REST services, also contains action function for web
 * application
 * 
 * @author Kariem Mohamed
 * @version 1.3
 * @since 2014-02-15
 *
 */
@Path("/")
@Produces("text/html")
public class UserController {
	private static UserEntity currentActiveUser = null; 
	private static UserEntity requestedUser = null; 
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
				"http://1-dot-sweii-socialnetwork.appspot.com/rest/SearchService", urlParameters,
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
	@GET
	@Path("/signup")
	public Response signUp() {
		
		return Response.ok(new Viewable("/jsp/register")).build();
	}
	
	/**
	 * Action function to render the search button press
	 * the user must be signed in to be able to view search page
	 * 
	 * @return search page
	 */
	
	@GET
	@Path("/search")
	public Response search(){
		if(currentActiveUser == null)
			return Response.ok(new Viewable("/jsp/loginFirst")).build();
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

		String serviceUrl = "http://1-dot-sweii-socialnetwork.appspot.com/rest/RegistrationService";
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
	public Response home(@FormParam("uname") String uname,
			@FormParam("password") String pass) {
		String urlParameters = "uname=" + uname + "&password=" + pass;

		String retJson = Connection.connect(
				"http://1-dot-sweii-socialnetwork.appspot.com/rest/LoginService", urlParameters,
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
	 * Action function to render the press on send friend request link on
	 * the home page of the other user
	 * 
	 * @return Request confirmation
	 */
	
	@GET
	@Path("/sendFriendRequest")
	@Produces("text/html")
	public String sendFriendRequest() {
		
		String retJson = Connection.connect(
				"http://1-dot-sweii-socialnetwork.appspot.com/rest/sendFriendRequestService","currentUserEmail=" + currentActiveUser.getEmail() + "&requestedUserEmail="+requestedUser.getEmail(),
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");

		return "Request Sent Successfully";

	}
	
	
	
	/**
	 * shows the friend requests sent to the user in the shape of a button for
	 * each email
	 * 
	 * @return an html page created dynamicaly
	 */
	
	@GET
	@Path("/showFriendRequest")
	@Produces("text/html")
	public String showFriendRequest() {
		if(currentActiveUser == null)
			return "you are not logged in.";
		String retJson = Connection.connect(
				"http://1-dot-sweii-socialnetwork.appspot.com/rest/showFriendRequestService","currentUserEmail=" + currentActiveUser.getEmail(),
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(retJson);
			JSONObject object = (JSONObject)obj;
			ArrayList<String> friendRequests = (ArrayList<String>)object.get("friendRequests");
			String html =  "<form action=\"/social/acceptRequest\" method=\"post\"> ";
			if(friendRequests.size() == 0)
				return "You Have No Friend Requests.";
			for(int i = 0; i < friendRequests.size(); i++)
				html += "<input type=\"submit\" name = \"email\" value=\"" + friendRequests.get(i)	+"\">" + "<br>";
			html += "</form>";
			return html;
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
		return "";

	}
	
	/**
	 * action function to handle the accepted friend Requests
	 * 
	 * @param email
	 *            the email the currently logged in user pressed its button
	 * 
	 * @return an html page created dynamicaly
	 */
	
	
	@POST
	@Path("/acceptRequest")
	@Produces("text/html")
	public String acceptRequest(@FormParam("email") String email) {
		
		String ret = Connection.connect(
				"http://1-dot-sweii-socialnetwork.appspot.com/rest/acceptRequestService","currentUserEmail=" + currentActiveUser.getEmail()+"&friendRequestEmail=" + email,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		
		
		
		return ret;

	}
	
	
	/**
	 * action function to show all the friends of a user
	 * dynamically create html page to accomodate the variable number and emails of the 
	 * friends of the user
	 * 
	 * 
	 * @return an html page created dynamicaly
	 */
	
	@GET
	@Path("/showFriends")
	@Produces("text/html")
	public String showFriends() {
		if(currentActiveUser == null)
			return "you are not logged in.";
		String retJson = Connection.connect(
				"http://1-dot-sweii-socialnetwork.appspot.com/rest/showFriendsService","currentUserEmail=" + currentActiveUser.getEmail(),
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
	 * handle the log out function
	 * 
	 * 
	 * @return String indicating the success of logging out process
	 */
	
	@GET
	@Path("/logOut")
	@Produces("text/html")
	public String logOut() {
		
		currentActiveUser = null;
		requestedUser = null;
		
		return "You have logged out successfully";
	}



}