
package Command;
public class Invoker {
    

	/**
	 * Action function to execute the command
	 * 
	 * 
	 * @return String 
	 */
	public String execute(Command command , Receiver receiver ){
	   String path = "";
	   path = command.execute(receiver);
	 
	    return path ;
	}
}