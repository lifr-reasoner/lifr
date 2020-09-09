/*
 * LogicFactory.java
 *
 * Created on 23. September 2004, 13:24
 *
 * Pocket KrHyper -
 * an automated theorem proving library for the
 * Java 2 Platform, Micro Edition (J2ME)
 * Copyright (C) 2005 Thomas Kleemann and Alex Sinner
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package lifr.logic.firstorder;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import lifr.logic.fuzzy.Degree;
import lifr.logic.fuzzy.Weight;

/**
 * A Factory class which manages creation of new instances of logic elements.
 * @author  sinner
 */
public final class LogicFactory {
    
    /**
     * Creates a new clause. The syntax is standard Prolog/Protein syntax:
     * headPredicate1;...;headPredicaten:-bodyPredicate1,...,bodyPredicaten.
     * Spaces are not recommended. Either head or body may be omitted. If the
     * body is omitted, the delimiter ':-' must also be omitted.<br>
     * Examples:<br>
     * <ul>
     * <li> p(X,Z):-p(X,Y),p(Y,Z).
     * <li> p(X,X).
     * <li> p(X,a);p(a,X).
     * <li> :-p(X,f(X)).
     * </ul>
     *
     * Clauses do not need to be unique, so there is no central hashtable for
     * all clauses.
     * @param clause A clause in Protein syntax.
     * @return a new instance of clause.
     */
    public static final Clause newClause(String clause, Degree deg){
        String clauseDelimiter=":-";
        int impIndex = clause.indexOf(clauseDelimiter);
        Vector<Predicate> headVector = null;
        Vector<Predicate> bodyVector = null;
        StringBuffer parseB = new StringBuffer();
        if (impIndex == -1){//Fact or Disjoint
            headVector = new Vector<Predicate>();
        } else if (impIndex == 0){//Empty Head
            bodyVector = new Vector<Predicate>();
        } else {
            bodyVector = new Vector<Predicate>();
            headVector = new Vector<Predicate>();
        }
        if (headVector != null){
            headEnumeration:
                for (int i=0 ; i < clause.length() ; i++){ //process head
                char c = clause.charAt(i);
                if ((c!=' ') && (c!='\n')){
                    if ((c == ';') || (c == '.')){
                        headVector.addElement(getPredicate(parseB.toString()));
                        parseB.setLength(0); //delete the buffer
                    } else if ( c == ':'){
                        headVector.addElement(getPredicate(parseB.toString()));
                        parseB.setLength(0); //delete the buffer
                        break headEnumeration;
                    } else {
                        parseB.append(c);
                    }
                }
                }
            for (int i = 0 ; i < headVector.size() ; i++){
                if (headVector.elementAt(i).equals(LogicFactory.getPredicate("false"))){
                    headVector.removeElementAt(i);
                    i--;
                }
            }
            //free unused space.
            headVector.trimToSize();
            if (headVector.size() == 0){
                headVector = null;
            }
        }
        if (bodyVector != null){
            int parStack = 0;
            for (int i=impIndex+clauseDelimiter.length() ; i < clause.length() ; i++){ //process head
                char c = clause.charAt(i);
                if ((c!=' ') && (c!='\n')){
                    if (c == '('){
                        parStack++;
                        parseB.append(c);
                    } else if (c == ')'){
                        parStack--;
                        parseB.append(c);
                    } else if ((parStack == 0) && ((c == ',') || (c == '.')) ){
                        bodyVector.addElement(getPredicate(parseB.toString()));
                        parseB.setLength(0); //delete the buffer
                    } else {
                        parseB.append(c);
                    }
                }
            }
            //free unused space.
            bodyVector.trimToSize();
        }
        Clause c = new Clause(headVector, bodyVector, deg);
        return c;
    }
    
    public static final Clause newClause(String clause){
    	return newClause(clause, Degree.EmptyDegree);
    }
    
    public static final Clause newFactClause(String clause, Degree degree){
    	Vector<Predicate> headVector = new Vector<Predicate>(1);
    	
    	Predicate p = getPredicate(clause.substring(0, clause.lastIndexOf('.')), degree);
    	String pName = p.getName();
    	String pString = clause.substring(0, clause.lastIndexOf('.'));
    	for (Enumeration<String> e = PREDICATES.keys(); e.hasMoreElements();){
    		String ps = e.nextElement();
    		if (PREDICATES.get(ps).getName().equalsIgnoreCase(pName)){
    			p.setWeight(PREDICATES.get(ps).getWeight());
    			PREDICATES.put(pString, p);
    		}
    	}
        headVector.addElement(p);


        Clause c = new Clause(headVector, null, degree);
        return c;
    }
    
    public static final Clause newFactClause(String clause, Degree degree, Weight weight){
        Vector<Predicate> headVector = new Vector<Predicate>(1);

        headVector.addElement(getPredicate(clause.substring(0, clause.lastIndexOf('.')), degree, weight));


        Clause c = new Clause(headVector, null, degree);
        return c;
    }
    
    /**
     * Create a new clause from a body and head.
     * @param head the head of the clause.
     * @param body the body of the clause.
     * @return a new instance of a clause.
     */
    public static final Clause newClause(Vector<Predicate> head, Vector<Predicate> body){
        return new Clause(head, body, Degree.EmptyDegree);
    }
    
    /**
     * Create a new clause from a body and head.
     * @param head the head of the clause.
     * @param body the body of the clause.
     * @return a new instance of a clause.
     */
    public static final Clause newClause(Vector<Predicate> head, Vector<Predicate> body, Degree degree){
        return new Clause(head, body, degree);
    }
    
    /**
     * Checks whether a Predicate with the same name and terms exists,
     * creates amd registers if not and returns a unique instance.
     * @param name the name of the predicate
     * @param terms the terms of the predicate
     * @return an instance of the named predicate with the given terms.
     */
    public static final Predicate newPredicate(String name, Vector<Term> terms){
        //Predicate p = new Predicate(name, terms);
        //p.toString();
        String pString = Predicate.name(name.toLowerCase(), terms);
        if (!PREDICATES.containsKey(pString)){
            Predicate p = new Predicate(name.toLowerCase(), terms);
            //System.out.println("New predicate: "+p.toString());
            PREDICATES.put(pString, p);
            return p;
        }
        return (Predicate)PREDICATES.get(pString);
    }
    
    //TO PANW MOLIS GINOUN OI PRAKSEIS NA FYGEI ENTELWS (???)
    public static final Predicate newPredicate(String name, Vector<Term> terms, Degree degree){
        //Predicate p = new Predicate(name, terms);
        //p.toString();
        String pString = Predicate.name(name.toLowerCase(), terms);
        if (!PREDICATES.containsKey(pString)){
            Predicate p = new Predicate(name.toLowerCase(), terms, degree, Weight.defaultWeight);
            //System.out.println("New predicate: "+p.toString());
            PREDICATES.put(pString, p);
            return p;
        }
        Predicate p = (Predicate)PREDICATES.get(pString);
        p.setDegree(degree);
        return p;
        //return (Predicate)PREDICATES.get(pString);
    }
    
    public static final Predicate newPredicate(String name, Vector<Term> terms, Weight weight){
        //Predicate p = new Predicate(name, terms);
        //p.toString();
        String pString = Predicate.name(name.toLowerCase(), terms);
        if (!PREDICATES.containsKey(pString)){
            Predicate p = new Predicate(name.toLowerCase(), terms, Degree.EmptyDegree, weight);
            //System.out.println("New predicate: "+p.toString());
            PREDICATES.put(pString, p);
            return p;
        }
        Predicate p = (Predicate)PREDICATES.get(pString);
        p.setWeight(weight);
        return p;
        //return (Predicate)PREDICATES.get(pString);
    }
    
    public static final Predicate newPredicate(String name, Vector<Term> terms, Degree degree, Weight weight){
        //Predicate p = new Predicate(name, terms);
        //p.toString();
        String pString = Predicate.name(name.toLowerCase(), terms);
        if (!PREDICATES.containsKey(pString)){
            Predicate p = new Predicate(name.toLowerCase(), terms, degree, weight);
            //System.out.println("New predicate: "+p.toString());
            PREDICATES.put(pString, p);
            return p;
        }
        Predicate p = (Predicate)PREDICATES.get(pString);
        p.setDegree(degree);
        p.setWeight(weight);
        PREDICATES.put(pString, p);
        return p;
        //return (Predicate)PREDICATES.get(pString);
    }
    
    /*
    public static final Predicate newNonExistingPredicate(String name, Degree degree){
    	if (degree == null) degree = Degree.EmptyDegree;
        Predicate p  = (Predicate)PREDICATES.get(name);
        if (p == null){
            p = makePredicate(name);
            p.setDegree(degree);
        }else p = null;
        return p;
    }
    */
    
    
    /**
     * Creates a new Predicate (if necessary) and returns it.
     * if the name of the predicate contains parentheses (e.g. p(X)),
     * all necessary subterms are also created.
     * If a given predicate already exists, no new instance is created, rather the
     * existing instance is returned.
     * @param name a string representation of the predicate.
     * @return an instance of the named predicate.
     */
    public static final Predicate getPredicate(String name, Degree degree, Weight weight){
        Predicate p  = (Predicate)PREDICATES.get(name);
        if (p == null){
//        	System.out.println(name);
            p = makePredicate(name, degree, weight);
        }
        return p;
    }
    
    public static final Predicate getPredicate(String name, Degree degree){
    	return getPredicate(name, degree, Weight.defaultWeight);
    }
    
    public static final Predicate getPredicate(String name){
        return getPredicate(name, Degree.EmptyDegree, Weight.defaultWeight);
    }
    
    public static final Predicate makePredicate (String name, Degree degree, Weight weight){
    	Predicate p = null;
    	
    	if (name.indexOf('(') > 0){
                int startPar = name.indexOf('(');
                int endPar = name.lastIndexOf(')');
                String predicateName = name.substring(0, startPar);
                String terms = name.substring(startPar+1, endPar);
                Vector<Term> subTerms = new Vector<Term>();
                while( terms.length()>0){
                    int stackSize = 0;
                    int endSubTerm = -1;
                    for (int i = 0 ; i< terms.length() ; i++){
                        if (terms.charAt(i) == '('){
                            stackSize++;
                        }else if (terms.charAt(i) == ')'){
                            stackSize--;
                        }else if ((terms.charAt(i) == ',') && (stackSize == 0)){
                            endSubTerm = i;
                            break;
                        }
                    }
                    if (endSubTerm == -1){
                        subTerms.addElement(LogicFactory.getTerm(terms));
                        terms="";
                    } else {
                        subTerms.addElement(LogicFactory.getTerm(terms.substring(0, endSubTerm)));
                        terms = terms.substring(endSubTerm+1);
                    }
                }
                subTerms.trimToSize();
                p = new Predicate(predicateName, subTerms);
            } else {
                p = new Predicate(name);
            }
            p.setDegree(degree);
            p.setWeight(weight);
            PREDICATES.put(name, p);
            
            return p;
    }
    
    /**
     * Creates a new Term (if necessary) and returns it.
     * if the name of the term contains parentheses (e.g. f(x),  it is considered to
     * be a function, if it is lower case without parentheses, it is considered
     * to be a constant, otherwise it is considered to be a variable.
     * In case of functions, all necessary subterms are also created.
     * If a given term already exists, no new instance is created, rather the
     * existing instance is returned.
     * @param name a String representing the term.
     * @return an instance of the named term.
     */
    public static final Term getTerm(String name){
        name = name.trim();
        if (name.indexOf('(') > 0){
            return getFunction(name);
        } else if (Character.isLowerCase(name.charAt(0)) || name.charAt(0) == '\''){
            return getConstant(name);
        } else return getVariable(name);
    }
    
    /**
     * Create a new instance of Variable if it does not yet exist.
     * @param name the name of the variable
     * @return the only instance of a Variable of the given name.
     */
    public static final Variable newVariable(String name){
        StringBuffer s = new StringBuffer(name);
        s.setCharAt(0, Character.toUpperCase(s.charAt(0)));
        name = s.toString();
        if (!VARIABLES.containsKey(name)){
            Variable v = new Variable(name);
            VARIABLES.put(name, v);
            return v;
        }
        return (Variable)VARIABLES.get(name);
    }
    
    /**
     * Create a new instance of Constant if it does not yet exist.
     * @param name the name of the constant
     * @return the only instance of a Constant of the given name.
     */
    public static final Constant newConstant(String name){
        StringBuffer s = new StringBuffer(name);
        s.setCharAt(0, Character.toLowerCase(s.charAt(0)));
        name = s.toString();
        if (!CONSTANTS.containsKey(name)){
            Constant c = new Constant(name);
            CONSTANTS.put(name, c);
            return c;
        }
        return (Constant)CONSTANTS.get(name);
    }
    
    /**
     * Create a new instance of Function if it does not yet exist.
     * @param name the name of the function
     * @param terms the subterms of the function
     * @return the only instance of a Function of the given name and subterms.
     */
    public static final Function newFunction(String name, Vector<Term> terms){
        String fString = Function.name(name, terms);
        if (!FUNCTIONS.containsKey(fString)){
            Function f = new Function(name, terms);
            FUNCTIONS.put(fString, f);
            return f;
        }
        return (Function)FUNCTIONS.get(fString);
    }
    
    /**
     * Create a new instance of Function if it does not yet exist.
     * @param name the name of the function
     * @param term the only subterm of the function
     * @return the only instance of a Function of the given name and subterm.
     */
    public static final Function newFunction(String name, Term term){
        Vector<Term> terms = new Vector<Term>();
        terms.addElement(term);
        String fString = Function.name(name, terms);
        if (!FUNCTIONS.containsKey(fString)){
            Function f = new Function(name, terms);
            FUNCTIONS.put(fString, f);
            return f;
        }
        return (Function)FUNCTIONS.get(fString);
    }
    
    
    /** Creates a new Variable (if necessary) and returns it. The name has to be upper
     * case, otherwise the constructor makes it upper case.
     * If the variable name already exists, the existing instance is returned.
     */
    private static final Variable getVariable(String name) {
        Variable v = (Variable)VARIABLES.get(name);
        if (v == null){
            v = new Variable(name);
            VARIABLES.put(name, v);
        }
        return v;
    }
    
    /** Creates a new function (if necessary) and returns it. All subterms are
     * also created if necessary. Functions are lower case and have subterms.
     * All Function instances are stored in LogicFactory.FUNCTIONS.
     */
    private static Function getFunction(String data) {
//    	System.out.println(data);
        Function f = (Function)FUNCTIONS.get(data);
        if (f == null){
            int startPar = data.indexOf('(');
            int endPar = data.lastIndexOf(')');
            String functionName = data.substring(0, startPar);
            String terms = data.substring(startPar+1, endPar);
            Vector<Term> subTerms = new Vector<Term>();
            while( terms.length()>0){
                int stackSize = 0;
                int endSubTerm = -1;
                for (int i = 0 ; i< terms.length() ; i++){
                    if (terms.charAt(i) == '('){
                        stackSize++;
                    }else if (terms.charAt(i) == ')'){
                        stackSize--;
                    }else if ((terms.charAt(i) == ',') && (stackSize == 0)){
                        endSubTerm = i;
                        break;
                    }
                }
                if (endSubTerm == -1){
                    subTerms.addElement(LogicFactory.getTerm(terms));
                    terms="";
                } else {
                    subTerms.addElement(LogicFactory.getTerm(terms.substring(0, endSubTerm)));
                    terms = terms.substring(endSubTerm+1);
                }
            }
            subTerms.trimToSize();
            f = new Function(functionName, subTerms);
            FUNCTIONS.put(data, f);
        }
        return f;
    }
    
    /** Creates a new Variable and returns it. The name has to be upper
     * case, otherwise the constructor makes it upper case.
     * If the variable name already exists, the existing instance is returned.
     */
    private static final Constant getConstant(String name) {
        Constant c = (Constant)CONSTANTS.get(name);
        if (c == null){
            c = new Constant(name);
            CONSTANTS.put(name, c);
        }
        return c;
    }
    
    /**
     * Clear all hashtables.
     */
    public static void cleanup(){
        VARIABLES.clear();
        CONSTANTS.clear();
        FUNCTIONS.clear();
        PREDICATES.clear();
    }
    
    public static void initialize(){
    	for (Enumeration<String> e = PREDICATES.keys(); e.hasMoreElements();){
    		String key = e.nextElement();
    		Predicate p = PREDICATES.get(key);
    		p.setDegree(Degree.EmptyDegree);
    		p.setWeight(Weight.defaultWeight);  //kainouria grammi
    		PREDICATES.put(key, p);
    	}
    }
    
    public static LogicFactoryTerms preserve(LogicFactoryTerms terms){
    	terms.setVariables(VARIABLES);
    	terms.setConstants(CONSTANTS);
    	terms.setFunctions(FUNCTIONS);
    	terms.setPredicates(PREDICATES);
    	return terms;
    }
    
    public static void restore(LogicFactoryTerms terms){
        PREDICATES.clear();
    	VARIABLES = terms.getVariables();
    	CONSTANTS = terms.getConstants();
    	FUNCTIONS = terms.getFunctions();
    	PREDICATES =terms.getPredicates();
    }
    
    public static String LFtoString(){
    	StringBuffer buffer = new StringBuffer();
        buffer.append("\nVARIABLES: ");
        for(java.util.Enumeration<String> e = VARIABLES.keys(); e.hasMoreElements();){
        	buffer.append(e.nextElement().toString()+"\n");
        }
        buffer.append("\nCONSTANTS: ");
        for(java.util.Enumeration<String> e = CONSTANTS.keys(); e.hasMoreElements();){
        	buffer.append(e.nextElement().toString()+"\n");
        }
        buffer.append("\nFUNCTIONS: ");
        for(java.util.Enumeration<String> e = FUNCTIONS.keys(); e.hasMoreElements();){
        	buffer.append(e.nextElement().toString()+"\n");
        }
        buffer.append("\nPREDICATES: ");
        buffer.append("\nKEYS: ");
        for(java.util.Enumeration<String> e = PREDICATES.keys(); e.hasMoreElements();){
        	buffer.append(e.nextElement().toString()+"\n");
        }
        buffer.append("\nVALUES: ");
        for(java.util.Enumeration<Predicate> e = PREDICATES.elements(); e.hasMoreElements();){
        	buffer.append(e.nextElement().toString()+"\n");
        }
        return buffer.toString();
    }
    
    public static Hashtable<String, Predicate> getAllPredicates(){
    	return PREDICATES;
    }
    
    public static Hashtable<String, Function> getAllFunctions(){
    	return FUNCTIONS;
    }
    
    private static Hashtable<String, Variable> VARIABLES = new Hashtable<String, Variable>(100);
    private static Hashtable<String, Constant> CONSTANTS = new Hashtable<String, Constant>(200);
    private static Hashtable<String, Function> FUNCTIONS = new Hashtable<String, Function>(200);
    private static Hashtable<String, Predicate> PREDICATES = new Hashtable<String, Predicate>(10000);
    
    /**
     * Private constructor to forbid inheritance and instantiation.
     */
    private LogicFactory() {
    }
}
