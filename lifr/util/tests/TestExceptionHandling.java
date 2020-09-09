package lifr.util.tests;

import java.util.Enumeration;
import java.util.Vector;

import lifr.logic.firstorder.KnowledgeBase;
import lifr.logic.firstorder.LogicFactory;
import lifr.logic.firstorder.Predicate;
import lifr.reasoner.ProofNotFoundException;
import lifr.reasoner.Reasoner;
import lifr.reasoner.krhyper.KrHyper;
import lifr.util.KRSSFuzzyParser;
import lifr.util.exceptions.OutsideDLPException;
import lifr.util.exceptions.ParseException;

public class TestExceptionHandling {
	
	protected static final int reasonerTimeout = 6000;
    protected static int unsolved_memory = 0;
    protected static int unsolved_timeout = 0;
    protected static final int mintermweight = 2;
    public static int refuted = 0;
    
    public static String problem(){
    	String s = "(IMPLIES A B)" + "\n";
    	s += "(IMPLIES C (NOT B))" + "\n";
    	s += "(INSTANCE m A >= 0.75)" + "\n";
    	s += "(INSTANCE m C >= 0.5)" + "\n";
    	return s;
    }
    
    public static String makeMatch(String currentConditions){
    	KnowledgeBase kb = new KnowledgeBase();
    	Reasoner reasoner = new KrHyper();
    	LogicFactory.initialize();
    	String problem = "";
    	
    	problem += currentConditions;
    	KRSSFuzzyParser parser;
		try {
			parser = new KRSSFuzzyParser(problem);
		
    	KnowledgeBase matchKb = parser.getKB();
    	System.out.println(matchKb.toString());
    	kb.mergeKnowledgeBase(matchKb);
        reasoner.setKnowledgeBase(kb);
        
        @SuppressWarnings("unused")
        String s = kb.toString();
        
        boolean model = false;
        try {
        	
        	Runtime r = Runtime.getRuntime();
            long currentTime = System.currentTimeMillis();
            long initBytes = r.totalMemory() - r.freeMemory();
            
            
            model = reasoner.reason(mintermweight,mintermweight+3,reasonerTimeout);
            
            long MemoryConsumed = r.totalMemory() - r.freeMemory() - initBytes;
            long time = System.currentTimeMillis() - currentTime;
            r.gc();
            
            if (model){
            	if (!reasoner.getModel().isEmpty()){
            		Predicate matchingPredicate = null;
            			System.out.println("Model found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.");
            			Vector<Predicate> matches = reasoner.getModel();
            			System.out.println(matches.toString());
            			System.out.println();
            			String results = "";
            			for(Enumeration<Predicate> iter = matches.elements(); iter.hasMoreElements();){
            				Predicate pred = iter.nextElement();
            				if(pred.name.equalsIgnoreCase("lm")){
            					System.out.println("RESULT: " + pred.toWeightandDegreeString());
            					results += pred.toWeightandDegreeString() + ", ";
            				}
            			}
            			return results;
            			
            	}
            } else {
//                System.out.println("Refutation found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n");
                refuted++;
                return "Refutation found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n";
            }
        } catch (ProofNotFoundException ex){
            unsolved_timeout++;
//            System.out.println("No Solution found for KRSS problem (Timeout)\n");
            return "No Solution found for KRSS problem (Timeout)\n";
        } catch (Error err){
            unsolved_memory++;
//            System.out.println("No Solution found for KRSS problem (Out of Memory)\n");
            return "No Solution found for KRSS problem (Out of Memory)\n";
        }
		} catch (OutsideDLPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        kb.clear();
        return "Problem running reasoner";
    }
    
    public static void main(String[] args) {
//    	try {
    		makeMatch(problem());
//    	}catch(Exception e) {
//    		e.printStackTrace();
//    		System.out.println(e.getCause());
//    		System.out.println(e.getLocalizedMessage());
//    		System.out.println(e.getMessage());
//    	}
    }

}
