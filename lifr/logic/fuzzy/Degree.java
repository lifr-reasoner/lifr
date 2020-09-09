/**
 * @(#)Degree.java
 *
 *
 * @author 
 * @version 1.00 2007/12/7
 */

package lifr.logic.fuzzy;

//import java.util.Vector;

public class Degree {
	
	private DegOperator oper1;
	private DegOperator oper2;
	private double deg1;
	private double deg2;
	private DegOperator origOper1;
	private double origDeg1;
	private DegOperator origOper2;
	private double origDeg2;

    public Degree(String operator, double deg) {
    	this.oper1 = createOperator(operator);
    	this.deg1 = deg;
    	this.origOper1 = createOperator(operator);
    	this.origDeg1 = deg;
    }
    
    public Degree(DegOperator operator, double deg) {
    	this.oper1 = operator;
    	this.deg1 = deg;
    	this.origOper1 = operator;
    	this.origDeg1 = deg;
    }
    
    public Degree(Degree degree) {
    	new Degree(degree, Weight.defaultWeight);
    }
    
    public Degree(Degree degree, Weight weight) {
    	if (degree.isInterval()){
    		new Degree(degree.getLowerBound(), degree.getUpperBound(), weight);
    	}else{
    		this.oper1 = degree.oper1;
    		this.deg1 = degree.deg1*weight.getWeight();
    		this.origOper1 = degree.oper1;
    		this.origDeg1 = degree.deg1;
    	}
    }
    
    public Degree(Degree d1, Degree d2) {
    	new Degree(d1, d2, Weight.defaultWeight);
    }
    
    public Degree(Degree d1, Degree d2, Weight weight) {
    	if ((d1.getOperator() == DegOperator.SMALLER)||(d1.getOperator() == DegOperator.SMALLER_EQUAL)) {
    		this.oper1 = d1.getOperator();
    		this.oper2 = d2.getOperator();
    		this.deg1 = d1.getDegree()*weight.getWeight();
    		this.deg2 = d2.getDegree()*weight.getWeight();
    		this.origOper1 = d1.getOperator();
    		this.origOper2 = d2.getOperator();
        	this.origDeg1 = d1.getDegree();
        	this.origDeg2 = d2.getDegree();
    	}else{
    		this.oper1 = d2.getOperator();
    		this.oper2 = d1.getOperator();
    		this.deg1 = d2.getDegree()*weight.getWeight();
    		this.deg2 = d1.getDegree()*weight.getWeight();
    		this.origOper1 = d2.getOperator();
    		this.origOper2 = d1.getOperator();
        	this.origDeg1 = d2.getDegree();
        	this.origDeg2 = d1.getDegree();
    	}
    }
    
    public static final Degree EmptyDegree = new Degree(DegOperator.SMALLER, -1);
    public static final Degree complementDegree = new Degree(DegOperator.GREATER, 0.5);
    public static final Degree relationDegree = new Degree(DegOperator.GREATER_EQUAL, 1);
    public static final Degree complementNegDegree = new Degree(DegOperator.SMALLER, 0.5);
    
    private DegOperator createOperator(String operator){
    	DegOperator oper = null;
    	
    	if (operator.equalsIgnoreCase(">")){
    		oper = DegOperator.GREATER;
    	}else if (operator.equalsIgnoreCase("<")){
    		oper = DegOperator.SMALLER;
    	}else if (operator.equalsIgnoreCase(">=")){
    		oper = DegOperator.GREATER_EQUAL;
    	}else if (operator.equalsIgnoreCase("<=")){
    		oper = DegOperator.SMALLER_EQUAL;
    	}else System.err.println("Wrong operator");
    	
    	return oper;
    }
    
    public DegOperator invertOperator(){
    	DegOperator oper = null;
    	
    	if (this.oper1 == DegOperator.GREATER){
    		oper = DegOperator.SMALLER_EQUAL;
    	}else if (this.oper1 == DegOperator.SMALLER){
    		oper = DegOperator.GREATER_EQUAL;
    	}else if (this.oper1 == DegOperator.GREATER_EQUAL){
    		oper = DegOperator.SMALLER;
    	}else{
    		oper = DegOperator.GREATER;
    	}
    	
    	return oper;
    }
    
    public double invertDegree(){
    	return 1 - this.deg1;
    }
        
    public DegOperator getOperator(){
    	return this.oper1;
    }
    
    public void setOperator(DegOperator degop){
    	this.oper1 = degop;
    }
    
    public void setOperator(String degop){
    	this.oper1 = createOperator(degop);
    }
    
    public DegOperator getInvertOperator(){
    	return this.getOperator().invertOperator(this.oper1.getValue());
    }
    
    public double getDegree(){
    	return this.deg1;
    }
    
    public void setDegree(double degree){
    	this.deg1 = degree;
    }
    
    public Degree getLowerBound(){
    	return new Degree(this.oper1, this.deg1);
    }
    
    public Degree getUpperBound(){
    	return new Degree(this.oper2, this.deg2);
    }
        
    public boolean isInterval(){
    	if (oper2 != null) return true;
    	else return false;
    }
    
    public Degree retainDegree(){
    	return new Degree(this.origOper1, this.origDeg1);
    }
    
    public void resetToDefault(boolean unary){
    	if (unary){
    		this.oper1 = Degree.EmptyDegree.getOperator();
    		this.oper2 = null;
    		this.deg1 = Degree.EmptyDegree.getDegree();
    		this.deg2 = 0;
    	}else{
    		this.oper1 = Degree.relationDegree.getOperator();
    		this.oper2 = null;
    		this.deg1 = Degree.relationDegree.getDegree();
    		this.deg2 = 0;
    	}
    }
    
    public void resetDegree(){
    	this.oper1 = this.origOper1;
		this.deg1 = this.origDeg1;
		if (this.isInterval()){
			this.oper2 = this.origOper2;
			this.deg2 = this.origDeg2;
		}
    }
    
    public String toString(){
    	if (this.isInterval()){
    		return "|"+this.deg1+" "+this.oper1.toString()+" "+this.oper2.toString()+" "+this.deg2;
    	}else{
    		return this.oper1.toString()
    		+" "+
    		this.deg1;
    	}
    }
}