/*
 * ConceptOperator.java
 *
 * Created on June 7, 2004, 10:55 PM
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

package lifr.logic.dl;

//import java.lang.StringBuffer;
/**
 * This class defines types for concept operators like conjunction, negation etc.
 * Most of them are merely symbols, but it is possible for them to also have paramters
 * (e.g. for number restriction).<br>
 * The main motivation for using these symbols is to give concept constructors a semantics
 * that can be interpreted by the implementation of a reasoning algorithm.
 * @author sinner
 * @version $Name:  $ $Revision: 1.4 $
 */
public class DLOperator {
    
    /** Creates a new instance of ConceptOperator 
      * private to disallow instantiation from outside.
      */
    private DLOperator(String name) {
        this.name = name;
    }
        
    /**
     * Get a String representation of an DL Operator.
     * @return the name of the DL operator
     */
    @Override
    public String toString(){
        return name;
    }
    
    /**
     * Universal concept
     * temporarily disabled, use Concept TOP instead
     */
//    public static final DLOperator TOP = new DLOperator("TOP");
    
    /**
     * Bottom concept
     * temporarily disabled, use Concept BOTTOM instead
     */
//    public static final DLOperator BOTTOM = new DLOperator("BOTTOM");
    
    /**
     * Atomic concept / role
     */
    public static final DLOperator ATOM = new DLOperator("");

    /**
     * Conjunction
     */
    public static final DLOperator AND = new DLOperator("AND");
    
    /**
     * Disjunction
     */
    public static final DLOperator OR = new DLOperator("OR");
    
    /**
     * Negation/Complement
     */
    public static final DLOperator NOT = new DLOperator("NOT");
    
    /**
     * Existential restriction. (concepts only)
     */
    public static final DLOperator EXISTS = new DLOperator("?");
    
    /**
     * Value restriction (concepts only)
     */
    public static final DLOperator FORALL = new DLOperator("!");
    
    /**
     * Role composition.
     */
    public static final DLOperator COMPOSE = new DLOperator("COMPOSE");
    
    /**
     * Inverse of a role.
     */
    public static final DLOperator INVERSE = new DLOperator("INV");
  
    private String name;
    
    public static final DLOperator reverseOperator(DLOperator optr){
    	DLOperator op = DLOperator.ATOM;
    	if (optr == DLOperator.AND){
    		op = DLOperator.OR;
    	}else if (optr == DLOperator.OR){
    		op = DLOperator.AND;
    	}else if (optr == DLOperator.EXISTS){
    		op = DLOperator.FORALL;
    	}else if (optr == DLOperator.FORALL){
    		op = DLOperator.EXISTS;
    	}else if (optr == DLOperator.ATOM){
    		op = DLOperator.NOT;
    	}
    	return op;
    }
}
