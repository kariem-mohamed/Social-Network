package Builder;

public class UserPostBuilder implements PostBuilder{
	

    public Post post ;
    
    public UserPostBuilder(){
    	
    	this.post = new Post();
    }
	@Override
	public void createPrivacy(String privacy) {
		post.setPrivacy(privacy);
	}

	@Override
	public void createName(String name) {
		post.setName(name);
	}

	@Override
	public void createLikes(String likes) {
		post.setLikes(likes);
	}

	@Override
    public void createPostContent(String postContent) {
		post.setPostContent(postContent);
	}
	public Post getPost(){
		return this.post ;
		
	}

}
