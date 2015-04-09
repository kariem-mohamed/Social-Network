package Command;

public class AcceptFriendRequest implements  Command{

	/**
	 * Action function to execute the choosen command
	 * 
	 * 
	 * @return String path
	 */
	 public String execute(Receiver receiver) {
		 String path = "" ;
		 path = receiver.acceptFriendRequest();
		 return path ;
	 } 
	
}
