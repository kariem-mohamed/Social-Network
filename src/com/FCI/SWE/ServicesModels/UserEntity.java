package com.FCI.SWE.ServicesModels;

import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
	private ArrayList<String> friendRequests = new ArrayList<String>();
	private ArrayList<String> friends = new ArrayList<String>();
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
		this.friends = new ArrayList<String>();
		this.friendRequests = new ArrayList<String>();
	}
	
	public UserEntity(String name, String email, String password, long id, ArrayList<String>friends, ArrayList<String>friendRequests) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.id = id;
		this.friends = new ArrayList<String>();
		this.friendRequests = new ArrayList<String>();
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
	
	public ArrayList<String> getFriendRequests(){
		return friendRequests;
	}
	
	public ArrayList<String> getFriends(){
		return friends;
	}
	public void setFriendRequests(Object friendRequests){
		this.friendRequests = (ArrayList<String>)friendRequests;
	}
	
	public void setFriends(Object friends){
		this.friends = (ArrayList<String>)friends;
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

	public static UserEntity getUser(String name, String pass) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("users");
		
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if (entity.getProperty("name").toString().equals(name) && entity.getProperty("password").toString().equals(pass)) {
				UserEntity returnedUser = new UserEntity(entity.getProperty("name").toString(), entity.getProperty("email")
						.toString(), entity.getProperty("password").toString());
				returnedUser.setFriendRequests(entity.getProperty("friendRequests"));
				returnedUser.setFriends(entity.getProperty("friends"));
				
				
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
		this.friendRequests = new ArrayList<String>();
		this.friends = new ArrayList<String>();
		newUser.setProperty("name", this.name);
		newUser.setProperty("email", this.email);
		newUser.setProperty("password", this.password);
		newUser.setUnindexedProperty("friendRequests", this.friendRequests);
		newUser.setUnindexedProperty("friends", this.friends);

		
		
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
				//entity.getProperty("friendRequests"));
				returnedUser.setFriendRequests(entity.getProperty("friendRequests"));
				returnedUser.setFriends(entity.getProperty("friends"));

				if(returnedUser.getFriendRequests() == null)
					returnedUser.setFriendRequests(new ArrayList<String>());
				if(returnedUser.getFriends() == null)
					returnedUser.setFriends(new ArrayList<String>());
				
				
				returnedUser.setId(entity.getKey().getId());
				return returnedUser;
			}
		}

		return null;
	}
	
	
	public static Boolean updateUser(String currentUserEmail, String requestedUserEmail) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("users");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		
		
		
		try {
		UserEntity updatedUser = UserEntity.getUser(requestedUserEmail);
		
		updatedUser.getFriendRequests().add(currentUserEmail);
		
		
		Entity updatedEntity = new Entity("users", updatedUser.id);
		updatedEntity.setProperty("name", updatedUser.name);
		updatedEntity.setProperty("email", updatedUser.email);
		updatedEntity.setProperty("password", updatedUser.password);
		updatedEntity.setProperty("id", updatedUser.id);
		updatedEntity.setUnindexedProperty("friendRequests", updatedUser.friendRequests);
		updatedEntity.setUnindexedProperty("friends", updatedUser.friends);		
		
		datastore.put(updatedEntity);
		txn.commit();
		}finally{
			if (txn.isActive()) {
		        txn.rollback();
		    }
		}
		return true;

	}
	public static void acceptRequest(String currentUserEmail, String friendRequestEmail){
		UserEntity currentUser = getUser(currentUserEmail);
		UserEntity friendUser = getUser(friendRequestEmail);
		
		for(int i = 0; i < currentUser.getFriendRequests().size(); i++){
			if(currentUser.getFriendRequests().get(i).equals(friendRequestEmail)){
				currentUser.getFriendRequests().remove(i);
				currentUser.getFriends().add(friendRequestEmail);
			}
		}
		
		friendUser.getFriends().add(currentUserEmail);
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("users");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		
		
		
		try {
		
		
		Entity updatedEntity = new Entity("users", currentUser.id);
		updatedEntity.setProperty("name", currentUser.name);
		updatedEntity.setProperty("email", currentUser.email);
		updatedEntity.setProperty("password", currentUser.password);
		updatedEntity.setProperty("id", currentUser.id);
		updatedEntity.setUnindexedProperty("friendRequests", currentUser.friendRequests);
		updatedEntity.setUnindexedProperty("friends", currentUser.friends);		
		
		datastore.put(updatedEntity);
		
		txn.commit();
		
		
		txn = datastore.beginTransaction();
		Entity updatedEntity2 = new Entity("users", friendUser.id);
		updatedEntity2.setProperty("name", friendUser.name);
		updatedEntity2.setProperty("email", friendUser.email);
		updatedEntity2.setProperty("password", friendUser.password);
		updatedEntity2.setProperty("id", friendUser.id);
		updatedEntity2.setUnindexedProperty("friendRequests", friendUser.friendRequests);
		updatedEntity2.setUnindexedProperty("friends", friendUser.friends);	
		
		datastore.put(updatedEntity2);
		txn.commit();
		}finally{
			if (txn.isActive()) {
		        txn.rollback();
		    }
		}
	}
}
