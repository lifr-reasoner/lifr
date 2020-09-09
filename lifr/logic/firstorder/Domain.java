/*
 * Domain.java
 *
 * Created on 24. August 2005, 12:45
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

//import fpocketkrhyper.util.EmptyEnumeration;
import java.util.Enumeration;
//import java.util.Stack;
import java.util.Vector;

/**
 * Domain is a class for managing the Domain and Herbrand Universes of e.g. a
 * knowledge base. It contains a set of functions and constants which are
 * used to enumerate the Herbrand Universe of these terms.
 *
 * @author  sinner
 * @version $Name:  $ $Revision: 1.3 $
 */
public class Domain {
    
    /**
     * Creates a new instance of Domain
     * @param kb The knowledge base for which to set up a Domain.
     */
    public Domain(KnowledgeBase kb) {
        functions = new Vector<Function>();
        constants = new Vector<Constant>();
        collectTerms(kb);
        functions.trimToSize();
        constants.trimToSize();
    }
    
    /**
     * Get the set of terms in the domain.
     * @return an Enumeration of all terms.
     */
    public final Enumeration<Object> getTerms(){
        Vector<Object> all = new Vector<Object>(functions.size() + constants.size());
        for (Enumeration<Function> e = functions.elements() ; e.hasMoreElements() ;){
            all.addElement(e.nextElement());
        }
        for (Enumeration<Constant> e = constants.elements() ; e.hasMoreElements() ;){
            all.addElement(e.nextElement());
        }
        return all.elements();
    }
    
    /**
     * Get the Herbrand Universe up to a given term depth. This might be quite a lengthy operation if the term depth is too deep.
     * @param termdepth the term depth limit.
     * @return a Vector with all ground instances up to the given term depth.
     */
    public final Vector<Term> getHerbrandUniverse(int termdepth){
        Vector<Term> herbrand = new Vector<Term>();
        if (constants.isEmpty()){
            constants.addElement((Constant)LogicFactory.getTerm("a"));
        }
        // first add all constants to the herbrand universe
        for (Enumeration<Constant> e = constants.elements() ; e.hasMoreElements() ;){
            herbrand.addElement(e.nextElement());
        }
        //Vector<Term> substVector = new Vector<Term>();
        for (int currentTermDepth = 0 ; currentTermDepth < termdepth ; currentTermDepth++){
            Vector<Term> newGroundTerms = new Vector<Term>();
            for (Enumeration<Function> fe = functions.elements() ; fe.hasMoreElements() ;){
                Function current = (Function)fe.nextElement();
                Vector<Term> substituted = new Vector<Term>();
                substituted.addElement(current);
                
                for (Enumeration<Term> subTerms = current.getSubTerms(); subTerms.hasMoreElements();){
                    //Enumeration subTerms = current.getSubTerms();
                    
                    Variable currentVar = (Variable) subTerms.nextElement();
                    Vector<Term> substitutions = new Vector<Term>();
                    for (Enumeration<Term> herbrandEnum = herbrand.elements() ; herbrandEnum.hasMoreElements();){
                        Term groundTerm = (Term) herbrandEnum.nextElement();
                        Substitution newSubst = new Substitution();
                        newSubst.addSubstitution(currentVar, groundTerm);
                        // now apply
                        for (Enumeration<Term> e = substituted.elements() ; e.hasMoreElements() ;){
                            Function semifinished = (Function) e.nextElement();
                            substitutions.addElement(newSubst.applyToTerm(semifinished));
                        }
                    }
                    substituted = substitutions;
                }
                
                for (Enumeration<Term> e = substituted.elements() ; e.hasMoreElements() ;){
                    newGroundTerms.addElement(e.nextElement());
                }
            }
            for (Enumeration<Term> e = newGroundTerms.elements() ; e.hasMoreElements() ;){
                Term newGround = (Term) e.nextElement();
                if (!herbrand.contains(newGround)){
                    herbrand.addElement(newGround);
                }
            }
            /*
            //DEBUG
            for (Enumeration e = herbrand.elements() ; e.hasMoreElements() ;){
                if (e.hasMoreElements()) System.out.println("HERBRAND UNIVERSE: "+e.nextElement());
                else System.out.println("EMPTY HERBRAND UNIVERSE");
            }
            //////
            */
        }
        return herbrand;
    }
    
    private final void collectTerms(KnowledgeBase kb){
        collectTerms(kb.facts());
        collectTerms(kb.queries());
        collectTerms(kb.rules());
    }
    
    /**
     * Collect all terms from an enumeration of clauses.
     * @param e an Enumeration of Clause
     */
    private final void collectTerms(Enumeration<Clause> e){
        while(e.hasMoreElements()){
            Clause c = (Clause)e.nextElement();
            collectTerms(c);
        }
    }
    
    private final void collectTerms(Clause c){
        if (c.headSize() > 0){
            for (Enumeration<Predicate> e = c.getHead() ; e.hasMoreElements() ;){
                Predicate p = (Predicate) e.nextElement();
                collectTerms(p);
            }
        }
        if (c.bodySize() > 0){
            for (Enumeration<Predicate> e = c.getBody() ; e.hasMoreElements() ;){
                Predicate p = (Predicate) e.nextElement();
                if (p.arity() > 0){
                    collectTerms(p);
                }
            }
        }
    }
    
    private final void collectTerms(Predicate p){
        for (Enumeration<Term> e = p.getTerms() ; e.hasMoreElements();){
            Term t = (Term) e.nextElement();
            for (Enumeration<Term> terms = t.getAllSubTerms() ; terms.hasMoreElements() ;){
                // terms are only added if they are either a constant or a function
                // which have not been added previously
                addTerm((Term) terms.nextElement());
            }
        }
    }
    
    /**
     * Add constant and functions to the collected terms if they have not
     * been already added previously. Variables are silently ignored.
     * Functions are added as an instance of function with different
     * variable subterms.
     * @param t any term
     */
    private final void addTerm(Term t){
        if (t.hasType(TermType.CONSTANT)){
            if (!constants.contains((Constant)t)){
                constants.addElement((Constant)t);
            }
        } else if (t.hasType(TermType.FUNCTION)){
            Vector<Term> subTerms = new Vector<Term>(t.arity());
            for (int i=0 ; i<t.arity() ; i++){
                Variable x = LogicFactory.newVariable("X"+i);
                subTerms.addElement(x);
            }
            Term function = LogicFactory.newFunction(t.getName(), subTerms);
            if (!functions.contains((Function)function)){
                functions.addElement((Function)function);
            }
        }
    }
    
    private Vector<Function> functions;
    private Vector<Constant> constants;
}
