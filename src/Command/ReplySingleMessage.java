package Command;

public class ReplySingleMessage implements  Command{


	/**
	 * Action function to execute the choosen command
	 * 
	 * 
	 * @return String path
	 */
	 public String execute(Receiver receiver) {
		 String path = "";
		 path= receiver.replySingleMessage();
		 return path ;
	 } 
	 
}
