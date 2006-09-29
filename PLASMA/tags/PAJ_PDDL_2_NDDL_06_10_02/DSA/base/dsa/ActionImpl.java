package dsa;

import java.util.*;

public class ActionImpl 
    extends TokenImpl 
    implements Action 
{
    public ActionImpl(String type, 
    		          int key, 
    		          int startLb, 
    		          int startUb, 
    		          int endLb, 
    		          int endUb, 
    		          int durationLb, 
    		          int durationUb)
    {
	    super(type, key, startLb, startUb, endLb, endUb, durationLb, durationUb);
    }

    public boolean hasViolations(){return getViolation() != 0;}

    public List<Action> getChildActions(){return null;}

    public List<Proposition> getConditions(){return null;}

    public List<Proposition> getEffects(){return null;}

    public List<Violation> getViolations(){return new ArrayList<Violation>();}

	public double getViolation() 
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
