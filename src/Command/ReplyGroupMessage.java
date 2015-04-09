package Command;

public class ReplyGroupMessage implements  Command{

	/**
	 * Action function to execute the choosen command
	 * 
	 * 
	 * @return String path
	 */
	 public String execute(Receiver receiver) {
       String path = "" ;
       path = receiver.replyGroupMessage();
       return path ;
	 } 
}
