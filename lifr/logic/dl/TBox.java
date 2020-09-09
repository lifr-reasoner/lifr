/*
 * TBox.java
 *
 * Created on 30. Juni 2004, 16:33
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
import java.util.Enumeration;

/**
 * A TBox is a collection of DL axioms. It allows for adding, removing and
 * getting axioms.<br>
 * It is possible to join two TBoxes, resulting in a new TBox, but leaving the other
 * TBoxes intact. Another possibility is to add a TBox to another, where the first
 * will be modified and contain also all axioms from the second.
 * @author sinner
 * @version $Name:  $ $Revision: 1.6 $
 */
public class TBox {
    
    /**
     * Creates a new instance of TBox.
     * You can specify the estimated number of Axioms contained in the TBox. This
     * makes it more resource-efficient.
     * @param numberAxioms the estimated size of the TBox. If the number is wrong, the implementation
     * will not break, but in the worst case it will either use up more
     * memory than necessary or more resources to free up additional memory.
     */
    public TBox(int numberAxioms) {
        this.axioms = new Vector<Axiom>(numberAxioms);
        roles = new Vector<Role>();
    }
        
    /**
     * Union two given TBoxes, resulting in a TBox containing axioms from both TBoxes.
     * @param one the first TBox
     * @param other another TBox
     * @return a new instance of TBox containing all axioms from both TBoxes.
     * tomkl: limited to Axioms.
     */    
    public static TBox join(TBox one, TBox other){
        TBox ret = new TBox(one.size()+other.size());
        for (Enumeration<Axiom> i = one.getAxioms(); i.hasMoreElements();){
            ret.addAxiom((Axiom)i.nextElement());
        }
        for (Enumeration<Axiom> i = other.getAxioms(); i.hasMoreElements();){
            ret.addAxiom((Axiom)i.nextElement());
        }
        return ret;
    }
    
    
    /**
     * Add all axioms of a given TBox to the calling TBox instance.
     * tomkl: limited to Axioms.
     * @param t the TBox whose Axioms will be added to the current TBox.
     */    
    public void addTBox(TBox t){
        axioms.ensureCapacity(axioms.size() + t.size());
        for (Enumeration<Axiom> i = t.getAxioms(); i.hasMoreElements();){
            axioms.addElement(i.nextElement());
        }
    }
    
    /**
     * Add a new role to the collection of roles in this Tbox
     * @param r a Role to add to the TBox
     */    
    public void add(Role r){
        if (!roles.contains(r)) {
            roles.addElement(r);
        }
    }

    /**
     * Get an enumeration of all axioms in the TBox.
     * @return an enumeration of all axioms
     */    
    public Enumeration<Role> getRoles(){
        return roles.elements();
    }
    
    /**
     * Get an enumeration of all axioms in the TBox.
     * @return an enumeration of all axioms
     */    
    public Enumeration<Axiom> getAxioms(){
        return axioms.elements();
    }
    
    /**
     * Add a single axiom
     * @param a the axiom to add to the TBox
     */    
    public void addAxiom(Axiom a){
        axioms.addElement(a);
    }

    /**
     * Removes the first occurence of a given axiom from the TBox. 
     * Calls the removeElement(Object) from 
     * the Vector class on the axioms vector.
     * @return true if the axiom was in the TBox, otherwise false
     * @param a a given axiom
     */    
    public boolean removeAxiom(Axiom a){
        return axioms.removeElement(a);
    }
    /**
     * Get the number of Axioms in the TBox
     * @return the size of the axiom vector
     */    
    public int size(){
        return axioms.size();
    }
    
    /**
     * Empty the TBox.
     */
    public void clear() {
        axioms.removeAllElements();
        roles.removeAllElements();
    }
    
    /**
     * Get a String Representation of the TBox.
     * @return A String in no specific syntax.
     */
    public String toString() {
        StringBuffer sbuf=new StringBuffer();
        sbuf.append("\nRoles\n");
        sbuf.append(roles.toString());
        sbuf.append("\nAxioms\n");
        sbuf.append(axioms.toString());
        return(sbuf.toString());
    }
    
    private Vector<Axiom> axioms;

    private Vector<Role> roles;
}
