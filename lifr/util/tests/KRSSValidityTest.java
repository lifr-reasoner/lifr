package lifr.util.tests;

import lifr.logic.firstorder.KnowledgeBase;
import lifr.util.KRSSFuzzyParser;
import lifr.util.exceptions.OutsideDLPException;
import lifr.util.exceptions.ParseException;
import lifr.util.tests.io.FileIO;

public class KRSSValidityTest {
	
	public static KnowledgeBase validateKBonly(String problem) {
		KRSSFuzzyParser parser;
		KnowledgeBase matchKb = null;
		try {
			parser = new KRSSFuzzyParser(problem);
			matchKb = parser.getKB();
//			System.out.println(matchKb.toString());
//			System.out.println(matchKb.getFacts().toString());
		} catch (OutsideDLPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return matchKb;
	}
	
	public static String validateReasoningWithKB(KnowledgeBase kb) {
		String result = "";
		try {
			result = ReasonTest.reasoning(kb);
			
//			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		String problem = FileIO.readFileWLines("resources/protein1.3.krss");
		
//		problem = problem.trim() + "\n" + FileIO.readFileWLines("resources/testPROTEINprofile.txt");
		
//		problem = problem.trim() + "\n" + FileIO.readFileWLines("resources/BEEF_MEAL.txt");
		
		KnowledgeBase kb = validateKBonly(problem);
		
		System.out.println();
		
		validateReasoningWithKB(kb);
	}

}
