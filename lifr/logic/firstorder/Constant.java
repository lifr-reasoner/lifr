/*
 * Constant.java
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
import java.util.Enumeration;

import lifr.util.EmptyEnumerationTerm;
//import java.util.Vector;
//import fpocketkrhyper.util.EmptyEnumeration;


/**
 * A constant is a term which may not be substituted.
 * @author  sinner
 * @version $Name:  $ $Revision: 1.10 $
 */
public class Constant extends Function{
    
    /**
     * Creates a new instance of Constant
     * @param name The name of the constant
     */
    public Constant(String name) {
        super(name, null);
    }
            
    /**
     * check whether a given term is either a variable or a function.
     * @return true if the type is CONSTANT
     * @param t the parameter TermType
     */    
    public boolean hasType(TermType t) {
        return (t.equals(TermType.CONSTANT));
    }
    
    /**
     * Get the subterms, which is empty in the case of constants
     * @return an empty Enumeration.
     */
    public Enumeration<Term> getSubTerms() {
        //Vector<Term> sTerms = new Vector<Term>(0); 
    	//return sTerms.elements();
    	return EmptyEnumerationTerm.INSTANCE;
    }
    
    /**
     * Constants are written in lower case. They may not be preceded with a '_'
     * symbol, which is reserved for variables.
     * @return the name of the Constant
     */
    public String toString(){
        return name;
    }
   
    /**
     * Get the arity (number of subterms) of a term.
     * For Constants this is 0.
     * @return 0
     */
    public final int arity(){
        return 0;
    }

    /**
     * get the term depth, which is 0 for constants.
     * @return 0
     */
    public final int termDepth() {
        return 0;
    }

}
