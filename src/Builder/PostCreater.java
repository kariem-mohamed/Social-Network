package Builder;

import com.FCI.SWE.Controller.Connection;
import com.FCI.SWE.Controller.UserController;

public class PostCreater {
	
	public PostBuilder postBuilder ;
	
	public PostCreater( PostBuilder postBuilder ){
		
		this.postBuilder = postBuilder ;
	}
	public Post getPost(){
		return this.postBuilder.getPost() ;
		
	}


	public void makePost(String privacy , String name , String likes , String postContent){

		this.postBuilder.createPrivacy(privacy);
		this.postBuilder.createName(name);
		this.postBuilder.createLikes(likes);
		this.postBuilder.createPostContent(postContent);
		

	       Connection.connect("http://localhost:8888/rest/hashService",  "post=" + postContent ,
					"POST", "application/x-www-form-urlencoded;charset=UTF-8");


		
		Connection.connect(
     "http://localhost:8888/rest/postService", "currentUserEmail=" + name   
     + "&privacy=" + privacy + "&post=" + postContent  ,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");

	 }
	
	

}
