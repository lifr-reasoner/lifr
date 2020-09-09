/*
 * KRSSFuzzyParser.java
 *
 */

package lifr.util;

//import fpocketkrhyper.reasoner.*;
//import fpocketkrhyper.reasoner.krhyper.*;
//import fpocketkrhyper.logic.dl.DLExpression;

import java.util.Vector;
import java.util.Enumeration;
//import java.util.Hashtable;
import java.lang.StringIndexOutOfBoundsException;
import java.lang.Double;

import lifr.logic.dl.Axiom;
import lifr.logic.dl.Concept;
import lifr.logic.dl.ConceptExpression;
import lifr.logic.dl.Role;
import lifr.logic.dl.RoleExpression;
import lifr.logic.dl.TBox;
import lifr.logic.firstorder.*;
import lifr.logic.fuzzy.Degree;
import lifr.logic.transform.TBox2KB;


/**
 *
 * @author Dorothea
 * @version $Name:  $ $Revision: 1.1 $
 */
@Deprecated
public class KRSSFuzzyParser_BackUp {
	
	private Vector<Axiom> axioms = new Vector<Axiom>();
	private Vector<Role> roles = new Vector<Role>();
	private Vector<String> abox = new Vector<String>();
	private Vector<String> subsClauses = new Vector<String>();
	private Vector<Degree> degrees = new Vector<Degree>();
	//private Vector counterparts = new Vector();
	private int axiomCount = 0;
	private StringBuffer complexExpression = new StringBuffer();
	//private ConceptExpression toMakeCounterparts = null;
	
	private boolean outsideDLP = false;
	private boolean preserveDLP = false;
	private boolean checkforSOME = false;
	private boolean checkforALL = false;
	
    public KRSSFuzzyParser_BackUp (String problem) {
    	parse(problem);
    }
	
    public void parse(String problem){
    	char[] characters;
    	characters = problem.toCharArray();
        StringBuffer clause = new StringBuffer();
        Vector<String> clauses = new Vector<String>();
        
        try{
        for (int i = 0; i < characters.length ; i++ ) { //get clauses
        
            switch (characters[i]){
                case '\n':
                	if (clause.toString().indexOf('(') != -1){
            			clauses.addElement((clause.toString().substring((clause.toString()).indexOf('(') + 1, (clause.toString()).lastIndexOf(')'))).trim());
            			clause.setLength(0);
                	}
            	break;
            	//case '|':
            	//break;
            	default: 
            		clause.append(characters[i]);
            }
			
        }   
        } catch (Exception exc) {}
        
        String definition = "";
        String defClause = "";
        
        clauses.trimToSize();
        
        for (Enumeration<String> e = clauses.elements(); e.hasMoreElements();){
			String c = (String)e.nextElement();
			definition = c.substring(0,c.indexOf(' '));
			defClause = c.substring(c.indexOf(' ') + 1, c.length());
			
			if ((definition.equalsIgnoreCase("instance")) || (definition.equalsIgnoreCase("related")) ){
				convertAbox(definition.trim(), defClause.trim());
			}else if (definition.equalsIgnoreCase("glb")) {
				convertAbox("instance", defClause.trim());
			}else if (definition.equalsIgnoreCase("subsumes")) {
				subsumingClauses(defClause.trim());
			}else {
				axiomCount++;
				convertTbox(definition.trim(), defClause.trim());
			}
    	}
    }
    
    private void subsumingClauses(String defClause){
    	String concept1 = defClause.substring(0, defClause.indexOf(' '));
    	defClause = (defClause.substring(defClause.indexOf(' ') + 1, defClause.length())).trim();
    	String concept2 = defClause.trim();
    	
    	String subsumption = concept2+"(Xground):-"+concept1+"(Xground).";
    	//System.out.println(subsumption);
    	
    	subsClauses.addElement(subsumption);
    }
    
    private void convertAbox(String definition, String defClause){
    	if (definition.equalsIgnoreCase("instance")){
    		String instance = defClause.substring(0, defClause.indexOf(' '));
    		defClause = (defClause.substring(defClause.indexOf(' ') + 1, defClause.length())).trim();
    		String concept = defClause.substring(0, defClause.indexOf(' '));
    		defClause = (defClause.substring(defClause.indexOf(' ') + 1, defClause.length())).trim();
    		String operator = defClause.substring(0, defClause.indexOf(' '));
    		defClause = (defClause.substring(defClause.indexOf(' ') + 1, defClause.length())).trim();
    		//String degree = defClause;
    		double degree = Double.parseDouble(defClause.trim());
    		String newClause = concept+"("+instance+").";
    		String notClause = "-"+concept+"("+instance+").";
    		Degree deg = new Degree(operator, degree);
    		Degree invertDeg = new Degree(deg.invertOperator(), deg.invertDegree());
    		if ((!operator.equalsIgnoreCase("<"))&&(!operator.equalsIgnoreCase("<=")))
    		{
    			abox.addElement(newClause);
    			degrees.addElement(deg);
    		}else{
    			abox.addElement(newClause);
    			degrees.addElement(deg);
    			abox.addElement(notClause); 
    			degrees.addElement(invertDeg);
    		}
    		
    		//System.out.println(operator+" "+degree);
    	}else{
    		String instance1 = defClause.substring(0, defClause.indexOf(' '));
    		defClause = (defClause.substring(defClause.indexOf(' ') + 1, defClause.length())).trim();
    		String instance2 = defClause.substring(0, defClause.indexOf(' '));
    		defClause = defClause.substring(defClause.indexOf(' ') + 1, defClause.length());
    		String relation = defClause.trim();
    		/*
    		String relation = (defClause.substring(0, defClause.indexOf(' '))).trim();
    		defClause = defClause.substring(defClause.indexOf(' ') + 1, defClause.length());
    		String operator = (defClause.substring(0, defClause.indexOf(' '))).trim();
    		defClause = defClause.substring(defClause.indexOf(' ') + 1, defClause.length());
    		//String degree = defClause;
    		double degree = Double.parseDouble(defClause.trim());
    		Degree deg = new Degree(operator, degree);
    		//Degree invertDeg = new Degree(invertOperator(deg.invertOperator(), deg.invertDegree()));
    		*/
    		String newClause = relation+"("+instance1+","+instance2+").";
    		abox.addElement(newClause);
    		degrees.addElement(Degree.relationDegree);
    		//System.out.println(operator+" "+degree);
    	}
    	
    	abox.trimToSize();
    }
    
    private void convertTbox(String definition, String defClause){
    	String role1 = "";
    	String role2 = "";
    	outsideDLP = false;
    	checkforSOME = false;
    	checkforALL = false;
    	ConceptExpression conex1 = ConceptExpression.BOTTOM;
    	ConceptExpression conex2 = ConceptExpression.BOTTOM;
    	//ConceptExpression not_conex1 = null;
    	//ConceptExpression not_conex2 = null;
    		
    	if (defClause.charAt(0) != '('){
    		//simple expression
    		String concept1 = defClause.substring(0, defClause.indexOf(' '));
    		defClause = (defClause.substring(defClause.indexOf(' ') + 1, defClause.length())).trim();
    		conex1 = simpleExpression(concept1);
    		if ((definition.equalsIgnoreCase("inverse")) || (definition.equalsIgnoreCase("parent"))) role1 = concept1;
    		//toMakeCounterparts = notConcept(concept1);
    		//not_conex1 = notConcept(concept1);
    			
    		if (defClause.charAt(0) != '('){
    			//right simple expression
    			String concept2 = defClause;
    			conex2 = simpleExpression(concept2);
    			if ((definition.equalsIgnoreCase("inverse")) || (definition.equalsIgnoreCase("parent"))) role2 = concept2;
    			//toMakeCounterparts = notConcept(concept2);
    			//not_conex2 = notConcept(concept2);
    		}else{
    			//right complex expression
    			String s = defClause.substring(defClause.indexOf('(') + 1, defClause.lastIndexOf(')'));
    			s = s.trim();
    			checkforALL = false;
    			checkforSOME = true;
    			conex2 = resolveComplex(s);
    			
    			if (s.startsWith("some")) outsideDLP = true;
    			if (s.startsWith("all")) preserveDLP = true;
    			//not_conex2 = resolveComplex(defClause.substring(1, defClause.lastIndexOf(')')),true);
    			//!!!//
    			//outsideDLP = false;
    			//preserveDLP = false;
    			//!!!//
    		}
    	}else{
    		defClause = (defClause.substring(scanComplex(defClause) + 1, defClause.length())).trim();
    		//defClause = (defClause.substring(0, scanComplex(defClause) + 1)).trim();
    		String leftExpression = (complexExpression.toString()).trim();
    		String s = leftExpression.substring(1, leftExpression.lastIndexOf(')'));
    		s = s.trim();
    		checkforALL = true;
    		checkforSOME = false;
    		conex1 = resolveComplex(s);
    		if (s.startsWith("all")) outsideDLP = true;
    		if (s.startsWith("some")) preserveDLP = true;
    		//!!!//
    		//	outsideDLP = false;
    		//	preserveDLP = false;
    			//!!!//
    		//not_conex1 = resolveComplex(leftExpression.substring(1, leftExpression.lastIndexOf(')')),true);
    		if (defClause.charAt(0) != '('){
    			//right simple expression
    			String concept2 = defClause;
    			conex2 = simpleExpression(concept2);
    			//toMakeCounterparts = notConcept(concept2);
    			//not_conex2 = notConcept(concept2);
    		}else{
    			//right complex expression
    			checkforALL = false;
    			checkforSOME = true;

    			String sr = defClause.substring(defClause.indexOf('(') + 1, defClause.lastIndexOf(')'));
    			conex2 = resolveComplex(sr);
    			
    			//not_conex2 = resolveComplex(defClause.substring(1, defClause.lastIndexOf(')')),true);
    			if (sr.startsWith("some")) outsideDLP = true;
    			if (sr.startsWith("all")) preserveDLP = true;
    			//!!!//
    			//outsideDLP = false;
    			//preserveDLP = false;
    			//!!!//
    		}
    	}
    	
    	if (definition.equalsIgnoreCase("equivalent")){
    		Axiom equivalence = null;
    		if (!preserveDLP) equivalence = Axiom.conceptDefine(conex1,conex2);
    		else {
    			equivalence = Axiom.conceptImplies(conex1,conex2);
    			System.out.println("Forced implication to stay within DLP: "+equivalence.toString());
    		}
    		//Axiom not_equivalence = Axiom.conceptDefine(not_conex2,not_conex1);   
    		if (!outsideDLP) 
    				axioms.addElement(equivalence);
    		else System.out.println("Outside DLP!!! "+equivalence.toString());
    		//axioms.addElement(not_equivalence);
    		preserveDLP = false;
    		outsideDLP = false;
    	}else if (definition.equalsIgnoreCase("implies")){
    		Axiom implication = Axiom.conceptImplies(conex1,conex2);
    		//Axiom not_implication = Axiom.conceptImplies(not_conex2,not_conex1);
    		if (!outsideDLP) axioms.addElement(implication);
    		else System.out.println("Outside DLP!!! "+implication.toString());
    		//axioms.addElement(not_implication);
    		preserveDLP = false;
    		outsideDLP = false;
    	}else if (definition.equalsIgnoreCase("disjoint")){
    		//Axiom disjoint = Axiom.conceptDefine(ConceptExpression.and(conex1,conex2),ConceptExpression.BOTTOM);
    		Axiom disjoint = Axiom.conceptDisjoint(conex1,conex2);
    		//Axiom not_disjoint = Axiom.conceptDefine(ConceptExpression.and(not_conex1,not_conex2),ConceptExpression.BOTTOM);
    		axioms.addElement(disjoint);
    		//axioms.addElement(not_disjoint);
    	}else if (definition.equalsIgnoreCase("inverse")){
    		Role inverse = Role.getOrCreate(role1);
    		inverse.addInverse(role2);
    		//Axiom not_disjoint = Axiom.conceptDefine(ConceptExpression.and(not_conex1,not_conex2),ConceptExpression.BOTTOM);
    		roles.addElement(inverse);
    		//axioms.addElement(not_disjoint);
    	}else if (definition.equalsIgnoreCase("parent")){
    		Role child = Role.getOrCreate(role2);
    		child.addParent(role1);
    		//Axiom not_disjoint = Axiom.conceptDefine(ConceptExpression.and(not_conex1,not_conex2),ConceptExpression.BOTTOM);
    		roles.addElement(child);
    		//axioms.addElement(not_disjoint);
    	}else{
    		System.out.println("Wrong KRSS concept definition!!!!"+definition);
    	}
    	
    	//axioms.trimToSize();
    }
    
    
    
    //RESOLVE COMPLEX!!!!
    private ConceptExpression resolveComplex (String clause) {
    	
    	outsideDLP = false;
    	
    	ConceptExpression conex1 = null;
    	ConceptExpression conex2 = null;
    	RoleExpression rolex = null;
    	Vector<Object> conex = new Vector<Object>();
    	Vector<String> AndOrConcepts = new Vector<String>();
    	
    	//System.out.println("Init Clause: "+clause);
    	clause = clause.trim();
    	String operator = (clause.substring(0, clause.indexOf(' '))).trim();
    	String defClause = (clause.substring(clause.indexOf(' ') + 1, clause.length())).trim();
    	//System.out.println("def Clause: "+defClause);
    	//System.out.println("Operator: "+operator);
    /*	
    if (not){
    	if (operator.equalsIgnoreCase("some")) operator = "all";
    	else if (operator.equalsIgnoreCase("all")) operator = "some";
    }
    */	
    if (operator.equalsIgnoreCase("not")){
    	if (defClause.charAt(0) != '('){
    		String concept1 = defClause.trim();
    		//if (!not) 
    			conex1 = simpleExpression(concept1);
    			//toMakeCounterparts = notConcept(concept1);
    		//else conex1 = notConcept(concept1);
    	}else{
    		conex1 = resolveComplex((defClause.substring(1, defClause.lastIndexOf(')'))).trim());
    	}
    	
    //and, or
    }else if ((operator.equalsIgnoreCase("and")) || (operator.equalsIgnoreCase("or"))){
    	//int end = 0;
    	
    	StringBuffer andorClause = new StringBuffer();
        for (int i = 0; i < defClause.length(); i++ ) { 
        
            switch (defClause.charAt(i)){
                case ' ':
            		if (defClause.charAt(i+1) == '('){
            			//complex expression follows
            			AndOrConcepts.addElement(andorClause.toString());
            			andorClause.setLength(0);
            		}else if ((defClause.indexOf('(', i) == -1) && (defClause.indexOf(')', i) == -1)){
            			//only simple expressions from now on
            			AndOrConcepts.addElement(andorClause.toString());
            			andorClause.setLength(0);
            		}else {
            			andorClause.append(defClause.charAt(i));
            		}
            	break;
            	case ')':
            		if (defClause.indexOf(')', i+1) == -1){
            			//last complex expression
            			andorClause.append(defClause.charAt(i));
            			AndOrConcepts.addElement(andorClause.toString());
            			andorClause.setLength(0);
            		}else {
            			andorClause.append(defClause.charAt(i));
            		}
            	break;
            	default: 
            		andorClause.append(defClause.charAt(i));
            }
			
        }
        //last simple expression
        String clearWhiteSpace = andorClause.toString();
        clearWhiteSpace = clearWhiteSpace.trim();
        AndOrConcepts.addElement(clearWhiteSpace);
        andorClause.setLength(0);
    		
    	AndOrConcepts.trimToSize();
    	
    	
    	
    	for (Enumeration<String> e = AndOrConcepts.elements(); e.hasMoreElements();){
    		String concept = (String) e.nextElement();
    		try{
    			concept = concept.trim();
    			if (concept.charAt(0) != '('){
    				//System.out.println("SIMPLE: "+ concept);
    				//if (!not) 
    					conex1 = simpleExpression(concept);
    					//toMakeCounterparts = notConcept(concept);
    				//else conex1 = notConcept(concept);
    				//if (not) toMakeCounterparts = notConcept(concept);
    				conex.addElement(conex1);
    			}else{
    				//System.out.println("COMPLEX: "+ concept);
    				conex2 = resolveComplex(concept.substring(1, concept.lastIndexOf(')')));
    				conex.addElement(conex2);
    			}
    		//}catch (StringIndexOutOfBoundsException se){ System.out.println ("\n"+se+"\n"); }
    		}catch (StringIndexOutOfBoundsException se){ }
    	}
    		
    		
    		AndOrConcepts.setSize(0);
    		conex.trimToSize();
    		
    //some, all
    }else{
    	if ((operator.equalsIgnoreCase("some"))){
    		 if (checkforSOME) outsideDLP = true;
    		 else preserveDLP = true;
    		 //!!!//
    		//	outsideDLP = false;
    		//	preserveDLP = false;
    			//!!!//
    	}
    	if ((operator.equalsIgnoreCase("all"))){
    		if (checkforALL) outsideDLP = true;
    		else preserveDLP = true;
    		//!!!//
    		//	outsideDLP = false;
    		//	preserveDLP = false;
    			//!!!//
    	}
    	
    	if (defClause.charAt(0) != '('){
    			//left simple expression
    			String concept1 = defClause.substring(0, defClause.indexOf(' '));
    			defClause = (defClause.substring(defClause.indexOf(' ') + 1, defClause.length())).trim();
    				
    			Role role = Role.getOrCreate(concept1);
    			rolex = RoleExpression.atom(role);
    			
    			if (defClause.charAt(0) != '('){
    				//right simple expression
    				String concept2 = defClause;
    				//if (!not) 
    					conex2 = simpleExpression(concept2);
    					//ConceptExpression toMakeNot = notConcept(concept2);
    				//else conex2 = notConcept(concept2);
    			}else{
    				//right complex expression
    				conex2 = resolveComplex(defClause.substring(1, defClause.lastIndexOf(')')));
    			}
    		}else{
    			//left complex expression
    			defClause = (defClause.substring(scanComplex(defClause) + 1, defClause.length())).trim();
    			String leftExpression = (complexExpression.toString()).trim();
    			conex1 = resolveComplex(leftExpression.substring(1, leftExpression.lastIndexOf(')')));
    				
    			if (defClause.charAt(0) != '('){
    				//right simple expression
    				String concept2 = defClause;
    				//if (!not) 
    					conex2 = simpleExpression(concept2);
    					//toMakeCounterparts = notConcept(concept2);
    				//else conex2 = notConcept(concept2);
    			}else{
    				//right complex expression
    				conex2 = resolveComplex(defClause.substring(1, defClause.lastIndexOf(')')));
    			}
    		}
    }
    	ConceptExpression result = null;
    	complexExpression.setLength(0);
    	
    	if (operator.equalsIgnoreCase("and")){
    		//if (!not) 
    			result = ConceptExpression.and(conex);
    		//else{
    		//	result = createComplexCounterpart(conex, "and");
    		//}
    	}else if (operator.equalsIgnoreCase("or")){
    		//if (!not) 
    			result = ConceptExpression.or(conex);
    		//else{
    		//	result = createComplexCounterpart(conex, "or");
    		//}
    	}else if (operator.equalsIgnoreCase("not")){
    		result = ConceptExpression.not(conex1);
    	}else if (operator.equalsIgnoreCase("some")){
    		result = ConceptExpression.exists(rolex, conex2);
    	}else if (operator.equalsIgnoreCase("all")){
    		result = ConceptExpression.forall(rolex, conex2);
    	}else{
    		System.out.println("Wrong KRSS operator!!!!"+operator);
    	}
    
    	
    	return result;
    }
    
    
    private ConceptExpression simpleExpression (String concept1){
    	ConceptExpression conex1 = ConceptExpression.BOTTOM;
    	
    	if (concept1.equalsIgnoreCase("top")){
    		conex1 = ConceptExpression.TOP;
    	}else if (concept1.equalsIgnoreCase("bottom")){
    		conex1 = ConceptExpression.BOTTOM;
    	}else {
    		Concept con1 = Concept.getOrCreate(concept1);
    		conex1 = ConceptExpression.atom(con1);
    	}
    	
    	return conex1;
    }
    
    /*
    private ConceptExpression notConcept (String concept1){
    	ConceptExpression conex1 = null;
    	Vector body = new Vector();
    	
    	concept1 = concept1.trim();
    	
    	if (concept1.equalsIgnoreCase("top")){
    		conex1 = ConceptExpression.TOP;
    	}else if (concept1.equalsIgnoreCase("bottom")){
    		conex1 = ConceptExpression.BOTTOM;
    	}else {
    		Concept notCon = Concept.getOrCreate("not_"+concept1);
    		conex1 = ConceptExpression.atom(notCon);
    		
    		Variable varxterm = LogicFactory.newVariable("X");
            Vector varvector = new Vector();
            varvector.addElement(varxterm);
            body.addElement(LogicFactory.newPredicate(("not_"+concept1.toLowerCase()).toLowerCase(),varvector));
            body.addElement(LogicFactory.newPredicate(concept1.toLowerCase(),varvector)); 
            
            Clause c = new Clause(null, body);
            
    		if (!counterparts.contains(c)) 
    			counterparts.addElement(c);
    	}
    	return conex1;
    }
    */
    
    /*!!!!!!!!!!!
    private String clearDoubleWhiteSpace(String concept){
    	StringBuffer s = new StringBuffer();
    	for (int i = 0; i < concept.length(); i++){
    		try
    		{
    			switch (concept.charAt(i)){
    				case ' ':
    					if (concept.charAt(i+1) == ' ') break;
    					else s.append(concept.charAt(i));
    				break;
    				default:
    					s.append(concept.charAt(i));
    			}
    		}catch (StringIndexOutOfBoundsException e){}
    	}
    	return s.toString();
    }
    */
    
    private int scanComplex (String defClause){
    	//StringBuffer left = new StringBuffer();
    	complexExpression.setLength(0);
    	int parenthCount = 0;
    	int stop = 1;
    	for (int i = 0; i <= stop; i++){
    				
    		switch (defClause.charAt(i)){
    			case '(':
    				complexExpression.append(defClause.charAt(i));
    				parenthCount++;
    			break;
    			case ')':
    				complexExpression.append(defClause.charAt(i));
    				parenthCount--;
    			break;
    			default:
    				complexExpression.append(defClause.charAt(i));
    		}
    		if (parenthCount != 0) stop++;
    	}
    	return stop;
    }
    
    public static final Clause singleClause(String clauseToString){
    	KRSSFuzzyParser_BackUp newparser = new KRSSFuzzyParser_BackUp(clauseToString);
    	Clause c = null;
    	Clause cg = null;
    	for (int j = 0; j < newparser.subsClauses.size(); j++){
    		String clause = ((String)newparser.subsClauses.elementAt(j)).toLowerCase();
    		c = LogicFactory.newClause(clause, Degree.relationDegree);
    		Enumeration<Predicate> e = c.getBody();
    		cg = LogicFactory.newFactClause(e.nextElement().toString(), Degree.relationDegree);
    		//kb.addClause(c);
    		//kb.addClause(cg);
    	}
    	return cg;
    }
    
    public final KnowledgeBase getKB(){
    	TBox tb = new TBox(axiomCount);
    	KnowledgeBase kb = new KnowledgeBase();
    	for (Enumeration<Axiom> eax = axioms.elements(); eax.hasMoreElements();){
    		Axiom ax = (Axiom)eax.nextElement();
    		tb.addAxiom(ax);
    	}
    	for (Enumeration<Role> er = roles.elements(); er.hasMoreElements();){
    		Role r = (Role)er.nextElement();
    		tb.add(r);
    	}
    	TBox2KB.addToKnowledgeBase(tb, kb);
    	//System.out.println("\n\nTbox: "+tb.toString());
    	/*
    	for (Enumeration ec = counterparts.elements(); ec.hasMoreElements();){
    		Clause clause = (Clause)ec.nextElement();
    		//System.out.println("ABOX CLAUSE: "+clause);
    		kb.addClause(clause);
    	}
    	*/
    	//for (Enumeration eab = abox.elements(); eab.hasMoreElements();){
    	for (int i = 0; i < abox.size(); i++){
    		//String clause = ((String)eab.nextElement()).toLowerCase();
    		String clause = ((String)abox.elementAt(i)).toLowerCase();
    		Degree degree = (Degree)degrees.elementAt(i);
    		//System.out.println("ABOX CLAUSE: "+clause);
    		kb.addClause(LogicFactory.newFactClause(clause, degree));
    	}
    	
    	for (int j = 0; j < subsClauses.size(); j++){
    		String clause = ((String)subsClauses.elementAt(j)).toLowerCase();
    		Clause c = LogicFactory.newClause(clause, Degree.relationDegree);
    		Enumeration<Predicate> e = c.getBody();
    		Clause cg = LogicFactory.newFactClause(e.nextElement().toString(), Degree.relationDegree);
    		//kb.addClause(c);
    		kb.addClause(cg);
    	}
    	
    	//kb.addClause(LogicFactory.newClause("foo(a)."));
    	return kb;
    }
    
}
