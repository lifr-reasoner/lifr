package lifr.util.examples;

import lifr.logic.firstorder.KnowledgeBase;
import lifr.util.KRSSFuzzyParser;
import lifr.util.exceptions.OutsideDLPException;
import lifr.util.exceptions.ParseException;

public class KRSStoInternal {
	
	
	/**
	 * Parses the KRSS-based Knowledge Base input and outputs an internally structured KB.
	 *
	 * @param in the KRSS input
	 * @return the knowledge base
	 */
	public static KnowledgeBase parseKRSSinput(String in){
    	KnowledgeBase kb = new KnowledgeBase();
    	KRSSFuzzyParser parser;
		try {
			parser = new KRSSFuzzyParser(in);
			kb = parser.getKB();
		} catch (OutsideDLPException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		System.out.println(kb.toString());
    	return kb;
    }
	
	public static KnowledgeBase mergingKBsExample(KnowledgeBase kb1, KnowledgeBase kb2){
    	KnowledgeBase kb = new KnowledgeBase();

    	kb.setKnowledgeBase(kb1);
    	kb.mergeKnowledgeBase(kb2);
    	
		System.out.println(kb.toString());
    	return kb;
    }
	
	public static KnowledgeBase mergingMultipleKRSS2SingleKBExample(String tbox, String profile1abox, String profile2abox){
    	KnowledgeBase kb = new KnowledgeBase();

    	kb.setKnowledgeBase(parseKRSSinput(tbox));
    	kb.mergeKnowledgeBase(parseKRSSinput(profile1abox));
    	kb.mergeKnowledgeBase(parseKRSSinput(profile2abox));
    	
		System.out.println(kb.toString());
    	return kb;
    }


}
