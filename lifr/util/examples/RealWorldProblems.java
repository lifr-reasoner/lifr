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
    	String onto = SimpleFileReader.readFile("resources/nact_1.5_incon.krss");
    	String user = "user1"; //user2;
    	String userProfile = SimpleFileReader.readFile("resources/" + user + ".txt");
    	String[] candidateFilenames = {"m11", "m22", "m33", "m44", "m55"};
    	
    	String backgroundProblem = onto.trim() + "\n"
    			+ userProfile.trim() + "\n"
    	;
    	
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
