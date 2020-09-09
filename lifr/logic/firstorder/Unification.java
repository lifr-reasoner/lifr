/*
 * Unification.java
 *
 * Created on 2. November 2004, 15:34
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
import java.util.Vector;

/**
 * This class implements a standard unification algorithm.
 * A most general unifier is returned while unifying two predicates.
 * @author  sinner
 * @version $Name:  $ $Revision: 1.7 $
 */
public class Unification {
    
    /**
     * Check whether a Predicate p1 subsumes (i.e. is more general) than a
     * Predicate p2. This is quite similar to unification.
     * Precondition : Variables from p2 must not occur in p1!
     * @param p1 a first predicate
     * @param p2 a second predicate
     * @return true if p1 >= p2
     */
    public static boolean subsumes(Predicate p1, Predicate p2){
        // two different predicates do not subsume!
        if (!(p1.getName().equals(p2.getName())) || (p1.arity() != p2.arity())){
            return false;
        }
        if (p1.arity() == 0){ //two same propositional predicates subsume each other.
            return true;
        }
        //Substitution s = new Substitution();
        Vector<Term[]> termEquations = new Vector<Term[]>();
        for (Enumeration<Term> e1 = p1.getTerms(), e2 = p2.getTerms() ; e1.hasMoreElements() && e2.hasMoreElements() ; ){
            Term t1 = (Term) e1.nextElement();
            Term t2 = (Term) e2.nextElement();
            if (checkSubsumptionConflict(t1, t2, null)){
                return false;
            }
            if (!deleteSubsumption(t1, t2)){
                Term[] foo = {t1, t2};
                termEquations.addElement(foo);
            }
        }
        if (termEquations.size() <= 0){
            return true;
        }
        
        for (int i = 0 ; i < termEquations.size() ; i++ ){
            Term[] currentEq = (Term[]) termEquations.elementAt(i);
            if (checkSubsumptionConflict(currentEq[0], currentEq[1], termEquations)){
                return false;
            } else {
                if (decompose(currentEq[0], currentEq[1])){
                    for (Enumeration<Term> e1 = currentEq[0].getSubTerms(), e2 = currentEq[1].getSubTerms() ; e1.hasMoreElements() && e2.hasMoreElements();){
                        Term[] foo = {(Term)e1.nextElement(), (Term)e2.nextElement()};
                        termEquations.addElement(foo);
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Unify two predicates.
     * If they are not unifiable, null is returned, otherwise a (possibly empty)
     * Substitution transforming p1 into p2.
     * Precondition : Variables from p2 must not occur in p1!
     * @param p1 the first Predicate
     * @param p2 the second Predicate
     * @return null if p1 and p2 are not unifiable, else a substitution unifying p1 and p2
     */
    public static Substitution unify(Predicate p1, Predicate p2){
        // two different predicates are not unifiable
        if (!(p1.getName().equals(p2.getName())) || (p1.arity() != p2.arity())){
            return null;
        }
        if (p1.arity() == 0){
            return Substitution.EMPTYSUBSTITUTION;
        }
        Substitution s = new Substitution();
        Vector<Term[]> termEquations = new Vector<Term[]>();
        for (Enumeration<Term> e1 = p1.getTerms(), e2 = p2.getTerms() ; e1.hasMoreElements() && e2.hasMoreElements() ; ){
            Term t1 = (Term) e1.nextElement();
            Term t2 = (Term) e2.nextElement();
            if (checkConflict(t1, t2)){
                return null;
            }
            if (!delete(t1, t2)){
                Term[] foo = {t1, t2};
                termEquations.addElement(foo);
            }
        }
        
        if (termEquations.size() <= 0){
            // if there are no termequations, this means the predicates
            // are unifiable with an empty substitution
            return Substitution.EMPTYSUBSTITUTION;
        }
        boolean loop = true;
        while (loop){
            loop = false;
            for (int i = 0 ; i < termEquations.size() ; i++ ){
                Term[] currentEq = (Term[]) termEquations.elementAt(i);
                // eliminate same variables
                if (delete(currentEq[0], currentEq[1])){
                    termEquations.removeElementAt(i);
                    i--; // we have one less element!
                    // conflict check
                } else if (checkConflict(currentEq[0], currentEq[1])){
                    return null;
                    // occur check
                } else if (occurCheck(currentEq[0], currentEq[1])){
                    return null;
                } else {
                    // orient
                    if (orient(currentEq[0], currentEq[1])){
                        Term foo = currentEq[0];
                        currentEq[0] = currentEq[1];
                        currentEq[1] = foo;
                        foo = null;
                        loop = true;
                    }
                    // decompose
                    if (decompose(currentEq[0], currentEq[1])){
                        for (Enumeration<Term> e1 = currentEq[0].getSubTerms(), e2 = currentEq[1].getSubTerms() ; e1.hasMoreElements() && e2.hasMoreElements();){
                            Term[] foo = {(Term)e1.nextElement(), (Term)e2.nextElement()};
                            termEquations.addElement(foo);
                        }
                        termEquations.removeElementAt(i);
                        i--;
                        loop = true;
                    }
                    // eliminate and coalesce
                    if (eliminate(currentEq[0], termEquations, i)){
                        Substitution eli = new Substitution();
                        eli.addSubstitution((Variable)currentEq[0], currentEq[1]);
                        for (int eq = 0 ; eq < termEquations.size() ; eq++ ){
                            if (eq != i){
                                ((Term[])termEquations.elementAt(eq))[0] = eli.applyToTerm(((Term[])termEquations.elementAt(eq))[0]);
                                ((Term[])termEquations.elementAt(eq))[1] = eli.applyToTerm(((Term[])termEquations.elementAt(eq))[1]);
                            }
                        }
                        loop = true;
                    }
                }
            }
        }
        for (Enumeration<Term[]> e = termEquations.elements(); e.hasMoreElements() ;){
            Term[] foo = (Term[])e.nextElement();
            s.addSubstitution((Variable)foo[0], foo[1]);
        }
        return s;
    }
    
    /**
     * Check for incompatible functions/constants
     */
    private static final boolean checkConflict(Term t1, Term t2){
        if ((t1.hasType(TermType.VARIABLE)) ||(t2.hasType(TermType.VARIABLE))){
            return false;
        } else if (t1.getName().equals(t2.getName()) && t1.arity() == t2.arity()){
            return false;
        } else {
            return true;
        }
    }
    
    private static final boolean checkSubsumptionConflict(Term t1, Term t2, Vector<Term[]> termEquations){
        if (t1.hasType(TermType.VARIABLE)){
            if (termEquations == null) return false;
            for (Enumeration<Term[]> e = termEquations.elements() ; e.hasMoreElements() ;){
                Term[] eq = (Term[]) e.nextElement();
                if (t1.equals(eq[0]) && !(t2.equals(eq[1]))){
                    return true;
                }
            }
            return false;
        } else if (t1.getName().equals(t2.getName()) && t1.arity() == t2.arity()){
            return false;
        }  else {
            return true;
        }
    }
    /**
     * Make an occur check (assignment of a variable to a function containing the
     * same variable
     */
    private static final boolean occurCheck(Term t1, Term t2){
        // check in both directions
        if (t1.hasType(TermType.VARIABLE)){
            for (Enumeration<Term> e = t2.getAllSubTerms(); e.hasMoreElements();){
                if (t1.equals((Term)e.nextElement())){
                    return true;
                }
            }
        } else if (t2.hasType(TermType.VARIABLE)){
            for (Enumeration<Term> e = t1.getAllSubTerms(); e.hasMoreElements();){
                if (t2.equals((Term)e.nextElement())){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Check whether an equation of terms qualifies for elimination.
     */
    private static final boolean delete(Term t1, Term t2){
        return (t1.equals(t2));
    }
    
    private static final boolean deleteSubsumption(Term t1, Term t2){
        if (t1.hasType(TermType.VARIABLE)){
            return false;
        } else {
            return (t1.equals(t2));
        }
    }
    /**
     * check whether we have to swap an equation.
     */
    private static final boolean orient(Term t1, Term t2){
        return (!t1.hasType(TermType.VARIABLE) && t2.hasType(TermType.VARIABLE));
    }
    
    /**
     * check whether an equation is decomposable into the subterms of functions
     */
    private static final boolean decompose(Term t1, Term t2){
        return (t1.hasType(TermType.FUNCTION)
        && t2.hasType(TermType.FUNCTION)
        && t1.getName().equals(t2.getName())
        && (t1.arity() == t2.arity()));
    }
    
    /**
     * check whether we can eliminate a variable by applying the corresponding
     * substitution to all other equations.
     */
    private static final boolean eliminate(Term t, Vector<Term[]> termEquations, int ignoreIndex){
        if (t.hasType(TermType.VARIABLE)){
            for (int i = 0; i< termEquations.size(); i++){
                if (i != ignoreIndex){
                    Term[] foo = (Term[])termEquations.elementAt(i);
                    for (Enumeration<Term> vars = foo[0].getAllSubTerms() ; vars.hasMoreElements();){
                        Term next = (Term) vars.nextElement();
                        if (next.equals(t)){
                            return true;
                        }
                    }
                    for (Enumeration<Term> vars = foo[1].getAllSubTerms() ; vars.hasMoreElements();){
                        Term next = (Term) vars.nextElement();
                        if (next.equals(t)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
}
