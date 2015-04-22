package Builder;

public class Post implements PostElements{
 

	String postPrivacy ;
	String postName ;
	String postLikes;
	String postContent;

	@Override
	public void setPrivacy(String privacy) {


		postPrivacy = privacy ;
	}
	@Override
	public void setLikes(String likes) {

		postLikes = likes ;
	}
	@Override
	public void setName(String name) {

		postName = name ;
	}
	
	@Override
	public void setPostContent(String post) {

		postContent = post ;
	}
	
}
