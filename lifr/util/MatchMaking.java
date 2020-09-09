/**
 * @(#)MatchMaking.java
 *
 *
 * @author 
 * @version 1.00 2007/11/22
 */
package lifr.util;

import java.util.*;

import lifr.logic.dl.*;
import lifr.logic.firstorder.*;
import lifr.logic.transform.*;
//import java.lang.*;


public class MatchMaking {
	
	protected KnowledgeBase kb, tempKb;
	//private ConceptExpression expr1, expr2;
	private Domain domain;
	private Vector<Term> herbrand;
	private Vector<String> KBClauses;

    public MatchMaking() {
    	herbrand = null;
    	KBClauses = new Vector<String>();
    }

    public void MakeMatch (ConceptExpression dl1, ConceptExpression dl2){
    	TBox tb = createTbox(dl1, dl2);
    	tempKb = new KnowledgeBase();
    	try {	TBox2KB.addToKnowledgeBase(tb, tempKb);
    	}catch (Exception e){}
    	
    	String negation = "";
    	try
    	{
    	for (Enumeration<Clause> e = tempKb.rules(); e.hasMoreElements();){
    		Clause c = (Clause)e.nextElement();
    		KBClauses.addElement(c.toString());
    		for (Enumeration<Predicate> eh = c.getHead(); eh.hasMoreElements();){
    			Predicate ph = (Predicate)eh.nextElement();
    			Clause ch = LogicFactory.newClause(ph.toString()+".");
    			if (!KBClauses.contains(ch.toString())){
    				tempKb.addClause(ch);
    				KBClauses.addElement(ch.toString());
    			}
    		}
    		for (Enumeration<Predicate> eb = c.getBody(); eb.hasMoreElements();){
    			Predicate pb = (Predicate)eb.nextElement();
    			
    			Clause cb = LogicFactory.newClause(pb.toString()+".");
    			
    				
    			if (eb.hasMoreElements()) negation = pb.toString()+",";
    			else negation = pb.toString()+".";
    			
    			Clause neg = LogicFactory.newClause("false:-"+negation);
    			
    			if (!KBClauses.contains(cb.toString())){
    				tempKb.addClause(cb);
    				KBClauses.addElement(cb.toString());
    			}
    			if (!KBClauses.contains(neg.toString())){
    				tempKb.addClause(neg);
    				KBClauses.addElement(neg.toString());
    			}
    		}
    	}
    	}catch (Exception e){
    	}
    	
    	
    	createGroundClauses();
    }
    
        
    private void createGroundClauses(){
    	domain = new Domain(tempKb);
    	if (herbrand == null){ // we have to enumerate the herbrand universe once for each term weight
            herbrand = domain.getHerbrandUniverse(1);
        }
        Vector<Clause> groundClauses = new Vector<Clause>();
    	for (Enumeration<Clause> ef = tempKb.facts() ; ef.hasMoreElements() ;){
    		Clause c = (Clause) ef.nextElement();
    		groundClauses.addElement(ground(c));
    	}
    	 	
    	for (Enumeration<Clause> er = tempKb.rules() ; er.hasMoreElements() ;){
    		Clause c = (Clause) er.nextElement();
    		groundClauses.addElement(ground(c));
    	}
    	
    	for (Enumeration<Clause> eq = tempKb.queries() ; eq.hasMoreElements() ;){
    		Clause c = (Clause) eq.nextElement();
    		groundClauses.addElement(ground(c));
    	}
    	
    	
    	for (Enumeration<Clause> egr = groundClauses.elements() ; egr.hasMoreElements() ;){
    		Clause cground = LogicFactory.newClause(egr.nextElement().toString());
    		tempKb.addClause(cground);
    		KBClauses.addElement(cground.toString());
    	}
    	//System.out.println("\n\nKB:\n"+kb.toString());
    }
    
    private Clause ground(Clause c){
    	Vector<Variable> vars = new Vector<Variable>();
        for (Enumeration<Predicate> doms = c.getBody() ; doms.hasMoreElements() ; ){
            Predicate domp = (Predicate) doms.nextElement();
            for (Enumeration<Variable> vare = domp.getVariables() ; vare.hasMoreElements() ;){
                Variable v = (Variable)vare.nextElement();
                if (!vars.contains(v)){
                    vars.addElement(v);
                }
            }
        }
        
        for (Enumeration<Predicate> doms = c.getHead() ; doms.hasMoreElements() ; ){
            Predicate domp = (Predicate) doms.nextElement();
            for (Enumeration<Variable> vare = domp.getVariables() ; vare.hasMoreElements() ;){
                Variable v = (Variable)vare.nextElement();
                if (!vars.contains(v)){
                    vars.addElement(v);
                }
            }
        }
        
        Clause newClause = null;
        // Now we have both all domain variables and the herbrand universe, so we generate all ground instances
        for (int i = 0 ; i< vars.size() ; i++){
            for (Enumeration<Term> he = herbrand.elements() ; he.hasMoreElements() ;){
                Substitution s = new Substitution();
                s.addSubstitution((Variable)vars.elementAt(i), (Term) he.nextElement());
                newClause = s.applyToClause(c);
            }
        }
        
        return newClause;
        
    }
    
    private TBox createTbox(ConceptExpression dl1, ConceptExpression dl2){
    	TBox tb = new TBox(2);
    	//Axiom test = Axiom.conceptImplies(dl2,dl1);
    	Axiom test = Axiom.conceptImplies(dl1,dl2);
    	tb.addAxiom(test);
    	//System.out.println("TBox: "+tb.toString());
    	
    	return tb;
    }
        
    public KnowledgeBase getKB(){
    	return kb;
    }
    
    public void addToKB(){
    	kb = new KnowledgeBase();
    	for (Enumeration<String> e = KBClauses.elements(); e.hasMoreElements();){
    		String cname = (String)e.nextElement();
    		Clause c = LogicFactory.newClause(cname);
    		kb.addClause(c);
    	}
    }
    
}