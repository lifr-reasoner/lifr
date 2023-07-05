package lifr.util.examples;

import lifr.logic.firstorder.KnowledgeBase;
import lifr.util.KRSSFuzzyParser;
import lifr.util.exceptions.OutsideDLPException;
import lifr.util.exceptions.ParseException;

/**
 * The Class KRSStoInternal. 
 * 
 * Contains exemplary methods to convert problems formulated the LiFR-compliant KRSS syntax variant to 
 * the internal KB. 
 */
public class KRSStoInternal {
	
	
	/**
	 * Parses the KRSS-based Knowledge Base input and outputs an internal KB object.
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
	
	/**
	 * Example method on how to merge KBs.
	 *
	 * @param kb1 the first KB
	 * @param kb2 the second KB
	 * @return the merged knowledge base
	 */
	public static KnowledgeBase mergingKBsExample(KnowledgeBase kb1, KnowledgeBase kb2){
    	KnowledgeBase kb = new KnowledgeBase();

    	kb.setKnowledgeBase(kb1);
    	kb.mergeKnowledgeBase(kb2);
    	
		System.out.println(kb.toString());
    	return kb;
    }
	
	/**
	 * Example method on how to merge multiple KBs.
	 *
	 * @param tbox the first KB, e.g. a tbox
	 * @param profile1abox the second KB, e.g. the first part of an abox (could be a user profile)
	 * @param profile2abox the third KB, e.g. the second part of an abox (could be a content item profile)
	 * @return the merged knowledge base
	 */
	public static KnowledgeBase mergingMultipleKRSS2SingleKBExample(String tbox, String profile1abox, String profile2abox){
    	KnowledgeBase kb = new KnowledgeBase();

    	kb.setKnowledgeBase(parseKRSSinput(tbox));
    	kb.mergeKnowledgeBase(parseKRSSinput(profile1abox));
    	kb.mergeKnowledgeBase(parseKRSSinput(profile2abox));
    	
		System.out.println(kb.toString());
    	return kb;
    }


}
