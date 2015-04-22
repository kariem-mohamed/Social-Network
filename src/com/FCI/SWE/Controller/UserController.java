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

	public static String userPage = ""; 
	public static UserEntity requestedUser = null; 
	public static ArrayList<String> messageMembers = new ArrayList<String>() ;
	public static String likedPost = "" ;
	
	/**
	 * Action function to render Signup page, this function will be executed
	 * using url like this /rest/signup
	 * 
	 * @return sign up page
	 */

	@GET
	@Path("/signup")
	public Response signUp() {
		
		return Response.ok(new Viewable("/jsp/register")).build();
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
	
	
	@POST
	@Path("/doSearch")
	public String usersList(@FormParam("email") String email){


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
				

				try {
					

                String retJson2   = Connection.connect(
						"http://localhost:8888/rest/showFriendsService","currentUserEmail=" + currentActiveUser.getEmail(),
						"POST", "application/x-www-form-urlencoded;charset=UTF-8");
				JSONParser parser2 = new JSONParser();
				Object obj2= parser2.parse(retJson2);

				JSONObject object2 = (JSONObject)obj2;
			


					 ArrayList<String> friends = (ArrayList<String>)object2.get("friends");
			   String type = "";



					if ( friends.indexOf(email) != -1 )
					
						type = "1";
					else
						type = "2";
					
						urlParameters = "email=" + email + "&type=" + type ; 


			   		String retJson3 = Connection.connect(
		   				"http://localhost:8888/rest/showPostService", urlParameters,
		   				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
					JSONParser parser3 = new JSONParser();
					Object obj3= parser3.parse(retJson3);
					JSONObject object3 = (JSONObject) obj3;
					

		   			ArrayList<String> posts = ( ArrayList<String>) object3.get("posts") ; 
		   			requestedUser = new UserEntity (email , "");
		   		  String html = "  <p>User INFO</p>" +
		           "<p>  <br> Email: " + email + "</p>"+
		"<a href='/social/sendFriendRequest'>Send Friend Request</a>"+
	   "<br><br>" ;

		   		
		   		html +=  "<form action='/social/sharePosts' method='post'>";
		   				
			     	for(int i = 0; i < posts.size(); i+=2){
			     		likedPost = posts.get(i+1)	; 
			     		
						html +="<a href='/social/likePost/'> Like </a><input type='submit' value='share' />  " + posts.get(i)	+ "  like  <br><br> <br><br> " +
						 "<textarea name='post' rows='4' cols='50' name='post'>"+ posts.get(i+1)	+
				 		"	</textarea> <br> ---------------------------------- <br> <br><br><br>";
			     	     
			     	}
			     	html += "</form>" ;
					
		           return html ;
				}

			   catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			  }
			}
		}catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}


		
		return null;
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
	public String home(@FormParam("email") String email,
			@FormParam("password") String pass ) {
		
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
				return "fail";
		 
			currentActiveUser = new  UserEntity(  email,  pass) ;
	
			urlParameters = "email=" + email + "&type=1" ; 

	   		String retJson2 = Connection.connect(
   				"http://localhost:8888/rest/showPostService", urlParameters,
   				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
			JSONParser parser2 = new JSONParser();
			Object obj2;
			
			obj2 = parser2.parse(retJson2);
			JSONObject object2 = (JSONObject) obj;
			
			object2 = (JSONObject) obj2;
				
   			ArrayList<String> posts = ( ArrayList<String>) object2.get("posts") ; 
   		

            String html = "<p> Welcome b2a ya " + email +"</p> " + 
            "<a href='/social/search/'>Search for a Friend</a><br>" +
"<a href='/social/showFriends'>Show all Friends<a><br>" + 
"<a href='/social/Notification'>Notification <a><br>"+
"<a href='/social/sendMessageFriends'>Send Message<a><br>"+
"<a href='/social/GroupMessaging'>Create Group Messaging<a><br>"+

"<a href='/social/OpenGroupMessaging'>Open Group Messaging<a><br>"+

"<a href='/social/createPage'>Create Page<a><br>"+

"<a href='/social/searchPage'>Search page<a><br>"+
"<a href='/social/pagePost'>Post with your page<a><br>"+
"<a href='/social/searchHashPosts'>show hash tag posts <a><br><br><br>"+
 "<form action='/social/userPost' method='post'>"+
		
      
  "    	<textarea rows='4' cols='50' name='post'>"+
		"	</textarea><br><br><br>"+
		"	Privacy : <select name = 'privacy'>"+
  		"		<option value = 'public'>Public</option>"+
  		"		<option value = 'private''>Private</option>"+
  		"	</select>"+
		"<input type='submit' value='post' />"+
	"</form>" ;
            	for(int i = 0; i < posts.size(); i+=2)
				html +=  "<p>" + posts.get(i)	+ "  like  <br> <br> "  + posts.get(i+1)	+"' </p>  <br> ---------------------------------- <br>";
		
			
           return html ;
           
           
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
	 //    new NotificationGroupMessage( sub );

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

	
}