/*
 * ConceptAxiom.java
 *
 * Created on 8. Juni 2004, 11:32
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


/**
 * Defines the type of an axiom. There are for both concepts and roles 
 * equivalence and inclusion types.
 * @author sinner
 */
public class AxiomType {
    
    /** Creates a new instance of ConceptAxiom */
    private AxiomType(String print) {
        this.print = print;
    }
    
    /**
     * Get the String representation of the AxiomType. Equivalences are represented as
     * =, while subsumption is represented as &lt;
     * @return the String representation of the operator represented by the AxiomType.
     */    
    @Override
    public String toString(){
        return print;
    }
        
    /**
     * The axiom type for concept equality.
     */    
    public static final AxiomType CONCEPT_EQUALITY = new AxiomType("=");
    
    /**
     * The axiom type for concept inclusion.
     */    
    public static final AxiomType CONCEPT_INCLUSION = new AxiomType("<");
    
    public static final AxiomType CONCEPT_DISJOINTNESS = new AxiomType("^");

    /**
     * The axiom type for role equality.
     */    
    public static final AxiomType ROLE_EQUALITY = new AxiomType("=");
    
    /**
     * The axiom type for role inclusion.
     */    
    public static final AxiomType ROLE_INCLUSION = new AxiomType("<");
    private String print;
}
