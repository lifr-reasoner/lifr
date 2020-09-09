/*
 * Role.java
 *
 * Created on June 8, 2004, 12:44 AM
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

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import lifr.util.Named;

/**
 * Role represents atomic DL roles. They consist of a name and a transitive flag.
 * <br>
 * Since we assume a unique name assumption, the constructor of Role is not public,
 * rather, there is a static factory method create which creates new Roles and registers them
 * in  a Hashtable, avoiding duplicate Roles.
 * @author  sinner
 */
public class Role extends Operand implements Named{
    
    /** Creates a new instance of Role */
    private Role(String name, boolean reflexive, boolean symmetric, boolean transitive, Role inverse) {
        super(name);
        this.transitive = transitive;
        this.symmetric = symmetric;
        this.reflexive = reflexive;
        this.inverseRoles=new Vector<Role>(1);
        this.parentRoles=new Vector<Role>(1);
    }
    
    
    /**
     * get the inverse roles of this role.
     * @return null if no inverse role has been defined, otherwise
     * the inverse role.
     */
    public final Enumeration<Role> getInverseRoles(){
        return inverseRoles.elements();
    }
    
    /**
     * get the inverse roles of this role.
     * @return null if no inverse role has been defined, otherwise
     * the inverse role.
     */
    public final Enumeration<Role> getParentRoles(){
        return parentRoles.elements();
    }
    
    /**
     * a flag for defining whether the role is transitive
     * @return true if the role is transitive, false otherwise
     */
    public final boolean isTransitive(){
        return transitive;
    }
    
    /**
     * a flag for defining whether the role is symmetric.
     * @return true if the role is symmetric, false otherwise
     */
    public final boolean isSymmetric(){
        return symmetric;
    }
    
    /**
     * a flag for defining whether the role is reflexive
     * @return true if the role is reflexive, false otherwise
     */
    public final boolean isReflexive(){
        return reflexive;
    }
    
    
    //private String name;
    private boolean transitive;
    private boolean reflexive;
    private boolean symmetric;
    private Vector<Role> inverseRoles;
    private Vector<Role> parentRoles;
    
    
    /**
     * Factory method for creating a new Role.<br>
     * To ensure a unique name assumption, every role first has to be declared (created)
     * This method creates an instance of a role, registers it in an internal Hashtable
     * and returns it. If you try to create  an existing role, a
     * UniqueNameAssumptionException is thrown.
     * @return the instance of the new role
     * @param reflexive - a flag marking a role as reflexive
     * @param symmetric - a flag marking a role as symmetric
     * @param name - the name of the new role
     * @param transitive - a flag marking the role as transitive
     */
    public final static Role create(String name, boolean reflexive, boolean symmetric, boolean transitive){
        if (exists(name)){
            throw new UniqueNameAssumptionException(name);
        }
        Role r = new Role(name, reflexive, symmetric, transitive, null);
        roles.put(name, r);
        return r;
    }
    
    /**
     * Factory method for creating a new Role.<br>
     * To ensure a unique name assumption, every role first has to be declared (created)
     * This method creates an instance of a role, registers it in an internal Hashtable
     * and returns it. If you try to create  an existing role, a
     * UniqueNameAssumptionException is thrown.
     * @return the instance of the new role
     * @param name - the name of the new role
     * @param inverse - the inverse role corresponding to this new one
     * @throws UniqueNameAssumptionException when the role to be created violates the unique name assumption
     */
    public final static Role create(String name, Role inverse){
        if (exists(name)){
            throw new UniqueNameAssumptionException(name);
        }
        Role r = new Role(name, inverse.reflexive, inverse.symmetric, inverse.transitive, inverse);
        if (inverse != null){
            if (inverse.inverseRoles.size()>0){
                for (Enumeration<Role> e = ((Role)inverse.inverseRoles.firstElement()).getInverseRoles() ; e.hasMoreElements();){
                    Role next = (Role)e.nextElement();
                    r.inverseRoles.addElement(next); //copy the inverses from an equivalent role
                    next.inverseRoles.addElement(r);
                }
            } else {
                r.inverseRoles.addElement(inverse);
                inverse.inverseRoles.addElement(r);
            }
        }
        roles.put(name, r);
        return r;
    }
    
    /**
     * Factory method for creating a new role without inverse, which is neither transitive, symmetric nor reflexive.<p>
     * To ensure a unique name assumption, every role first has to be declared (created)
     * This method creates an instance of a role, registers it in an internal Hashtable
     * and returns it. If you try to create  an existing role, a
     * UniqueNameAssumptionException is thrown.
     * @return the instance of the new role
     * @param name - the name of the new role
     * @throws UniqueNameAssumptionException when the role to be created violates the unique name assumption
     */
    public final static Role create(String name){
        if (exists(name)){
            throw new UniqueNameAssumptionException(name);
        }
        Role r = new Role(name, false, false, false, null);
        roles.put(name, r);
        return r;
    }
    
    private static Hashtable<String, Role> roles = new Hashtable<String, Role>();
    
    
    /**
     * Get an already existing role by name.<br>
     * Before using this method, make sure that the given role has been created
     * using the create method. If the role exists, it is returned, otherwise
     * a UndefinedNameException is thrown.
     * @return the instance of the specified role
     * @param name the name of the role to get.
     * @throws UndefinedNameException when the role has not been created previously
     */
    public final static Role get(String name){
        Role stored = (Role)roles.get(name);
        if (stored == null){
            throw new UndefinedNameException(name);
        }
        return stored;
    }
    
    /**
     * Checks whether a specified role already exists. A role exists if it was
     * created previously using create.
     * @param name the name of the role whose existence is checked.
     * @return true if the specified role exists, false otherwise.
     */
    public final static boolean exists(String name){
        return (roles.get(name) != null);
    }
    
    /**
     * Create a role if it does not yet exist.
     * @param name the name of the role
     * @return an instance of Role whose name is given by the parameter
     */
    public static final Role getOrCreate(String name){
        if (exists(name)){
            return get(name);
        } else {
            return create(name);
        }
    }
    
    /**
     * Get the role which is the inverse of a given role
     * @param name the name of the role
     * @param inverse the inverse role
     * @return the inverse role of inverse
     */
    public static final Role getOrCreate(String name, Role inverse){
        if (exists(name)){
            return get(name);
        } else {
            return create(name, inverse);
        }
    }
    
    /**
     * get a String Representation of the role. This is the name of the role followed by
     * an optional plus symbol if the role is transitive.
     * @return a String representation of the role
     */
    @Override
    public String toString(){
        StringBuffer s = new StringBuffer();
        s.append(name);
        if (isTransitive()){
            s.append('+');
        }
        if (isReflexive()){
            s.append('@');
        }
        if (isSymmetric()){
            s.append('~');
        }
        if (parentRoles.size()>0) {
            s.append(" parents [");
            for (Enumeration<Role> en=parentRoles.elements(); en.hasMoreElements(); ) {
                s.append(((Role) en.nextElement()).getName());
            }
            s.append("]");
        }
        if (inverseRoles.size()>0) {
            s.append(" invers [");
            for (Enumeration<Role> en=inverseRoles.elements(); en.hasMoreElements(); ) {
                s.append(((Role) en.nextElement()).getName());
            }
            s.append("]");
        }
        return s.toString();
    }
    
    
    /**
     * The universal role.
     */
    public static final Role UNIVERSAL = Role.create("UNIVERSAL", true, true, true);
    /**
     * The identity role.
     */
    public static final Role IDENTITY = Role.create("ID",true, true, true);
    
    /**
     * set attribute of the role
     * @param transitive sets a flag to indicate wether the role is transitive
     */
    public void setTransitive(boolean transitive) {
        this.transitive = transitive;
        for (Enumeration<Role> en = inverseRoles.elements(); en.hasMoreElements(); ){
            ((Role) en.nextElement()).setTransitive(transitive);
        }
        // update all inverse roles
    }
    
    /**
     * set attribute of the role
     * @param reflexive sets a flag to indicate wether the role is reflexive
     */
    public void setReflexive(boolean reflexive) {
        this.reflexive = reflexive;
        for (Enumeration<Role> en = inverseRoles.elements(); en.hasMoreElements(); ){
            ((Role) en.nextElement()).setReflexive(reflexive);
        }
        // update all inverse roles
    }
    
    /**
     * set attribute of the role
     * @param symmetric sets a flag to indicate wether the role is symmetric
     */
    public void setSymmetric(boolean symmetric) {
        this.symmetric = symmetric;
        for (Enumeration<Role> en = inverseRoles.elements(); en.hasMoreElements(); ){
            ((Role) en.nextElement()).setSymmetric(symmetric);
        }
        // update all inverse roles
    }
    
    /**
     * add an inverse to the role, join attributes from inverse iff inverse exists
     * @param invname of inverse
     * @return the inverse Role
     */
    public Role addInverse(String invname) {
        Role invRole = null;
        if (exists(invname)) {
            invRole = get(invname);
            if (!inverseRoles.contains(invRole)) {
                inverseRoles.addElement(invRole);
                //setTransitive(transitive || invRole.isTransitive());
                //setSymmetric(symmetric || invRole.isSymmetric());
                //setReflexive(reflexive || invRole.isReflexive());
            }
            invRole.addInverse(this); // and vice versa
        } else {
            invRole = create(invname, this);
        }
        return(invRole);
    }
    
    /**
     * add an inverse to the role, attributes joined
     * @param invRole the inverse Role
     * @return the inverse Role
     */
    public Role addInverse(Role invRole) {
        if (!inverseRoles.contains(invRole)) {
            inverseRoles.addElement(invRole);
            //setTransitive(transitive || invRole.isTransitive());
            //setSymmetric(symmetric || invRole.isSymmetric());
            //setReflexive(reflexive || invRole.isReflexive());
//            invRole.inverseRoles.addElement(this); // and vice versa
            //invRole.setTransitive(transitive);
            //invRole.setSymmetric(symmetric);
            //invRole.setReflexive(reflexive);
        }
        return(invRole);
    }
    
    /**
     * add a parent to the role, create if neccessary
     * @param parentname name of parent role
     * @return the parent Role, new or retrieved
     */
    public Role addParent(String parentname) {
        Role parRole = null;
        if (exists(parentname)) {
            parRole = get(parentname);
            parentRoles.addElement(parRole);
        } else {
            parRole = create(parentname);
        }
        return(parRole);
    }
    
    /**
     * add a parent to the role, create if neccessary
     * @param parRole The parent role.
     * @return the parent Role
     */
    public Role addParent(Role parRole) {
        parentRoles.addElement(parRole);
        return(parRole);
    }
}
