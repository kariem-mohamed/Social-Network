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
	
    private static long id;
	

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
	
	public UserEntity( String email, String password) {
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
				UserEntity returnedUser = new UserEntity( entity.getProperty("email")
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
   public static int getIdPost(){

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("posts");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
	   
		return list.size() + 1;
   }
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
				UserEntity returnedUser = new UserEntity( entity.getProperty("email")
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

public static Boolean post( String currentUserEmail ,String privacy  ,String post ) {
	
	DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	Transaction txn = datastore.beginTransaction();
	Query gaeQuery = new Query("posts");
	PreparedQuery pq = datastore.prepare(gaeQuery);
	List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
	
	try {
	Entity Userpost = new Entity("posts", list.size() + 1);

	Userpost.setProperty("name", currentUserEmail );
	Userpost.setProperty("privacy", privacy);
	Userpost.setProperty("post",post);
	Userpost.setProperty("likes","0");
	Userpost.setProperty("idPost",list.size() + 1 );
	//id = list.size() + 1 ;
	
    datastore.put(Userpost);
	txn.commit();
	}finally{
		if (txn.isActive()) {
	        txn.rollback();
	    }
	}
		
	
	return true;
 }


public static Boolean hashTag(String post ) {

		String[] hashTag = post.split(" ");

		DatastoreService datastore2 = DatastoreServiceFactory
				.getDatastoreService();
		Query gaeQuery2 = new Query("posts");
		PreparedQuery pq2 = datastore2.prepare(gaeQuery2);
		List<Entity> list2 = pq2.asList(FetchOptions.Builder.withDefaults());
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("Hash");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
		
		try {
		Entity Userpost = new Entity("Hash", list.size() + 1);

		for ( int i = 0 ; i < hashTag.length ; i ++ ){
		
			 if ( hashTag[i].charAt(0) == '#' ){

			Userpost.setProperty("post", post );
			Userpost.setProperty("Hash Tag", hashTag[i] );
			Userpost.setProperty("idPost", list2.size()+1 );
		 
		     datastore.put(Userpost);
			 }
		}
		txn.commit();
		}finally{
			if (txn.isActive()) {
		        txn.rollback();
		    }
		}
			
		
		return true;

}

public static ArrayList<String>  showPosts(String email , String type ){
	DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
 	
	Query gaeQuery = new Query("posts");
	 ArrayList<String> posts = new ArrayList<String>();
	PreparedQuery pq = datastore.prepare(gaeQuery);
	

	for (Entity entity : pq.asIterable()) {

		 if (  type.equals("1") )
		 {
		if ( entity.getProperty("name").toString().equals(email)  ) {

			posts.add( entity.getProperty("likes").toString());
			posts.add( entity.getProperty("post").toString());
			
		
				datastore.put(entity);
		 }
		}
		 else
		 {
				if ( entity.getProperty("name").toString().equals(email) &&
						entity.getProperty("privacy").toString().equals("public")  ) {

					posts.add( entity.getProperty("likes").toString());
					posts.add( entity.getProperty("post").toString());
					
						datastore.put(entity);
				 }
			 
		 }	 
	}


	return posts;
	
	}


public static boolean  likePost(String email , String post ){
	DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
 	
	Query gaeQuery = new Query("posts");

	PreparedQuery pq = datastore.prepare(gaeQuery);
   
	for (Entity entity : pq.asIterable()) {
		
		

		if ( entity.getProperty("name").toString().equals(email)  &&
				entity.getProperty("post").toString().equals(post)  ) {

			String likes = entity.getProperty("likes").toString();
			
			 int numLikes =  Integer.parseInt(likes) + 1;
			 
			 entity.setProperty("likes",Integer.toString( numLikes));
				
				datastore.put(entity);
				

				return true;
		 }	
	}
	

	return false ;
	}

public static Boolean createPage( String pageName , String currentUserEmail  ) {
	
	DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	Transaction txn = datastore.beginTransaction();
	Query gaeQuery = new Query("pages");
	PreparedQuery pq = datastore.prepare(gaeQuery);
	List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
	
	try {
	Entity Userpost = new Entity("pages", list.size() + 1);

	Userpost.setProperty("page name", pageName );
	Userpost.setProperty("owner email", currentUserEmail);
	Userpost.setProperty("likes","0");

	Userpost.setProperty("number of seen","0");
	
	datastore.put(Userpost);
	txn.commit();
	}finally{
		if (txn.isActive()) {
	        txn.rollback();
	    }
	}
		
	
	return true;
 }



public static  ArrayList <ArrayList<String>>  getHashPost(String hash ){
	DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
 	
	Query gaeQuery = new Query("Hash");
	 ArrayList <ArrayList<String>> posts = new  ArrayList <ArrayList<String>>();
	PreparedQuery pq = datastore.prepare(gaeQuery);
	

	for (Entity entity : pq.asIterable()) 
		if ( entity.getProperty("Hash Tag").toString().equals(hash)  ) {
			  	
			posts.add(showHashPosts(entity.getProperty("idPost").toString()));
		
		}
	
	return posts;
	
	}


public static ArrayList<String>  showHashPosts(String idPost  ){
	DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
 	
	Query gaeQuery = new Query("posts");
	 ArrayList<String> posts = new ArrayList<String>();
	PreparedQuery pq = datastore.prepare(gaeQuery);
	

	for (Entity entity : pq.asIterable()) {

		if ( entity.getProperty("idPost").toString().equals(idPost)  ) {

			posts.add( entity.getProperty("name").toString());
			posts.add( entity.getProperty("likes").toString());
			posts.add( entity.getProperty("post").toString());

		
		 }
	}


	return posts;
	
	}



public static ArrayList<String>  getPage(String page  ){
	DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
 	
	Query gaeQuery = new Query("pages");
	 ArrayList<String> pageInfo = new ArrayList<String>();
	PreparedQuery pq = datastore.prepare(gaeQuery);
	

	System.out.println("ent "+page);
	for (Entity entity : pq.asIterable()) {

		if ( entity.getProperty("page name").toString().equals(page)  ) {

			pageInfo.add( entity.getProperty("page name").toString());
			pageInfo.add( entity.getProperty("likes").toString());
			pageInfo.add( entity.getProperty("number of seen").toString());
            

			
			String numSeen = entity.getProperty("number of seen").toString();

			
			 int newnNumSeen =  Integer.parseInt(numSeen) + 1;
			 
			 entity.setProperty("number of seen",Integer.toString( newnNumSeen));
				
				datastore.put(entity);
				
			return pageInfo;
		 }
	}


	  return null ;
	
	}

public static ArrayList<String>  showPostPage(String page  ){
	DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
 	
	Query gaeQuery = new Query("posts");
	 ArrayList<String> posts = new ArrayList<String>();
	PreparedQuery pq = datastore.prepare(gaeQuery);
	
	
	for (Entity entity : pq.asIterable()) {

		if ( entity.getProperty("name").toString().equals(page)  ) {

			posts.add( entity.getProperty("likes").toString());
			posts.add( entity.getProperty("post").toString());

		
		 }
	}


	return posts;
	
	}


}
