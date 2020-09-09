/*
 * Substitution.java
 *
 * Created on 8. Juli 2004, 16:23
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

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A substitution replaces Variables with Terms.
 * @author  sinner
 * @version $Name:  $ $Revision: 1.8 $
 */
public class Substitution {
    
    /** Creates a new instance of Substitution */
    public Substitution() {
        substitutions = new Hashtable<Variable, Term>();
    }
    
    /**
     * Add a new substitution, where a variable is to be replaced by another
     * term.
     *
     * @param v the Variable to be replaced
     * @param t the Term to replace the Variable
     */
    public void addSubstitution(Variable v, Term t){
        substitutions.put(v, t);
    }
    
    /**
     * Check whether a given Variable is to be substituted
     * @param v a given Variable
     * @return true if v is already to be substituted
     */
    public boolean containsSubstitutionFor(Variable v){
        return substitutions.containsKey(v);
    }
    
    /**
     * Applies the substitution to a clause. All necessary Variables are
     * replaced by their respective Terms.
     * The initial Clause is not modified, but a new Clause with the given
     * substitutions is returned.
     * @param c A clause to apply the substitution to
     * @return a new clause with the executed substitution
     */
    public Clause applyToClause(Clause c){
        boolean needToSubstitute = false;
        for (Enumeration<Predicate> e = c.getHead();e.hasMoreElements() && (!needToSubstitute);){
            Predicate p = (Predicate)e.nextElement();
            for (Enumeration<Variable> e1 = p.getVariables(); e1.hasMoreElements()&& (!needToSubstitute);){
                Variable v = (Variable)e1.nextElement();
                if (substitutions.get(v) != null){
                    needToSubstitute = true;
                }
            }
        }
        for (Enumeration<Predicate> e = c.getBody();e.hasMoreElements() && (!needToSubstitute);){
            Predicate p = (Predicate)e.nextElement();
            for (Enumeration<Variable> e1 = p.getVariables(); e1.hasMoreElements()&& (!needToSubstitute);){
                Variable v = (Variable)e1.nextElement();
                if (substitutions.get(v) != null){
                    needToSubstitute = true;
                }
            }
        }
        if (!needToSubstitute){
            return c;
        } else {
            Vector<Predicate> newHead = new Vector<Predicate>();
            Vector<Predicate> newBody = new Vector<Predicate>();
            for (Enumeration<Predicate> e = c.getHead(); e.hasMoreElements();){
                Predicate p = (Predicate)e.nextElement();
                newHead.addElement(this.applyToPredicate(p));
            }
            for (Enumeration<Predicate> e = c.getBody(); e.hasMoreElements();){
                Predicate p = (Predicate)e.nextElement();
                newBody.addElement(this.applyToPredicate(p));
            }
            return new Clause(newHead, newBody);
        }
    }
    
    /**
     * Applies a Substitution to a Predicate.
     * If the Predicate has any subTerms, they are substituted recursively.
     * A new Predicate object is only created when necessary.
     * @param p a Predicate to apply the substitution to
     * @return a new Predicate with the applied substitution
     */
    public Predicate applyToPredicate(Predicate p){
        // first check if we have to substitute anything at all
        boolean needToSubstitute = false;
        for (Enumeration<Variable> e = p.getVariables(); e.hasMoreElements();){
            Variable v = (Variable)e.nextElement();
         //   System.out.println("Variable: "+v.toString());
         //   if (substitutions.get(v) != null) System.out.println("Value: "+substitutions.get(v).toString());
         //   else System.out.println("Value: null");
            if (substitutions.get(v) != null){
         //   	System.out.println("Substituting: "+substitutions.get(v).toString());
                needToSubstitute = true;
                break;
            }
        }
        if (!needToSubstitute){
            return p;
        } else {
            Vector<Term> newSubTerms = new Vector<Term>();
            for (Enumeration<Term> e = p.getTerms(); e.hasMoreElements();){
                newSubTerms.addElement(this.applyToTerm((Term)e.nextElement()));
            }
            return LogicFactory.newPredicate(p.getName(), newSubTerms);
        }
    }
    
    /**
     * Applies a Substitution to a Term.
     * If the Term is a Function with subTerms, this method is called recursively
     * to replace all subTerms in question.
     * A new Term object is only created when necessary.
     * @param t A term to apply the substitution to
     * @return a new Term with the applied substitution
     */
    public Term applyToTerm(Term t){
        if (t.hasType(TermType.CONSTANT)){
            return t;
        } else if (t.hasType(TermType.VARIABLE)){
            Term tNew = (Term)substitutions.get(t);
            if (tNew == null){
                return t;
            } else {
                return tNew;
            }
        } else { // In case of functions
            // first check if there is anything to do at all
            // for saving space that is
            boolean needToSubstitute = false;
            for (Enumeration<Term> e = t.getAllSubTerms(); e.hasMoreElements() ;){
                Term tNew = (Term) e.nextElement();
                if (tNew.hasType(TermType.VARIABLE) && (substitutions.get(tNew) != null)){
                    needToSubstitute = true;
                    break;
                }
            }
            if (needToSubstitute == false) {
                return t;
            } else {
                Vector<Term> newSubTerms = new Vector<Term>();
                for (Enumeration<Term> e = t.getSubTerms(); e.hasMoreElements();){
                    newSubTerms.addElement(this.applyToTerm((Term)e.nextElement()));
                }
                return LogicFactory.newFunction(t.getName(),newSubTerms);
            }
        }
    }
    
    /**
     * Just needed for testing
     * @return A String representation of the substitution
     */
    @Override
    public String toString(){
        StringBuffer str = new StringBuffer();
        for (Enumeration<Variable> e = substitutions.keys() ; e.hasMoreElements();){
            Term t = (Term)e.nextElement();
            str.append(t.toString()+"/"+substitutions.get(t).toString()+"\n");
        }
        return str.toString();
    }
    
    /**
     * A static instance of an empty substitution
     */
    public static final Substitution EMPTYSUBSTITUTION = new Substitution();
    
    protected Hashtable<Variable, Term> substitutions;
}
