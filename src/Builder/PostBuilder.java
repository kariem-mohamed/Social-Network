package Builder;

public interface PostBuilder {
 
	public void createPrivacy (String privacy);

	public void createName (String name);

	public void createLikes (String likes);

    public void createPostContent(String postContent);

	public Post getPost();
}
