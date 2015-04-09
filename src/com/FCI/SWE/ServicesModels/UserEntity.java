package com.FCI.SWE.ServicesModels;

import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.FCI.SWE.Controller.UserController;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

/**
 * <h1>User Entity class</h1>
 * <p>
 * This class will act as a model for user, it will holds user data
 * </p>
 *
 * @author Mohamed Samir
 * @version 1.0
 * @since 2014-02-12
 */
public class UserEntity {
	private String name;
	private String email;
	private String password;
	
    private long id;
	

	/**
	 * Constructor accepts user data
	 * 
	 * @param name
	 *            user name
	 * @param email
	 *            user email
	 * @param password
	 *            user provided password
	 */
	
	public UserEntity(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;

	}
	
	public UserEntity(String name, String email, String password, long id, ArrayList<String>friends, ArrayList<String>friendRequests) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.id = id;
		
}
	
	private void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPass() {
		return password;
	}
	
	
	
	/**
	 * 
	 * This static method will form UserEntity class using user name and
	 * password This method will serach for user in datastore
	 * 
	 * @param name
	 *            user name
	 * @param pass
	 *            user password
	 * @return Constructed user entity
	 */

	public static UserEntity getUser(String email, String pass) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("users");
		
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if (entity.getProperty("email").toString().equals(email) && entity.getProperty("password").toString().equals(pass)) {
				UserEntity returnedUser = new UserEntity(entity.getProperty("name").toString(), entity.getProperty("email")
						.toString(), entity.getProperty("password").toString());
				
				
				returnedUser.setId(entity.getKey().getId());
				return returnedUser;
			}
		}

		return null;
	}

	/**
	 * This method will be used to save user object in datastore
	 * 
	 * @return boolean if user is saved correctly or not
	 */
	public Boolean saveUser() {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("users");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
		
		
		try {
		Entity newUser = new Entity("users", list.size() + 1);
	
     	newUser.setProperty("name", this.name);
		newUser.setProperty("email", this.email);
		newUser.setProperty("password", this.password);
	
		
		
		datastore.put(newUser);
		txn.commit();
		}finally{
			if (txn.isActive()) {
		        txn.rollback();
		    }
		}
		return true;

	}
	
	public static UserEntity getUser(String email) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();


		Query gaeQuery = new Query("users");

		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if ( entity.getProperty("email").toString().equals(email) ) {
				UserEntity returnedUser = new UserEntity(entity.getProperty("name").toString(), entity.getProperty("email")
						.toString(), entity.getProperty("password").toString());
				
                 
				returnedUser.setId(entity.getKey().getId());
				return returnedUser;
			}
		}

		return null;
	}
	

	/**
	 * This method will be used to add friend  datastore
	 * 
	 * @return boolean if add friend saved correctly or not
	 */
	public static Boolean addFriend(String currentUserEmail, String requestedUserEmail) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("Friends");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
		
		
		
		
		try {

		
		Entity newUser = new Entity("Friends", list.size() + 1);
	
	    newUser.setProperty("User email",  currentUserEmail);
		newUser.setProperty("Friend email",  requestedUserEmail);
		newUser.setProperty("status", "0");
		
		datastore.put(newUser);
		txn.commit();
		}finally{
			if (txn.isActive()) {
		        txn.rollback();
		    }
		}
		return true;

	}


	/**
	 * This method will be used to send message to friend  datastore
	 * 
	 * @return boolean if send message saved correctly or not
	 */
	public static Boolean sendMessage(String currentUserEmail, String requestedUserEmail , String Message) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("Messages");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
		
		
		
		try {

		Entity entity = new Entity("Messages", list.size() + 1);


		
		entity.setProperty("Sender email",  currentUserEmail);
		entity.setProperty("User email",  requestedUserEmail);
		entity.setProperty("Message", Message);
		entity.setProperty("status", "not seen");
	
		datastore.put(entity);
		txn.commit();
		}finally{
			if (txn.isActive()) {
		        txn.rollback();
		    }
		}
		return true;

	}


	/**
	 * This method will be used to accept friend request  
	 * 
	 * @return boolean if accept friend request   saved correctly or not
	 */
	public static boolean  acceptRequest(String currentUserEmail, String friendRequestEmail){
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("Friends");
		
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if ( entity.getProperty("User email").toString().equals(friendRequestEmail ) &&
					entity.getProperty("Friend email").toString().equals(currentUserEmail )	) {
		
     		entity.setProperty("status", "1" );
				datastore.put(entity);
				
			           
				return true;
			}
		}

		return false;
		
	

	}

	/**
	 * This method will be used to show all friend request  
	 * 
	 * @return ArrayList<String> of friend requests
	 */
public static ArrayList<String>  FriendRequests(String currentUserEmail){
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("Friends");
		 ArrayList<String> FriendRequests = new ArrayList<String>();
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if ( entity.getProperty("Friend email").toString().equals(currentUserEmail) &&
					entity.getProperty("status").toString().equals("0")	) {
		
		
      		FriendRequests.add( entity.getProperty("User email").toString());
                      
			}
		}

		return FriendRequests;
		
	

	}


public static ArrayList<String>  showFriends(String currentUserEmail){
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("Friends");
		 ArrayList<String> Friends = new ArrayList<String>();
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if ( (entity.getProperty("User email").toString().equals(currentUserEmail) ||
					entity.getProperty("Friend email").toString().equals(currentUserEmail) 
					) &&entity.getProperty("status").toString().equals("1")	) {
		
		         if (entity.getProperty("User email").toString().equals(currentUserEmail) )
				Friends.add( entity.getProperty("Friend email").toString());
		         else if (entity.getProperty("Friend email").toString().equals(currentUserEmail) )
						Friends.add( entity.getProperty("User email").toString());
		                      
			}
		}

		return Friends;
		
	

	}


/**
 * This method will be used to show messages   
 * 
 * @return ArrayList<String> of all messages
 */
public static ArrayList<String>  showMessages(String currentUserEmail){
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("Messages");
		 ArrayList<String> Messages = new ArrayList<String>();
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if ( entity.getProperty("User email").toString().equals(currentUserEmail) &&
					entity.getProperty("status").toString().equals("not seen")	) {
		
		
				Messages.add( entity.getProperty("Sender email").toString());
				Messages.add( entity.getProperty("Message").toString());
				entity.setProperty("status", "seen" );
				

				datastore.put(entity);
			}
		}
		

		return Messages;
		
	

	}



/**
 * This method will be used to  show all group messages    
 * 
 * @return ArrayList<String> of  all group messages 
 */
public static ArrayList<String>  showMessagesGroup(String currentUserEmail){
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		ArrayList <String> members =  UserController.messageMembers ;
		ArrayList <String> status =  new  ArrayList <String> ();
		ArrayList <String> messages  =  new  ArrayList <String> ();;
		Query gaeQuery = new Query("Group Message");
		PreparedQuery pq = datastore.prepare(gaeQuery);
	    for (Entity entity : pq.asIterable()) {
	    	ArrayList <String> membersDB  = (ArrayList <String> )entity.getProperty("Members") ;
	    	ArrayList <String> messagesDB  = (ArrayList <String> )entity.getProperty("Message") ;

    	    status = (ArrayList <String> )entity.getProperty("status") ;

    	    for ( int i  = membersDB.indexOf(currentUserEmail) , j = 0 ;i <  status.size(); j += 2 , i +=  members.size() )
    	    {
               if ( membersDB.indexOf(currentUserEmail) != -1 && status.get(i).equals("not seen") )
               {

				  messages.add(messagesDB.get(j)) ;
				  messages.add(messagesDB.get(j+1)) ;
					
            	  status.set( i ,  "seen");
			
				  entity.setProperty("status", status);
					

			   }	
	      }

			  datastore.put(entity);
	    }
	    
	    
		return messages;
		
			


	}


/**
 * This method will be used to  show all group messages    
 * 
 * @return Boolean of  all group messages 
 */
public static Boolean saveMessageGroup( String member) {
	ArrayList <String> messages = new ArrayList <String>();
	ArrayList <String> members =  UserController.messageMembers ;
	ArrayList <String> status = new ArrayList <String>() ;
	

	DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	

	Query gaeQuery = new Query("Group Message");
	PreparedQuery pq = datastore.prepare(gaeQuery);
	List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());

	Entity group = new Entity("Group Message", list.size() + 1);
	 	
	messages.add("");
	status.add("");
		
	//group.setProperty("Message", messages);
	group.setProperty("Message", messages);
	group.setProperty("Members", members);

	group.setProperty("status", status);
			datastore.put(group);
	
		return true;

}

/**
 * This method will be used to send message group  
 * 
 * @return boolean if send message to group    saved correctly or not
 */

public static Boolean sendMessageGroup( String currentUserEmail ,String message) {
	

	ArrayList <String> members =  UserController.messageMembers ;
	ArrayList <String> status =  new  ArrayList <String> ();

	ArrayList <String> messages =  new  ArrayList <String> ();


    DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	Query gaeQuery = new Query("Group Message");
	PreparedQuery pq = datastore.prepare(gaeQuery);
   

    for (Entity entity : pq.asIterable()) {
    	ArrayList <String> membersDB  = (ArrayList <String> )entity.getProperty("Members") ;
    	int checkExist = 0 ;
    	if ( membersDB.size() != members.size() )
    		continue ;
    	else
    	{
    		 for ( int i = 0 ;i <  members.size() ; i ++ )
    			 if ( !members.get(i).equals(membersDB.get(i)))
    				 checkExist = 1 ;
    		
    	}
    	
		if (  checkExist == 0 )	 {
			status = (ArrayList <String> )entity.getProperty("status") ;
			messages  = (ArrayList <String> )entity.getProperty("Message") ;
			
			if (status.get(0).equals(""))
				status = new ArrayList <String> ();

			if (messages.get(0).equals(""))
				messages = new ArrayList <String> ();
			
			for ( int i  = 0 ;i < members.size(); i ++ )
				status.add("not seen");
	

		    for ( int i  = 0 ;i <  status.size(); i +=  members.size() )
			status.set( (members.indexOf(currentUserEmail) )+ i,  "seen");

			 messages.add(currentUserEmail);
	    	 messages.add(message);

					 
			 entity.setProperty("Message" , messages);
			 entity.setProperty("status", status);
		 	
             
			datastore.put(entity);
			return true;

		}
	}

	return false;

}
}
