/*
 * Axiom.java
 *
 * Created on 28. Juni 2004, 15:41
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

import java.lang.StringBuffer;

/**
 * A TBox consists of a set of DL axioms. Each axiom specifies some relationships
 * between concepts. There are equivalence axioms and subsumption axioms. The type
 * of axiom can be checked by asking getType().
 * @author sinner
 * @version $Name:  $ $Revision: 1.5 $
 */
public class Axiom {
    
    /** Creates a new instance of Axiom */
    private Axiom(AxiomType type, DLExpression left, DLExpression right) {
        this.left = left;
        this.right = right;
        this.type = type;
    }
    
    private DLExpression left;
    private DLExpression right;
    private AxiomType type;
    
    /**
     * Factory method for creating a new concept definition axiom.
     * null parameters are not allowed.
     * @throws IllegalArgumentException if either left or right is null.
     * @param left what is left of the equivalence symbol.
     * For definitorial TBoxes, use only atomar concepts.
     * @param right what is right of the equivalence symbol. Defines the semantics of left.
     * @return a new instance of Axiom
     */    
    public static Axiom conceptDefine(ConceptExpression left, ConceptExpression right){
        if (left == null || right == null) throw new IllegalArgumentException();
        return new Axiom(AxiomType.CONCEPT_EQUALITY, left, right);
    }

    /**
     * Factory method for creating a new concept definition axiom.
     * null parameters are not allowed.
     * @throws IllegalArgumentException if either left or right is null.
     * @param left what is left of the equivalence symbol.
     * For definitorial TBoxes, use only atomar concepts.
     * @param right what is right of the equivalence symbol. Defines the semantics of left.
     * @return a new instance of Axiom
     */    
    public static Axiom conceptDefine(Concept left, ConceptExpression right){
        if (left == null || right == null) throw new IllegalArgumentException();
        return new Axiom(AxiomType.CONCEPT_EQUALITY, ConceptExpression.atom(left), right);
    }
    
    public static Axiom conceptDisjoint(ConceptExpression left, ConceptExpression right){
        if (left == null || right == null) throw new IllegalArgumentException();
        return new Axiom(AxiomType.CONCEPT_DISJOINTNESS, left, right);
    }

    /**
     * Factory method for creating a new concept inclusion axiom. left is subsumed by right.
     * null parameters are not allowed.
     * @throws IllegalArgumentException if either left or right is null.
     * @param left what is left of the subsumption symbol.
     * For definitorial TBoxes, use only atomar concepts.
     * @param right what is right of the subsumption symbol. Defines the semantics of left.
     * @return a new instance of Axiom
     */    
    public static Axiom conceptImplies(ConceptExpression left, ConceptExpression right){
        if (left == null || right == null) throw new IllegalArgumentException();
        return new Axiom(AxiomType.CONCEPT_INCLUSION, left, right);
    }

    /**
     * Factory method for creating a new concept definition axiom.
     * null parameters are not allowed.
     * @throws IllegalArgumentException if either left or right is null.
     * @param left what is left of the equivalence symbol.
     * For definitorial TBoxes, use only atomar concepts.
     * @param right what is right of the equivalence symbol. Defines the semantics of left.
     * @return a new instance of Axiom
     */    
    public static Axiom roleDefine(RoleExpression left, RoleExpression right){
        if (left == null || right == null) throw new IllegalArgumentException();
        return new Axiom(AxiomType.ROLE_EQUALITY, left, right);
    }

    /**
     * Factory method for creating a new concept inclusion axiom. left is subsumed by right.
     * null parameters are not allowed.
     * @throws IllegalArgumentException if either left or right is null.
     * @param left what is left of the subsumption symbol.
     * For definitorial TBoxes, use only atomar concepts.
     * @param right what is right of the subsumption symbol. Defines the semantics of left.
     * @return a new instance of Axiom
     */    
    public static Axiom roleImplies(RoleExpression left, RoleExpression right){
        if (left == null || right == null) throw new IllegalArgumentException();
        return new Axiom(AxiomType.ROLE_INCLUSION, left, right);
    }
    
    /**
     * NEW! For domain and range restrictions to roles
     * 
     * @param left
     * @param right
     * @return
     */
    public static Axiom roleImplies(ConceptExpression left,  RoleExpression right){
        if (left == null || right == null) throw new IllegalArgumentException();
        return new Axiom(AxiomType.ROLE_INCLUSION, left, right);
    }
    /**
     * Get the String representation of an axiom.
     * @return a String representation of the axiom
     */    
    @Override
    public final String toString(){
        StringBuffer s = new StringBuffer();
        s.append(left.toString());
        s.append(type.toString());
        s.append(right.toString());
        return s.toString();
    }
 
    /**
     * Get the type of the axiom. It may be either an equivalence Axiom or a subsumption Axiom.
     * @return The type of the axiom. See see@{AxiomType}
     */    
    public final AxiomType getType(){
        return type;
    }
    
    /**
     * get the head of an Axiom.
     * @return the head of the Axiom.
     * If getType returns CONCEPT_xxx, it is a ConceptExpression, otherwise a RoleExpression.
     */
    public final DLExpression getHead(){
        return left;
    }

    /**
     * get the body of an Axiom.
     * @return the body of the Axiom.
     * If getType returns CONCEPT_xxx, it is a ConceptExpression, otherwise a RoleExpression.
     */
    public final DLExpression getBody(){
        return right;
    }
}
