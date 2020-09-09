/*
 * HyperNode.java
 *
 * Created on 28. Oktober 2004, 17:18
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

package lifr.reasoner.krhyper;

import java.util.Vector;
import java.util.Enumeration;

import lifr.logic.firstorder.Clause;
import lifr.logic.firstorder.Predicate;
import lifr.logic.fuzzy.Degree;
import lifr.logic.fuzzy.Weight;


/**
 * Class HyperNode stores all the information needed for a node in a
 * KRHyper proof tree. This is the current branch, a list of open disjuncts,
 * a list of deltas and a list of new expansions.
 * @author  sinner
 * @version $Name:  $ $Revision: 1.8 $
 */
public class HyperNode {
    
    /** Creates a new empty instance of HyperNode */
    HyperNode() {
        expansions = new Vector<Predicate>();
        branch = new Vector<Predicate>();
        disjunctions = new Vector<Vector<Predicate>>();
        notRangeRestrictedClauses = new Vector<Clause>();
        delta = new Vector<Predicate>();
    }
    
    HyperNode(Vector<Predicate> expansions, Vector<Vector<Predicate>> disjunctions, Vector<Clause> notRangeRestrictedDisjunctions){
        this.expansions = expansions;
        branch = new Vector<Predicate>();
        this.disjunctions = new Vector<Vector<Predicate>>(disjunctions.size());
        this.notRangeRestrictedClauses = new Vector<Clause>(notRangeRestrictedDisjunctions.size());
        for (Enumeration<Vector<Predicate>> e = disjunctions.elements() ; e.hasMoreElements() ;){
            this.disjunctions.addElement(e.nextElement());
        }
        for (Enumeration<Clause> e = notRangeRestrictedDisjunctions.elements() ; e.hasMoreElements() ;){
            this.notRangeRestrictedClauses.addElement(e.nextElement());
        }
        delta = new Vector<Predicate>();
    }
    
    
    public Vector<Predicate> delta;
    public Vector<Vector<Predicate>> disjunctions;
    public Vector<Clause> notRangeRestrictedClauses;
    public Vector<Predicate> expansions;
    public Vector<Predicate> branch;
}
