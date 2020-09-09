/*
 * ConceptExpression.java
 *
 * Created on 28. Juni 2004, 15:49
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

import java.util.Vector;
//import java.util.Enumeration;

/**
 * ConceptExpression serves two purposes:
 * First, it is an abstract class from which real concept expressions, like
 * conjunction, disjunction, value restriction, existential restriction etc.
 * inherit. These expressions allow you to express any DL expression. <br>
 * Also, it is a factory for cocnept expressions. It provides methods for
 * creating DL expressions ranging from <tt>top</tt> to <tt>bottom</tt>, from 
 * atom to conjunctions, disjunctions and quantifications.<br>
 * to create the DL expression <tt>
 *
 * @author  sinner
 * @version $Name:  $ $Revision: 1.8 $
 */
public class ConceptExpression extends DLExpression{
    
    /** Creates a new instance of ConceptExpression */
    public ConceptExpression(DLOperator operator, Vector<Object> operands) {
        super(operator, operands);
    }

    
    /**
     * The atomic TOP Concept.
     */
    public static final ConceptExpression TOP = atom(Concept.TOP);
    
    /**
     * The atomic BOTTOM Concept.
     */
    public static final ConceptExpression BOTTOM = atom(Concept.BOTTOM);

    /**
     * factory method for creating a new ATOM concept.
     * getOperator of a resulting expression is ConceptOperator.ATOM<br>
     * @param concept The name of the atomic concept
     * @return an atomic concept expression.
     * @throws IllegalArgumentException if concept is null
     */    
    public static final ConceptExpression atom(Concept concept){
        if (concept == null) throw new IllegalArgumentException();
        Vector<Object> v = new Vector<Object>(1);
        v.addElement(concept);
        return new ConceptExpression(DLOperator.ATOM, v);
    }
    
    /**
     * <p>
     * factory method for creating a new NOT (negation) expression.
     * getOperator of a resulting expression is ConceptOperator.NOT
     * <p>
     * There are two exceptions : if a is either TOP or BOTTOM, BOTTOM resp. TOP are returned
     * <p>
     * Negation Normal Form (NNF) is not enforced by the implementation. The programmer is
     * responsible to create NNF before using inference algorithms.
     * @param a the concept to negate
     * @return a new instance of a NOT concept expression or BOTTOM if a is TOP or TOP if a is BOTTOM
     * @throws IllegalArgumentException if a is null
     */
    public static final ConceptExpression not(ConceptExpression a){
        if (a == null) throw new IllegalArgumentException();
        if (a == TOP){
            return BOTTOM;
        } else if (a == BOTTOM){
            return TOP;
        } else {
            Vector<Object> v = new Vector<Object>(1);
            v.addElement(a);
            return new ConceptExpression(DLOperator.NOT, v);
        }
    }
        
    /**
     * <p>
     * factory method for creating a new AND (conjunction) expression.
     * getOperator of a resulting expression is ConceptOperator.AND<br>
     * If you want a conjunction operator with more than 2 concepts, use this version
     * with the vector parameter.
     * <p>
     * If concepts is empty, TOP is returned. If concepts consists of a single element, 
     * that element is returned.
     *
     * @param concepts a vector of concepts in the conjunction
     * @return a new AND (conjunction) expression if concepts.size()>=2
     */
    public static final ConceptExpression and(Vector<Object> concepts){
        if (concepts == null) throw new IllegalArgumentException();
        if (concepts.contains(null))throw new IllegalArgumentException();
        if (concepts.size() == 0){
            return TOP;
        } else if (concepts.size() == 1){
            return (ConceptExpression)concepts.firstElement();
        } else {
            return new ConceptExpression(DLOperator.AND, concepts);
        }
    }
    
    /**
     * factory method for creating a new AND (conjunction) expression.
     * getOperator of a resulting expression is ConceptOperator.AND<br>
     * If you want a conjunction operator with exactly 2 concepts, use this version
     * with two concept parameters.
     * @param a the first concept
     * @param b the second concept
     * @return a new AND (conjunction) expression
     */
    public static final ConceptExpression and(ConceptExpression a, ConceptExpression b){
        if (a == null || b == null) throw new IllegalArgumentException();
        Vector<Object> v = new Vector<Object>(2);
        v.addElement(a);
        v.addElement(b);
        return new ConceptExpression(DLOperator.AND, v);
    }
    
    /**
     * factory method for creating a new OR (disjunction) expression.
     * getOperator of a resulting expression is ConceptOperator.OR<br>
     * If you want a disjunction operator with more than 2 concepts, use this version
     * with the vector parameter.
     * <p>
     * If concepts is empty, TOP is returned. If concepts consists of a single element, 
     * that element is returned.
     * @param concepts a vector of concepts in the disjunction
     * @return a new OR (disjunction) expression if concepts.size()>=2
     */
    public static final ConceptExpression or(Vector<Object> concepts){
        if (concepts == null) throw new IllegalArgumentException();
        if (concepts.contains(null))throw new IllegalArgumentException();
        if (concepts.size() == 0){
            return TOP;
        } else if (concepts.size() == 1){
            return (ConceptExpression)concepts.firstElement();
        } else {
            return new ConceptExpression(DLOperator.OR, concepts);
        }
    }
    
    /**
     * factory method for creating a new OR (disjunction) expression.
     * getOperator of a resulting expression is ConceptOperator.OR<br>
     * If you want a disjunction operator with exactly 2 concepts, use this version
     * with two concept parameters.
     * @param a the first concept
     * @param b the second concept
     * @return a new OR (disjunction) expression
     */
    public static final ConceptExpression or(ConceptExpression a, ConceptExpression b){
        if (a == null || b == null) throw new IllegalArgumentException();
        Vector<Object> v = new Vector<Object>(2);
        v.addElement(a);
        v.addElement(b);
        return new ConceptExpression(DLOperator.OR, v);
    }
    
    /**
     * factory method for creating a new EXISTS (existential quantification) expression.
     * getOperator of a resulting expression is ConceptOperator.EXISTS<br>
     * @param role the quantified role
     * @param concept the quantified concept expression
     * @return a new EXISTS expression
     */
    public static final ConceptExpression exists(RoleExpression role, ConceptExpression concept){
        if (role == null || concept == null) throw new IllegalArgumentException();
        Vector<Object> v = new Vector<Object>(2);
        v.addElement(role);
        v.addElement(concept);
        return new ConceptExpression(DLOperator.EXISTS, v);
    }
    
    /**
     * factory method for creating a new FORALL (value restriction) expression.
     * getOperator of a resulting expression is ConceptOperator.FORALL<br>
     * @param role the quantified role
     * @param concept the quantified concept expression
     * @return a new FORALL expression
     */
    public static final ConceptExpression forall(RoleExpression role, ConceptExpression concept){
        if (role == null || concept == null) throw new IllegalArgumentException();
        Vector<Object> v = new Vector<Object>(2);
        v.addElement(role);
        v.addElement(concept);
        return new ConceptExpression(DLOperator.FORALL, v);
    }
    
    public static Concept getConceptExpressionConcept(String conex){
    	Concept con = Concept.get(conex);
    	return con;
    }
}
