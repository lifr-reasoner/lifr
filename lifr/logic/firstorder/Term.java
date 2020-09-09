/*
 * Term.java
 *
 * Created on 8. Juli 2004, 15:27
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

import java.util.Vector;
import java.util.Enumeration;

/**
 * A first order logic term. Term is an abstract class, from which Variable,
 * Constant and Function inherit.
 * @author  sinner
 * @version $Name:  $ $Revision: 1.9 $
 */
public abstract class Term {
    
    /**
     * Dummy constructor for inheritance. The name is not set because
     * subclasses may preprocess the string before storing it.
     */
    protected Term(){
        this.name = null;
    }
    /**
     * Constructs a new Term.
     * @param name The string representation of the term.
     */
    public Term(String name){
        this.name = name;
    }
    
    /**
     * get the name of the term.
     * @return a string representing the name of the term. In case of functions, this is not equivalent to toString().
     */
    public String getName(){
        return name;
    }
    
    /**
     * check whether a given term is either a variable or a function.
     * @return The type of the term. This may either be a function (or constant) or a variable.
     * @param t the TermType to check against
     */
    public abstract boolean hasType(TermType t);
    
    /**
     * Get the direct subterms of this term.
     * @return an Enumeration of subterms
     */
    public abstract Enumeration<Term> getSubTerms();

    /**
     * Get all subterms including the term in question. For variables,
     * constants, this only returns the term in question, for functions
     * it returns the function and all the subterms contained in the function.
     * The implementation is recursive, which means not very memory efficient...
     * @return the recursive enumeration of all subterms
     */ 
    public final Enumeration<Term> getAllSubTerms(){
        Vector<Term> terms = new Vector<Term>();
        terms.addElement(this);
        // only functions enter the for loop
        for (Enumeration<Term> e = this.getSubTerms(); e.hasMoreElements();){
            Term t = (Term)e.nextElement();
            for (Enumeration<Term> e1 = t.getAllSubTerms(); e1.hasMoreElements();){
                terms.addElement(e1.nextElement());
            }
        }
        return terms.elements();
    }

    /**
     * Get the arity (number of subterms) of a term
     * @return the arity
     */
    public abstract int arity();
    
    /**
     * Get the maximum term depth of a term. It is 0 for variables and constants,
     * and the maximum depth of subterms for functions. 
     * f(X) has depth 1, f(f(X) has depth 2, g(f(X), X) has depth 2 etc...
     * @return the term depth
     */
    public abstract int termDepth();
    
    /**
     * The string representation name of the term
     */
    protected String name;
}

