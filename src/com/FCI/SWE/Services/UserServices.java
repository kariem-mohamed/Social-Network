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

import com.FCI.SWE.Controller.UserController;
import com.FCI.SWE.ServicesModels.GroupEntity;
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
	public String loginService(@FormParam("email") String email,
			@FormParam("password") String pass) {
		JSONObject object = new JSONObject();
		UserEntity user = UserEntity.getUser(email, pass);
		if (user == null) {
			object.put("Status", "Failed");

		} else {
			object.put("Status", "OK");
			object.put("name", user.getName());
			object.put("email", user.getEmail());
			object.put("password", user.getPass());
			object.put("id", user.getId());
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
	@Path("/sendFriendRequestService")
	public String sendFriendRequestService(@FormParam("currentUserEmail") String currentUserEmail,
			@FormParam("requestedUserEmail") String requestedUserEmail) {
		JSONObject object = new JSONObject();
		UserEntity.addFriend(currentUserEmail, requestedUserEmail);
		
		
		return object.toString();

	}
	/**
	 * send message  Service, this service will be called to send message process
	 * 
	 * @param currentUserEmail provided user email
	 * @param requestedUserEmail provided receiver email
	 * @param Message provided message to receiver email
	 * @return status in json format
	 */
	@POST
	@Path("/sendMessageService")
	public String sendMessage(@FormParam("currentUserEmail") String currentUserEmail,
			@FormParam("friendEmail") String requestedUserEmail,
			@FormParam("Message") String Message) {
		JSONObject object = new JSONObject();

	
	
		UserEntity.sendMessage(currentUserEmail, requestedUserEmail,Message);
		
		object.put("status", "OK" );
		
		
		return object.toString();

	}

	/**
	 * show Friend Request Service, this service will be called to show all friend request
	 * 
	 * @param currentUserEmail provided user email
     *
	 * @return  all friend request in json format
	 */
	@POST
	@Path("/showFriendRequestService")
	public String showFriendRequestService(@FormParam("currentUserEmail") String currentUserEmail) {
		JSONObject object = new JSONObject();
		
		ArrayList<String> e = new ArrayList<String>(UserEntity.FriendRequests(currentUserEmail));
		
		
		object.put("friendRequests", e );
		
		
		
		return object.toString();

	}
	
	@POST
	@Path("/showFriendsService")
	public String showFriendsService(@FormParam("currentUserEmail") String currentUserEmail) {
		JSONObject object = new JSONObject();
	   
		ArrayList<String> e = new ArrayList<String>(UserEntity.showFriends(currentUserEmail));
		object.put("friendRequests", e );
		
		
		object.put("friends",e);
		
		
		
		return object.toString();

	}


	/**
	 * show messages  Service, this service will be called to show all messages 
	 * 
	 * @param currentUserEmail provided user email
     *
	 * @return  all messages in json format
	 */
	@POST
	@Path("/showMessagesService")
	public String showMessagesService(@FormParam("currentUserEmail") String currentUserEmail) {
		JSONObject object = new JSONObject();
	   
		ArrayList<String> e = new ArrayList<String>(UserEntity.showMessages(currentUserEmail));
		object.put("Messages", e );
		
		return object.toString();

	}
	
	@POST
	@Path("/showGroupMessagesService")
	public String showGroupMessagesService(@FormParam("currentUserEmail") String currentUserEmail) {
		JSONObject object = new JSONObject();
	   
		ArrayList<String> e = new ArrayList<String>(UserEntity.showMessagesGroup(currentUserEmail));
		object.put("Messages", e );
		
		return object.toString();

	}
	@POST
	@Path("/acceptRequestService")
	public String acceptRequestService(@FormParam("currentUserEmail") String currentUserEmail, @FormParam("friendRequestEmail") String friendRequestEmail){
		UserEntity.acceptRequest(currentUserEmail, friendRequestEmail);
		
	
		return "You Are Now Friend With " + friendRequestEmail;
	}
	
	@POST
	@Path("/createGroupMessageService")
	public String createGroupMessage(@FormParam("Members") String Member){
		
       UserEntity.saveMessageGroup(Member );
	 
			
		JSONObject object = new JSONObject();

		object.put("status", "OK" );
		
		return object.toString();	
	}
	


	/**
	 * send messages  Service, this service will be called to send messages to group 
	 * 
	 * @param currentUserEmail provided user email
     *
	 * @return  status in json format
	 */
	@POST
	@Path("/sendMessageGroupService")
	public String sendMessageGroupService( @FormParam("currentUserEmail") String currentUserEmail ,@FormParam("Message") String Message ) {

		JSONObject object = new JSONObject();
	
	
		
		if ( UserEntity.sendMessageGroup(currentUserEmail , Message) !=true )
			object.put("status", "Faild" );
			
			else
			object.put("status", "OK" );
			
		
		return object.toString();

	}
	
}