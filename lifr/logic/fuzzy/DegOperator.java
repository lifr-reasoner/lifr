/**
 * @(#)DegOperator.java
 *
 *
 * @author 
 * @version 1.00 2007/12/10
 */

package lifr.logic.fuzzy;

import java.lang.Math;

public class DegOperator {
	
	private String name;
	private int value;

    private DegOperator(String name) {
    	this.name = name;
    	if (name.equalsIgnoreCase("<")) this.value = 0;
    	else if (name.equalsIgnoreCase("<=")) this.value = 1;
    	else if (name.equalsIgnoreCase(">")) this.value = 2;
    	else if (name.equalsIgnoreCase(">=")) this.value = 3;
    }
    
    public String toString(){
        return name;
    }
    
    public int getValue(){
 		return this.value;
 	}
 	
 	
 	private static final DegOperator getDegOp(int value){
 		if (value == 0) return DegOperator.SMALLER;
    	else if (value == 1) return DegOperator.SMALLER_EQUAL;
    	else if (value == 2) return DegOperator.GREATER;
    	else //if (value == 3) 
    		return DegOperator.GREATER_EQUAL;
 	}
 	
 	public DegOperator invertOperator(int value){
    	if (value > 1) return getDegOp(Math.abs(value - 2));
    	else return getDegOp(value + 2);
    }
    
    public static final DegOperator GREATER = new DegOperator(">");
    
    public static final DegOperator SMALLER = new DegOperator("<");
    
    public static final DegOperator GREATER_EQUAL = new DegOperator(">=");
    
    public static final DegOperator SMALLER_EQUAL = new DegOperator("<=");
        
    public static DegOperator minOperator(DegOperator d1, DegOperator d2){
    	/*
    	DegOperator degop = null;
		
    	if ((d1 == DegOperator.GREATER)&&(d2 == DegOperator.GREATER_EQUAL))
    		degop = d1;
    	else if ((d1 == DegOperator.GREATER_EQUAL)&&(d2 == DegOperator.GREATER))
    			degop = d2;
    	else if ((d1 == DegOperator.SMALLER)&&(d2 == DegOperator.SMALLER_EQUAL))
    		degop = d1;
    	else if ((d1 == DegOperator.SMALLER_EQUAL)&&(d2 == DegOperator.SMALLER))
    		degop = d2;
    	else 
    		//System.out.println("REFUTATION!!\nin DegOperator.minOperator");
    		degop = d1;
    	
    	return degop;
    	*/
    	return getDegOp(Math.min(d1.getValue(), d2.getValue()));
    }
    
    public static DegOperator maxOperator(DegOperator d1, DegOperator d2){
    	/*
    	DegOperator degop = null;
    	
    	if ((d1 == DegOperator.GREATER)&&(d2 == DegOperator.GREATER_EQUAL))
    		degop = d2;
    	else if ((d1 == DegOperator.GREATER_EQUAL)&&(d2 == DegOperator.GREATER))
    			degop = d1;
    	else if ((d1 == DegOperator.SMALLER)&&(d2 == DegOperator.SMALLER_EQUAL))
    		degop = d2;
    	else if ((d1 == DegOperator.SMALLER_EQUAL)&&(d2 == DegOperator.SMALLER))
    			degop = d1;
    	else 
    		//System.out.println("REFUTATION!!\nin DegOperator.maxOperator");
    		degop = d1;
    	
    	
    	return degop;
    	*/
    	return getDegOp(Math.max(d1.getValue(), d2.getValue()));
    }
    
    public static boolean sameOperator(DegOperator d1, DegOperator d2){
    	
    	if ((d1.getValue() > 1) && (d2.getValue() > 1))
    		//((((d1 == DegOperator.GREATER_EQUAL)||(d1 == DegOperator.GREATER))&&((d2 == DegOperator.GREATER_EQUAL)||(d2 == DegOperator.GREATER))))
    		return true;
    	else if ((d1.getValue() < 2) && (d2.getValue() < 2))
    		//((((d1 == DegOperator.SMALLER_EQUAL)||(d1 == DegOperator.SMALLER))&&((d2 == DegOperator.SMALLER_EQUAL)||(d2 == DegOperator.SMALLER))))
    		return true;
    	else return false;
    	
    }
    
    
}