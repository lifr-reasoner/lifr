package lifr.util.examples;

import lifr.logic.firstorder.KnowledgeBase;
import lifr.logic.firstorder.LogicFactory;
import lifr.util.examples.io.SimpleFileReader;

/**
 * An example class containing real-world problems, i.e. how LiFR can be used in applications to serve deduction for a variety of purposes.
 */
public class RealWorldProblems {
	
	
	/**
	 * A match test, based on a version of the <a href="https://github.com/nutritionactivityontology/nact">NAct</a> ontology.
	 * 
	 * This test aims to find out if a candidate meal (based on its ingredients) matches a user's nutritional profile. 
	 * 
	 * Example users and meals are provided in the resources. 
	 */
	public static void nactTest() {
    	String onto = SimpleFileReader.readFile("resources/NAct.krss");//nact_1.9.1_food_incon.krss
    	//Users user1, user2, user3 work with older versions of NAct (from 1.9.1 and before)
    	//Users nuser1, nuser2, nuser3, nuser4 work with the latest version of NAct (NAct.krss here) or converted version of NAct from GitHub)
    	String user = "nuser3"; 
    	String userProfile = SimpleFileReader.readFile("resources/" + user + ".txt");
    	//Candidate meals {"m11", "m22", "m33", "m44", "m55"} work with older versions of NAct (from 1.9.1 and before)
    	//Candidate meals {"m1", "m2", "m3", "m4", "m5"} work with the latest version of NAct (NAct.krss here) or converted version of NAct from GitHub)
    	String[] candidateFilenames = {"m1", "m2", "m3", "m4", "m5"};//{"m11", "m22", "m33", "m44", "m55"};
    	
    	String backgroundProblem = onto.trim() + "\n"
    			+ userProfile.trim() + "\n"
    	;
    	
    	System.out.println();
    	
    	for(String candidateFilename: candidateFilenames) {
    		String candidate = SimpleFileReader.readFile("resources/" + candidateFilename + ".txt");
    		
    		String problem = backgroundProblem + candidate.trim() + "\n";
    		
    		System.out.println("Does candidate meal **" + candidateFilename + "** match user's *" + user + "* nutritional profile?");
    		
    		LogicFactory.initialize();
    		KnowledgeBase kb = KRSStoInternal.parseKRSSinput(problem);
    		
//    		ReasoningTasks.reason(kb);
    		
    		boolean match = ReasoningTasks.meetGoalEntailment(user, "candidate", kb);
    		System.out.println("> " + match);
    		
    		System.out.println();
    		kb.clear();
    	}
    	
    }
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		nactTest();
	}

}
