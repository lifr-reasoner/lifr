/*
 * Variable.java
 *
 * Created on 8. Juli 2004, 15:37
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

//import java.util.Hashtable;
import java.util.Enumeration;

import lifr.util.EmptyEnumerationTerm;
//import java.util.Vector;


//import fpocketkrhyper.util.EmptyEnumeration;

/**
 * The Variable class represents first order logic variable terms
 * @author sinner
 * @version $Name:  $ $Revision: 1.11 $
 */
public class Variable extends Term{
    
    /**
     * Creates a new instance of Variable
     * @param name the name of the Variable
     */
    public Variable(String name) {
        this.name = name;
    }
    
    
    /**
     * Check whether the instance of this variable is of a given TermType
     * @param t the TermType to check against
     * @return true if t is VARIABLE
     */
    public boolean hasType(TermType t) {
        return (t.equals(TermType.VARIABLE));
    }
    
    /**
     * Get all subterms of a term. This is empty for Variables.
     * @return an empty enumeration
     */
    public Enumeration<Term> getSubTerms() {
    	//Vector<Term> sTerms = new Vector<Term>(0); 
    	//return sTerms.elements();
    	return EmptyEnumerationTerm.INSTANCE;
    }
    
    
    /**
     * Variables are Upper case
     * @return a String representation of the variable
     */
    @Override
    public String toString(){
        return name;
    }
    
    /**
     * Get the arity (number of subterms) of a term.
     * For variables this is 0.
     * @return 0
     */
    public final int arity(){
        return 0;
    }

    /**
     * Get the maximum term depth of a term. It is 0 for variables and constants,
     * and the maximum depth of subterms for functions. 
     * f(X) has depth 1, f(f(X) has depth 2, g(f(X), X) has depth 2 etc...
     * @return 0
     */
    public final int termDepth() {
        return 0;
    }

}
