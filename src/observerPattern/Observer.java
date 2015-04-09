package observerPattern;

public abstract class Observer  {
	  protected EventSubject subj; 

	  public abstract String update(String userEmail);
}
