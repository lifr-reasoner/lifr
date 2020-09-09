package lifr.run;

import java.util.Enumeration;
import java.util.Vector;

import lifr.logic.firstorder.KnowledgeBase;
import lifr.logic.firstorder.Predicate;

public class MakeMatch {
	
	public static String matchPredicateText(KnowledgeBase inKB, String predicateName, boolean printTime, boolean printModel) {
		
		Object reasoningResult = Reason.reasoning(inKB, printTime, printModel);
		
		if(reasoningResult.getClass().isInstance(String.class)) {
			return (String) reasoningResult;
		}else if(reasoningResult.getClass().isInstance(Vector.class)) {
			String results = "";
			for(Enumeration<Predicate> iter = ((Vector<Predicate>) reasoningResult).elements(); iter.hasMoreElements();){
				Predicate pred = iter.nextElement();
				if(pred.name.equalsIgnoreCase(predicateName)){
//					System.out.println("RESULT: " + pred.toWeightandDegreeString());
					results += pred.toWeightandDegreeString() + ", ";
				}else return "Reasoning succesful, no match found for this KB.";
			}
			return results;
		}
		
		return "Some problem occured during matchmaking.";
	}
	
	public static boolean matchPredicate(KnowledgeBase inKB, String predicateName, boolean printTime, boolean printModel) {
		
		Object reasoningResult = Reason.reasoning(inKB, printTime, printModel);
		
		if(reasoningResult.getClass().isInstance(String.class)) {
			System.out.println((String) reasoningResult);
			return false;
		}else if(reasoningResult.getClass().isInstance(Vector.class)) {
			String results = "";
			for(Enumeration<Predicate> iter = ((Vector<Predicate>) reasoningResult).elements(); iter.hasMoreElements();){
				Predicate pred = iter.nextElement();
				if(pred.name.equalsIgnoreCase(predicateName)){
//					System.out.println("RESULT: " + pred.toWeightandDegreeString());
					results += pred.toWeightandDegreeString() + ", ";
				}else {
					System.out.println("Reasoning succesful, no match found for this KB.");
					return false;
				}
			}
			System.out.println(results);
			return true;
		}
		System.out.println("Some problem occured during matchmaking.");
		return false;
	}
	
	
	
//	if (model){
//    	if (!reasoner.getModel().isEmpty()){
//    			System.out.println("Model found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.");
//    			
//    			Vector<Predicate> matches = reasoner.getModel();
//    			
//    			System.out.println(matches.toString());
//    			System.out.println();
//    			
//    			String results = "";
//    			for(Enumeration<Predicate> iter = matches.elements(); iter.hasMoreElements();){
//    				Predicate pred = iter.nextElement();
//    				if(pred.name.equalsIgnoreCase(name)){
//    					System.out.println("RESULT: " + pred.toWeightandDegreeString());
//    					results += pred.toWeightandDegreeString() + ", ";
//    				}
//    			}
//    			return results;
//    			
//    	}
//    }

}
