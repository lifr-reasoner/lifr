/*
 * KRSSFuzzyParser.java
 *
 */

package lifr.util;


import java.util.Vector;

import java.util.Enumeration;
import java.util.Hashtable;
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
import lifr.util.exceptions.OutsideDLPException;
import lifr.util.exceptions.ParseException;


/**
 *
 * @author Dorothea
 * @version $Name:  $ $Revision: 1.1 $
 */
public class KRSSFuzzyParser extends Throwable{
	
	private static final long serialVersionUID = 1L;
	
	private Vector<Axiom> axioms = new Vector<Axiom>();
	private Vector<Role> roles = new Vector<Role>();
	private Vector<String> abox = new Vector<String>();
	private Vector<Predicate> subsClauses = new Vector<Predicate>();
	private Vector<Degree> degrees = new Vector<Degree>();
	private static Hashtable<String, String> weightedConcepts = new Hashtable<String, String>(15);
	//private Vector counterparts = new Vector();
	private int axiomCount = 0;
	private StringBuffer complexExpression = new StringBuffer();
	//private ConceptExpression toMakeCounterparts = null;
	private double m1 = 0.8;
	private double m2 = 0.2;
	
	private boolean outsideDLP = false;
	private boolean preserveDLP = false;
	private boolean checkforSOME = false;
	private boolean checkforALL = false;
	
	private String subconceptChar;
	
	 public KRSSFuzzyParser (String problem)  throws OutsideDLPException, ParseException{
	    	parse(problem);
	    	this.subconceptChar = "";
	    }
	
	public KRSSFuzzyParser (String problem, String subconceptChar) throws OutsideDLPException, ParseException {
    	parse(problem);
    	this.subconceptChar = subconceptChar;
    }
	
    public void parse(String problem)  throws OutsideDLPException, ParseException{
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
				subsumingClauses(defClause.trim(), false);
			}else if (definition.equalsIgnoreCase("rsubsumes")) {
				subsumingClauses(defClause.trim(), true);
			}else if (definition.equalsIgnoreCase("role")) {
				convertRoles(defClause.trim());
			}else if (definition.equalsIgnoreCase("weight")) {
				addWeightedConcept(defClause.trim());
			}else if ((definition.equalsIgnoreCase("domain"))  || (definition.equalsIgnoreCase("range")) ) {
				axiomCount++;
				convertRoleTboxTop(definition.trim(), defClause.trim());
			}else if (definition.equalsIgnoreCase("r-role")) {
				axiomCount++;
				convertRoleTbox(defClause.trim());
			}else {
				axiomCount++;
				convertTbox(definition.trim(), defClause.trim());
			}
    	}
    }
    
    private void subsumingClauses(String defClause, boolean reverse)  throws ParseException{
    	String concept1 = defClause.substring(0, defClause.indexOf(' '));
//    	defClause = (defClause.substring(defClause.indexOf(' ') + 1, defClause.length())).trim();
//    	String concept2 = defClause.trim();
    	Term ground = new Constant("grnd");
		Vector<Term> terms = new Vector<Term>(1);
		terms.add(ground);
    	Predicate subsumption;
    	if (!reverse){ 
    		//subsumption = concept2+"(grnd):-"+concept1+"(grnd).";
    		Degree deg = new Degree(">=", m1);
    		subsumption = LogicFactory.newPredicate(concept1, terms, deg);
    	}else{
    		Degree deg = new Degree(">=", m2);
    		subsumption = LogicFactory.newPredicate(concept1, terms, deg);
    	}
    	//System.out.println(subsumption);
    	
    	subsClauses.addElement(subsumption);
    }
    
    private void addWeightedConcept(String defClause)  throws ParseException{
    	String concept = defClause.substring(0, defClause.indexOf(' '));
		defClause = (defClause.substring(defClause.indexOf(' ') + 1, defClause.length())).trim();
		String weight = defClause.trim();
		weightedConcepts.put(concept, weight);
    }
    
    private void convertRoles(String defClause)  throws ParseException{
    	String r = (defClause.substring(0, defClause.indexOf(" "))).trim();
    	defClause = (defClause.substring(defClause.indexOf(':') - 1)).trim();

    	Role role = Role.getOrCreate(r);
    	
    	if (defClause.contains(":PARENT")){
    		defClause = defClause.substring(defClause.indexOf(' '));
    		String par = "";
    		if (defClause.indexOf(':') != -1) {
    			par = (defClause.substring(0, defClause.indexOf(':') - 1) ).trim();
    			defClause = (defClause.substring(defClause.indexOf(':') - 1) ).trim();
    		}
    		else par = defClause.trim();
    		
    		Role parent = Role.getOrCreate(par);
    		role.addParent(parent);
    	}
    	if (defClause.contains(":INVERSE")){
    		defClause = defClause.substring(defClause.indexOf(' '));
    		String inv = "";
    		if (defClause.indexOf(':') != -1) {
    			inv = (defClause.substring(0, defClause.indexOf(':') - 1) ).trim();
    			defClause = (defClause.substring(defClause.indexOf(':') - 1) ).trim();
    		}
    		else inv = defClause.trim();
    		
    		Role inverse = Role.getOrCreate(inv);
    		role.addInverse(inverse);
    	}
    	if (defClause.contains(":TRANSITIVE")){
    		role.setTransitive(true);
    	}
    	
    	if (defClause.contains(":SYMMETRIC")){
    		role.setSymmetric(true);
    	}
    	
    	roles.add(role);
    }
    
    private void convertAbox(String definition, String defClause) throws ParseException{
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
    
    private void convertTbox(String definition, String defClause) throws OutsideDLPException, ParseException{
    	outsideDLP = false;
    	checkforSOME = false;
    	checkforALL = false;
    	ConceptExpression conex1 = ConceptExpression.BOTTOM;
    	ConceptExpression conex2 = ConceptExpression.BOTTOM;
    	//ConceptExpression not_conex1 = null;
    	//ConceptExpression not_conex2 = null;
    		
    	if (defClause.charAt(0) != '('){
    		//simple expression
//    		System.out.println(defClause.toString());
    		String concept1 = defClause.substring(0, defClause.indexOf(' '));
//    		System.out.println(defClause);
    		defClause = (defClause.substring(defClause.indexOf(' ') + 1, defClause.length())).trim();
    		conex1 = simpleExpression(concept1);
    		//if ((definition.equalsIgnoreCase("inverse")) || (definition.equalsIgnoreCase("parent"))) role1 = concept1;
    		//toMakeCounterparts = notConcept(concept1);
    		//not_conex1 = notConcept(concept1);
    			
    		if (defClause.charAt(0) != '('){
    			//right simple expression
    			String concept2 = defClause;
    			conex2 = simpleExpression(concept2);
    			//if ((definition.equalsIgnoreCase("inverse")) || (definition.equalsIgnoreCase("parent"))) role2 = concept2;
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
    			throw new OutsideDLPException("Forced one-sided implication insertion in KB to stay within DLP for statement: "+equivalence.toString());//System.err.println("Forced implication to stay within DLP: "+equivalence.toString());
    		}
    		//Axiom not_equivalence = Axiom.conceptDefine(not_conex2,not_conex1);   
    		if (!outsideDLP) 
    				axioms.addElement(equivalence);
    		else throw new OutsideDLPException("Statement is outside the DLP expressivity fragment and will not be included in KB:" + equivalence.toString());//System.err.println("Outside DLP!!! "+equivalence.toString());
    		//axioms.addElement(not_equivalence);
    		preserveDLP = false;
    		outsideDLP = false;
    	}else if (definition.equalsIgnoreCase("implies")){
    		Axiom implication = Axiom.conceptImplies(conex1,conex2);
    		//Axiom not_implication = Axiom.conceptImplies(not_conex2,not_conex1);
    		if (!outsideDLP) axioms.addElement(implication);
    		else throw new OutsideDLPException("Statement is outside the DLP expressivity fragment and will not be included in KB:" + implication.toString());//System.err.println("Outside DLP!!! "+implication.toString());
    		//axioms.addElement(not_implication);
    		preserveDLP = false;
    		outsideDLP = false;
    	}else if (definition.equalsIgnoreCase("disjoint")){
    		//Axiom disjoint = Axiom.conceptDefine(ConceptExpression.and(conex1,conex2),ConceptExpression.BOTTOM);
    		Axiom disjoint = Axiom.conceptDisjoint(conex1,conex2);
    		//Axiom not_disjoint = Axiom.conceptDefine(ConceptExpression.and(not_conex1,not_conex2),ConceptExpression.BOTTOM);
    		axioms.addElement(disjoint);
    		//axioms.addElement(not_disjoint);
    		/*
    	}else if (definition.equalsIgnoreCase("inverse")){
    		Role inverse = Role.getOrCreate(role1);
    		Role inv = null;
    		if ((inverse.getInverseRoles()).hasMoreElements()) inv = (inverse.getInverseRoles()).nextElement();
    		if ((!roles.contains(inverse)) && (!roles.contains(inv)) ){
    			inverse.addInverse(role2);
    			roles.addElement(inverse);
    		}else{
    			roles.get(roles.indexOf(inverse)).addInverse(role2);
    		}
    	}else if (definition.equalsIgnoreCase("parent")){
    		Role child = Role.getOrCreate(role2);
    		if (!roles.contains(child)){
    			child.addParent(role1);
    			roles.addElement(child);
    		}else{
    			roles.get(roles.indexOf(child)).addParent(role1);
    		}
    	*/
    	}else{
    		throw new ParseException("Wrong KRSS concept definition for axiom: " + definition);//System.err.println("Wrong KRSS concept definition!!!!"+definition);
    	}
    	
    	//axioms.trimToSize();
    }
    
    private void convertRoleTbox(String defClause)  throws ParseException{
    	RoleExpression rolex = RoleExpression.UNIVERSAL;
    	ConceptExpression domain = ConceptExpression.TOP;
    	ConceptExpression range = ConceptExpression.TOP;
    	
    	String r = (defClause.substring(0, defClause.indexOf(" "))).trim();
    	defClause = (defClause.substring(defClause.indexOf(':') - 1)).trim();

    	Role role = Role.getOrCreate(r);
    	rolex = RoleExpression.atom(role);
    	
    	if (defClause.contains(":DOMAIN")){
    		defClause = defClause.substring(defClause.indexOf(' '));
    		String dom = "";
    		if (defClause.indexOf(':') != -1) {
    			dom = (defClause.substring(0, defClause.indexOf(':') - 1) ).trim();
    			defClause = (defClause.substring(defClause.indexOf(':') - 1) ).trim();
    		}
    		else dom = defClause.trim();
    		
    		domain = ConceptExpression.atom(Concept.getOrCreate(dom));
    	}
    	if (defClause.contains(":RANGE")){
    		defClause = defClause.substring(defClause.indexOf(' '));
    		String ran = "";
    		if (defClause.indexOf(':') != -1) {
    			ran = (defClause.substring(0, defClause.indexOf(':') - 1) ).trim();
    			defClause = (defClause.substring(defClause.indexOf(':') - 1) ).trim();
    		}
    		else ran = defClause.trim();
    		
    		Role inverse = Role.getOrCreate(ran);
    		role.addInverse(inverse);
    		
    		range = ConceptExpression.atom(Concept.getOrCreate(ran));
    	}
    	
    	Axiom roleImplication = null;
		
		ConceptExpression body = ConceptExpression.and(domain, range);
		
		roleImplication = Axiom.roleImplies(body, rolex);
    	
		axioms.addElement(roleImplication);
    }
    
    private void convertRoleTboxTop(String definition, String defClause)  throws ParseException{
    	ConceptExpression conex = ConceptExpression.TOP;
    	final ConceptExpression conex_top = ConceptExpression.TOP;
    	RoleExpression rolex = RoleExpression.UNIVERSAL;
    		
    	if (defClause.charAt(0) != '('){
    		//simple expression
    		String role = defClause.substring(0, defClause.indexOf(' '));
    		defClause = (defClause.substring(defClause.indexOf(' ') + 1, defClause.length())).trim();
    		Role ro1 = Role.getOrCreate(role);
    		rolex = RoleExpression.atom(ro1);
    			
    		if (defClause.charAt(0) != '('){
    			//right simple expression
    			String concept = defClause;
    			conex = simpleExpression(concept);
    		}else{
    			throw new ParseException("Wrong KRSS role restriction definition for statement: " + definition);//System.err.println("Wrong KRSS role restriction definition!!!!"+definition);
    		}
    	}else{
    		throw new ParseException("Wrong KRSS role restriction definition for statement: " + definition);//System.err.println("Wrong KRSS role restriction definition!!!!"+definition);
    	}
    	
    	if (definition.equalsIgnoreCase("domain")){
    		Axiom roleImplication = null;
    		
    		ConceptExpression body = ConceptExpression.and(conex, conex_top);
    		
    		roleImplication = Axiom.roleImplies(body, rolex);
    		
//    		System.out.println(roleImplication.toString());
    		
    		axioms.addElement(roleImplication);
    		
    	}else if (definition.equalsIgnoreCase("range")){
    		Axiom roleImplication = null;
    		
    		ConceptExpression body = ConceptExpression.and(conex_top, conex);
    		
    		roleImplication = Axiom.roleImplies(body, rolex);
    		
//    		System.out.println(roleImplication.toString());
    		
    		axioms.addElement(roleImplication);
    	}else{
    		throw new ParseException("Wrong KRSS concept definition for statement: " + definition);//System.err.println("Wrong KRSS concept definition!!!!"+definition);
    	}
    	
    	//axioms.trimToSize();
    }
    
    
    //RESOLVE COMPLEX!!!!
    private ConceptExpression resolveComplex (String clause)  throws OutsideDLPException, ParseException{
    	
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
    	//System.out.println(defClause);
    	int nest = 0;
    	StringBuffer andorClause = new StringBuffer();
    	
    	for (int i = 0; i < defClause.length(); i++ ) {
    		switch (defClause.charAt(i)){
    			case ' ':
    				if (nest == 0){
    					AndOrConcepts.addElement(andorClause.toString());
    					//System.out.println(andorClause.toString());
            			andorClause.setLength(0);
    				}else{
    					andorClause.append(defClause.charAt(i));
    				}
    			break;
    			case '(':
    				andorClause.append(defClause.charAt(i));
    				nest++;
    			break;
    			case ')':
    				nest--;
    				andorClause.append(defClause.charAt(i));
    				if (nest == 0){
    					AndOrConcepts.addElement(andorClause.toString());
    					//System.out.println(andorClause.toString());
            			andorClause.setLength(0);
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
    		throw new ParseException("Wrong KRSS operator: " + operator);//System.err.println("Wrong KRSS operator!!!!"+operator);
    	}
    
    	
    	return result;
    }
    
    
    private ConceptExpression simpleExpression (String concept1) throws ParseException{
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
    
    private int scanComplex (String defClause) throws ParseException{
    	//StringBuffer left = new StringBuffer();
//    	System.out.println(defClause);
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
   
    /*
    public static final Clause singleClause(String clauseToString){
    	KRSSFuzzyParser newparser = new KRSSFuzzyParser(clauseToString);
    	Clause c = null;
    	Clause cg = null;
    	for (int j = 0; j < newparser.subsClauses.size(); j++){
    		String clause = ((String)(newparser.subsClauses.elementAt(j)).toString()).toLowerCase();
    		c = LogicFactory.newClause(clause, Degree.relationDegree);
    		Enumeration<Predicate> e = c.getBody();
    		cg = LogicFactory.newFactClause(e.nextElement().toString(), Degree.relationDegree);
    		//kb.addClause(c);
    		//kb.addClause(cg);
    	}
    	return cg;
    }
    */
    
    public Vector<String> separateSimple(String expression) throws ParseException{
    	StringBuffer andorClause = new StringBuffer();
    	Vector<String> concepts = new Vector<String>();
    	for (int i = 0; i < expression.length(); i++ ) { 
    	        
            switch (expression.charAt(i)){
                case ' ':
                	concepts.addElement(andorClause.toString());
                	andorClause.setLength(0);
                break;
                default: 
                	andorClause.append(expression.charAt(i));
               }
         }
         //last simple expression
         String clearWhiteSpace = andorClause.toString();
         clearWhiteSpace = clearWhiteSpace.trim();
         concepts.addElement(clearWhiteSpace);
         andorClause.setLength(0);
    
         return concepts;
    }
    
    public final KnowledgeBase getKB(){
    	TBox tb = new TBox(axiomCount);
    	KnowledgeBase kb = new KnowledgeBase();
    	for (Enumeration<Axiom> eax = axioms.elements(); eax.hasMoreElements();){
    		Axiom ax = (Axiom)eax.nextElement();
    		tb.addAxiom(ax);
    	}
    	//System.out.println("ROLES IN KB: ");
    	for (Enumeration<Role> er = roles.elements(); er.hasMoreElements();){
    		Role r = (Role)er.nextElement();
    		//System.out.println(r.getName());
    		tb.add(r);
    	}
    	TBox2KB.addToKnowledgeBase(tb, kb, subconceptChar, weightedConcepts);
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
    	
    	for (Enumeration<Predicate> e = subsClauses.elements(); e.hasMoreElements();){
//    		String clause = ((String)subsClauses.elementAt(j)).toLowerCase();
//    		Clause c = LogicFactory.newClause(clause, Degree.relationDegree);
//    		Enumeration<Predicate> e = c.getBody();
    		Predicate p = e.nextElement();
    		Clause cg = LogicFactory.newFactClause(p.toString(), p.getDegree());
    		//kb.addClause(c);
    		kb.addClause(cg);
    	}
    	
    	//kb.addClause(LogicFactory.newClause("foo(a)."));
    	return kb;
    }
    
}
