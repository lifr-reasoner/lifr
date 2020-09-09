package lifr.util.tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import lifr.logic.firstorder.KnowledgeBase;
import lifr.logic.firstorder.LogicFactory;
import lifr.logic.firstorder.Predicate;
import lifr.reasoner.ProofNotFoundException;
import lifr.reasoner.Reasoner;
import lifr.reasoner.krhyper.KrHyper;
import lifr.run.MakeMatch;
import lifr.util.KRSSFuzzyParser;
import lifr.util.exceptions.OutsideDLPException;
import lifr.util.exceptions.ParseException;
import lifr.util.tests.io.FileIO;


public class Test {
	
    public static String problem(){
    	String s = "(IMPLIES A B)" + "\n";
    	s += "(IMPLIES C (NOT B))" + "\n";
    	s += "(INSTANCE m A >= 0.75)" + "\n";
    	s += "(INSTANCE m C >= 0.5)" + "\n";
    	return s;
    }
    
    public static String testMakeMatch(String currentConditions, String what2match){
    	String problem = 
//    		"(IMPLIES E (OR A B))" + "\n" + 
//        	"(IMPLIES Z (OR A C D))" + "\n" + 
//    		"(IMPLIES B A)" + "\n" +
//    		"(IMPLIES C M)" + "\n" +
//        	"(DISJOINT M A)" + "\n" + 
//        	"(INSTANCE a B >= 1.0)" + "\n" + 
//        	"(INSTANCE a C >= 1.0)" + "\n" + 
//        	"(INSTANCE a M >= 1.0)" + "\n";
//        	"(INSTANCE a M < 0.1)" + "\n";
//    		"(IMPLIES A B)" + "\n" +
//    		"(IMPLIES B (and C D))" + "\n" +
//    		"(IMPLIES E D)" + "\n" +
//    		"(INSTANCE a A >= 0.6)" + "\n" +
//    		"(INSTANCE a E < 0.8)" + "\n";
//    		"(INSTANCE a E > 0.8)" + "\n";
    			///
    			///swsto
    		/*
    		"(IMPLIES LA (ALL hasLAM LAM_candidate))" + "\n" +
    		"(IMPLIES LM (ALL materialisesLAM LAM_available))" + "\n" +
    		"(IMPLIES (AND LAM_available LAM_candidate) LAM)" + "\n" +
//    		"(IMPLIES (SOME actuates TOP) Actuator)" + "\n" +
//    		"(IMPLIES TOP (ALL actuates LM))" + "\n" +
			"(IMPLIES (SOME actuates LM) Actuator)" + "\n" +
			"(IMPLIES Actuator (ALL actuates LM))" + "\n" +
    		"(IMPLIES Robot Actuator)" + "\n" +

    		"(INSTANCE athena Robot >= 1.0)" + "\n" +
    		"(INSTANCE la1 LA >= 0.7)" + "\n" +
//    		"(INSTANCE la2 LA >= 0.5)" + "\n" +
    		
			"(RELATED athena rdfid134 actuates)" + "\n" +
    		"(RELATED la1 lam1 hasLAM)" + "\n" +
//    		"(RELATED la2 lam2 hasLAM)" + "\n" +
    		"(RELATED rdfid134 lam1 materialisesLAM)" + "\n" +
//    		"(RELATED rdfid123 lam2 materialisesLAM)" + "\n" +
			*/
    			///
    		/*
    			//YUP!!!!
    		"(IMPLIES (AND (SOME materialises LA) (SOME performableBy PA)) LM)" + "\n" +
//    		"(IMPLIES (AND A B) LM)" + "\n" +
//    		"(IMPLIES (SOME r LA) A)" + "\n" +
//    		"(IMPLIES (SOME p PA) B)" + "\n" +
    		"(IMPLIES Environment (ALL hasActuator PA))" + "\n" +

			"(IMPLIES Robot PA)" + "\n" +
			"(IMPLIES School Environment)" + "\n" +
    		
			"(RELATED naomark230 athena performableBy)" + "\n" +
			"(RELATED naomark230 matching_shapes materialises)" + "\n" +
			"(RELATED second_elementary athena hasActuator)" + "\n" +
			
//			"(INSTANCE athena Robot >= 1.0)" + "\n" +
			"(INSTANCE second_elementary School >= 1.0)" + "\n" +
			"(INSTANCE matching_shapes LA >= 0.7)" + "\n" +
    		
    		*/
    		/*
    		"(IMPLIES (AND (SOME materialises LA) (SOME performableBy PA)) LM)" + "\n" +
    		"(IMPLIES Environment (ALL hasActuator PA))" + "\n" +

			"(IMPLIES Robot PA)" + "\n" +
			"(IMPLIES IWB PA)" + "\n" +
			"(IMPLIES Smartphone PA)" + "\n" +
			"(IMPLIES School Environment)" + "\n" +
			"(IMPLIES Classroom Environment)" + "\n" +
			"(IMPLIES Home Environment)" + "\n" +
			"(IMPLIES Open_air Environment)" + "\n" +
			"(DISJOINT (AND Robot IWB) BOTTOM)" + "\n" +
    		
			"(RELATED naomark230 athena performableBy)" + "\n" +
			"(RELATED rfid134 athena performableBy)" + "\n" +
			"(RELATED turnover iwb_2819fd performableBy)" + "\n" +
			"(RELATED turnover lg500f performableBy)" + "\n" +
			"(RELATED shape_in_slot iwb_2819fd performableBy)" + "\n" +
			"(RELATED shape_in_slot lg500f performableBy)" + "\n" +
			
			"(RELATED naomark230 matching_shapes materialises)" + "\n" +
			"(RELATED turnover matching_shapes materialises)" + "\n" +
			"(RELATED rfid134 find_diff_shapes materialises)" + "\n" +
			"(RELATED shape_in_slot find_diff_shapes materialises)" + "\n" +
			
//    		*/
    		/*
			//FREE DOMAIN - RANGE vs DOMAIN *AND* RANGE
    		//FREE 
    		"(IMPLIES (SOME R TOP) A)" + "\n" +
    		"(IMPLIES TOP (ALL R B))" + "\n" +
    		//DOMAIN *AND* RANGE
//			"(IMPLIES (SOME R B) A)" + "\n" +
//			"(IMPLIES A (ALL R B))" + "\n" +
			
			//To test the forall
//			"(INSTANCE a A >= 1.0)" + "\n" +
			//To test the exists
			"(INSTANCE b B >= 1.0)" + "\n" +
			"(RELATED a b R)" + "\n" +
			*/
        	"";
    	problem += currentConditions;
    	KRSSFuzzyParser parser;
    	String result = "";
		try {
			parser = new KRSSFuzzyParser(problem);
			KnowledgeBase matchKb = parser.getKB();
			System.out.println(matchKb.toString());
			
			result = MakeMatch.matchPredicateText(matchKb, what2match, true, true);
		} catch (OutsideDLPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return result;
    }
    
    public static String readFileWLines(String filename){
		BufferedReader fileReader = null;
		StringBuffer readBuffer = new StringBuffer();
		try{
			fileReader = new BufferedReader(new FileReader(filename));
		} catch(FileNotFoundException e){
			e.printStackTrace();
			return "";
		}
		String line;
		try {
			while ( (line = fileReader.readLine()) != null){
				if (line.trim().length() > 0){
					try{
						readBuffer.append(new String(line.getBytes(), "UTF8"));
						readBuffer.append('\n');
					}catch(UnsupportedEncodingException e){
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}
//		readBuffer.append('\n');
		try{
			fileReader.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		return readBuffer.toString();
    }
    
    @SuppressWarnings("unused")
	private static String propertiesTest() {
    	String problem = readFileWLines("resources/domran.txt");
//    	System.out.println(problem);
    	String abox = "(INSTANCE grrmartin author >= 0.98)\n(INSTANCE got literary_work >= 0.59)\n";
    	
    	problem += abox;
    	
    	return ReasonTest.reasonKRSS(problem);
    }
    
    @SuppressWarnings("unused")
	private static String proofOfConceptPropertiesTest() {
    	String problem = 
    			"(R-ROLE hasAuthor :DOMAIN Author :RANGE Literary_work)\n"
    			+ "(INSTANCE grrmartin author >= 0.98)\n"
    			+ "(INSTANCE got literary_work >= 0.59)\n";
    	
    	return ReasonTest.reasonKRSS(problem);
    }
    
    @SuppressWarnings("unused")
	private static String properProofOfConceptPropertiesTest() {
    	String problem = 
    			"(DOMAIN hasAuthor Author)\n"
    			+ "(RANGE hasAuthor Literary_work)\n"
    			+ "(INSTANCE grrmartin author >= 0.98)\n"
    			+ "(INSTANCE got literary_work >= 0.59)\n"
    			;
    	
    	return ReasonTest.reasonKRSS(problem);
    }
    
    private static String proteinTest() {
    	LogicFactory.initialize();
//    	String onto = FileIO.readFileWLines("resources/proteinonto.txt");
    	String onto = FileIO.readFileWLines("resources/lumo4.txt");
    	String user = FileIO.readFileWLines("resources/user2.txt");
//    	String candidate = FileIO.readFileWLines("resources/PORK_MEAL.txt");
    	String candidate = FileIO.readFileWLines("resources/OTHER_MEAL_3.txt");
    	
    	String problem = onto.trim() + "\n"
    			+ user.trim() + "\n"
    			+ candidate.trim() + "\n"
    	;
    	
    	return ReasonTest.reasonKRSS(problem);
    }
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		String premise = 
//				"(RELATED classroom1 athena hasActuator)" + "\n" +
//				"(RELATED classroom1 iwb_2819fd hasActuator)" + "\n" +
//				"(RELATED lyndhurst_rd lg500f hasActuator)" + "\n" +
				"(RELATED user1_bus_ride lg500f hasActuator)" + "\n" +
				
//				"(INSTANCE classroom1 Classroom >= 1.0)" + "\n" +
//				"(INSTANCE classroom2 Classroom >= 1.0)" + "\n" +
//				"(INSTANCE lyndhurst_rd Home >= 1.0)" + "\n" +
				"(INSTANCE user1_bus_ride Open_air >= 1.0)" + "\n" +
				"";
		String curr1 = 
				"(INSTANCE matching_shapes LA >= 0.7)" + "\n" +
				"";
		String curr2 = 
				"(INSTANCE find_diff_shapes LA >= 0.5)" + "\n" +
				"";
		ArrayList<String> currs = new ArrayList<String>();
		currs.add(curr1);
		currs.add(curr2);
		ArrayList<String> printout = new ArrayList<String>();
		for(int i = 0; i < currs.size(); i++){
			printout.add("Condition " + (i+1) + ": " + makeMatch(premise + currs.get(i)));
		}
		for(int i = 0; i < currs.size(); i++){
			System.out.println(printout.get(i).toString());
		}
		*/
		
//		System.out.println(manualTest());
		
//		System.out.println(propertiesTest());
//		System.out.println(properProofOfConceptPropertiesTest());
//		System.out.println(ReasonTest.reasonKRSS(problem()));
//		System.out.println(manualTest());
		System.out.println(proteinTest());
//		System.out.println(makeMatch(problem()));
		
	}

}
