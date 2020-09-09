package lifr.logic.firstorder;

import java.util.Hashtable;

public class LogicFactoryTerms {
	    public LogicFactoryTerms() {
	    	this.VARIABLES = new Hashtable<String, Variable>(100);
	    	this.CONSTANTS = new Hashtable<String, Constant>(20);
	    	this.FUNCTIONS = new Hashtable<String, Function>(200);
	    	this.PREDICATES = new Hashtable<String, Predicate>(100);
		}
	    	
	    public Hashtable<String, Variable> getVariables(){
	    	return VARIABLES;
	    }
	    public Hashtable<String, Constant> getConstants(){
	    	return CONSTANTS;
	    }
	    public Hashtable<String, Function> getFunctions(){
	    	return FUNCTIONS;
	    }
	    public Hashtable<String, Predicate> getPredicates(){
	    	return PREDICATES;
	    }
	    	
	    public void setVariables(Hashtable<String, Variable> vars){
	    	this.VARIABLES = vars;
	    }
	    public void setConstants(Hashtable<String, Constant> cons){
	    	this.CONSTANTS = cons;
	    }
	    public void setFunctions(Hashtable<String, Function> funcs){
	    	this.FUNCTIONS = funcs;
	    }
	    public void setPredicates(Hashtable<String, Predicate> preds){
	    	this.PREDICATES = preds;
	    }
	    
	    public Hashtable<String, Variable> VARIABLES;
	    public Hashtable<String, Constant> CONSTANTS;
	    public Hashtable<String, Function> FUNCTIONS;
	    public Hashtable<String, Predicate> PREDICATES;
}
