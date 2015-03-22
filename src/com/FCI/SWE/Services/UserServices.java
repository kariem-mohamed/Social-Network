package com.FCI.SWE.Services;

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

import com.FCI.SWE.ServicesModels.UserEntity;

/**
 * This class contains REST services, also contains action function for web
 * application
 * 
 * @author Kariem Mohamed
 * @version 1.3
 * @since 2015-02-23
 *
 */
@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class UserServices {
	
	
	

		/**
	 * Registration Rest service, this service will be called to make
	 * registration. This function will store user data in data store
	 * 
	 * @param uname
	 *            provided user name
	 * @param email
	 *            provided user email
	 * @param pass
	 *            provided password
	 * @return Status json
	 */
	@POST
	@Path("/RegistrationService")
	public String registrationService(@FormParam("uname") String uname,
			@FormParam("email") String email, @FormParam("password") String pass) {
		UserEntity user = new UserEntity(uname, email, pass);
		
		user.saveUser();
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}
	
	@POST
	@Path("/SearchService")
	public String searchService(@FormParam("email") String email) {
		
		JSONObject object = new JSONObject();
		UserEntity reqUser = UserEntity.getUser(email);
		if(reqUser == null)
			object.put("Status","Failed" );
		else{
			object.put("Status", "OK");
			object.put("name", reqUser.getName());
			object.put("email", reqUser.getEmail());
			object.put("password", reqUser.getPass());
			object.put("id", reqUser.getId());
			object.put("friendRequests", reqUser.getFriendRequests());
			object.put("friends", reqUser.getFriends());
		}
		return object.toString();
	}

	/**
	 * Login Rest Service, this service will be called to make login process
	 * also will check user data and returns new user from datastore
	 * @param uname provided user name
	 * @param pass provided user password
	 * @return user in json format
	 */
	@POST
	@Path("/LoginService")
	public String loginService(@FormParam("uname") String uname,
			@FormParam("password") String pass) {
		JSONObject object = new JSONObject();
		UserEntity user = UserEntity.getUser(uname, pass);
		if (user == null) {
			object.put("Status", "Failed");

		} else {
			object.put("Status", "OK");
			object.put("name", user.getName());
			object.put("email", user.getEmail());
			object.put("password", user.getPass());
			object.put("id", user.getId());
			object.put("friendRequests", user.getFriendRequests());
			object.put("friends", user.getFriends());

		}
		return object.toString();

	}
	
	
	/**
	 * this rest service allow the user to send friend request to the specified user 
	 * and update the data store appropriately
	 * 
	 * 
	 * @param currentUserEmail 
	 *                 currently logged in email
	 * @param requestedUserEmail
	 *                 the email of the user the currently logged in user sent friend request to 
	 * @return status in json format
	 */
	
	@POST
	@Path("/sendFriendRequestService")
	public String sendFriendRequestService(@FormParam("currentUserEmail") String currentUserEmail,
			@FormParam("requestedUserEmail") String requestedUserEmail) {
		JSONObject object = new JSONObject();
		UserEntity.updateUser(currentUserEmail, requestedUserEmail);
		
		
		return object.toString();

	}
	
	/**
	 * this rest service allow the user to show all the friends requests
	 * other users sent to him
	 * 
	 * 
	 * @param currentUserEmail 
	 *                 currently logged in email
	 * 
	 * @return status in json format
	 */
	
	
	
	@POST
	@Path("/showFriendRequestService")
	public String showFriendRequestService(@FormParam("currentUserEmail") String currentUserEmail) {
		JSONObject object = new JSONObject();
		
		UserEntity e = UserEntity.getUser(currentUserEmail);
		
		
		object.put("friendRequests",e.getFriendRequests());
		
		
		
		return object.toString();

	}
	
	/**
	 * this rest service allow the user to show all his friends emails
	 *
	 * 
	 * 
	 * @param currentUserEmail 
	 *                 currently logged in email
	 * 
	 * @return status in json format
	 */
	
	
	
	@POST
	@Path("/showFriendsService")
	public String showFriendsService(@FormParam("currentUserEmail") String currentUserEmail) {
		JSONObject object = new JSONObject();
		
		UserEntity e = UserEntity.getUser(currentUserEmail);
		
		
		object.put("friends",e.getFriends());
		
		
		
		return object.toString();

	}

	/**
	 * this rest service allow the user to accept a specified friend request by
	 * clicking on the desired email
	 *
	 * 
	 * 
	 * @param currentUserEmail 
	 *                 currently logged in email
	 *                 
	 * @param friendRequestEmail
	 *                 the email the user accept to be friends with
	 * 
	 * @return a String confirming their friendship
	 */
	
	
	
	
	@POST
	@Path("/acceptRequestService")
	public String acceptRequestService(@FormParam("currentUserEmail") String currentUserEmail, @FormParam("friendRequestEmail") String friendRequestEmail){
		UserEntity.acceptRequest(currentUserEmail, friendRequestEmail);
		return "You Are Now Friend With " + friendRequestEmail;
	}

}